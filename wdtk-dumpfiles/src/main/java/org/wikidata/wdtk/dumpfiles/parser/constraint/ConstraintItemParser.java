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
import java.util.List;

import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.constraint.ConstraintItem;
import org.wikidata.wdtk.dumpfiles.parser.template.Template;

/**
 * 
 * @author Julian Mendez
 * 
 */
class ConstraintItemParser implements ConstraintParser {

	public ConstraintItemParser() {
	}

	public ConstraintItem parse(Template template) {
		ConstraintItem ret = null;
		String page = template.getPage();
		if (page != null) {
			String propertyStr = template
					.get(ConstraintParserConstant.P_PROPERTY);
			PropertyIdValue property = null;
			String itemStr = template.get(ConstraintParserConstant.P_ITEM);
			ItemIdValue item = null;
			String property2Str = template
					.get(ConstraintParserConstant.P_PROPERTY_2);
			PropertyIdValue property2 = null;
			String item2Str = template.get(ConstraintParserConstant.P_ITEM_2);
			ItemIdValue item2 = null;
			String itemsStr = template.get(ConstraintParserConstant.P_ITEMS);
			List<ItemIdValue> items = new ArrayList<ItemIdValue>();
			String exceptionsStr = template
					.get(ConstraintParserConstant.P_EXCEPTIONS);
			List<ItemIdValue> exceptions = new ArrayList<ItemIdValue>();
			DataObjectFactoryImpl factory = new DataObjectFactoryImpl();
			PropertyIdValue constrainedProperty = factory.getPropertyIdValue(
					page.toUpperCase(), ConstraintMainParser.DEFAULT_BASE_IRI);
			if (propertyStr != null) {
				property = factory.getPropertyIdValue(
						propertyStr.toUpperCase(),
						ConstraintMainParser.DEFAULT_BASE_IRI);
			}
			if (itemStr != null) {
				item = factory.getItemIdValue(itemStr.toUpperCase(),
						ConstraintMainParser.DEFAULT_BASE_IRI);
			}
			if (property2Str != null) {
				property2 = factory.getPropertyIdValue(
						property2Str.toUpperCase(),
						ConstraintMainParser.DEFAULT_BASE_IRI);
			}
			if (item2Str != null) {
				item2 = factory.getItemIdValue(item2Str.toUpperCase(),
						ConstraintMainParser.DEFAULT_BASE_IRI);
			}
			if (itemsStr != null) {
				items = ConstraintMainParser.parseListOfItems(itemsStr);
			}
			if (exceptionsStr != null) {
				exceptions = ConstraintMainParser
						.parseListOfItems(exceptionsStr);
			}
			ret = new ConstraintItem(constrainedProperty, property, item,
					property2, item2, items, exceptions);
		}
		return ret;
	}

}
