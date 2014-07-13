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

import org.mapdb.BTreeKeySerializer;
import org.mapdb.Bind;
import org.mapdb.Serializer;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.datamodel.StringValue;
import org.wikidata.wdtk.storage.datamodel.StringValueImpl;
import org.wikidata.wdtk.storage.datamodel.Value;

public class StringValueDictionary extends BaseValueDictionary {

	class StringValueIterator implements Iterator<Value> {

		final Iterator<String> rawValueIterator;

		public StringValueIterator(Iterator<String> rawValueIterator) {
			this.rawValueIterator = rawValueIterator;
		}

		@Override
		public boolean hasNext() {
			return this.rawValueIterator.hasNext();
		}

		@Override
		public Value next() {
			String rvfs = this.rawValueIterator.next();
			return new StringValueImpl(rvfs, StringValueDictionary.this.sort);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	final Bind.MapWithModificationListener<Long, String> values;
	final Map<String, Long> ids;

	public StringValueDictionary(Sort sort, DatabaseManager databaseManager) {
		super(sort, databaseManager);

		this.values = databaseManager.getDb()
				.createTreeMap("sort-values-" + sort.getName())
				.valueSerializer(Serializer.STRING).makeOrGet();
		this.ids = databaseManager.getDb()
				.createTreeMap("sort-ids-" + sort.getName())
				.keySerializer(BTreeKeySerializer.STRING).makeOrGet();
		Bind.mapInverse(this.values, this.ids);
	}

	@Override
	public Value getValue(long id) {
		String string = this.values.get(id);
		if (string == null) {
			return null;
		} else {
			return new StringValueImpl(string, this.sort);
		}
	}

	@Override
	public long getId(Value value) {
		return getIdInternal(((StringValue) value).getString());
	}

	private long getIdInternal(String string) {
		Long result = this.ids.get(string);
		return (result == null) ? -1L : result;
	}

	@Override
	public long getOrCreateId(Value value) {
		String string = ((StringValue) value).getString();
		long id = getIdInternal(string);
		if (id == -1L) {
			id = this.nextId.incrementAndGet();
			this.values.put(id, string);
		}
		return id;
	}

	@Override
	public Iterator<Value> iterator() {
		return new StringValueIterator(this.values.values().iterator());
	}

}
