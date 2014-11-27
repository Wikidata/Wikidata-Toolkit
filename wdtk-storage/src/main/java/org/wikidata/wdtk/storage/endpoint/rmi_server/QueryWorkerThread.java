package org.wikidata.wdtk.storage.endpoint.rmi_server;

import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.storage.endpoint.shared.WdtkItemQuery;
import org.wikidata.wdtk.storage.endpoint.shared.WdtkItemQueryResult;
import org.wikidata.wdtk.storage.endpoint.shared.WdtkQuery;
import org.wikidata.wdtk.storage.endpoint.shared.WdtkQueryResult;
import org.wikidata.wdtk.storage.endpoint.shared.WdtkQueryState;
import org.wikidata.wdtk.storage.wdtkbindings.WdtkDatabaseManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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

	// FIXME part of the ItemDocument serialization hack
	private static ObjectMapper mapper = new ObjectMapper();
	static Logger logger = LoggerFactory.getLogger(QueryWorkerThread.class);
	private QueryInformation qInformation;

	QueryWorkerThread(QueryInformation qInformation) {
		this.qInformation = qInformation;
	}

	@Override
	public List<WdtkQueryResult> call() throws Exception {
		// this is where the database will be queried
		qInformation.setState(WdtkQueryState.PROCESSING);

		if (qInformation.getQuery() instanceof WdtkItemQuery) {
			this.handleQuery((WdtkItemQuery) qInformation.getQuery());
		} else {
			this.handleQuery(this.qInformation.getQuery());
		}

		logger.debug("Done handling query for query {}", qInformation.getQuery());
		return qInformation.getResults();
	}

	/**
	 * Default handling of {@link WdtkQuery}-objects. This is intended for the
	 * queries, where no special handling method exists.
	 * 
	 * Sets the state of the query to INVALID.
	 * 
	 * @param query
	 */
	private void handleQuery(WdtkQuery query) {
		// no known way of handling a generic query
		logger.error("Unknown query type");
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
	private void handleQuery(WdtkItemQuery query) {

		logger.debug("Handling query for item {}", query.getId());
		WdtkDatabaseManager dbManager = DefaultQueryService.getDatabaseManger();
		String itemId = query.getId();

		EntityDocument resultItem = dbManager.getEntityDocument(Datamodel
				.makeWikidataItemIdValue(itemId));
		logger.debug("DB entry retrieved");
		
		if(resultItem instanceof ItemDocument){
			WdtkQueryResult queryResult;
			try {
				queryResult = new WdtkItemQueryResult(mapper.writeValueAsString(resultItem));
				qInformation.addResult(queryResult);
				logger.debug("Query result written");
			} catch (JsonProcessingException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
			qInformation.setState(WdtkQueryState.COMPLETE);
		} else {
			logger.error("Query for an item returned no ItemDocument");
			qInformation.setState(WdtkQueryState.INVALID);
		}
		
	}

}
