package org.wikidata.wdtk.storage.dboldcode;

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

public class RecordValueForSerialization {

	final int[] refs;
	final Object[] objects;

	public RecordValueForSerialization(int[] refs, Object[] strings) {
		this.refs = refs;
		this.objects = strings;
	}

	public int[] getRefs() {
		return this.refs;
	}

	public Object[] getObjects() {
		return this.objects;
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
		result = prime * result + Arrays.hashCode(refs);
		result = prime * result + Arrays.hashCode(objects);
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
		if (!(obj instanceof RecordValueForSerialization)) {
			return false;
		}
		RecordValueForSerialization other = (RecordValueForSerialization) obj;
		return Arrays.equals(refs, other.refs)
				&& Arrays.equals(objects, other.objects);
	}
}
