package digit.validator;

import com.jayway.jsonpath.JsonPath;
import digit.config.PGRConfiguration;
import digit.repository.PGRRepository;
import digit.util.HRMSUtil;
import digit.web.models.RequestSearchCriteria;
import digit.web.models.Service;
import digit.web.models.ServiceRequest;
import digit.web.models.ServiceWrapper;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static digit.config.ServiceConstants.*;

@Component
public class PGRValidator {

	private final PGRConfiguration config;

	private final PGRRepository repository;

	private final HRMSUtil hrmsUtil;

	@Autowired
	public PGRValidator(PGRConfiguration config, PGRRepository repository, HRMSUtil hrmsUtil) {
		this.config = config;
		this.repository = repository;
		this.hrmsUtil = hrmsUtil;
	}

	public void validateCreateRequest(ServiceRequest request, Object mdmsData) {
		Map<String,String> errorMap = new HashMap<>();
		validateUserData(request,errorMap);
		validateSource(request.getPgrEntity().getService().getSource());
		validateMDMS(request, mdmsData);
		if(config.getIsValidateDeptEnabled()) validateDepartment(request, mdmsData);
		if(!errorMap.isEmpty())
			throw new CustomException(errorMap);
	}

	public void validateUpdate(ServiceRequest request, Object mdmsData){

		String id = request.getPgrEntity().getService().getId();
		String tenantId = request.getPgrEntity().getService().getTenantId();
		validateSource(request.getPgrEntity().getService().getSource());
		validateMDMS(request, mdmsData);
		validateDepartment(request, mdmsData);
		validateReOpen(request);
		RequestSearchCriteria criteria = RequestSearchCriteria.builder().ids(Collections.singleton(id)).tenantId(tenantId).build();
		criteria.setIsPlainSearch(false);
//		List<ServiceWrapper> serviceWrappers = repository.getServiceWrappers(criteria);

//		if(CollectionUtils.isEmpty(serviceWrappers))
//			throw new CustomException("INVALID_UPDATE","The record that you are trying to update does not exists");
	}

	private void validateUserData(ServiceRequest request,Map<String, String> errorMap){

		RequestInfo requestInfo = request.getRequestInfo();

		if(requestInfo.getUserInfo().getType().equalsIgnoreCase(USERTYPE_EMPLOYEE)){
			User citizen = request.getPgrEntity().getService().getCitizen();
			if(citizen == null)
				errorMap.put("INVALID_REQUEST","Citizen object cannot be null");
			else if(citizen.getMobileNumber()==null || citizen.getName()==null)
				errorMap.put("INVALID_REQUEST","Name and Mobile Number is mandatory in citizen object");
		}

	}


	private void validateMDMS(ServiceRequest request, Object mdmsData){

		String serviceCode = request.getPgrEntity().getService().getServiceCode();
		String jsonPath = MDMS_SERVICEDEF_SEARCH.replace("{SERVICEDEF}",serviceCode);

		List<Object> res = null;

		try{
			res = JsonPath.read(mdmsData,jsonPath);
		}
		catch (Exception e){
			throw new CustomException("JSONPATH_ERROR","Failed to parse mdms response");
		}

		if(CollectionUtils.isEmpty(res))
			throw new CustomException("INVALID_SERVICECODE","The service code: "+serviceCode+" is not present in MDMS");
	}

	private void validateDepartment(ServiceRequest request, Object mdmsData){

		String serviceCode = request.getPgrEntity().getService().getServiceCode();
		List<String> assignes = request.getPgrEntity().getWorkflow().getAssignes();

		if(CollectionUtils.isEmpty(assignes))
			return;

		List<String> departments = hrmsUtil.getDepartment(assignes, request.getRequestInfo(),request.getPgrEntity().getService().getTenantId());

		String jsonPath = MDMS_DEPARTMENT_SEARCH.replace("{SERVICEDEF}",serviceCode);

		List<String> res = null;
		String departmentFromMDMS;

		try{
			res = JsonPath.read(mdmsData,jsonPath);
		}
		catch (Exception e){
			throw new CustomException("JSONPATH_ERROR","Failed to parse mdms response for department");
		}

		if(CollectionUtils.isEmpty(res))
			throw new CustomException("PARSING_ERROR","Failed to fetch department from mdms data for serviceCode: "+serviceCode);
		else departmentFromMDMS = res.get(0);

		Map<String, String> errorMap = new HashMap<>();

		if(!departments.contains(departmentFromMDMS))
			errorMap.put("INVALID_ASSIGNMENT","The application cannot be assigned to employee of department: "+departments.toString());


		if(!errorMap.isEmpty())
			throw new CustomException(errorMap);

	}

	private void validateReOpen(ServiceRequest request){

		if(!request.getPgrEntity().getWorkflow().getAction().equalsIgnoreCase(PGR_WF_REOPEN))
			return;


		Service service = request.getPgrEntity().getService();
		RequestInfo requestInfo = request.getRequestInfo();
		Long lastModifiedTime = service.getAuditDetails().getLastModifiedTime();

		if(requestInfo.getUserInfo().getType().equalsIgnoreCase(USERTYPE_CITIZEN)){
			if(!requestInfo.getUserInfo().getUuid().equalsIgnoreCase(service.getAccountId()))
				throw new CustomException("INVALID_ACTION","Not authorized to re-open the complain");
		}

		if(System.currentTimeMillis()-lastModifiedTime > config.getComplainMaxIdleTime())
			throw new CustomException("INVALID_ACTION","Complaint is closed");

	}
	
	public void validateSearch(RequestInfo requestInfo, RequestSearchCriteria criteria){

		/*
		 * Checks if tenatId is provided with the search params
		 * */
		if( (criteria.getMobileNumber()!=null
				|| criteria.getServiceRequestId()!=null || criteria.getIds()!=null
				|| criteria.getServiceCode()!=null )
				&& criteria.getTenantId()==null)
			throw new CustomException("INVALID_SEARCH","TenantId is mandatory search param");

		validateSearchParam(requestInfo, criteria);

	}
	
	private void validateSearchParam(RequestInfo requestInfo, RequestSearchCriteria criteria){

		if(requestInfo.getUserInfo().getType().equalsIgnoreCase("EMPLOYEE" ) && criteria.isEmpty())
			throw new CustomException("INVALID_SEARCH","Search without params is not allowed");

		String allowedParamStr = null;

		if(requestInfo.getUserInfo().getType().equalsIgnoreCase("CITIZEN" ))
			allowedParamStr = config.getAllowedCitizenSearchParameters();
		else if(requestInfo.getUserInfo().getType().equalsIgnoreCase("EMPLOYEE" ) || requestInfo.getUserInfo().getType().equalsIgnoreCase("SYSTEM") )
			allowedParamStr = config.getAllowedEmployeeSearchParameters();
		else throw new CustomException("INVALID SEARCH","The userType: "+requestInfo.getUserInfo().getType()+
					" does not have any search config");

		List<String> allowedParams = Arrays.asList(allowedParamStr.split(","));

		if(criteria.getServiceCode()!=null && !allowedParams.contains("serviceCode"))
			throw new CustomException("INVALID SEARCH","Search on serviceCode is not allowed");

		if(criteria.getServiceRequestId()!=null && !allowedParams.contains("serviceRequestId"))
			throw new CustomException("INVALID SEARCH","Search on serviceRequestId is not allowed");

		if(criteria.getApplicationStatus()!=null && !allowedParams.contains("applicationStatus"))
			throw new CustomException("INVALID SEARCH","Search on applicationStatus is not allowed");

		if(criteria.getMobileNumber()!=null && !allowedParams.contains("mobileNumber"))
			throw new CustomException("INVALID SEARCH","Search on mobileNumber is not allowed");

		if(criteria.getIds()!=null && !allowedParams.contains("ids"))
			throw new CustomException("INVALID SEARCH","Search on ids is not allowed");

	}

	private void validateSource(String source){

		List<String> allowedSourceStr = Arrays.asList(config.getAllowedSource().split(","));

		if(!allowedSourceStr.contains(source))
			throw new CustomException("INVALID_SOURCE","The source: "+source+" is not valid");

	}
}
