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

import java.io.DataInput;
import java.io.IOException;
import java.util.Map;

import org.mapdb.BTreeKeySerializer;
import org.mapdb.Bind.MapWithModificationListener;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Hasher;
import org.wikidata.wdtk.storage.datamodel.ObjectValue;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.serialization.Serialization;

public class ObjectValueDictionary extends
		BaseValueDictionary<ObjectValue, byte[]> {

	public ObjectValueDictionary(Sort sort, DatabaseManager databaseManager) {
		super(sort, databaseManager);
	}

	@Override
	protected byte[] getInnerObject(ObjectValue outer) {
		DataOutput2 out = new DataOutput2();
		try {
			Serialization
					.serializeObjectValue(out, outer, this.databaseManager);
		} catch (IOException e) {
			throw new RuntimeException(e.toString(), e);
		}
		return out.copyBytes();
	}

	@Override
	public ObjectValue getOuterObject(byte[] inner) {
		// TODO Use DataInputByteArray as soon as available in stable MapDB
		// DataInput in = new DataIO.DataInputByteArray(inner);
		DataInput in = new DataInput2(inner);
		try {
			return Serialization.deserializeObjectValue(in, this.sort,
					this.databaseManager);
		} catch (IOException e) {
			throw new RuntimeException(e.toString(), e);
		}
	}

	@Override
	protected MapWithModificationListener<Integer, byte[]> initValues(
			String name) {
		return databaseManager.getDb().createTreeMap(name)
				.keySerializer(BTreeKeySerializer.ZERO_OR_POSITIVE_INT)
				.makeOrGet();
	}

	@Override
	protected Map<byte[], Integer> initIds(String name) {
		return databaseManager.getDb().createHashMap(name)
				.hasher(Hasher.BYTE_ARRAY).makeOrGet();
	}
}
