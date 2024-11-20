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

import static digit.config.ServiceConstants.USERTYPE_CITIZEN;

@org.springframework.stereotype.Service
public class EnrichmentService {

	private final PGRUtil pgrUtil;

	private final IdgenUtil idgenUtil;

	private final PGRConfiguration config;

	private final UserService userService;

	@Autowired
	public EnrichmentService(PGRUtil pgrUtil, IdgenUtil idgenUtil, PGRConfiguration config, UserService userService) {
		this.pgrUtil = pgrUtil;
		this.idgenUtil = idgenUtil;
		this.config = config;
		this.userService = userService;
	}

	/**
	 * Enriches a service request during its creation process by populating necessary fields.
	 *
	 * @param request The service request to be enriched.
	 */
	public void enrichCreateRequest(ServiceRequest request) {

		RequestInfo requestInfo = request.getRequestInfo();
		Service service = request.getPgrEntity().getService();
		Workflow workflow = request.getPgrEntity().getWorkflow();
		String tenantId = service.getTenantId();

		// Enrich accountId of the logged in citizen
		if (requestInfo.getUserInfo().getType().equalsIgnoreCase(USERTYPE_CITIZEN))
			request.getPgrEntity().getService().setAccountId(requestInfo.getUserInfo().getUuid());

		userService.callUserService(request);


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

	/**
	 * Enriches a service request during its update process by populating necessary fields.
	 *
	 * @param serviceRequest The service request to be enriched during the update.
	 */
	public void enrichUpdateRequest(ServiceRequest serviceRequest) {

		RequestInfo requestInfo = serviceRequest.getRequestInfo();
		Service service = serviceRequest.getPgrEntity().getService();
		AuditDetails auditDetails = pgrUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), service, false);

		service.setAuditDetails(auditDetails);

		userService.callUserService(serviceRequest);
	}

	/**
	 * Enriches the search criteria for a service request search by populating necessary fields.
	 *
	 * @param requestInfo The request info containing user details.
	 * @param criteria    The search criteria to be enriched.
	 */
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
