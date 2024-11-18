package digit.web.controllers;


import digit.service.PGRService;
import digit.web.models.ServiceRequest;
import digit.web.models.ServiceResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-11-18T12:48:36.305467838+05:30[Asia/Kolkata]")
@Controller
@RequestMapping("")
public class RequestApiController {

	private final PGRService pgrService;

	@Autowired
	public RequestApiController(PGRService pgrService) {
		this.pgrService = pgrService;
	}

	@PostMapping(value = "/request/_count")
	public ResponseEntity<ServiceResponse> requestCountPost(@NotNull @Parameter(in = ParameterIn.QUERY, description = "Unique id for a tenant.", required = true, schema = @Schema()) @Valid @RequestParam(value = "tenantId", required = true) String tenantId, @Parameter(in = ParameterIn.QUERY, description = "Allows search for service type - comma separated list", schema = @Schema()) @Valid @RequestParam(value = "serviceCode", required = false) List<String> serviceCode, @Parameter(in = ParameterIn.QUERY, description = "Search by list of UUID", schema = @Schema()) @Valid @RequestParam(value = "ids", required = false) List<String> ids, @Parameter(in = ParameterIn.QUERY, description = "Search by mobile number of service requester", schema = @Schema()) @Valid @RequestParam(value = "mobileNo", required = false) String mobileNo, @Parameter(in = ParameterIn.QUERY, description = "Search by serviceRequestId of the complaint", schema = @Schema()) @Valid @RequestParam(value = "serviceRequestId", required = false) String serviceRequestId, @Parameter(in = ParameterIn.QUERY, description = "Search by list of Application Status", schema = @Schema()) @Valid @RequestParam(value = "applicationStatus", required = false) List<String> applicationStatus) {


		return new ResponseEntity<ServiceResponse>(HttpStatus.NOT_IMPLEMENTED);
	}

	@PostMapping(value = "/request/_create")
	public ResponseEntity<ServiceResponse> requestCreatePost(@Parameter(in = ParameterIn.DEFAULT, description = "Request schema.", required = true, schema = @Schema()) @Valid @RequestBody ServiceRequest serviceRequest) {
		ServiceRequest enrichedRequest = pgrService.create(serviceRequest);

		return new ResponseEntity<ServiceResponse>(HttpStatus.NOT_IMPLEMENTED);
	}

	@PostMapping(value = "/request/_search")
	public ResponseEntity<ServiceResponse> requestSearchPost(@NotNull @Parameter(in = ParameterIn.QUERY, description = "Unique id for a tenant.", required = true, schema = @Schema()) @Valid @RequestParam(value = "tenantId", required = true) String tenantId, @Parameter(in = ParameterIn.QUERY, description = "Allows search for service type - comma separated list", schema = @Schema()) @Valid @RequestParam(value = "serviceCode", required = false) List<String> serviceCode, @Parameter(in = ParameterIn.QUERY, description = "Search by list of UUID", schema = @Schema()) @Valid @RequestParam(value = "ids", required = false) List<String> ids, @Parameter(in = ParameterIn.QUERY, description = "Search by mobile number of service requester", schema = @Schema()) @Valid @RequestParam(value = "mobileNo", required = false) String mobileNo, @Parameter(in = ParameterIn.QUERY, description = "Search by serviceRequestId of the complaint", schema = @Schema()) @Valid @RequestParam(value = "serviceRequestId", required = false) String serviceRequestId, @Parameter(in = ParameterIn.QUERY, description = "Search by list of Application Status", schema = @Schema()) @Valid @RequestParam(value = "applicationStatus", required = false) List<String> applicationStatus) {


		return new ResponseEntity<ServiceResponse>(HttpStatus.NOT_IMPLEMENTED);
	}

	@PostMapping(value = "/request/_update")
	public ResponseEntity<ServiceResponse> requestUpdatePost(@Parameter(in = ParameterIn.DEFAULT, description = "Request schema.", required = true, schema = @Schema()) @Valid @RequestBody ServiceRequest serviceRequest) {
//		ServiceRequest enrichedRequest = pgrService.update(serviceRequest);

		return new ResponseEntity<ServiceResponse>(HttpStatus.NOT_IMPLEMENTED);
	}

}
