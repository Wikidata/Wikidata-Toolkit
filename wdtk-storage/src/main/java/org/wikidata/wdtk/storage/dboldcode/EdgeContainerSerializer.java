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

public class EdgeContainerSerializer implements
		Serializer<EdgeContainerForSerialization>, Serializable {

	private static final long serialVersionUID = 6230338257167453117L;

	@Override
	public void serialize(DataOutput out, EdgeContainerForSerialization value)
			throws IOException {
		out.writeInt(value.getSource());

		int propertyCount = value.getProperties().length;
		out.writeInt(propertyCount);
		for (int i = 0; i < propertyCount; i++) {
			out.writeInt(value.getProperties()[i]);

			int targetCount = value.getTargetQualifiers()[i].length;
			out.writeInt(targetCount);
			for (int j = 0; j < targetCount; j++) {
				int qualifierCount = (value.getTargetQualifiers()[i][j].length - 1) / 2;
				out.writeInt(qualifierCount);

				out.writeInt(value.getTargetQualifiers()[i][j][0]);
				for (int k = 0; k < qualifierCount; k++) {
					out.writeInt(value.getTargetQualifiers()[i][j][2 * k + 1]);
					out.writeInt(value.getTargetQualifiers()[i][j][2 * k + 2]);
				}
			}
		}
	}

	@Override
	public EdgeContainerForSerialization deserialize(DataInput in, int available)
			throws IOException {
		int source = in.readInt();
		int propertyCount = in.readInt();
		int[] properties = new int[propertyCount];
		int[][][] targetQualifiers = new int[propertyCount][][];

		for (int i = 0; i < propertyCount; i++) {
			properties[i] = in.readInt();
			int targetCount = in.readInt();
			targetQualifiers[i] = new int[targetCount][];
			for (int j = 0; j < targetCount; j++) {
				int qualifierCount = in.readInt();
				targetQualifiers[i][j] = new int[1 + 2 * qualifierCount];
				targetQualifiers[i][j][0] = in.readInt();
				for (int k = 0; k < qualifierCount; k++) {
					targetQualifiers[i][j][2 * k + 1] = in.readInt();
					targetQualifiers[i][j][2 * k + 2] = in.readInt();
				}
			}
		}

		return new EdgeContainerForSerialization(source, properties,
				targetQualifiers);
	}

	@Override
	public int fixedSize() {
		return -1;
	}

}
