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

import java.util.Map;

import org.mapdb.BTreeKeySerializer;
import org.mapdb.Bind.MapWithModificationListener;
import org.mapdb.Serializer;
import org.wikidata.wdtk.storage.datamodel.ObjectValue;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePair;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.datamodel.StringValue;

public class StringRecordValueDictionary extends
		BaseValueDictionary<ObjectValue, String> {

	public StringRecordValueDictionary(Sort sort,
			DatabaseManager databaseManager) {
		super(sort, databaseManager);
	}

	@Override
	public ObjectValue getOuterObject(String inner) {
		return new LazyStringRecordValue(inner, this.sort);
	}

	@Override
	protected String getInnerObject(ObjectValue outer) {
		StringBuilder stringBuilder = new StringBuilder();

		boolean isFirst = true;
		for (PropertyValuePair pvp : outer) {
			if (isFirst) {
				isFirst = false;
			} else {
				stringBuilder.append("|");
			}
			stringBuilder.append(((StringValue) pvp.getValue()).getString()
					.replace("@", "@@").replace("|", "@i"));
		}

		return stringBuilder.toString();
	}

	@Override
	protected MapWithModificationListener<Integer, String> initValues(
			String name) {
		return this.databaseManager.getAuxDb(this.sort.getName() + "-values")
				.createTreeMap(name)
				.keySerializer(BTreeKeySerializer.ZERO_OR_POSITIVE_INT)
				// .nodeSize(120)
				.valueSerializer(Serializer.STRING).makeOrGet();
	}

	@Override
	protected Map<String, Integer> initIds(String name) {
		return this.databaseManager.getAuxDb(this.sort.getName() + "-ids")
				.createTreeMap(name).keySerializer(BTreeKeySerializer.STRING)
				.makeOrGet();
	}

}
