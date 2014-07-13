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
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.datamodel.StringValue;
import org.wikidata.wdtk.storage.datamodel.StringValueImpl;

public class StringValueDictionary extends
		BaseValueDictionary<StringValue, String> {

	public StringValueDictionary(Sort sort, DatabaseManager databaseManager) {
		super(sort, databaseManager);
	}

	@Override
	protected String getInnerObject(StringValue outer) {
		return outer.getString();
	}

	@Override
	protected StringValue getOuterObject(String inner) {
		return new StringValueImpl(inner, this.sort);
	}

	@Override
	protected MapWithModificationListener<Long, String> initValues(String name) {
		return databaseManager.getDb().createTreeMap(name)
				.valueSerializer(Serializer.STRING).makeOrGet();
	}

	@Override
	protected Map<String, Long> initIds(String name) {
		return databaseManager.getDb().createTreeMap(name)
				.keySerializer(BTreeKeySerializer.STRING).makeOrGet();
	}

}
