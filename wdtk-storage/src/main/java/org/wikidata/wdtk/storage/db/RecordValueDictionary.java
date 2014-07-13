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

import org.mapdb.Bind;
import org.mapdb.HTreeMap;
import org.wikidata.wdtk.storage.datamodel.ObjectValue;
import org.wikidata.wdtk.storage.datamodel.PropertyRange;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePair;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.datamodel.StringValue;
import org.wikidata.wdtk.storage.datamodel.Value;

public class RecordValueDictionary extends BaseValueDictionary {

	class LazyValueIterator implements Iterator<Value> {

		final Iterator<RecordValueForSerialization> rawValueIterator;

		public LazyValueIterator(
				Iterator<RecordValueForSerialization> rawValueIterator) {
			this.rawValueIterator = rawValueIterator;
		}

		@Override
		public boolean hasNext() {
			return this.rawValueIterator.hasNext();
		}

		@Override
		public Value next() {
			RecordValueForSerialization rvfs = this.rawValueIterator.next();
			return new LazyRecordValue(rvfs, RecordValueDictionary.this);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	final HTreeMap<Long, RecordValueForSerialization> values;
	final Map<RecordValueForSerialization, Long> ids;

	final int propertyCount;
	final int stringCount;
	final int longCount;
	final boolean[] valueIsString;

	public RecordValueDictionary(Sort sort, DatabaseManager databaseManager) {
		super(sort, databaseManager);

		this.propertyCount = sort.getPropertyRanges().size();
		this.valueIsString = new boolean[this.propertyCount];

		int sCount = 0;
		int i = 0;
		for (PropertyRange pr : sort.getPropertyRanges()) {
			if (Sort.SORTNAME_STRING.equals(pr.getRange())) {
				sCount++;
				this.valueIsString[i] = true;
			} else {
				this.valueIsString[i] = false;
			}
			i++;
		}
		this.stringCount = sCount;
		this.longCount = sort.getPropertyRanges().size() - sCount;

		RecordSerializer recordSerializer = new RecordSerializer(sort);

		this.values = databaseManager.getDb()
				.createHashMap("sort-values-" + sort.getName())
				.valueSerializer(recordSerializer).makeOrGet();
		this.ids = databaseManager.getDb()
				.createHashMap("sort-ids-" + sort.getName())
				.keySerializer(recordSerializer).makeOrGet();
		Bind.mapInverse(this.values, this.ids);
	}

	@Override
	public Iterator<Value> iterator() {
		return new LazyValueIterator(this.values.values().iterator());
	}

	@Override
	public Value getValue(long id) {
		RecordValueForSerialization rvfs = this.values.get(id);
		if (rvfs == null) {
			return null;
		} else {
			return new LazyRecordValue(rvfs, this);
		}
	}

	@Override
	public long getId(Value value) {
		return getIdInternal(getRecordValueForSerialization((ObjectValue) value));
	}

	private long getIdInternal(RecordValueForSerialization rvfs) {
		Long result = this.ids.get(rvfs);
		return (result == null) ? -1L : result;
	}

	@Override
	public long getOrCreateId(Value value) {
		RecordValueForSerialization rvfs = getRecordValueForSerialization((ObjectValue) value);
		long id = getIdInternal(rvfs);
		if (id == -1L) {
			id = this.nextId.incrementAndGet();
			this.values.put(id, rvfs);
		}
		return id;
	}

	boolean isString(int i) {
		return this.valueIsString[i];
	}

	RecordValueForSerialization getRecordValueForSerialization(
			ObjectValue objectValue) {
		String[] strings = new String[this.stringCount];
		int iString = 0;
		long[] longs = new long[this.longCount];
		int iLong = 0;

		Iterator<PropertyValuePair> propertyValuePairs = objectValue.iterator();
		Iterator<PropertyRange> propertyRanges = this.sort.getPropertyRanges()
				.iterator();

		for (int i = 0; i < this.propertyCount; i++) {
			PropertyValuePair pvp = propertyValuePairs.next();
			PropertyRange pr = propertyRanges.next();

			// TODO evaluate the performance hit of this check; maybe use
			// assertions instead
			if (!pr.getProperty().equals(pvp.getProperty())
					|| !pr.getRange()
							.equals(pvp.getValue().getSort().getName())) {
				String message = "The given object value of type "
						+ objectValue.getClass()
						+ "\ndoes not match the sort specification of \""
						+ this.sort.getName() + "\":\n" + "Position " + i
						+ " should be " + pr.getProperty() + ":"
						+ pr.getRange() + " but was " + pvp.getProperty() + ":"
						+ pvp.getValue().getSort().getName();
				throw new IllegalArgumentException(message);
			}

			if (this.valueIsString[i]) {
				// Inline strings:
				strings[iString] = ((StringValue) pvp.getValue()).getString();
				iString++;
			} else {
				longs[iLong] = this.databaseManager.getOrCreateValueId(pvp
						.getValue());
				iLong++;
			}
		}

		return new RecordValueForSerialization(longs, strings);
	}

}
