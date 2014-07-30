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

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.mapdb.Atomic;
import org.mapdb.Bind;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.datamodel.Value;
import org.wikidata.wdtk.util.Timer;

public abstract class BaseValueDictionary<Outer extends Value, Inner>
		implements ValueDictionary, InnerToOuterObjectConverter<Inner, Outer> {

	final Timer timerGet;
	final Timer timerPut;

	protected final Sort sort;
	protected final Atomic.Integer nextId;
	protected final DatabaseManager databaseManager;

	final Bind.MapWithModificationListener<Integer, Inner> values;
	final Map<Inner, Integer> ids;

	public BaseValueDictionary(Sort sort, DatabaseManager databaseManager) {
		Validate.notNull(sort, "sort cannot be null");
		Validate.notNull(databaseManager, "database manager cannot be null");

		this.timerGet = Timer.getNamedTimer("Get-values-" + sort.getName());
		this.timerPut = Timer.getNamedTimer("Put-" + sort.getName());

		this.sort = sort;
		this.databaseManager = databaseManager;

		nextId = databaseManager.getDb().getAtomicInteger(
				"sort-inc-" + sort.getName());

		this.values = initValues("sort-values-" + sort.getName());
		this.ids = initIds("sort-ids-" + sort.getName());

		// Bind.mapInverse(this.values, this.ids);
	}

	@Override
	public Sort getSort() {
		return this.sort;
	}

	@Override
	public Iterator<Value> iterator() {
		return new LazyOuterObjectIterator<Inner, Value>(this.values.values()
				.iterator(), this);
	}

	@Override
	public Value getValue(int id) {
		Inner inner = this.values.get(id);
		if (inner == null) {
			return null;
		} else {
			return getOuterObject(inner);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public int getId(Value value) {
		return getIdInternal(getInnerObject((Outer) value));
	}

	@Override
	public int getOrCreateId(Value value) {
		@SuppressWarnings("unchecked")
		Inner inner = getInnerObject((Outer) value);
		int id = getIdInternal(inner);
		if (id == -1) {
			this.timerPut.start();
			id = this.nextId.incrementAndGet();
			this.values.put(id, inner);
			this.ids.put(inner, id);
			this.timerPut.stop();
			if (this.timerPut.getMeasurements() % 100000 == 0) {
				System.out.println(this.timerPut);
			}
		}
		return id;
	}

	@Override
	public abstract Outer getOuterObject(Inner inner);

	protected abstract Inner getInnerObject(Outer outer);

	protected abstract Bind.MapWithModificationListener<Integer, Inner> initValues(
			String name);

	protected abstract Map<Inner, Integer> initIds(String name);

	private int getIdInternal(Inner inner) {
		this.timerGet.start();
		Integer result = this.ids.get(inner);
		this.timerGet.stop();
		if (this.timerGet.getMeasurements() % 100000 == 0) {
			System.out.println(this.timerGet);
		}
		return (result == null) ? -1 : result;
	}
}
