package digit.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

/**
 * Collection of audit related fields used by most models
 */
@Schema(description = "Collection of audit related fields used by most models")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-11-18T12:48:36.305467838+05:30[Asia/Kolkata]")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditDetails {
	@JsonProperty("createdBy")

	private String createdBy = null;

	@JsonProperty("lastModifiedBy")

	private String lastModifiedBy = null;

	@JsonProperty("createdTime")

	private Long createdTime = null;

	@JsonProperty("lastModifiedTime")

	private Long lastModifiedTime = null;


}
