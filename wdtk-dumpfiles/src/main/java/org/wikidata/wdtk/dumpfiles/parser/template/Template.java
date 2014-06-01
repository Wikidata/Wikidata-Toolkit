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

import org.apache.commons.lang3.Validate;

/**
 * 
 * @author Julian Mendez
 * 
 */
public class Template {

	final String id;
	final String page;
	final Map<String, String> parameters = new TreeMap<String, String>();

	public Template(String page, String id, Map<String, String> parameters) {
		Validate.notNull(page, "Page cannot be null.");
		Validate.notNull(id, "ID cannot be null.");
		Validate.notNull(parameters, "Parameters cannot be null.");
		this.page = page;
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
		return this.id;
	}

	public String getPage() {
		return this.page;
	}

	public Map<String, String> getParameters() {
		return Collections.unmodifiableMap(this.parameters);
	}

	public Set<String> keySet() {
		return this.parameters.keySet();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(ParserConstant.OPENING_BRACES);
		sb.append(this.id);
		for (String key : this.parameters.keySet()) {
			sb.append(ParserConstant.NEWLINE);
			sb.append(ParserConstant.VERTICAL_BAR);
			sb.append(ParserConstant.SPACE);
			sb.append(key);
			String value = this.parameters.get(key);
			if ((value != null) && !value.isEmpty()) {
				sb.append(ParserConstant.EQUALS_SIGN);
				sb.append(value);
			}
		}
		sb.append(ParserConstant.NEWLINE);
		sb.append(ParserConstant.CLOSING_BRACES);
		return sb.toString();
	}

}
