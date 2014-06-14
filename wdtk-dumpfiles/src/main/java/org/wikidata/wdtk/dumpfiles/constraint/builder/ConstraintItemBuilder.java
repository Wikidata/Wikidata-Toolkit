package org.wikidata.wdtk.dumpfiles.constraint.builder;

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
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintItem;
import org.wikidata.wdtk.dumpfiles.constraint.template.Template;

/**
 * 
 * @author Julian Mendez
 * 
 */
class ConstraintItemBuilder implements ConstraintBuilder {

	public ConstraintItemBuilder() {
	}

	@Override
	public ConstraintItem parse(PropertyIdValue constrainedProperty,
			Template template) {
		ConstraintItem ret = null;
		if (constrainedProperty != null) {
			String propertyStr = template
					.getValue(ConstraintBuilderConstant.P_PROPERTY);
			PropertyIdValue property = null;
			String itemStr = template.getValue(ConstraintBuilderConstant.P_ITEM);
			ItemIdValue item = null;
			String property2Str = template
					.getValue(ConstraintBuilderConstant.P_PROPERTY_2);
			PropertyIdValue property2 = null;
			String item2Str = template.getValue(ConstraintBuilderConstant.P_ITEM_2);
			ItemIdValue item2 = null;
			String itemsStr = template.getValue(ConstraintBuilderConstant.P_ITEMS);
			List<ItemIdValue> items = new ArrayList<ItemIdValue>();
			String exceptionsStr = template
					.getValue(ConstraintBuilderConstant.P_EXCEPTIONS);
			List<ItemIdValue> exceptions = new ArrayList<ItemIdValue>();
			DataObjectFactoryImpl factory = new DataObjectFactoryImpl();
			if (propertyStr != null) {
				property = factory.getPropertyIdValue(
						propertyStr.toUpperCase(),
						ConstraintMainBuilder.PREFIX_WIKIDATA);
			}
			if (itemStr != null) {
				item = factory.getItemIdValue(itemStr.toUpperCase(),
						ConstraintMainBuilder.PREFIX_WIKIDATA);
			}
			if (property2Str != null) {
				property2 = factory.getPropertyIdValue(
						property2Str.toUpperCase(),
						ConstraintMainBuilder.PREFIX_WIKIDATA);
			}
			if (item2Str != null) {
				item2 = factory.getItemIdValue(item2Str.toUpperCase(),
						ConstraintMainBuilder.PREFIX_WIKIDATA);
			}
			if (itemsStr != null) {
				items = ConstraintMainBuilder.parseListOfItems(itemsStr);
			}
			if (exceptionsStr != null) {
				exceptions = ConstraintMainBuilder
						.parseListOfItems(exceptionsStr);
			}
			ret = new ConstraintItem(constrainedProperty, property, item,
					property2, item2, items, exceptions);
		}
		return ret;
	}

}
