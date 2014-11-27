package org.wikidata.wdtk.storage.dboldcode;

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

import java.util.Iterator;

import org.apache.commons.lang3.Validate;
import org.mapdb.Atomic;
import org.mapdb.Bind;
import org.mapdb.Serializer;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.datamodel.StringValue;
import org.wikidata.wdtk.storage.datamodel.StringValueImpl;
import org.wikidata.wdtk.storage.datamodel.Value;
import org.wikidata.wdtk.storage.db.DatabaseManager;
import org.wikidata.wdtk.storage.db.InnerToOuterObjectConverter;
import org.wikidata.wdtk.storage.db.LazyOuterObjectIterator;
import org.wikidata.wdtk.storage.db.ValueDictionary;
import org.wikidata.wdtk.util.Timer;

public class BlobDictionary implements
		InnerToOuterObjectConverter<String, StringValue>, ValueDictionary {

	final Timer timerPut;

	protected final Sort sort;
	protected final Atomic.Integer nextId;
	protected final DatabaseManager databaseManager;

	final Bind.MapWithModificationListener<Integer, String> values;

	public BlobDictionary(Sort sort, DatabaseManager databaseManager) {
		Validate.notNull(sort, "sort cannot be null");
		Validate.notNull(databaseManager, "database manager cannot be null");

		this.timerPut = Timer.getNamedTimer("Put-" + sort.getName());

		this.sort = sort;
		this.databaseManager = databaseManager;

		nextId = databaseManager.getDb().getAtomicInteger(
				"sort-inc-" + sort.getName());

		this.values = databaseManager
				.getAuxDb(this.sort.getName() + "-values")
				.createHashMap("sort-values-" + sort.getName())
				.valueSerializer(
						new Serializer.CompressionWrapper<>(Serializer.STRING))
				.makeOrGet();

		// Bind.mapInverse(this.values, this.ids);
	}

	@Override
	public Iterator<Value> iterator() {
		return new LazyOuterObjectIterator<String, Value>(this.values.values()
				.iterator(), this);
	}

	@Override
	public StringValue getValue(int id) {
		String inner = this.values.get(id);
		if (inner == null) {
			return null;
		} else {
			return getOuterObject(inner);
		}
	}

	@Override
	public int getId(Value value) {
		// Not supported
		return -1;
	}

	@Override
	public int getOrCreateId(Value value) {
		this.timerPut.start();
		int id = this.nextId.incrementAndGet();
		this.values.put(id, ((StringValue) value).getString());
		this.timerPut.stop();
		if (this.timerPut.getMeasurements() % 10000 == 0) {
			System.out.println(this.timerPut);
		}
		return id;
	}

	@Override
	public StringValue getOuterObject(String inner) {
		return new StringValueImpl(inner, this.sort);
	}

	@Override
	public Sort getSort() {
		return this.sort;
	}

}
