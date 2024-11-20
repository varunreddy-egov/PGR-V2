package digit.repository;

import digit.repository.rowmapper.PGRQueryBuilder;
import digit.repository.rowmapper.PGRRowMapper;
import digit.util.PGRUtil;
import digit.web.models.RequestSearchCriteria;
import digit.web.models.Service;
import digit.web.models.ServiceWrapper;
import digit.web.models.Workflow;
import lombok.extern.slf4j.Slf4j;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class PGRRepository {

	private final PGRQueryBuilder queryBuilder;

	private final PGRRowMapper rowMapper;

	private final JdbcTemplate jdbcTemplate;

	private final PGRUtil pgrUtil;

	@Autowired
	public PGRRepository(PGRQueryBuilder queryBuilder, PGRRowMapper rowMapper, JdbcTemplate jdbcTemplate, PGRUtil pgrUtil) {
		this.queryBuilder = queryBuilder;
		this.rowMapper = rowMapper;
		this.jdbcTemplate = jdbcTemplate;
		this.pgrUtil = pgrUtil;
	}

	/**
	 * Retrieves the service wrappers based on search criteria.
	 * @param criteria (RequestSearchCriteria) - Criteria used to filter service requests.
	 * @return List<ServiceWrapper> (List<ServiceWrapper>) - List of service wrappers matching the search criteria.
	 */
	public List<ServiceWrapper> getServiceWrappers(RequestSearchCriteria criteria) {
		List<Service> services = getServices(criteria);
		List<String> serviceRequestids = services.stream().map(Service::getServiceRequestId).collect(Collectors.toList());
		Map<String, Workflow> idToWorkflowMap = new HashMap<>();
		List<ServiceWrapper> serviceWrappers = new ArrayList<>();

		for (Service service : services) {
			ServiceWrapper serviceWrapper = ServiceWrapper.builder().service(service).workflow(idToWorkflowMap.get(service.getServiceRequestId())).build();
			serviceWrappers.add(serviceWrapper);
		}
		return serviceWrappers;
	}

	/**
	 * Retrieves the list of services based on the search criteria.
	 * @param criteria (RequestSearchCriteria) - Criteria used to filter services.
	 * @return List<Service> (List<Service>) - List of services that match the search criteria.
	 */
	public List<Service> getServices(RequestSearchCriteria criteria) {

		String tenantId = criteria.getTenantId();
		List<Object> preparedStmtList = new ArrayList<>();
		String query = queryBuilder.getPGRSearchQuery(criteria, preparedStmtList);
		try {
			log.info("Search Query: {}", query);
			query = pgrUtil.replaceSchemaPlaceholder(query, tenantId);
		} catch (Exception e) {
			throw new CustomException("PGR_UPDATE_ERROR",
					"TenantId length is not sufficient to replace query schema in a multi state instance");
		}
		return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
	}

	/**
	 * Retrieves the count of services based on the search criteria.
	 * @param criteria (RequestSearchCriteria) - Criteria used to filter the count of services.
	 * @return Integer (Integer) - The count of services that match the search criteria.
	 */
	public Integer getCount(RequestSearchCriteria criteria) {

		String tenantId = criteria.getTenantId();
		List<Object> preparedStmtList = new ArrayList<>();
		String query = queryBuilder.getCountQuery(criteria, preparedStmtList);
		try {
			log.info("Count Query: {}", query);
			query = pgrUtil.replaceSchemaPlaceholder(query, tenantId);
		} catch (Exception e) {
			throw new CustomException("PGR_REQUEST_COUNT_ERROR",
					"TenantId length is not sufficient to replace query schema in a multi state instance");
		}
		return jdbcTemplate.queryForObject(query, preparedStmtList.toArray(), Integer.class);
	}
}
