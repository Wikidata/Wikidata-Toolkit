package org.wikidata.wdtk.examples;

import java.rmi.RemoteException;

import org.wikidata.wdtk.storage.endpoint.rmi_server.DefaultQueryService;

/**
 * This is the main class for an application that serves queries to a
 * WDTK-database. It holds all necessary context for setting up a receiving RMI
 * end and answering queries that arrive by this way.
 * 
 * @author fredo
 *
 */
public class QueryDatabaseViaRmi {
	
	public static void main(String[] args) {
		ExampleHelpers.configureLogging();
		// TODO make sure the codebase is set up correctly
		// TODO make sure the security policy is set correctly

		// setup the RMI service
		DefaultQueryService service = new DefaultQueryService();

		try {
			service.launch();
		} catch (RemoteException e) {
			System.err.println("Launching the query service failed.");
			e.printStackTrace();
			return;
		}

		// TODO send some queries

		service.terminate();
		System.out.println("Service terminated.");
	}

}
