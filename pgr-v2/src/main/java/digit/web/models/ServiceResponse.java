package digit.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

/**
 * Response to the service request
 */
@Schema(description = "Response to the service request")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-11-18T12:48:36.305467838+05:30[Asia/Kolkata]")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceResponse {
	@JsonProperty("responseInfo")

	@Valid
	private ResponseInfo responseInfo = null;

	@JsonProperty("pgrEntities")
	@Valid
	private List<ServiceWrapper> pgrEntities = null;


	public ServiceResponse addPgREntitiesItem(ServiceWrapper pgREntitiesItem) {
		if (this.pgrEntities == null) {
			this.pgrEntities = new ArrayList<>();
		}
		this.pgrEntities.add(pgREntitiesItem);
		return this;
	}

}
