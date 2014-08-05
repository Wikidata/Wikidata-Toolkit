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
import java.util.HashMap;
import java.util.Map;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer;
import org.wikidata.wdtk.storage.datamodel.PropertyRange;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.datamodel.Value;
import org.wikidata.wdtk.storage.serialization.EdgeContainerFromSerialization;
import org.wikidata.wdtk.storage.serialization.EdgeContainerIndex;

/**
 * Overall management class for a database instance. Manages schema information
 * as well as data access.
 *
 * @author Markus Kroetzsch
 *
 */
public class DatabaseManager {

	protected final DbSortSchema sortSchema;

	protected final String dbName;

	protected final DB db;
	protected final Map<String, DB> auxDbs;

	protected final Map<String, ValueDictionary> sortNameDictionaries;
	protected final Map<Integer, ValueDictionary> sortIdDictionaries;

	protected final Map<String, EdgeContainerIndex> sortNameEdgeContainerIndexes;

	protected final PropertyDictionary propertyDictionary;

	public DatabaseManager(String dbName) {
		this.dbName = dbName;

		File dbFile = new File(dbName + ".mapdb");
		// this.db = DBMaker.newMemoryDirectDB().make();

		this.db = DBMaker.newFileDB(dbFile).transactionDisable()
				.mmapFileEnableIfSupported().cacheSize(100000)
				.asyncWriteEnable().closeOnJvmShutdown().make();
		this.auxDbs = new HashMap<>();

		this.sortNameDictionaries = new HashMap<>();
		this.sortIdDictionaries = new HashMap<>();
		this.propertyDictionary = new PropertyDictionary(this);
		this.sortNameEdgeContainerIndexes = new HashMap<>();

		this.sortSchema = new DbSortSchema(this);
	}

	public DbSortSchema getSortSchema() {
		return this.sortSchema;
	}

	public DB getDb() {
		return this.db;
	}

	public DB getAuxDb(String name) {
		if (!this.auxDbs.containsKey(name)) {
			File dbFile = new File(this.dbName + "-" + name + ".mapdb");
			// this.db = DBMaker.newMemoryDirectDB().make();

			this.auxDbs.put(name, DBMaker.newFileDB(dbFile)
					.transactionDisable().mmapFileEnableIfSupported()
					.cacheSize(50000).asyncWriteEnable().closeOnJvmShutdown()
					.make());
		}
		return this.auxDbs.get(name);
	}

	public void commit() {
		this.db.commit();
		for (DB auxDb : this.auxDbs.values()) {
			auxDb.commit();
		}
	}

	public void close() {
		this.db.close();
		for (DB auxDb : this.auxDbs.values()) {
			auxDb.close();
		}
	}

	public void updateEdges(EdgeContainer edgeContainer) {
		EdgeContainerIndex eci = this
				.getEdgeContainerIndexBySortName(edgeContainer.getSource()
						.getSort().getName());
		eci.updateEdges(edgeContainer);
	}

	public Iterable<EdgeContainerFromSerialization> edgeContainers(
			String sortName) {
		return this.getEdgeContainerIndexBySortName(sortName);
	}

	public Value fetchValue(int id, String sortName) {
		Dictionary<Value> dictionary = getDictionaryBySortName(sortName);
		return dictionary.getValue(id);
	}

	public Value fetchValue(int id, int sortId) {
		Dictionary<Value> dictionary = getDictionaryBySortId(sortId);
		return dictionary.getValue(id);
	}

	public PropertySignature fetchPropertySignature(int id) {
		return this.propertyDictionary.getValue(id);
	}

	public EdgeContainer fetchEdgeContainer(Value value) {
		Dictionary<Value> dictionary = getDictionaryBySortName(value.getSort()
				.getName());
		int id = dictionary.getId(value);
		if (id == -1) {
			return null;
		} else {
			return fetchEdgeContainer(id, value.getSort().getName());
		}
	}

	public EdgeContainer fetchEdgeContainer(int id, String sortName) {
		EdgeContainerIndex eci = this.getEdgeContainerIndexBySortName(sortName);
		return eci.getEdgeContainer(id);
	}

	public int getOrCreateValueId(Value value) {
		Dictionary<Value> dictionary = getDictionaryBySortName(value.getSort()
				.getName());
		return dictionary.getOrCreateId(value);
	}

	public int getValueId(Value value) {
		Dictionary<Value> dictionary = getDictionaryBySortName(value.getSort()
				.getName());
		return dictionary.getId(value);
	}

	public int getOrCreatePropertyId(String propertyName, String domainSort,
			String rangeSort) {
		int domainId = this.sortSchema.getSortId(domainSort);
		int rangeId = this.sortSchema.getSortId(rangeSort);

		return this.propertyDictionary.getOrCreateId(new PropertySignature(
				propertyName, domainId, rangeId));
	}

	public int getOrCreatePropertyId(String propertyName, int domainId,
			int rangeId) {
		// TODO for testing; maybe not needed as public?
		return this.propertyDictionary.getOrCreateId(new PropertySignature(
				propertyName, domainId, rangeId));
	}

	public Iterable<Value> values(String sortName) {
		return getDictionaryBySortName(sortName);
	}

	public Iterable<PropertySignature> properties() {
		return this.propertyDictionary;
	}

	Dictionary<Value> getDictionaryBySortName(String sortName) {
		Dictionary<Value> dictionary = this.sortNameDictionaries.get(sortName);
		if (dictionary != null) {
			return dictionary;
		} else {
			throw new IllegalArgumentException("Objects of sort \"" + sortName
					+ "\" are not managed in any known dictionary.");
		}
	}

	Dictionary<Value> getDictionaryBySortId(int sortId) {
		Dictionary<Value> dictionary = this.sortIdDictionaries.get(sortId);
		if (dictionary != null) {
			return dictionary;
		} else {
			throw new IllegalArgumentException("No sort with id \"" + sortId
					+ "\" is known.");
		}
	}

	EdgeContainerIndex getEdgeContainerIndexBySortName(String sortName) {
		EdgeContainerIndex eci = this.sortNameEdgeContainerIndexes
				.get(sortName);
		if (eci == null) {
			Sort sort = this.sortSchema.getSort(sortName);
			eci = new EdgeContainerIndex(sort, this);
			this.sortNameEdgeContainerIndexes.put(sortName, eci);
		}
		return eci;
	}

	protected ValueDictionary createSortDictionary(Sort sort) {
		switch (sort.getType()) {
		case STRING:
			return new StringValueDictionary(sort, this);
		case OBJECT:
			return new ObjectValueDictionary(sort, this);
		case RECORD:
			boolean isStringRecord = true;
			for (PropertyRange pr : sort.getPropertyRanges()) {
				if (!Sort.SORTNAME_STRING.equals(pr.getRange())) {
					isStringRecord = false;
					break;
				}
			}
			if (isStringRecord) {
				return new StringRecordValueDictionary(sort, this);
			} else {
				return new RecordValueDictionary(sort, this);
			}
		default:
			return null;
		}
	}

	ValueDictionary initializeDictionary(Sort sort, int id) {
		ValueDictionary dictionary = createSortDictionary(sort);

		if (dictionary != null) {
			this.sortNameDictionaries.put(sort.getName(), dictionary);
			this.sortIdDictionaries.put(id, dictionary);
		} else {
			System.out.println("Not setting up dictionary for sort "
					+ sort.getName());
		}

		return dictionary;
	}
}
