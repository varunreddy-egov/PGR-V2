package digit.service;

import digit.config.PGRConfiguration;
import digit.util.IdgenUtil;
import digit.util.PGRUtil;
import digit.web.models.*;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

import static digit.config.ServiceConstants.*;

@org.springframework.stereotype.Service
public class EnrichmentService {

	private PGRUtil pgrUtil;

	private IdgenUtil idgenUtil;

	private PGRConfiguration config;

	private UserService userService;

	@Autowired
	public EnrichmentService(PGRUtil pgrUtil, IdgenUtil idgenUtil, PGRConfiguration config, UserService userService) {
		this.pgrUtil = pgrUtil;
		this.idgenUtil = idgenUtil;
		this.config = config;
		this.userService = userService;
	}

	public void enrichCreateRequest(ServiceRequest serviceRequest) {

		RequestInfo requestInfo = serviceRequest.getRequestInfo();
		Service service = serviceRequest.getPgrEntity().getService();
		Workflow workflow = serviceRequest.getPgrEntity().getWorkflow();
		String tenantId = service.getTenantId();

		// Enrich accountId of the logged in citizen
		if (requestInfo.getUserInfo().getType().equalsIgnoreCase(USERTYPE_CITIZEN))
			serviceRequest.getPgrEntity().getService().setAccountId(requestInfo.getUserInfo().getUuid());

		userService.callUserService(serviceRequest);


		AuditDetails auditDetails = pgrUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), service, true);

		service.setAuditDetails(auditDetails);
		service.setId(UUID.randomUUID().toString());
		service.getAddress().setId(UUID.randomUUID().toString());
		service.getAddress().setTenantId(tenantId);

		if (workflow.getVerificationDocuments() != null) {
			workflow.getVerificationDocuments().forEach(document -> {
				document.setId(UUID.randomUUID().toString());
			});
		}

		if (StringUtils.isEmpty(service.getAccountId()))
			service.setAccountId(service.getCitizen().getUuid());

		List<String> customIds = idgenUtil.getIdList(requestInfo, tenantId, config.getServiceRequestIdGenName(), config.getServiceRequestIdGenFormat(), 1);

		service.setServiceRequestId(customIds.get(0));


	}

	public void enrichUpdateRequest(ServiceRequest serviceRequest) {

		RequestInfo requestInfo = serviceRequest.getRequestInfo();
		Service service = serviceRequest.getPgrEntity().getService();
		AuditDetails auditDetails = pgrUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), service, false);

		service.setAuditDetails(auditDetails);

		userService.callUserService(serviceRequest);
	}

	public void enrichSearchRequest(RequestInfo requestInfo, RequestSearchCriteria criteria) {

		if (criteria.isEmpty() && requestInfo.getUserInfo().getType().equalsIgnoreCase(USERTYPE_CITIZEN)) {
			String citizenMobileNumber = requestInfo.getUserInfo().getUserName();
			criteria.setMobileNumber(citizenMobileNumber);
		}

		criteria.setAccountId(requestInfo.getUserInfo().getUuid());

		String tenantId = (criteria.getTenantId() != null) ? criteria.getTenantId() : requestInfo.getUserInfo().getTenantId();

		if (criteria.getMobileNumber() != null) {
			userService.enrichUserIds(tenantId, criteria);
		}

		if (criteria.getLimit() == null)
			criteria.setLimit(config.getDefaultLimit());

		if (criteria.getOffset() == null)
			criteria.setOffset(config.getDefaultOffset());

		if (criteria.getLimit() != null && criteria.getLimit() > config.getMaxLimit())
			criteria.setLimit(config.getMaxLimit());

	}
}
