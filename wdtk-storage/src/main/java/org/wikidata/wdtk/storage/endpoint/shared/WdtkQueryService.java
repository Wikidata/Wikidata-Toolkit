package org.wikidata.wdtk.storage.endpoint.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * The query interface used for RMI. This interface is to be satisfied by any
 * server offering to serve WDTK-database queries.
 * 
 * @author Fredo Erxleben
 *
 */
public interface WdtkQueryService extends Remote {

	/**
	 * Submits a given {@link WdtkQuery} to the server, which the server then
	 * attempts to process. The server hands the client an identifier which the
	 * client can use to poll the server for the state of the query.
	 * 
	 * If the submitted query is <b>null</b> the server marks the query as
	 * {@link WdtkQueryState.INVALID}.
	 * 
	 * @param query
	 * @return a positive non-null integer identifying the submitted query
	 */
	public int submitQuery(WdtkQuery query) throws RemoteException;

	public WdtkQueryState getQueryState(int identifier) throws RemoteException;

	/**
	 * Request the next available results for a query given by its identifier.
	 * <p>
	 * The server returns every result once. Unless the query is not
	 * {@link WdtkQueryState.COMPLETED}, {@link Wdtk.QueryService.ABORTED},
	 * {@link WdtkQueryState.INVALID} or {@link WdtkQueryState.NO_SUCH_QUERY} it
	 * is not guaranteed that there will be no more query results when
	 * requesting all results until none more are returned.
	 * </p>
	 * <p>
	 * If there are no more results available (even if only momentarily), the
	 * server returns an empty list.
	 * </p>
	 * 
	 * @param identifier a positive non-null integer identifying the submitted query
	 * @return
	 * @throws RemoteException
	 */
	public List<WdtkQueryResult> getNextQueryResults(int identifier)
			throws RemoteException;

	/**
	 * Informs the server that it no longer needs to process the query
	 * associated with the given identifier or keep the results available. It is
	 * up to the server, how it handles the request to discard a query.
	 * 
	 * @param identifier a positive non-null integer identifying the submitted query
	 * @throws RemoteException
	 */
	public void discardQuery(int identifier) throws RemoteException;

	/*
	 * NOTE: the option to discard a query might be used maliciously. It is
	 * therefore a good idea to
	 * 
	 * a) allow the server to ignore discard requests for a certain time or
	 * until result are collected
	 * 
	 * b) use at least session tracking to make sure no one can discard the
	 * requests of somebody else
	 */

}
