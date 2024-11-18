package digit.util;

import com.jayway.jsonpath.JsonPath;
import digit.config.PGRConfiguration;
import digit.repository.ServiceRequestRepository;
import digit.web.models.RequestInfoWrapper;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static digit.config.ServiceConstants.*;

@Component
public class HRMSUtil {
	private ServiceRequestRepository serviceRequestRepository;

	private PGRConfiguration config;


	@Autowired
	public HRMSUtil(ServiceRequestRepository serviceRequestRepository, PGRConfiguration config) {
		this.serviceRequestRepository = serviceRequestRepository;
		this.config = config;
	}

	public List<String> getDepartment(List<String> uuids, RequestInfo requestInfo, String tenantId) {

		StringBuilder url = getHRMSURI(uuids, tenantId);

		RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

		Object res = serviceRequestRepository.fetchResult(url, requestInfoWrapper);

		List<String> departments = null;

		try {
			departments = JsonPath.read(res, HRMS_DEPARTMENT_JSONPATH);
		} catch (Exception e) {
			throw new CustomException("PARSING_ERROR", "Failed to parse HRMS response");
		}

		if (CollectionUtils.isEmpty(departments))
			throw new CustomException("DEPARTMENT_NOT_FOUND", "The Department of the user with uuid: " + uuids.toString() + " is not found");

		return departments;

	}

	public StringBuilder getHRMSURI(List<String> uuids, String tenantId) {

		StringBuilder builder = new StringBuilder(config.getHrmsHost());
		builder.append(config.getHrmsEndPoint());
		builder.append("?tenantId=");
		builder.append(tenantId);
		builder.append("&uuids=");
		builder.append(StringUtils.join(uuids, ","));

		return builder;
	}

}
