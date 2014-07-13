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
}
