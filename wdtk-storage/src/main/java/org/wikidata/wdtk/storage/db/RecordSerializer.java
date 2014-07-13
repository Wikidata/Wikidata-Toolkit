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
import org.wikidata.wdtk.storage.datamodel.PropertyRange;
import org.wikidata.wdtk.storage.datamodel.Sort;

public class RecordSerializer implements
		Serializer<RecordValueForSerialization>, Serializable {

	private static final long serialVersionUID = 4397282628980400400L;

	final Sort sort;
	transient int stringCount;
	transient int longCount;
	transient int fixedSize;

	public RecordSerializer(Sort sort) {
		this.sort = sort;
		initAuxliliaryFields();
	}

	@Override
	public RecordValueForSerialization deserialize(DataInput in, int available)
			throws IOException {
		long[] longs = new long[this.longCount];
		String[] strings = new String[this.stringCount];

		for (int i = 0; i < this.longCount; i++) {
			longs[i] = in.readLong();
		}
		for (int i = 0; i < this.stringCount; i++) {
			strings[i] = in.readUTF();
		}

		return new RecordValueForSerialization(longs, strings);
	}

	@Override
	public int fixedSize() {
		return this.fixedSize;
	}

	@Override
	public void serialize(DataOutput out, RecordValueForSerialization rvfs)
			throws IOException {

		for (int i = 0; i < this.longCount; i++) {
			out.writeLong(rvfs.getLongs()[i]);
		}
		for (int i = 0; i < this.stringCount; i++) {
			out.writeUTF(rvfs.getStrings()[i]);
		}
	}

	protected void initAuxliliaryFields() {
		int sCount = 0;
		for (PropertyRange pr : sort.getPropertyRanges()) {
			if (Sort.SORTNAME_STRING.equals(pr.getRange())) {
				sCount++;
			}
		}
		this.stringCount = sCount;
		this.longCount = sort.getPropertyRanges().size() - sCount;

		if (this.stringCount == 0) {
			this.fixedSize = 8 * this.longCount;
		} else {
			this.fixedSize = -1;
		}
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		initAuxliliaryFields();
	}
}
