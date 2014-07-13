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

public class PropertySignatureSerializer implements
		Serializer<PropertySignature>, Serializable {

	private static final long serialVersionUID = 7782215462083157912L;

	@Override
	public void serialize(DataOutput out, PropertySignature value)
			throws IOException {
		out.writeUTF(value.getPropertyName());
		out.writeInt(value.getRangeId());
		out.writeInt(value.getDomainId());
	}

	@Override
	public PropertySignature deserialize(DataInput in, int available)
			throws IOException {
		String propertyName = in.readUTF();
		int rangeId = in.readInt();
		int domainId = in.readInt();
		return new PropertySignature(propertyName, domainId, rangeId);
	}

	@Override
	public int fixedSize() {
		return -1;
	}

}
