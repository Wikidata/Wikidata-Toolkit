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
import java.util.ArrayList;
import java.util.List;

import org.mapdb.Serializer;
import org.wikidata.wdtk.storage.datamodel.PropertyRange;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.datamodel.SortType;

// TODO rely on POJO serializer instead? 
// maybe efficiency gain is negligible for the small number of sorts
public class DbSortDataSerializer implements Serializer<DbSortData>,
		Serializable {

	private static final long serialVersionUID = -4225023564347516207L;

	@Override
	public DbSortData deserialize(DataInput in, int available)
			throws IOException {
		int id = in.readInt();
		boolean useDictionary = in.readBoolean();

		SortType sortType = SortType.getByNumericValue(in.readByte());
		String name = in.readUTF();
		int propRangeSize = in.readInt();
		List<PropertyRange> propertyRanges = null;
		if (propRangeSize > 0) {
			propertyRanges = new ArrayList<>();
			for (int i = 0; i < propRangeSize; i++) {
				String property = in.readUTF();
				String range = in.readUTF();
				propertyRanges.add(new PropertyRange(property, range));
			}
		}
		Sort sort = new Sort(name, sortType, propertyRanges);

		return new DbSortData(sort, id, useDictionary);
	}

	@Override
	public int fixedSize() {
		return -1;
	}

	@Override
	public void serialize(DataOutput out, DbSortData sortData)
			throws IOException {
		out.writeInt(sortData.id);
		out.writeBoolean(sortData.useDictionary);
		out.writeByte(sortData.sort.getType().getValue());
		out.writeUTF(sortData.sort.getName());
		if (sortData.sort.getPropertyRanges() == null) {
			out.writeInt(0);
		} else {
			out.writeInt(sortData.sort.getPropertyRanges().size());
			for (PropertyRange pr : sortData.sort.getPropertyRanges()) {
				out.writeUTF(pr.getProperty());
				out.writeUTF(pr.getRange());
			}
		}
	}

}
