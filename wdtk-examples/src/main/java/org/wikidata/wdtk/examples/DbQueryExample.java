package org.wikidata.wdtk.examples;

import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.storage.wdtkbindings.WdtkDatabaseManager;

public class DbQueryExample {

	final static String dbName = "wdtkDatabaseFull-20141013";
	final static WdtkDatabaseManager wdtkDatabaseManager = new WdtkDatabaseManager(
			dbName);

	public static void main(String[] args) {
		// Print data for Q42:
		System.out.println(wdtkDatabaseManager.getEntityDocument(Datamodel
				.makeWikidataItemIdValue("Q42")));

		// always close DB, even if nothing was written
		wdtkDatabaseManager.close();
	}

}
