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
 * An object of this class builds a {@link Constraint} using a {@link Template}.
 * 
 * @author Julian Mendez
 *
 */
public class ConstraintMainBuilder implements ConstraintBuilder {

	public static final String PREFIX_WIKIDATA = "http://www.wikidata.org/entity/";
	public static final PropertyIdValue PROPERTY_INSTANCE_OF = (new DataObjectFactoryImpl())
			.getPropertyIdValue("P31", ConstraintMainBuilder.PREFIX_WIKIDATA);
	public static final PropertyIdValue PROPERTY_SUBCLASS_OF = (new DataObjectFactoryImpl())
			.getPropertyIdValue("P279", ConstraintMainBuilder.PREFIX_WIKIDATA);

	static final Logger logger = LoggerFactory
			.getLogger(ConstraintMainBuilder.class);
	final Map<String, ConstraintBuilder> mapOfBuilders = new HashMap<String, ConstraintBuilder>();

	/**
	 * Constructs a new main builder.
	 */
	public ConstraintMainBuilder() {
		registerIds();
	}

	/**
	 * Returns a given string with the first letter in upper case. For example,
	 * 'kindergarten' is transformed to 'Kindergarten', but '24 hours' is
	 * returned unchanged.
	 * 
	 * @param str
	 *            string
	 * @return a given string with the first letter in upper case
	 */
	static String firstLetterToUpperCase(String str) {
		if (str == null) {
			return null;
		} else if (str.isEmpty()) {
			return str;
		} else {
			return str.substring(0, 1).toUpperCase() + str.substring(1);
		}
	}

	/**
	 * Returns a given string without square brackets, braces, nor vertical
	 * bars. For example, '{{Q|42}}' and '[[Q|42]]' are both transformed to
	 * 'Q42'.
	 * 
	 * @param str
	 *            string
	 * @return a given string without square brackets, braces, nor vertical bars
	 */
	static String removeBrackets(String str) {
		Validate.notNull(str);
		return str.replace(TemplateConstant.OPENING_BRACKETS, "")
				.replace(TemplateConstant.CLOSING_BRACKETS, "")
				.replace(TemplateConstant.OPENING_BRACES, "")
				.replace(TemplateConstant.CLOSING_BRACES, "")
				.replace(TemplateConstant.VERTICAL_BAR, "");
	}

	/**
	 * Returns a list of properties parsed from a string. For example,
	 * "{{P|580}}, {{P|582}}, {{P|805}}, {{P|1480}}" is parsed as a list
	 * containing P580, P582, P805, and P1480.
	 * 
	 * @param listOfPropertiesStr
	 *            string containing a list of properties
	 * @return a list of properties parsed from a string
	 */
	static List<PropertyIdValue> parseListOfProperties(
			String listOfPropertiesStr) {
		Validate.notNull(listOfPropertiesStr);
		List<PropertyIdValue> ret = new ArrayList<PropertyIdValue>();
		String str = removeBrackets(listOfPropertiesStr);
		DataObjectFactoryImpl factory = new DataObjectFactoryImpl();
		StringTokenizer stok = new StringTokenizer(str, TemplateConstant.COMMA);
		while (stok.hasMoreTokens()) {
			String propertyIdValueStr = stok.nextToken().trim();
			PropertyIdValue property = factory.getPropertyIdValue(
					ConstraintMainBuilder
							.firstLetterToUpperCase(propertyIdValueStr),
					ConstraintMainBuilder.PREFIX_WIKIDATA);
			ret.add(property);
		}
		return ret;
	}

	/**
	 * Returns a list of items parsed from a string. For example,
	 * "{{Q|46}}, {{Q|48}}, {{Q|15}}" is parsed as a list containing Q46, Q48,
	 * and Q15.
	 * 
	 * @param listOfItemsStr
	 *            string containing a list of items
	 * @return a list of items parsed from a string
	 */
	static List<ItemIdValue> parseListOfItems(String listOfItemsStr) {
		Validate.notNull(listOfItemsStr);
		List<ItemIdValue> ret = new ArrayList<ItemIdValue>();
		String str = removeBrackets(listOfItemsStr);
		DataObjectFactoryImpl factory = new DataObjectFactoryImpl();
		StringTokenizer stok = new StringTokenizer(str, TemplateConstant.COMMA);
		while (stok.hasMoreTokens()) {
			String itemStr = stok.nextToken().trim();
			try {
				ItemIdValue item = factory.getItemIdValue(
						ConstraintMainBuilder.firstLetterToUpperCase(itemStr),
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

	/**
	 * Returns a list of property-values ({@link PropertyValues}) parsed from a
	 * string. Property-values are sets of pairs property-value, for a fixed
	 * property, and can be constructed to span all possible values, or to have
	 * only some specific values. For example,
	 * "{{P|31}}: {{Q|4167410}}; {{P|625}}" would be interpreted as two
	 * property-values: the first one includes only pair (P31, Q4167410), the
	 * second one includes all pairs (P625, <i>q</i>), where <i>q</i> is some
	 * item.
	 * 
	 * @param listOfPropertyValuesStr
	 *            string containing a list of property-values
	 * @return a list of property-values parsed from a string
	 */
	static List<PropertyValues> parseListOfPropertyValues(
			String listOfPropertyValuesStr) {
		Validate.notNull(listOfPropertyValuesStr);
		List<PropertyValues> ret = new ArrayList<PropertyValues>();
		String str = removeBrackets(listOfPropertyValuesStr);
		DataObjectFactoryImpl factory = new DataObjectFactoryImpl();
		StringTokenizer stok = new StringTokenizer(str,
				TemplateConstant.SEMICOLON);
		while (stok.hasMoreTokens()) {
			String propertyValuesStr = stok.nextToken().trim();
			int pos = propertyValuesStr.indexOf(TemplateConstant.COLON);
			if (pos == -1) {
				PropertyIdValue property = factory.getPropertyIdValue(
						ConstraintMainBuilder
								.firstLetterToUpperCase(propertyValuesStr),
						ConstraintMainBuilder.PREFIX_WIKIDATA);
				ret.add(new PropertyValues(property));
			} else {
				PropertyIdValue property = factory.getPropertyIdValue(
						ConstraintMainBuilder
								.firstLetterToUpperCase(propertyValuesStr
										.substring(0, pos).trim()),
						ConstraintMainBuilder.PREFIX_WIKIDATA);
				List<ItemIdValue> values = parseListOfItems(propertyValuesStr
						.substring(pos + 1));
				ret.add(new PropertyValues(property, values));
			}
		}
		return ret;
	}

	/**
	 * Returns a list of strings parsed from a given string. For example,
	 * "a, b, c" is parsed as a list containing three strings: "a", "b", and
	 * "c". Whitespaces are removed from the beginning and the end of the
	 * resulting strings.
	 * 
	 * @param listOfStringsStr
	 *            string containing a list of strings
	 * @return a list of strings parsed from a given string
	 */
	static List<String> parseListOfStrings(String listOfStringsStr) {
		Validate.notNull(listOfStringsStr);
		List<String> ret = new ArrayList<String>();
		StringTokenizer stok = new StringTokenizer(listOfStringsStr,
				TemplateConstant.COMMA);
		while (stok.hasMoreTokens()) {
			String str = stok.nextToken().trim();
			ret.add(str);
		}
		return ret;
	}

	/**
	 * Returns a list of integers parsed from a given string. For example,
	 * "1, 2, 3, 4, 5, 6, 7, 8, 9, 10" is parsed as a list containing the first
	 * 10 positive integers.
	 * 
	 * @param listOfQuantitiesStr
	 *            string containing a list of quantities
	 * @return a list of quantities parsed from a given string
	 */
	static List<Integer> parseListOfQuantities(String listOfQuantitiesStr) {
		Validate.notNull(listOfQuantitiesStr);
		List<Integer> ret = new ArrayList<Integer>();
		List<String> list = parseListOfStrings(listOfQuantitiesStr);
		for (String str : list) {
			ret.add(Integer.parseInt(str));
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
				try {
					ret = constraintBuilder
							.parse(constrainedProperty, template);
				} catch (IllegalArgumentException e) {
					logger.warn("WARNING: Ignoring invalid template for constraint: property='"
							+ constrainedProperty
							+ "', template='"
							+ template.toString() + "'.");
					logger.warn(e.toString());
				}
			}
		}
		return ret;
	}

	/**
	 * Returns a constraint builder for the given identifier.
	 * 
	 * @param name
	 *            identifier
	 * @return a constraint builder for the given identifier
	 */
	public ConstraintBuilder getConstraintBuilder(String name) {
		return this.mapOfBuilders.get(name);
	}

	/**
	 * Normalizes a string to be a lower case string, starting with a capital
	 * letter, and without underscores. For example, "LISP" &rarr; "Lisp",
	 * "text" &rarr; "Text", "Big_Data" &rarr; "Big data", "2-Aminoethanol"
	 * &rarr; "2-aminoethanol"
	 * 
	 * @param str
	 *            string
	 * @return a normalized string
	 */
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

	/**
	 * Registers a constraint builder.
	 * 
	 * @param str
	 *            constraint identifier
	 * @param builder
	 *            constraint builder
	 */
	private void register(String str, ConstraintBuilder builder) {
		this.mapOfBuilders.put(normalize(str), builder);
	}

	/**
	 * This method registers all the available constraint builders.
	 */
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
		register(ConstraintBuilderConstant.C_COMMONS_LINK,
				new ConstraintCommonsLinkBuilder());
		register(ConstraintBuilderConstant.C_TARGET_REQUIRED_CLAIM,
				new ConstraintTargetRequiredClaimBuilder());
		register(ConstraintBuilderConstant.C_ITEM, new ConstraintItemBuilder());
		register(ConstraintBuilderConstant.C_TYPE, new ConstraintTypeBuilder());
		register(ConstraintBuilderConstant.C_VALUE_TYPE,
				new ConstraintValueTypeBuilder());
		register(ConstraintBuilderConstant.C_RANGE,
				new ConstraintRangeBuilder());
		register(ConstraintBuilderConstant.C_DIFF_WITHIN_RANGE,
				new ConstraintDiffWithinRangeBuilder());
		register(ConstraintBuilderConstant.C_MULTI_VALUE,
				new ConstraintMultiValueBuilder());
		register(ConstraintBuilderConstant.C_CONFLICTS_WITH,
				new ConstraintConflictsWithBuilder());
		register(ConstraintBuilderConstant.C_QUALIFIERS,
				new ConstraintQualifiersBuilder());
		register(ConstraintBuilderConstant.C_QUALIFIER,
				new ConstraintQualifierBuilder());
	}

}
