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

import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintOneOf;
import org.wikidata.wdtk.dumpfiles.constraint.template.Template;
import org.wikidata.wdtk.rdf.WikidataPropertyTypes;

/**
 * 
 * @author Julian Mendez
 * 
 */
class ConstraintOneOfBuilder implements ConstraintBuilder {

	public ConstraintOneOfBuilder() {
	}

	@Override
	public ConstraintOneOf parse(PropertyIdValue constrainedProperty,
			Template template) {
		ConstraintOneOf ret = null;
		String values = template.getValue(ConstraintBuilderConstant.P_VALUES);
		if ((constrainedProperty != null) && (values != null)) {
			WikidataPropertyTypes wdPropertyTypes = new WikidataPropertyTypes();
			String propertyType = wdPropertyTypes
					.getPropertyType(constrainedProperty);

			if (propertyType.equals(DatatypeIdValue.DT_ITEM)) {
				ret = new ConstraintOneOf(constrainedProperty,
						ConstraintMainBuilder.parseListOfItems(values));

			} else if (propertyType.equals(DatatypeIdValue.DT_QUANTITY)) {
				ret = new ConstraintOneOf(
						ConstraintMainBuilder.parseListOfQuantities(values),
						constrainedProperty);

			} else {
				throw new IllegalArgumentException(
						"'Constraint:One of' cannot be used for property '"
								+ constrainedProperty.getId()
								+ "' because its type is '" + propertyType
								+ "'.");
			}
		}
		return ret;
	}

}
