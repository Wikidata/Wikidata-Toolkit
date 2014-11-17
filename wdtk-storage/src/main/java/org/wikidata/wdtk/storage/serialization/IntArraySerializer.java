package org.wikidata.wdtk.storage.serialization;

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
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;

public class IntArraySerializer implements Serializer<int[]>, Serializable {

	private static final long serialVersionUID = -8446849003471347271L;

	@Override
	public void serialize(DataOutput out, int[] value) throws IOException {
		DataOutput2.packInt(out, value.length);
		for (int v : value) {
			DataOutput2.packInt(out, v);
		}
	}

	@Override
	public int[] deserialize(DataInput in, int available) throws IOException {
		int length = DataInput2.unpackInt(in);
		int[] value = new int[length];
		for (int i = 0; i < length; i++) {
			value[i] = DataInput2.unpackInt(in);
		}
		return value;
	}

	@Override
	public int fixedSize() {
		return -1;
	}
}
