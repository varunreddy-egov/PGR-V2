package digit.service;

import digit.config.PGRConfiguration;
import digit.kafka.Producer;
import digit.repository.PGRRepository;
import digit.util.MdmsUtil;
import digit.util.WorkflowUtil;
import digit.validator.PGRValidator;
import digit.web.models.RequestSearchCriteria;
import digit.web.models.ServiceRequest;
import digit.web.models.ServiceWrapper;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
public class PGRService {

	private final PGRConfiguration config;
	private final MdmsUtil mdmsUtil;
	private final PGRValidator pgrValidator;
	private final EnrichmentService enrichmentService;
	private final WorkflowUtil workflowUtil;
	private final Producer producer;
	private final PGRRepository pgrRepository;
	private final UserService userService;

	@Autowired
	public PGRService(PGRConfiguration config, MdmsUtil mdmsUtil, PGRValidator pgrValidator,
					  EnrichmentService enrichmentService, WorkflowUtil workflowUtil,
					  Producer producer, PGRRepository pgrRepository, UserService userService) {
		this.config = config;
		this.mdmsUtil = mdmsUtil;
		this.pgrValidator = pgrValidator;
		this.enrichmentService = enrichmentService;
		this.workflowUtil = workflowUtil;
		this.producer = producer;
		this.pgrRepository = pgrRepository;
		this.userService = userService;
	}

	/**
	 * Creates a new complaint, forwarding it from "NULL" application status to another.
	 * This method handles the validation, enrichment, and workflow updates necessary before publishing the complaint.
	 *
	 * @param request The request containing the complaint details to be processed.
	 * @return The updated ServiceRequest object.
	 */
	public ServiceRequest create(ServiceRequest request) {
		String tenantId = request.getPgrEntity().getService().getTenantId();
		Object mdmsData = mdmsUtil.fetchMdmsData(request.getRequestInfo(), tenantId);
		pgrValidator.validateCreateRequest(request, mdmsData);
		enrichmentService.enrichCreateRequest(request);
		workflowUtil.updateWorkflowStatus(request);

		producer.push(tenantId, config.getCreateTopic(), request);
		return request;
	}

	/**
	 * Updates an existing complaint and it's application status i.e., workflow state.
	 * This method performs necessary validations, enrichments, and workflow status updates before pushing the updated request to the appropriate Kafka topic.
	 *
	 * @param request The request containing the updated complaint details.
	 * @return The updated ServiceRequest object.
	 */
	public ServiceRequest update(ServiceRequest request) {
		String tenantId = request.getPgrEntity().getService().getTenantId();
		Object mdmsData = mdmsUtil.fetchMdmsData(request.getRequestInfo(), tenantId);
		pgrValidator.validateUpdate(request, mdmsData);
		enrichmentService.enrichUpdateRequest(request);
		workflowUtil.updateWorkflowStatus(request);
		producer.push(tenantId, config.getUpdateTopic(), request);
		return request;
	}

	/**
	 * Counts the number of complaints matching the provided search criteria.
	 *
	 * @param criteria The criteria used to filter complaints.
	 * @return The total count of complaints matching the criteria.
	 */
	public Integer count(RequestSearchCriteria criteria) {
		return pgrRepository.getCount(criteria);
	}

	/**
	 * Searches for complaints based on the provided search criteria.
	 * This method performs validation, enrichment, and sorting by the creation time of the service wrappers.
	 *
	 * @param requestInfo The request information.
	 * @param criteria The search criteria used to filter complaints.
	 * @return A list of ServiceWrapper objects containing the details of matching complaints, sorted by creation time.
	 */
	public List<ServiceWrapper> search(RequestInfo requestInfo, RequestSearchCriteria criteria) {
		pgrValidator.validateSearch(requestInfo, criteria);

		enrichmentService.enrichSearchRequest(requestInfo, criteria);

		if (criteria.isEmpty())
			return new ArrayList<>();

		List<ServiceWrapper> serviceWrappers = pgrRepository.getServiceWrappers(criteria);

		if (CollectionUtils.isEmpty(serviceWrappers))
			return new ArrayList<>();

		userService.enrichUsers(serviceWrappers);
		List<ServiceWrapper> enrichedServiceWrappers = workflowUtil.enrichWorkflow(requestInfo, serviceWrappers);
		Map<Long, List<ServiceWrapper>> sortedWrappers = new TreeMap<>(Collections.reverseOrder());
		for (ServiceWrapper svc : enrichedServiceWrappers) {
			if (sortedWrappers.containsKey(svc.getService().getAuditDetails().getCreatedTime())) {
				sortedWrappers.get(svc.getService().getAuditDetails().getCreatedTime()).add(svc);
			} else {
				List<ServiceWrapper> serviceWrapperList = new ArrayList<>();
				serviceWrapperList.add(svc);
				sortedWrappers.put(svc.getService().getAuditDetails().getCreatedTime(), serviceWrapperList);
			}
		}
		List<ServiceWrapper> sortedServiceWrappers = new ArrayList<>();
		for (Long createdTimeDesc : sortedWrappers.keySet()) {
			sortedServiceWrappers.addAll(sortedWrappers.get(createdTimeDesc));
		}
		return sortedServiceWrappers;
	}
}
