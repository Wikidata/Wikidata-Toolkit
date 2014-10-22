package org.wikidata.wdtk.storage.endpoint.rmi_server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import org.wikidata.wdtk.storage.endpoint.shared.WdtkQuery;
import org.wikidata.wdtk.storage.endpoint.shared.WdtkQueryResult;
import org.wikidata.wdtk.storage.endpoint.shared.WdtkQueryService;
import org.wikidata.wdtk.storage.endpoint.shared.WdtkQueryServiceName;
import org.wikidata.wdtk.storage.endpoint.shared.WdtkQueryState;

/**
 * 
 * @author Fredo Erxleben
 *
 */
public class DefaultQueryService implements WdtkQueryService {

	/**
	 * The highest identifier that has been handed out so far. Any newly handed
	 * out identifier must be greater then that. (Except for re-used
	 * identifiers.)
	 */
	private int highestId = 0;
	private Queue<Integer> reusableIdentifiers = new LinkedBlockingQueue<>();
	private QueryServiceWorkerThread serviceThread;

	private ExecutorService executor;
	private Registry registry;

	/**
	 * This holds all queries, the service currently knows about. The key is the
	 * identifier associated with the query, the value is a
	 * {@link QueryInformation}-object that holds everything related to the
	 * query
	 */
	private Map<Integer, QueryInformation> currentQueries = new HashMap<>();

	// TODO forwarding requests to the database
	// TODO enable graceful service shutdown
	// TODO logging
	// TODO configurability

	public DefaultQueryService() {
		this.executor = Executors.newCachedThreadPool();
		this.serviceThread = new QueryServiceWorkerThread(this);
		this.executor.execute(this.serviceThread);
		// no future is collected, since there is no useful return value so far
	}

	/**
	 * Looks up the {@link reusableIdentifiers}-queue first, and takes the next
	 * identifier in line to be re-used. If no reusable identifier is available
	 * a new one will be generated.
	 * 
	 * @return the next available identifier
	 */
	private int getNextFreeIdentifier() {
		if (reusableIdentifiers.isEmpty()) {
			this.highestId++;
			return this.highestId;
		}
		return this.reusableIdentifiers.remove();
	}

	/**
	 * Adds an identifier to the {@link reusableIdentifiers}-queue, so it might
	 * be used again.
	 * 
	 * @param identifier
	 */
	private void freeIdentifier(int identifier) {
		this.reusableIdentifiers.add(identifier);
	}

	@Override
	public int submitQuery(WdtkQuery query) throws RemoteException {
		int identifier = this.getNextFreeIdentifier();
		QueryInformation qInformation = new QueryInformation(query);
		Future<List<WdtkQueryResult>> future = this.executor
				.submit(new QueryWorkerThread(qInformation));

		qInformation.setFuture(future);

		this.currentQueries.put(identifier, qInformation);
		return identifier;
	}

	@Override
	public WdtkQueryState getQueryState(int identifier) throws RemoteException {
		QueryInformation qInformation = this.currentQueries.get(identifier);

		if (qInformation == null) {
			return WdtkQueryState.NO_SUCH_QUERY;
		}
		return qInformation.getState();
	}

	@Override
	public List<WdtkQueryResult> getNextQueryResults(int identifier)
			throws RemoteException {
		QueryInformation qInformation = this.currentQueries.get(identifier);

		if (qInformation == null) {
			return Collections.emptyList();
		}
		return qInformation.getResults();
	}

	@Override
	public void discardQuery(int identifier) throws RemoteException {
		QueryInformation qInformation = this.currentQueries.get(identifier);
		if (qInformation == null) {
			return;
		}

		// Abort thread, if running
		Future<List<WdtkQueryResult>> future = qInformation.getFuture();
		if (future != null) {
			future.cancel(true);
		}

		this.currentQueries.remove(identifier);
		this.freeIdentifier(identifier);

	}

	/**
	 * Attempts to register the RMI capabilities at an existing RMI registry
	 * service or launch one on its own.
	 * 
	 * @throws RemoteException
	 *             if neither a RMI registry server is present nor one could be
	 *             created
	 */
	public void registerRmiService() throws RemoteException {
		// preparations
		// set up registry
		try {
			registry = LocateRegistry.getRegistry();
		} catch (RemoteException initialException) {
			// none already running, create one
			LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
			registry = LocateRegistry.getRegistry();

		}
		// set up query service
		WdtkQueryService stub = (WdtkQueryService) UnicastRemoteObject
				.exportObject(this, 0);

		registry.rebind(WdtkQueryServiceName.WDTK_QUERY, stub);
	}

	Map<Integer, QueryInformation> getCurrentQueries() {
		return this.currentQueries;
	}

}
