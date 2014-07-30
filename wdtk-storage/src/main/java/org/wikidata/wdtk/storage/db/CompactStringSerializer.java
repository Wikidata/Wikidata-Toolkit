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

import org.apache.commons.compress.utils.Charsets;
import org.mapdb.Serializer;

public class CompactStringSerializer implements Serializer<String>,
		Serializable {

	private static final long serialVersionUID = -2702835850468310949L;

	@Override
	public void serialize(DataOutput out, String value) throws IOException {
		byte[] b = value.getBytes(Charsets.US_ASCII);
		boolean isAscii = true;
		for (int i = 0; i < b.length; i++) {
			if ((char) b[i] == '?' && value.charAt(i) != '?') {
				isAscii = false;
				break;
			}
		}

		if (isAscii && value.length() > 0) {
			out.writeInt(b.length);
			out.write(b);
		} else {
			out.writeInt(0);
			out.writeUTF(value);
		}
	}

	@Override
	public String deserialize(DataInput in, int available) throws IOException {
		int byteLength = in.readInt();
		if (byteLength > 0) {
			byte[] b = new byte[byteLength];
			in.readFully(b);
			return new String(b, Charsets.US_ASCII);
		} else {
			return in.readUTF();
		}
	}

	@Override
	public int fixedSize() {
		return -1;
	}

}
