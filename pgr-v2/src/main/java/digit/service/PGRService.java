package digit.service;

import digit.config.PGRConfiguration;
import digit.kafka.Producer;
import digit.util.MdmsUtil;
import digit.util.WorkflowUtil;
import digit.validator.PGRValidator;
import digit.web.models.ServiceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PGRService {

	private final PGRConfiguration config;
	private final MdmsUtil mdmsUtil;
	private final PGRValidator pgrValidator;
	private final EnrichmentService enrichmentService;
	private final WorkflowUtil workflowUtil;
	private final Producer producer;

	@Autowired
	public PGRService(PGRConfiguration config, MdmsUtil mdmsUtil, PGRValidator pgrValidator,
					  EnrichmentService enrichmentService, WorkflowUtil workflowUtil,
					  Producer producer) {
		this.config = config;
		this.mdmsUtil = mdmsUtil;
		this.pgrValidator = pgrValidator;
		this.enrichmentService = enrichmentService;
		this.workflowUtil = workflowUtil;
		this.producer = producer;
	}

	public ServiceRequest create(ServiceRequest serviceRequest) {
		String tenantId = serviceRequest.getPgrEntity().getService().getTenantId();
		Object mdmsData = mdmsUtil.fetchMdmsData(serviceRequest.getRequestInfo(), tenantId);
		pgrValidator.validateCreateRequest(serviceRequest, mdmsData);
		enrichmentService.enrichCreateRequest(serviceRequest);
		workflowUtil.updateWorkflowStatus(serviceRequest);

		producer.push(tenantId, config.getCreateTopic(), serviceRequest);
		return serviceRequest;
	}

//	public ServiceRequest update(ServiceRequest serviceRequest) {
//	}
}
