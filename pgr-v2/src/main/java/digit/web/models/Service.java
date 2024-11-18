package digit.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.User;
import org.springframework.validation.annotation.Validated;

/**
 * Instance of Service request raised for a particular service. As per extension propsed in the Service definition \&quot;attributes\&quot; carry the input values requried by metadata definition in the structure as described by the corresponding schema.  * Any one of &#x27;address&#x27; or &#x27;(lat and lang)&#x27; or &#x27;addressid&#x27; is mandatory
 */
@Schema(description = "Instance of Service request raised for a particular service. As per extension propsed in the Service definition \"attributes\" carry the input values requried by metadata definition in the structure as described by the corresponding schema.  * Any one of 'address' or '(lat and lang)' or 'addressid' is mandatory ")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-11-18T12:48:36.305467838+05:30[Asia/Kolkata]")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Service {
	@JsonProperty("citizen")

	@Valid
	private User citizen = null;

	@JsonProperty("id")

	@Size(min = 2, max = 64)
	private String id = null;

	@JsonProperty("tenantId")
	@NotNull

	@Size(min = 2, max = 64)
	private String tenantId = null;

	@JsonProperty("serviceCode")
	@NotNull

	@Size(min = 2, max = 64)
	private String serviceCode = null;

	@JsonProperty("serviceRequestId")

	@Size(min = 2, max = 128)
	private String serviceRequestId = null;

	@JsonProperty("description")

	@Size(min = 2, max = 256)
	private String description = null;

	@JsonProperty("accountId")

	@Size(min = 2, max = 64)
	private String accountId = null;

	@JsonProperty("additionalDetail")

	private Object additionalDetail = null;

	@JsonProperty("applicationStatus")

	private String applicationStatus = null;

	@JsonProperty("source")

	@Size(min = 2, max = 64)
	private String source = null;

	@JsonProperty("address")

	@Valid
	private Address address = null;

	@JsonProperty("auditDetails")

	@Valid
	private AuditDetails auditDetails = null;


}
