package org.wikidata.wdtk.dumpfiles.constraint.template;

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
 * This class represents a particular transclusion of a Wikibase template.
 * <p>
 * A template is a page created to be included in other pages in the Wikibase
 * system. The transclusion is a method of inclusion where the wikitext of a
 * page contains a reference to the template.
 * <p>
 * The syntax in the transclusion is of the form:
 * <p>
 * {{ <i>template_name</i> | <i>parameter</i><sub>1</sub> |
 * <i>parameter</i><sub>2</sub> | &hellip; | <i>parameter</i><sub><i>n</i></sub>
 * }}
 * <p>
 * Each <i>parameter</i><sub><i>i</i></sub> can be of the form:
 * <i>name</i><sub><i>i</i></sub>=<i>value</i><sub><i>i</i></sub>, or just
 * <i>value</i><sub><i>i</i></sub>. The former type of parameter is called
 * <i>named parameter</i>, and the latter is called <i>unnamed parameter</i>.
 * All unnamed parameters are actually implicitly named as sequential positive
 * integers. Thus, the first one is "1", the second one is "2", and so on and so
 * forth.
 *
 * @author Julian Mendez
 *
 */
public class Template {

	final String name;

	final TreeMap<String, String> parameters = new TreeMap<String, String>();

	/**
	 * Constructs a new template using an identifier, its parameter names with
	 * their values. The values need to be already 'resolved'. If they contain
	 * references to other templates or Wikibase commands, these values will be
	 * considered as simple text.
	 *
	 * @param name
	 *            template name
	 * @param parameters
	 *            a map containing the template's parameter names with their
	 *            values
	 */
	public Template(String name, Map<String, String> parameters) {
		Validate.notNull(name, "Name cannot be null.");
		Validate.notNull(parameters, "Parameters cannot be null.");
		this.name = name.trim();
		for (String key : parameters.keySet()) {
			String value = parameters.get(key);
			value = ((value == null) ? "" : value.trim());
			this.parameters.put(key.trim(), value);
		}
	}

	/**
	 * Returns the identifier of this template.
	 *
	 * @return the identifier of this template
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns a map containing all parameter names with the associated values
	 * that this template has.
	 *
	 * @return a map containing all parameter names with the associated values
	 *         that this template has
	 */
	public Map<String, String> getParameters() {
		return Collections.unmodifiableMap(this.parameters);
	}

	/**
	 * Returns the parameter names that this template has.
	 *
	 * @return the parameter names that this template has
	 */
	public Set<String> getParameterNames() {
		return this.parameters.keySet();
	}

	/**
	 * Returns the value associated to the specified parameter name
	 *
	 * @param parameterName
	 *            the parameter name
	 * @return the value associated to the specified parameter name
	 */
	public String getValue(String parameterName) {
		return this.parameters.get(parameterName);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(TemplateConstant.OPENING_BRACES);
		sb.append(this.name);
		for (String key : this.parameters.keySet()) {
			sb.append(TemplateConstant.VERTICAL_BAR);
			sb.append(key);
			String value = this.parameters.get(key);
			if ((value != null) && !value.isEmpty()) {
				sb.append(TemplateConstant.EQUALS_SIGN);
				sb.append(value);
			}
		}
		sb.append(TemplateConstant.CLOSING_BRACES);
		return sb.toString();
	}

}
