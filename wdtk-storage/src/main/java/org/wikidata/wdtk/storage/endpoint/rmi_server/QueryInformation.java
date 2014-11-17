package org.wikidata.wdtk.storage.endpoint.rmi_server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.wikidata.wdtk.storage.endpoint.shared.WdtkQuery;
import org.wikidata.wdtk.storage.endpoint.shared.WdtkQueryResult;
import org.wikidata.wdtk.storage.endpoint.shared.WdtkQueryState;

// TODO thread safety
/**
 * This class bundles all information related with a {@link WdtkQuery} that is
 * currently known to a {@link DefaultQueryServer}.
 * 
 * @author Fredo Erxleben
 *
 */
class QueryInformation {

	/**
	 * The query itself.
	 */
	private WdtkQuery query;

	/**
	 * The current state the query is in.
	 */
	private WdtkQueryState state;

	/**
	 * A list of all results that came in since they were fetched the last time
	 * by the client.
	 */
	private List<WdtkQueryResult> results = new ArrayList<>();

	private Future<List<WdtkQueryResult>> future;

	QueryInformation(WdtkQuery query) {
		this.query = query;
		this.state = WdtkQueryState.PENDING;
	}

	/**
	 * Forces the query information to be updated. The future, if there is any
	 * will be polled to see if it is done.
	 */
	void update() {
		if (this.state == WdtkQueryState.ABORTED
				|| this.state == WdtkQueryState.INVALID
				|| this.state == WdtkQueryState.NO_SUCH_QUERY) {
			// nothing to do in these cases
			return;
		}

		if (this.future == null) {
			// Not sure if this can really happen
			return;
		}

		if (this.future.isDone()) {
			this.state = WdtkQueryState.COMPLETE;
			try {
				this.results = this.future.get();
			} catch (InterruptedException | ExecutionException e) {
				// roll back the state to try again later
				this.state = WdtkQueryState.PROCESSING;
				e.printStackTrace();
			}
		}
	}
	
	WdtkQuery getQuery(){
		return this.query;
	}

	WdtkQueryState getState() {
		return this.state;
	}
	
	void setState(WdtkQueryState state){
		this.state = state;
	}

	/**
	 * Returns all currently available results. The returned results will be
	 * forgotten and are now the callers responsibility.
	 * 
	 * @return
	 */
	List<WdtkQueryResult> getResults() {
		List<WdtkQueryResult> temp = this.results;
		this.results = new ArrayList<>();
		return temp;
	}

	void setFuture(Future<List<WdtkQueryResult>> future) {
		this.future = future;
	}

	Future<List<WdtkQueryResult>> getFuture() {
		return this.future;
	}
	
	void addResult(WdtkQueryResult result){
		this.results.add(result);
	}
}
