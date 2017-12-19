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

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintDiffWithinRange;
import org.wikidata.wdtk.dumpfiles.constraint.template.Template;
import org.wikidata.wdtk.rdf.PropertyRegister;

/**
 * An object of this class is a builder of a 'Diff within range' constraint.
 * 
 * @author Julian Mendez
 * 
 */
class ConstraintDiffWithinRangeBuilder implements ConstraintBuilder {

	public Double parseDouble(String str) {
		Validate.notNull(str);
		Double ret = null;
		try {
			ret = Double.parseDouble(str);
		} catch (NumberFormatException e) {
		}
		return ret;
	}

	/**
	 * Constructs a new builder.
	 */
	public ConstraintDiffWithinRangeBuilder() {
	}

	@Override
	public ConstraintDiffWithinRange parse(PropertyIdValue constrainedProperty,
			Template template) {
		ConstraintDiffWithinRange ret = null;
		String basePropertyStr = template
				.getValue(ConstraintBuilderConstant.P_BASE_PROPERTY);
		String minStr = template.getValue(ConstraintBuilderConstant.P_MIN)
				.toLowerCase().trim();
		String maxStr = template.getValue(ConstraintBuilderConstant.P_MAX)
				.toLowerCase().trim();
		if ((constrainedProperty != null) && (basePropertyStr != null)
				&& (minStr != null) && (maxStr != null)) {

			DataObjectFactoryImpl factory = new DataObjectFactoryImpl();
			PropertyIdValue baseProperty = factory.getPropertyIdValue(
					ConstraintMainBuilder
							.firstLetterToUpperCase(basePropertyStr),
					ConstraintMainBuilder.PREFIX_WIKIDATA);

			PropertyRegister propertyRegister = PropertyRegister.getWikidataPropertyRegister();
			String propertyType = propertyRegister
					.getPropertyType(constrainedProperty);

			if (propertyType.equals(DatatypeIdValue.DT_TIME)
					|| propertyType.equals(DatatypeIdValue.DT_QUANTITY)) {
				Double minNum = parseDouble(minStr);
				Double maxNum = parseDouble(maxStr);
				if ((minNum != null) && (maxNum != null)) {
					ret = new ConstraintDiffWithinRange(constrainedProperty,
							baseProperty, minStr, maxStr,
							propertyType.equals(DatatypeIdValue.DT_TIME));
				}

			} else {
				throw new IllegalArgumentException("Property '"
						+ constrainedProperty.getId() + "' has type '"
						+ propertyType
						+ "' and cannot have a range constraint as in '"
						+ template + "'.");
			}

		}
		return ret;
	}

}
