package digit.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

/**
 * Top level wrapper object containing the Service and Workflow objects
 */
@Schema(description = "Top level wrapper object containing the Service and Workflow objects")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-11-18T12:48:36.305467838+05:30[Asia/Kolkata]")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceWrapper {
	@JsonProperty("service")
	@NotNull

	@Valid
	private Service service = null;

	@JsonProperty("workflow")

	@Valid
	private Workflow workflow = null;


}
