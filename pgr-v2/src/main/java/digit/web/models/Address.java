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
import org.springframework.validation.annotation.Validated;

/**
 * Representation of a address. Indiavidual APIs may choose to extend from this using allOf if more details needed to be added in their case.
 */
@Schema(description = "Representation of a address. Indiavidual APIs may choose to extend from this using allOf if more details needed to be added in their case. ")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-11-18T12:48:36.305467838+05:30[Asia/Kolkata]")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address {
	@JsonProperty("tenantId")
	@NotNull

	private String tenantId = null;

	@JsonProperty("doorNo")

	private String doorNo = null;

	@JsonProperty("plotNo")

	private String plotNo = null;

	@JsonProperty("id")

	private String id = null;

	@JsonProperty("landmark")

	private String landmark = null;

	@JsonProperty("city")

	private String city = null;

	@JsonProperty("district")

	private String district = null;

	@JsonProperty("region")

	private String region = null;

	@JsonProperty("state")

	private String state = null;

	@JsonProperty("country")

	private String country = null;

	@JsonProperty("pincode")

	private String pincode = null;

	@JsonProperty("additionDetails")

	private Object additionDetails = null;

	@JsonProperty("buildingName")

	@Size(min = 2, max = 64)
	private String buildingName = null;

	@JsonProperty("street")

	@Size(min = 2, max = 64)
	private String street = null;

	@JsonProperty("locality")
	@NotNull

	@Valid
	private Boundary locality = null;

	@JsonProperty("geoLocation")

	@Valid
	private GeoLocation geoLocation = null;


}
