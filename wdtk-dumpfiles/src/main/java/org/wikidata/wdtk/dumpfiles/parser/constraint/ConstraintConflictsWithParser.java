package org.wikidata.wdtk.dumpfiles.parser.constraint;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.constraint.ConstraintConflictsWith;
import org.wikidata.wdtk.dumpfiles.constraint.PropertyValues;
import org.wikidata.wdtk.dumpfiles.parser.template.Template;

/**
 * 
 * @author Julian Mendez
 * 
 */
class ConstraintConflictsWithParser implements ConstraintParser {

	final List<PropertyValues> list = new ArrayList<PropertyValues>();

	public ConstraintConflictsWithParser() {
	}

	public ConstraintConflictsWithParser(List<PropertyValues> list) {
		Validate.notNull(list, "List cannot be null.");
		this.list.addAll(list);
	}

	public List<PropertyValues> getList() {
		return Collections.unmodifiableList(this.list);
	}

	public ConstraintConflictsWith parse(Template template) {
		ConstraintConflictsWith ret = null;
		String page = template.getPage();
		String listStr = template.get(ConstraintParserConstant.P_LIST);
		if (page != null && listStr != null) {
			DataObjectFactoryImpl factory = new DataObjectFactoryImpl();
			PropertyIdValue constrainedProperty = factory.getPropertyIdValue(
					page.toUpperCase(), ConstraintMainParser.DEFAULT_BASE_IRI);
			ret = new ConstraintConflictsWith(constrainedProperty,
					ConstraintMainParser.parseListOfPropertyValues(listStr));
		}
		return ret;
	}

}
