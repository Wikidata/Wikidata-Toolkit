package org.wikidata.wdtk.storage.db;

/*
 * #%L
 * Wikidata Toolkit Storage
 * %%
 * Copyright (C) 2014 Wikidata Toolkit Developers
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.File;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer;
import org.wikidata.wdtk.storage.datamodel.SortSchema;

/**
 * Overall management class for a database instance. Manages schema information
 * as well as data access.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class DatabaseManager {

	protected final DbSortSchema sortSchema;

	protected final DB db;

	public DatabaseManager(String dbName) {
		File dbFile = new File(dbName + ".mapdb");
		// this.db = DBMaker.newMemoryDirectDB().make();
		this.db = DBMaker.newFileDB(dbFile).closeOnJvmShutdown().make();

		this.sortSchema = DbSortSchema.create(this);

		// TODO open dictionaries for basic sorts; maybe just for string
	}

	public SortSchema getSortSchema() {
		return this.sortSchema;
	}

	public DB getDb() {
		return this.db;
	}

	public void commit() {
		this.db.commit();
	}

	public void close() {
		this.db.close();
	}

	public void updateEdges(EdgeContainer edgeContainer) {
		// Recursively convert values to ids based on dictionaries
		// Build bytes for edge table
		// put
		// commit
	}
}
