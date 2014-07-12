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
import java.util.Iterator;
import java.util.List;

import org.mapdb.Serializer;
import org.wikidata.wdtk.storage.datamodel.ObjectValue;
import org.wikidata.wdtk.storage.datamodel.PropertyRange;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePair;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.datamodel.SortSchema;
import org.wikidata.wdtk.storage.datamodel.StringValue;

public class RecordSerializer implements Serializer<ObjectValue>, Serializable {

	private static final long serialVersionUID = 4397282628980400400L;

	final Sort sort;

	public RecordSerializer(Sort sort) {
		this.sort = sort;
	}

	@Override
	public ObjectValue deserialize(DataInput in, int available)
			throws IOException {
		List<PropertyValuePair> propertyValuePairs; // TODO we don't have an
													// implementation yet
		// TODO deserialize lazily, esp. for the object ref resolution
		for (PropertyRange pr : this.sort.getPropertyRanges()) {
			// TODO read values
		}
		return null;
	}

	@Override
	public int fixedSize() {
		return -1;
	}

	@Override
	public void serialize(DataOutput out, ObjectValue objectValue)
			throws IOException {
		Iterator<PropertyRange> propertyRanges = this.sort.getPropertyRanges()
				.iterator();
		for (PropertyValuePair pvp : objectValue) {
			PropertyRange pr = propertyRanges.next();

			// TODO evaluate the performance hit of this check; maybe use
			// assertions instead
			if (!pr.getProperty().equals(pvp.getProperty())
					|| !pr.getRange()
							.equals(pvp.getValue().getSort().getName())) {
				throw new IllegalArgumentException(
						"The given object value does not match the sort specification for this record.");
			}

			if (SortSchema.SORTNAME_STRING.equals(pr.getRange())) {
				// Inline strings:
				// TODO do we really want/need this type check?
				if (pvp.getValue() instanceof StringValue) {
					out.writeUTF(((StringValue) pvp.getValue()).getString());
				} else {
					throw new IllegalArgumentException("Expected StringValue");
				}
			} else {
				// TODO
				// declared sorts -> long id
				throw new UnsupportedOperationException(
						"Serialization only implemented for strings yet.");
			}
		}
	}
}
