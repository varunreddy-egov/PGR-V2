package digit.web.controllers;


import digit.service.PGRService;
import digit.util.ResponseInfoFactory;
import digit.web.models.*;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.List;

@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-11-18T12:48:36.305467838+05:30[Asia/Kolkata]")
@Controller
@RequestMapping("")
public class RequestApiController {

	private final PGRService pgrService;

	private final ResponseInfoFactory responseInfoFactory;

	@Autowired
	public RequestApiController(PGRService pgrService, ResponseInfoFactory responseInfoFactory) {
		this.pgrService = pgrService;
		this.responseInfoFactory = responseInfoFactory;
	}

	@PostMapping(value = "/request/_count")
	public ResponseEntity<CountResponse> requestCountPost(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
														  @Valid @ModelAttribute RequestSearchCriteria criteria) {
		Integer count = pgrService.count(criteria);
		ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
		CountResponse response = CountResponse.builder().responseInfo(responseInfo).count(count).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping(value = "/request/_create")
	public ResponseEntity<ServiceResponse> requestCreatePost(@Parameter(in = ParameterIn.DEFAULT, description = "Request schema.", required = true, schema = @Schema()) @Valid @RequestBody ServiceRequest request) {
		ServiceRequest enrichedRequest = pgrService.create(request);
		ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);
		ServiceWrapper serviceWrapper = ServiceWrapper.builder().service(enrichedRequest.getPgrEntity().getService()).workflow(enrichedRequest.getPgrEntity().getWorkflow()).build();
		ServiceResponse response = ServiceResponse.builder().responseInfo(responseInfo).pgrEntities(Collections.singletonList(serviceWrapper)).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping(value = "/request/_search")
	public ResponseEntity<ServiceResponse> requestSearchPost(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
															 @Valid @ModelAttribute RequestSearchCriteria criteria) {
		List<ServiceWrapper> serviceWrappers = pgrService.search(requestInfoWrapper.getRequestInfo(), criteria);
		ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
		ServiceResponse response = ServiceResponse.builder().responseInfo(responseInfo).pgrEntities(serviceWrappers).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping(value = "/request/_update")
	public ResponseEntity<ServiceResponse> requestUpdatePost(@Parameter(in = ParameterIn.DEFAULT, description = "Request schema.", required = true, schema = @Schema()) @Valid @RequestBody ServiceRequest request) {
		ServiceRequest enrichedRequest = pgrService.update(request);
		ServiceWrapper serviceWrapper = ServiceWrapper.builder().service(enrichedRequest.getPgrEntity().getService()).workflow(enrichedRequest.getPgrEntity().getWorkflow()).build();
		ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);
		ServiceResponse response = ServiceResponse.builder().responseInfo(responseInfo).pgrEntities(Collections.singletonList(serviceWrapper)).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
