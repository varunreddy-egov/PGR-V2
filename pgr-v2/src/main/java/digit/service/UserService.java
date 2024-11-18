package digit.service;

import digit.config.PGRConfiguration;
import digit.util.UserUtil;
import digit.web.models.RequestSearchCriteria;
import digit.web.models.ServiceRequest;
import digit.web.models.ServiceWrapper;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.common.contract.user.CreateUserRequest;
import org.egov.common.contract.user.UserDetailResponse;
import org.egov.common.contract.user.UserSearchRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static digit.config.ServiceConstants.*;

@Service
public class UserService {

	private UserUtil userUtil;

	private PGRConfiguration config;

	@Autowired
	public UserService(UserUtil userUtil, PGRConfiguration config) {
		this.userUtil = userUtil;
		this.config = config;
	}

	public void callUserService(ServiceRequest request){

		if(!StringUtils.isEmpty(request.getPgrEntity().getService().getAccountId()))
			enrichUser(request);
		else if(request.getPgrEntity().getService().getCitizen()!=null)
			upsertUser(request);

	}

	public void enrichUsers(List<ServiceWrapper> serviceWrappers){

		Set<String> uuids = new HashSet<>();

		serviceWrappers.forEach(serviceWrapper -> {
			uuids.add(serviceWrapper.getService().getAccountId());
		});

		Map<String, User> idToUserMap = searchBulkUser(new LinkedList<>(uuids));

		serviceWrappers.forEach(serviceWrapper -> {
			serviceWrapper.getService().setCitizen(idToUserMap.get(serviceWrapper.getService().getAccountId()));
		});

	}

	private void upsertUser(ServiceRequest request){

		User user = request.getPgrEntity().getService().getCitizen();
		String tenantId = request.getPgrEntity().getService().getTenantId();
		User userServiceResponse = null;

		// Search on mobile number as user name
		UserDetailResponse userDetailResponse = searchUser(userUtil.getStateLevelTenant(tenantId),null, user.getMobileNumber());
		if (!userDetailResponse.getUser().isEmpty()) {
			User userFromSearch = userDetailResponse.getUser().get(0);
			if(!user.getName().equalsIgnoreCase(userFromSearch.getName())){
				userServiceResponse = updateUser(request.getRequestInfo(),user,userFromSearch);
			}
			else userServiceResponse = userDetailResponse.getUser().get(0);
		}
		else {
			userServiceResponse = createUser(request.getRequestInfo(),tenantId,user);
		}

		// Enrich the accountId
		request.getPgrEntity().getService().setAccountId(userServiceResponse.getUuid());
	}

	private void enrichUser(ServiceRequest request){

		RequestInfo requestInfo = request.getRequestInfo();
		String accountId = request.getPgrEntity().getService().getAccountId();
		String tenantId = request.getPgrEntity().getService().getTenantId();

		UserDetailResponse userDetailResponse = searchUser(userUtil.getStateLevelTenant(tenantId),accountId,null);

		if(userDetailResponse.getUser().isEmpty())
			throw new CustomException("INVALID_ACCOUNTID","No user exist for the given accountId");

		else request.getPgrEntity().getService().setCitizen(userDetailResponse.getUser().get(0));

	}

	private User createUser(RequestInfo requestInfo,String tenantId, User userInfo) {

		userUtil.addUserDefaultFields(userInfo.getMobileNumber(),tenantId, userInfo);
		StringBuilder uri = new StringBuilder(config.getUserHost())
				.append(config.getUserContextPath())
				.append(config.getUserCreateEndpoint());


		UserDetailResponse userDetailResponse = userUtil.userCall(new CreateUserRequest(requestInfo, userInfo), uri);

		return userDetailResponse.getUser().get(0);

	}

	private User updateUser(RequestInfo requestInfo,User user,User userFromSearch) {

		userFromSearch.setName(user.getName());

		StringBuilder uri = new StringBuilder(config.getUserHost())
				.append(config.getUserContextPath())
				.append(config.getUserUpdateEndpoint());


		UserDetailResponse userDetailResponse = userUtil.userCall(new CreateUserRequest(requestInfo, userFromSearch), uri);

		return userDetailResponse.getUser().get(0);

	}

	private UserDetailResponse searchUser(String stateLevelTenant, String accountId, String userName){

		UserSearchRequest userSearchRequest =new UserSearchRequest();
		userSearchRequest.setActive(true);
		userSearchRequest.setUserType(USERTYPE_CITIZEN);
		userSearchRequest.setTenantId(stateLevelTenant);

		if(StringUtils.isEmpty(accountId) && StringUtils.isEmpty(userName))
			return null;

		if(!StringUtils.isEmpty(accountId))
			userSearchRequest.setUuid(Collections.singletonList(accountId));

		if(!StringUtils.isEmpty(userName))
			userSearchRequest.setUserName(userName);

		StringBuilder uri = new StringBuilder(config.getUserHost()).append(config.getUserSearchEndpoint());
		return userUtil.userCall(userSearchRequest,uri);

	}

	private Map<String,User> searchBulkUser(List<String> uuids){

		UserSearchRequest userSearchRequest =new UserSearchRequest();
		userSearchRequest.setActive(true);
		userSearchRequest.setUserType(USERTYPE_CITIZEN);


		if(!CollectionUtils.isEmpty(uuids))
			userSearchRequest.setUuid(uuids);


		StringBuilder uri = new StringBuilder(config.getUserHost()).append(config.getUserSearchEndpoint());
		UserDetailResponse userDetailResponse = userUtil.userCall(userSearchRequest,uri);
		List<User> users = userDetailResponse.getUser();

		if(CollectionUtils.isEmpty(users))
			throw new CustomException("USER_NOT_FOUND","No user found for the uuids");

		Map<String,User> idToUserMap = users.stream().collect(Collectors.toMap(User::getUuid, Function.identity()));

		return idToUserMap;
	}

	public void enrichUserIds(String tenantId, RequestSearchCriteria criteria){

		String mobileNumber = criteria.getMobileNumber();

		UserSearchRequest userSearchRequest =new UserSearchRequest();
		userSearchRequest.setActive(true);
		userSearchRequest.setUserType(USERTYPE_CITIZEN);
		userSearchRequest.setTenantId(tenantId);
		userSearchRequest.setMobileNumber(mobileNumber);

		StringBuilder uri = new StringBuilder(config.getUserHost()).append(config.getUserSearchEndpoint());
		UserDetailResponse userDetailResponse = userUtil.userCall(userSearchRequest,uri);
		List<User> users = userDetailResponse.getUser();

		Set<String> userIds = users.stream().map(User::getUuid).collect(Collectors.toSet());
		criteria.setUserIds(userIds);
	}

}
