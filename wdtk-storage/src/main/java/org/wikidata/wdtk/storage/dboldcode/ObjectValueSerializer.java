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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

import org.mapdb.Serializer;

public class ObjectValueSerializer implements
		Serializer<ObjectValueForSerialization>, Serializable {

	private static final long serialVersionUID = -8588038764305391572L;

	@Override
	public void serialize(DataOutput out, ObjectValueForSerialization value)
			throws IOException {
		out.writeInt(value.getTypes().length);
		out.write(value.getTypes());

		for (int propertyId : value.getProperties()) {
			out.writeInt(propertyId);
		}

		Serialization.serializeTypedFields(out, value.getTypes(),
				value.getRefs(), value.getObjects());

	}

	@Override
	public ObjectValueForSerialization deserialize(DataInput in, int available)
			throws IOException {

		int length = in.readInt();
		byte[] types = new byte[length];

		int refCount = 0;
		for (int i = 0; i < length; i++) {
			types[i] = in.readByte();
			if (types[i] == SerializationConverter.TYPE_REF) {
				refCount++;
			}
		}

		int[] properties = new int[length];
		for (int i = 0; i < length; i++) {
			properties[i] = in.readInt();
		}

		int[] refs = new int[refCount];
		Object[] objects = new Object[length - refCount];
		Serialization.deserializeTypedFields(in, types, refs, objects);

		return new ObjectValueForSerialization(properties, types, refs, objects);
	}

	@Override
	public int fixedSize() {
		return -1;
	}

}
