package org.wikidata.wdtk.storage.db;

import java.util.Arrays;

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

public class ObjectValueForSerialization {

	public static final byte TYPE_REF = 1;
	public static final byte TYPE_STRING = 2;

	final long[] properties;
	final byte[] types;
	final long[] refs;
	final String[] strings;

	ObjectValueForSerialization(long[] properties, byte[] types, long[] refs,
			String[] strings) {
		assert (properties.length == types.length);
		assert (properties.length == refs.length + strings.length);

		this.properties = properties;
		this.types = types;
		this.refs = refs;
		this.strings = strings;
	}

	public long[] getProperties() {
		return this.properties;
	}

	public byte[] getTypes() {
		return this.types;
	}

	public long[] getRefs() {
		return this.refs;
	}

	public String[] getStrings() {
		return this.strings;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ObjectValueForSerialization [properties="
				+ Arrays.toString(properties) + ", types="
				+ Arrays.toString(types) + ", refs=" + Arrays.toString(refs)
				+ ", strings=" + Arrays.toString(strings) + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(properties);
		result = prime * result + Arrays.hashCode(refs);
		result = prime * result + Arrays.hashCode(strings);
		result = prime * result + Arrays.hashCode(types);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ObjectValueForSerialization)) {
			return false;
		}
		ObjectValueForSerialization other = (ObjectValueForSerialization) obj;
		return Arrays.equals(types, other.types)
				&& Arrays.equals(refs, other.refs)
				&& Arrays.equals(strings, other.strings)
				&& Arrays.equals(properties, other.properties);
	}
}
