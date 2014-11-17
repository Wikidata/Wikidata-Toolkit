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

public class RecordSerializer implements
		Serializer<RecordValueForSerialization>, Serializable {

	private static final long serialVersionUID = 4397282628980400400L;

	transient int objectCount;
	transient int refCount;
	transient int fixedSize;
	final byte[] types;

	public RecordSerializer(byte[] types) {
		this.types = types;
		initAuxliliaryFields();
	}

	@Override
	public void serialize(DataOutput out, RecordValueForSerialization rvfs)
			throws IOException {
		Serialization.serializeTypedFields(out, this.types, rvfs.getRefs(),
				rvfs.getObjects());
	}

	@Override
	public RecordValueForSerialization deserialize(DataInput in, int available)
			throws IOException {
		int[] refs = new int[this.refCount];
		Object[] objects = new Object[this.objectCount];

		Serialization.deserializeTypedFields(in, this.types, refs, objects);

		return new RecordValueForSerialization(refs, objects);
	}

	@Override
	public int fixedSize() {
		return this.fixedSize;
	}

	protected void initAuxliliaryFields() {
		int rCount = 0;
		for (byte type : this.types) {
			if (type == SerializationConverter.TYPE_REF) {
				rCount++;
			}
		}
		this.refCount = rCount;
		this.objectCount = this.types.length - rCount;

		if (this.objectCount == 0) {
			this.fixedSize = (Integer.SIZE / 8) * this.refCount;
		} else {
			this.fixedSize = -1;
		}
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		initAuxliliaryFields();
	}

	// private void writeObject(java.io.ObjectOutputStream stream)
	// throws IOException {
	//
	// }
}
