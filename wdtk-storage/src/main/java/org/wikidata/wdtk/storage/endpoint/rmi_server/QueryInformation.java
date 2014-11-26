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
		synchronized (this) {
			if (this.state == WdtkQueryState.ABORTED
					|| this.state == WdtkQueryState.INVALID
					|| this.state == WdtkQueryState.NO_SUCH_QUERY
					|| this.state == WdtkQueryState.COMPLETE) {
				// nothing to do in these cases
				return;
			}

			if (this.future == null) { // Not sure if this can really happen
				return;
			}

			if (this.future.isCancelled()) {
				this.state = WdtkQueryState.ABORTED;
				return;
			}

			if (this.future.isDone()) {
				try {
					this.results = this.future.get();
					this.state = WdtkQueryState.COMPLETE;
				} catch (InterruptedException | ExecutionException e) {
					this.state = WdtkQueryState.ABORTED;
					e.printStackTrace();
				}
			}
		}
	}

	WdtkQuery getQuery() {
		return this.query;
	}

	WdtkQueryState getState() {
		synchronized (this.state) {
			return this.state;
		}
	}

	void setState(WdtkQueryState state) {
		synchronized (this.state) {
			this.state = state;
		}
	}

	/**
	 * Returns all currently available results. The returned results will be
	 * forgotten and are now the callers responsibility.
	 * 
	 * @return
	 */
	List<WdtkQueryResult> collectResults() {
		synchronized (this.results) {
			List<WdtkQueryResult> temp = this.results;
			this.results = new ArrayList<>();
			return temp;
		}
	}

	List<WdtkQueryResult> getResults() {
		synchronized (this.results) {
			return this.results;
		}
	}
	
	void addResult(WdtkQueryResult result) {
		synchronized (this.results) {
			this.results.add(result);
		}
	}

	void setFuture(Future<List<WdtkQueryResult>> future) {
		synchronized (this) {
			this.future = future;
		}
	}

	Future<List<WdtkQueryResult>> getFuture() {
		synchronized (this.future) {
			return this.future;
		}
	}
}
