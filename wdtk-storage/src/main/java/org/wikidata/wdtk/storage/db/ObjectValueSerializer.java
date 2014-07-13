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
		out.write(value.getTypes().length);
		out.write(value.getTypes());

		for (long propertyId : value.getProperties()) {
			out.writeLong(propertyId);
		}

		for (long l : value.getRefs()) {
			out.writeLong(l);
		}

		for (String s : value.getStrings()) {
			out.writeUTF(s);
		}
	}

	@Override
	public ObjectValueForSerialization deserialize(DataInput in, int available)
			throws IOException {

		int length = in.readInt();
		byte[] types = new byte[length];

		int refCount = 0;
		int stringCount = 0;
		for (int i = 0; i < length; i++) {
			types[i] = in.readByte();
			if (types[i] == ObjectValueForSerialization.TYPE_REF) {
				refCount++;
			} else {
				stringCount++;
			}
		}

		long[] properties = new long[length];
		for (int i = 0; i < length; i++) {
			properties[i] = in.readLong();
		}

		long[] refs = new long[refCount];
		for (int i = 0; i < refCount; i++) {
			refs[i] = in.readLong();
		}

		String[] strings = new String[refCount];
		for (int i = 0; i < stringCount; i++) {
			strings[i] = in.readUTF();
		}

		return new ObjectValueForSerialization(properties, types, refs, strings);
	}

	@Override
	public int fixedSize() {
		return -1;
	}

}
