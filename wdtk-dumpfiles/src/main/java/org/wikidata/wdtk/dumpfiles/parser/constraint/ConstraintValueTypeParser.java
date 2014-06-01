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

import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.constraint.ConstraintValueType;
import org.wikidata.wdtk.dumpfiles.constraint.RelationType;
import org.wikidata.wdtk.dumpfiles.parser.template.Template;

/**
 * 
 * @author Julian Mendez
 * 
 */
class ConstraintValueTypeParser implements ConstraintParser {

	public ConstraintValueTypeParser() {
	}

	public ConstraintValueType parse(Template template) {
		ConstraintValueType ret = null;
		String page = template.getPage();
		String classStr = template.get(ConstraintParserConstant.P_CLASS);
		String relationStr = template.get(ConstraintParserConstant.P_RELATION);
		if (page != null && classStr != null && relationStr != null) {
			DataObjectFactoryImpl factory = new DataObjectFactoryImpl();
			PropertyIdValue constrainedProperty = factory.getPropertyIdValue(
					page.toUpperCase(), ConstraintMainParser.PREFIX_WIKIDATA);
			ItemIdValue classId = factory.getItemIdValue(
					classStr.toUpperCase(),
					ConstraintMainParser.PREFIX_WIKIDATA);
			if (relationStr.equals(ConstraintParserConstant.V_INSTANCE)) {
				ret = new ConstraintValueType(constrainedProperty, classId,
						RelationType.INSTANCE);
			} else if (relationStr.equals(ConstraintParserConstant.V_SUBCLASS)) {
				ret = new ConstraintValueType(constrainedProperty, classId,
						RelationType.SUBCLASS);
			}
		}
		return ret;
	}

}
