package org.wikidata.wdtk.dumpfiles.constraint.format;

/*
 * #%L
 * Wikidata Toolkit Dump File Handling
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

import org.openrdf.model.Resource;

/**
 * 
 * @author Julian Mendez
 * 
 */
public class StringResource implements Resource {

	private static final long serialVersionUID = 6895761579959801235L;

	final String str;

	public StringResource(String str) {
		this.str = str;
	}

	@Override
	public String stringValue() {
		return this.str;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof StringResource)) {
			return false;
		}
		StringResource other = (StringResource) obj;
		return stringValue().equals(other.stringValue());
	}

	@Override
	public int hashCode() {
		return this.str.hashCode();
	}

	@Override
	public String toString() {
		return this.str;
	}

}
