package digit.util;

import digit.config.PGRConfiguration;
import digit.repository.ServiceRequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.utils.MultiStateInstanceUtil;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static digit.config.ServiceConstants.*;

@Slf4j
@Component
public class MdmsUtil {

	private final ServiceRequestRepository restRepo;

	private final PGRConfiguration config;

	private final MultiStateInstanceUtil multiStateInstanceUtil;

	@Autowired
	public MdmsUtil(ServiceRequestRepository restRepo, PGRConfiguration config, MultiStateInstanceUtil multiStateInstanceUtil) {
		this.restRepo = restRepo;
		this.config = config;
		this.multiStateInstanceUtil = multiStateInstanceUtil;
	}

	public Object fetchMdmsData(RequestInfo requestInfo, String tenantId) {
		StringBuilder uri = new StringBuilder();
		uri.append(config.getMdmsHost()).append(config.getMdmsEndPoint());
		MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequest(requestInfo,multiStateInstanceUtil.getStateLevelTenant(tenantId));
		return restRepo.fetchResult(uri, mdmsCriteriaReq);
	}

	private MdmsCriteriaReq getMdmsRequest(RequestInfo requestInfo, String tenantId) {
		List<ModuleDetail> pgrModuleRequest = getPGRModuleRequest();

		List<ModuleDetail> moduleDetails = new LinkedList<>(pgrModuleRequest);

		MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(moduleDetails).tenantId(tenantId).build();

		return MdmsCriteriaReq.builder().mdmsCriteria(mdmsCriteria)
				.requestInfo(requestInfo).build();
	}

	private List<ModuleDetail> getPGRModuleRequest() {

		// master details of PGR module
		List<MasterDetail> pgrMasterDetails = new ArrayList<>();

		// filter to only get active data from master data
		final String filterCode = "$.[?(@.active==true)]";

		pgrMasterDetails.add(MasterDetail.builder().name(MDMS_SERVICEDEF).filter(filterCode).build());

		ModuleDetail pgrModuleDetails = ModuleDetail.builder().masterDetails(pgrMasterDetails)
				.moduleName(MDMS_MODULE_NAME).build();


		return Collections.singletonList(pgrModuleDetails);

	}
}