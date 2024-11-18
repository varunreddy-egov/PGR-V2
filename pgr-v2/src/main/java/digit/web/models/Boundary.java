package digit.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

/**
 * Boundary
 */
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-11-18T12:48:36.305467838+05:30[Asia/Kolkata]")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Boundary {
	@JsonProperty("code")
	@NotNull

	private String code = null;

	@JsonProperty("name")
	@NotNull

	private String name = null;

	@JsonProperty("label")

	private String label = null;

	@JsonProperty("latitude")

	private String latitude = null;

	@JsonProperty("longitude")

	private String longitude = null;

	@JsonProperty("children")
	@Valid
	private List<Boundary> children = null;

	@JsonProperty("materializedPath")

	private String materializedPath = null;


	public Boundary addChildrenItem(Boundary childrenItem) {
		if (this.children == null) {
			this.children = new ArrayList<>();
		}
		this.children.add(childrenItem);
		return this;
	}

}
