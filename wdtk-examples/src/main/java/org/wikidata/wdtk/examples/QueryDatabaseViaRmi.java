package org.wikidata.wdtk.examples;

import java.rmi.RemoteException;

import org.wikidata.wdtk.storage.endpoint.rmi_server.DefaultQueryService;

public class QueryDatabaseViaRmi {
	
	public static void main(String[] args){
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
