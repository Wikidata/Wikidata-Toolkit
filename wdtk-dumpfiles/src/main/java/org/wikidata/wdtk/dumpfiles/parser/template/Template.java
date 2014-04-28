package org.wikidata.wdtk.dumpfiles.parser.template;

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

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * 
 * @author Julian Mendez
 * 
 */
public class Template {

	final String id;

	final Map<String, String> parameters = new TreeMap<String, String>();

	public Template(String id, Map<String, String> parameters) {
		if (id == null) {
			throw new IllegalArgumentException("Null argument.");
		}
		if (parameters == null) {
			throw new IllegalArgumentException("Null argument.");
		}
		this.id = id.trim();
		for (String key : parameters.keySet()) {
			String value = parameters.get(key);
			value = ((value == null) ? "" : value.trim());
			this.parameters.put(key.trim(), value);
		}
	}

	public String get(String key) {
		return this.parameters.get(key);
	}

	public String getId() {
		return id;
	}

	public Map<String, String> getParameters() {
		return Collections.unmodifiableMap(this.parameters);
	}

	public Set<String> keySet() {
		return this.parameters.keySet();
	}

}
