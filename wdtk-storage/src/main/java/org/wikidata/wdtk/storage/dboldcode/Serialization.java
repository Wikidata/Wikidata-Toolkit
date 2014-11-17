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

/**
 * Static methods for serializing objects.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class Serialization {

	public static void serializeTypedFields(DataOutput out, byte[] types,
			int[] refs, Object[] objects) throws IOException {
		int iRef = 0;
		int iObject = 0;
		for (byte type : types) {
			switch (type) {
			case SerializationConverter.TYPE_STRING:
				out.writeUTF((String) objects[iObject]);
				iObject++;
				break;
			case SerializationConverter.TYPE_OBJECT:
			case SerializationConverter.TYPE_RECORD:
				throw new RuntimeException("Can't serialize this type yet");
			case SerializationConverter.TYPE_REF:
				out.writeInt(refs[iRef]);
				iRef++;
				break;
			default:
				throw new RuntimeException("Type not supported.");
			}
		}
	}

	public static void deserializeTypedFields(DataInput in, byte[] types,
			int[] refs, Object[] objects) throws IOException {

		int iRef = 0;
		int iObject = 0;
		for (int i = 0; i < types.length; i++) {
			switch (types[i]) {
			case SerializationConverter.TYPE_STRING:
				objects[iObject] = in.readUTF();
				iObject++;
				break;
			case SerializationConverter.TYPE_OBJECT:
			case SerializationConverter.TYPE_RECORD:
				throw new RuntimeException("Can't deserialize this type yet");
			case SerializationConverter.TYPE_REF:
				refs[iRef] = in.readInt();
				iRef++;
				break;
			default:
				throw new RuntimeException("Type not supported.");
			}
		}
	}

}
