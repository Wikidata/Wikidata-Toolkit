package org.wikidata.wdtk.storage.endpoint.rmi_server;

import java.util.List;
import java.util.concurrent.Callable;

import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.storage.endpoint.shared.WdtkItemQuery;
import org.wikidata.wdtk.storage.endpoint.shared.WdtkItemQueryResult;
import org.wikidata.wdtk.storage.endpoint.shared.WdtkQuery;
import org.wikidata.wdtk.storage.endpoint.shared.WdtkQueryResult;
import org.wikidata.wdtk.storage.endpoint.shared.WdtkQueryState;
import org.wikidata.wdtk.storage.wdtkbindings.WdtkDatabaseManager;

/**
 * This class handles the forwarding of the query to the database and the
 * collection or results.
 * 
 * It holds a aggregation towards the related {@link QueryInformation} to have a
 * channel to report back intermediate results or status changes.
 * 
 * The latter might be used when the database decides that a given query might
 * be invalid.
 * 
 * @author Fredo Erxleben
 *
 */
public class QueryWorkerThread implements Callable<List<WdtkQueryResult>> {

	private QueryInformation qInformation;

	QueryWorkerThread(QueryInformation qInformation) {
		this.qInformation = qInformation;
	}

	@Override
	public List<WdtkQueryResult> call() throws Exception {
		// this is where the database will be queried
		qInformation.setState(WdtkQueryState.PROCESSING);

		// the overloading will handle the type switching
		this.handleQuery(this.qInformation.getQuery());

		return qInformation.getResults();
	}

	/**
	 * Default handling of {@link WdtkQuery}-objects. This is intended for the
	 * queries, where no overloading handling method exists.
	 * 
	 * Sets the state of the query to INVALID.
	 * 
	 * @param query
	 */
	private void handleQuery(WdtkQuery query) {
		// no known way of handling a generic query
		this.qInformation.setState(WdtkQueryState.INVALID);
	}

	/**
	 * Handling of {@link WdtkItemQueries}.
	 * 
	 * There is only one Item requested. Once it is answered, the state of the
	 * query is set to COMPLETED and the result put into the result list.
	 * 
	 * @param query
	 */
	@SuppressWarnings("unused")
	private void handleQuery(WdtkItemQuery query) {

		WdtkDatabaseManager dbManager = DefaultQueryService.getDatabaseManger();
		String itemId = query.getId();

		EntityDocument resultItem = dbManager.getEntityDocument(Datamodel
				.makeWikidataItemIdValue(itemId));
		dbManager.close();

		WdtkQueryResult queryResult = new WdtkItemQueryResult(resultItem);
		qInformation.addResult(queryResult);
		qInformation.setState(WdtkQueryState.COMPLETE);
	}

}
