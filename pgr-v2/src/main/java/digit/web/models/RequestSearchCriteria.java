package digit.web.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestSearchCriteria {

	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("serviceCode")
	private Set<String> serviceCode;

	@JsonProperty("ids")
	private Set<String> ids;

	@JsonProperty("mobileNumber")
	private String mobileNumber;

	@JsonProperty("serviceRequestId")
	private String serviceRequestId;

	@JsonProperty("applicationStatus")
	private Set<String> applicationStatus;

	@JsonProperty("sortBy")
	private SortBy sortBy;

	@JsonProperty("sortOrder")
	private SortOrder sortOrder;

	@JsonProperty("limit")
	private Integer limit;

	@JsonProperty("offset")
	private Integer offset;

	@JsonIgnore
	private Set<String> userIds;

	@JsonIgnore
	private String accountId;

	public boolean isEmpty() {
		return (this.tenantId == null && this.serviceCode == null && this.mobileNumber == null && this.serviceRequestId == null
				&& this.applicationStatus == null && this.ids == null && this.userIds==null);
	}

	public enum SortOrder {
		ASC,
		DESC
	}

	public enum SortBy {
		applicationStatus,
		serviceRequestId
	}

}

