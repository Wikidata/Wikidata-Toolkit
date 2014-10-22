package org.wikidata.wdtk.storage.endpoint.rmi_server;

import java.util.List;
import java.util.concurrent.Callable;

import org.wikidata.wdtk.storage.endpoint.shared.WdtkQueryResult;

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
		// TODO this is where the database is queried
		return null;
	}

}
