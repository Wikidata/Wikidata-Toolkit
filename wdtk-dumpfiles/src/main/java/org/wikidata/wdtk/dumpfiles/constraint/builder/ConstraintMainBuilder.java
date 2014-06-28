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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.constraint.model.Constraint;
import org.wikidata.wdtk.dumpfiles.constraint.model.PropertyValues;
import org.wikidata.wdtk.dumpfiles.constraint.template.Template;
import org.wikidata.wdtk.dumpfiles.constraint.template.TemplateConstant;

/**
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintMainBuilder implements ConstraintBuilder {

	public static final String PREFIX_WIKIDATA = "http://www.wikidata.org/entity/";

	static final Logger logger = LoggerFactory
			.getLogger(ConstraintMainBuilder.class);
	final Map<String, ConstraintBuilder> mapOfBuilders = new HashMap<String, ConstraintBuilder>();

	/**
	 * Constructs a new main builder.
	 */
	public ConstraintMainBuilder() {
		registerIds();
	}

	static String removeBrackets(String str) {
		Validate.notNull(str);
		return str.replace(TemplateConstant.OPENING_BRACKETS, "")
				.replace(TemplateConstant.CLOSING_BRACKETS, "")
				.replace(TemplateConstant.OPENING_BRACES, "")
				.replace(TemplateConstant.CLOSING_BRACES, "")
				.replace(TemplateConstant.VERTICAL_BAR, "");
	}

	static List<ItemIdValue> parseListOfItems(String listOfItems) {
		Validate.notNull(listOfItems);
		List<ItemIdValue> ret = new ArrayList<ItemIdValue>();
		String str = removeBrackets(listOfItems);
		DataObjectFactoryImpl factory = new DataObjectFactoryImpl();
		StringTokenizer stok = new StringTokenizer(str, TemplateConstant.COMMA);
		while (stok.hasMoreTokens()) {
			String itemStr = stok.nextToken().trim();
			try {
				ItemIdValue item = factory.getItemIdValue(
						itemStr.toUpperCase(),
						ConstraintMainBuilder.PREFIX_WIKIDATA);
				ret.add(item);
			} catch (IllegalArgumentException e) {
				logger.warn("WARNING: Ignoring invalid item: '" + itemStr
						+ "'.");
				logger.warn(e.toString());
			}
		}
		return ret;
	}

	static List<PropertyValues> parseListOfPropertyValues(String listOfItems) {
		Validate.notNull(listOfItems);
		List<PropertyValues> ret = new ArrayList<PropertyValues>();
		String str = removeBrackets(listOfItems);
		DataObjectFactoryImpl factory = new DataObjectFactoryImpl();
		StringTokenizer stok = new StringTokenizer(str,
				TemplateConstant.SEMICOLON);
		while (stok.hasMoreTokens()) {
			String propertyValuesStr = stok.nextToken().trim();
			int pos = propertyValuesStr.indexOf(TemplateConstant.COLON);
			if (pos == -1) {
				PropertyIdValue property = factory.getPropertyIdValue(
						propertyValuesStr.toUpperCase(),
						ConstraintMainBuilder.PREFIX_WIKIDATA);
				ret.add(new PropertyValues(property));
			} else {
				PropertyIdValue property = factory.getPropertyIdValue(
						propertyValuesStr.substring(0, pos).trim()
								.toUpperCase(),
						ConstraintMainBuilder.PREFIX_WIKIDATA);
				List<ItemIdValue> values = parseListOfItems(propertyValuesStr
						.substring(pos + 1));
				ret.add(new PropertyValues(property, values));
			}
		}
		return ret;
	}

	static List<Integer> parseListOfQuantities(String listOfQuantities) {
		Validate.notNull(listOfQuantities);
		List<Integer> ret = new ArrayList<Integer>();
		StringTokenizer stok = new StringTokenizer(listOfQuantities,
				TemplateConstant.COMMA);
		while (stok.hasMoreTokens()) {
			ret.add(Integer.parseInt(stok.nextToken()));
		}
		return ret;
	}

	/**
	 * Creates a constraint based on a template, or <code>null</code> if the
	 * template does not correspond to a known constraint
	 * 
	 * @param template
	 *            template
	 * @return a constraint based on a template, or <code>null</code> if the
	 *         template does not correspond to a known constraint
	 */
	@Override
	public Constraint parse(PropertyIdValue constrainedProperty,
			Template template) {
		Validate.notNull(constrainedProperty);
		Validate.notNull(template);
		Constraint ret = null;
		String templateId = normalize(template.getName());
		String prefix = normalize(ConstraintBuilderConstant.T_CONSTRAINT);
		if (templateId.startsWith(prefix)) {
			String constraintId = normalize(templateId.substring(prefix
					.length()));
			ConstraintBuilder constraintBuilder = getConstraintBuilder(constraintId);
			if (constraintBuilder != null) {
				ret = constraintBuilder.parse(constrainedProperty, template);
			}
		}
		return ret;
	}

	public ConstraintBuilder getConstraintBuilder(String str) {
		return this.mapOfBuilders.get(str);
	}

	public String normalize(String str) {
		String ret = "";
		if (str != null) {
			ret = str
					.trim()
					.toLowerCase()
					.replace(TemplateConstant.UNDERSCORE,
							TemplateConstant.SPACE);
			if (ret.length() > 0) {
				ret = ret.substring(0, 1).toUpperCase() + ret.substring(1);
			}
		}
		return ret;
	}

	private void register(String str, ConstraintBuilder builder) {
		this.mapOfBuilders.put(normalize(str), builder);
	}

	private void registerIds() {
		register(ConstraintBuilderConstant.C_SINGLE_VALUE,
				new ConstraintSingleValueBuilder());
		register(ConstraintBuilderConstant.C_UNIQUE_VALUE,
				new ConstraintUniqueValueBuilder());
		register(ConstraintBuilderConstant.C_FORMAT,
				new ConstraintFormatBuilder());
		register(ConstraintBuilderConstant.C_ONE_OF,
				new ConstraintOneOfBuilder());
		register(ConstraintBuilderConstant.C_SYMMETRIC,
				new ConstraintSymmetricBuilder());
		register(ConstraintBuilderConstant.C_INVERSE,
				new ConstraintInverseBuilder());
		register(ConstraintBuilderConstant.C_EXISTING_FILE,
				new ConstraintExistingFileBuilder());
		register(ConstraintBuilderConstant.C_TARGET_REQUIRED_CLAIM,
				new ConstraintTargetRequiredClaimBuilder());
		register(ConstraintBuilderConstant.C_ITEM, new ConstraintItemBuilder());
		register(ConstraintBuilderConstant.C_TYPE, new ConstraintTypeBuilder());
		register(ConstraintBuilderConstant.C_VALUE_TYPE,
				new ConstraintValueTypeBuilder());
		register(ConstraintBuilderConstant.C_RANGE,
				new ConstraintRangeBuilder());
		register(ConstraintBuilderConstant.C_MULTI_VALUE,
				new ConstraintMultiValueBuilder());
		register(ConstraintBuilderConstant.C_CONFLICTS_WITH,
				new ConstraintConflictsWithBuilder());
		register(ConstraintBuilderConstant.C_QUALIFIER,
				new ConstraintQualifierBuilder());
		register(ConstraintBuilderConstant.C_PERSON,
				new ConstraintPersonBuilder());
		register(ConstraintBuilderConstant.C_TAXON,
				new ConstraintTaxonBuilder());
	}

}
