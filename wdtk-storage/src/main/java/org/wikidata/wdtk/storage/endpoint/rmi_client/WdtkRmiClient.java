package org.wikidata.wdtk.storage.endpoint.rmi_client;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.storage.endpoint.shared.WdtkItemQuery;
import org.wikidata.wdtk.storage.endpoint.shared.WdtkQuery;
import org.wikidata.wdtk.storage.endpoint.shared.WdtkQueryResult;
import org.wikidata.wdtk.storage.endpoint.shared.WdtkQueryService;
import org.wikidata.wdtk.storage.endpoint.shared.WdtkQueryState;
import org.wikidata.wdtk.storage.endpoint.shared.WdtkRemoteServiceName;

public class WdtkRmiClient {

	private Registry registry;
	private WdtkQueryService queryService;
	private static Logger logger = LoggerFactory.getLogger(WdtkRmiClient.class);

	// only one query supported at the moment
	private WdtkQuery currentQuery;
	private List<WdtkQueryResult> currentResult;
	private int currentId;
	private WdtkQueryState state;
	private boolean connected = false;

	public void connect(String uri) {
		logger.debug("Attempt to contact registry at {}", uri);

		try {
			this.registry = LocateRegistry.getRegistry(uri);

			logger.debug("Services offered by registry:");
			for (String entry : this.registry.list()) {
				logger.debug(" {}", entry);
			}

			this.queryService = (WdtkQueryService) registry
					.lookup(WdtkRemoteServiceName.WDTK_QUERY);
			logger.debug("Got service: {}", this.queryService);

			this.connected = true;

		} catch (AccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void queryForItem(int numericId) {
		if (queryService != null) {
			this.currentResult = null; // reset result
			this.currentQuery = new WdtkItemQuery(numericId);
			try {
				this.currentId = this.queryService.submitQuery(currentQuery);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				// lost connection?
				e.printStackTrace();
			}
		}
	}

	public WdtkQueryState getQueryUpdate() {
		if (queryService == null || currentQuery == null
				|| currentResult != null) {
			// nothing to expect
		} else {

			try {
				this.state = queryService.getQueryState(this.currentId);
				if (this.state == WdtkQueryState.COMPLETE) {
					this.currentResult = queryService
							.getNextQueryResults(this.currentId);
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return this.state;
	}

	public boolean isConnected() {
		return this.connected;
	}

	public List<WdtkQueryResult> getCurrentResult(){
		return this.currentResult;
	}
}
