package cz.cuni.mff.xrg.odcs.extractor.rdf;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;
import java.util.List;

/**
 * SPARQL extractor configuration.
 *
 * @author Petyr
 * @author Jiri Tomes
 *
 */
public class RDFExtractorConfig extends DPUConfigObjectBase {

	private String SPARQL_endpoint;

	private String Host_name;

	private String Password;

	private String SPARQL_query;

	private boolean ExtractFail;

	private boolean UseStatisticalHandler;

	private boolean failWhenErrors;

	private Long retryTime;

	private Integer retrySize;

	private ExtractorEndpointParams endpointParams;

	private List<String> GraphsUri;

	private boolean useSplitConstruct;

	private Integer splitConstructSize;

	public RDFExtractorConfig() {
		this.SPARQL_endpoint = "";
		this.Host_name = "";
		this.Password = "";
		this.SPARQL_query = "";
		this.ExtractFail = true;
		this.UseStatisticalHandler = true;
		this.failWhenErrors = false;
		this.retrySize = -1;
		this.retryTime = 1000L;
		this.endpointParams = new ExtractorEndpointParams();
		this.useSplitConstruct = false;
		this.splitConstructSize = 50000;
	}

	public RDFExtractorConfig(String SPARQL_endpoint, String Host_name,
			String Password, String SPARQL_query, boolean ExtractFail,
			boolean UseStatisticalHandler, boolean failWhenErrors, int retrySize,
			long retryTime, ExtractorEndpointParams endpointParams,
			boolean useSplitConstruct, int splitConstructSize) {

		this.SPARQL_endpoint = SPARQL_endpoint;
		this.Host_name = Host_name;
		this.Password = Password;
		this.SPARQL_query = SPARQL_query;
		this.ExtractFail = ExtractFail;
		this.UseStatisticalHandler = UseStatisticalHandler;
		this.failWhenErrors = failWhenErrors;
		this.retrySize = retrySize;
		this.retryTime = retryTime;
		this.endpointParams = endpointParams;
		this.useSplitConstruct = useSplitConstruct;
		this.splitConstructSize = splitConstructSize;
	}

	/**
	 * Returns parameters for target SPARQL endpoint as
	 * {@link ExtractorEndpointParams} instance.
	 *
	 * @return parameters for target SPARQL endpoint as
	 *         {@link ExtractorEndpointParams} instance.
	 */
	public ExtractorEndpointParams getEndpointParams() {
		return endpointParams;
	}

	/**
	 * Returns URL address of SPARQL endpoint as string.
	 *
	 * @return URL address of SPARQL endpoint as string.
	 */
	public String getSPARQLEndpoint() {
		return SPARQL_endpoint;
	}

	/**
	 * Returns host name for target SPARQL endpoint.
	 *
	 * @return host name for target SPARQL endpoint.
	 */
	public String getHostName() {
		return Host_name;
	}

	/**
	 * Returns password for access to the target SPARQL endpoint.
	 *
	 * @return password for access to the target SPARQL endpoint.
	 */
	public String getPassword() {
		return Password;
	}

	/**
	 * Returns string value of SPARQL query.
	 *
	 * @return SPARQL query.
	 */
	public String getSPARQLQuery() {
		return SPARQL_query;
	}

	/**
	 * Returns true, if extraction wil be stopped when errors, false otherwise.
	 *
	 * @return true, if extraction wil be stopped when errors, false otherwise.
	 */
	public boolean isExtractFail() {
		return ExtractFail;
	}

	/**
	 * Returns true, if is used statistical handler for data extraction, false
	 * otherwise.
	 *
	 * @return true, if is used statistical handler for data extraction, false
	 *         otherwise.
	 */
	public boolean isUsedStatisticalHandler() {
		return UseStatisticalHandler;
	}

	/**
	 * Returns true, if execution should fail when some errors are detected,
	 * false otherwise.
	 *
	 * @return true, if execution should fail when some errors are detected,
	 *         false otherwise.
	 */
	public boolean isFailWhenErrors() {
		return failWhenErrors;
	}

	/**
	 * Returns time in ms how long wait before re-connection attempt.
	 *
	 * @return Time in ms how long wait before re-connection attempt.
	 */
	public Long getRetryTime() {
		return retryTime;
	}

	/**
	 * Returns count of re-connection if connection failed. For infinite loop
	 * use zero or negative integer.
	 *
	 * @return Count of re-connection if connection failed. For infinite loop
	 *         use zero or negative integer.
	 *
	 */
	public Integer getRetrySize() {
		return retrySize;
	}

	/**
	 * Returns true, if construct query should be split in more SPARQL queries,
	 * false otherwise.
	 *
	 * @return true, if construct query should be split in more SPARQL queries,
	 *         false otherwise.
	 */
	public boolean isUsedSplitConstruct() {
		return useSplitConstruct;
	}

	/**
	 * Returns maximum size of one data part for contruct query when is used
	 * split.
	 *
	 * @return maximum size of one data part for contruct query when is used
	 *         split.
	 */
	public Integer getSplitConstructSize() {
		return splitConstructSize;
	}

	/**
	 * Returns true, if DPU configuration is valid, false otherwise.
	 *
	 * @return true, if DPU configuration is valid, false otherwise.
	 */
	@Override
	public boolean isValid() {
		return SPARQL_endpoint != null
				&& Host_name != null
				&& Password != null
				&& SPARQL_query != null
				&& retrySize != null
				&& retryTime != null
				&& retryTime > 0
				&& endpointParams != null;
	}

	/**
	 * Fill missing configuration with default values.
	 */
	@Override
	public void onDeserialize() {

		if (retrySize == null) {
			retrySize = -1;
		}
		if (retryTime == null) {
			retryTime = 1000L;
		}

		if (endpointParams == null) {
			endpointParams = new ExtractorEndpointParams();

			if (GraphsUri != null) {
				for (String defaultGraph : GraphsUri) {
					endpointParams.addDefaultGraph(defaultGraph);
				}
			}
		}

		if (splitConstructSize == null) {
			splitConstructSize = 50000;
		}
	}
}
