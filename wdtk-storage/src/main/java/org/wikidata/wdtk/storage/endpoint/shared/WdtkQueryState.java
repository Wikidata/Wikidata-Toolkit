package org.wikidata.wdtk.storage.endpoint.shared;

/**
 * An indicator for the state a {@link WdtkQuery} can be in, while being worked
 * on by a {@link WdtkQueryService}
 * 
 * @author Fredo Erxleben
 *
 */
public enum WdtkQueryState {

	/**
	 * Indicates that the query is submitted, but the query service has not yet
	 * begun processing it.
	 */
	PENDING,

	/**
	 * Indicates that the query is currently processed by the query service.
	 * Intermediate results may be available.
	 */
	PROCESSING,

	/**
	 * Indicates that the query has been completely processed and all result are
	 * available.
	 */
	COMPLETE,

	/**
	 * Indicates that the processing of a query has been aborted by the query
	 * service.
	 */
	ABORTED,

	/**
	 * Indicates that the submitted query could not be interpreted in a
	 * meaningful manner by the query server and will not be processed.
	 */
	INVALID,

	/**
	 * Indicates that the given query identifier is not associated with any
	 * query by the query service.
	 */
	NO_SUCH_QUERY
}
