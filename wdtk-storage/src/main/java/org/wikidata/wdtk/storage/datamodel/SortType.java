package org.wikidata.wdtk.storage.datamodel;

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

/**
 * Basic types of sorts supported. RECORD is the only one that requires
 * configuration. EDGES is used to represent the domain of qualifier properties;
 * edges are otherwise not treated as "values" that have a sort.
 *
 * @author Markus Kroetzsch
 *
 */
public enum SortType {
	STRING((byte) 1), LONG((byte) 2), RECORD((byte) 4), OBJECT((byte) 5), EDGES(
			(byte) 6);

	final byte value;

	private SortType(byte value) {
		this.value = value;
	}

	public byte getValue() {
		return value;
	}

	public static SortType getByNumericValue(byte value) {
		switch (value) {
		case 1:
			return SortType.STRING;
		case 2:
			return SortType.LONG;
		case 4:
			return SortType.RECORD;
		case 5:
			return SortType.OBJECT;
		case 6:
			return SortType.EDGES;
		default:
			return null;
		}
	}
}
