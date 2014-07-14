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

import org.mapdb.Bind.MapWithModificationListener;
import org.wikidata.wdtk.storage.datamodel.ObjectValue;
import org.wikidata.wdtk.storage.datamodel.PropertyRange;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePair;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.datamodel.StringValue;

public class RecordValueDictionary extends
		BaseValueDictionary<ObjectValue, RecordValueForSerialization> {

	final int propertyCount;
	final int stringCount;
	final int refCount;
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
		this.refCount = sort.getPropertyRanges().size() - sCount;
	}

	boolean isString(int i) {
		return this.valueIsString[i];
	}

	@Override
	protected RecordValueForSerialization getInnerObject(ObjectValue outer) {
		String[] strings = new String[this.stringCount];
		int iString = 0;
		long[] refs = new long[this.refCount];
		int iRef = 0;

		Iterator<PropertyValuePair> propertyValuePairs = outer.iterator();
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
						+ outer.getClass()
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
				refs[iRef] = this.databaseManager.getOrCreateValueId(pvp
						.getValue());
				iRef++;
			}
		}

		return new RecordValueForSerialization(refs, strings);
	}

	@Override
	public ObjectValue getOuterObject(RecordValueForSerialization inner) {
		return new LazyRecordValue(inner, this);
	}

	@Override
	protected MapWithModificationListener<Long, RecordValueForSerialization> initValues(
			String name) {
		return databaseManager.getDb().createHashMap(name)
				.valueSerializer(new RecordSerializer(sort)).makeOrGet();
	}

	@Override
	protected Map<RecordValueForSerialization, Long> initIds(String name) {
		return databaseManager.getDb().createHashMap(name)
				.keySerializer(new RecordSerializer(sort)).makeOrGet();
	}

}
