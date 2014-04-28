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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.constraint.Constraint;
import org.wikidata.wdtk.dumpfiles.constraint.PropertyValues;
import org.wikidata.wdtk.dumpfiles.parser.template.ParserConstant;
import org.wikidata.wdtk.dumpfiles.parser.template.Template;

/**
 * 
 * @author Julian Mendez
 *
 */
public class ConstraintMainParser implements ConstraintParser {

	public static final String DEFAULT_BASE_IRI = "http://www.wikidata.org/entity/";

	final Map<String, ConstraintParser> mapOfParsers = new HashMap<String, ConstraintParser>();

	public ConstraintMainParser() {
		registerIds();
	}

	static String removeBrackets(String str) {
		return str.replace(ParserConstant.OPENING_BRACKETS, "")
				.replace(ParserConstant.CLOSING_BRACKETS, "")
				.replace(ParserConstant.OPENING_BRACES, "")
				.replace(ParserConstant.CLOSING_BRACES, "")
				.replace(ParserConstant.VERTICAL_BAR, "");
	}

	static List<ItemIdValue> parseListOfItems(String listOfItems) {
		List<ItemIdValue> ret = new ArrayList<ItemIdValue>();
		String str = removeBrackets(listOfItems);
		DataObjectFactoryImpl factory = new DataObjectFactoryImpl();
		StringTokenizer stok = new StringTokenizer(str, ParserConstant.COMMA);
		while (stok.hasMoreTokens()) {
			String itemStr = stok.nextToken().trim();
			ItemIdValue item = factory.getItemIdValue(itemStr,
					ConstraintMainParser.DEFAULT_BASE_IRI);
			ret.add(item);
		}
		return ret;
	}

	static List<PropertyValues> parseListOfPropertyValues(String listOfItems) {
		List<PropertyValues> ret = new ArrayList<PropertyValues>();
		String str = removeBrackets(listOfItems);
		DataObjectFactoryImpl factory = new DataObjectFactoryImpl();
		StringTokenizer stok = new StringTokenizer(str,
				ParserConstant.SEMICOLON);
		while (stok.hasMoreTokens()) {
			String propertyValuesStr = stok.nextToken().trim();
			int pos = propertyValuesStr.indexOf(ParserConstant.COLON);
			if (pos == -1) {
				PropertyIdValue property = factory.getPropertyIdValue(
						propertyValuesStr,
						ConstraintMainParser.DEFAULT_BASE_IRI);
				ret.add(new PropertyValues(property));
			} else {
				PropertyIdValue property = factory.getPropertyIdValue(
						propertyValuesStr.substring(0, pos).trim(),
						ConstraintMainParser.DEFAULT_BASE_IRI);
				List<ItemIdValue> values = parseListOfItems(propertyValuesStr
						.substring(pos + 1));
				ret.add(new PropertyValues(property, values));
			}
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
	public Constraint parse(Template template) {
		Validate.notNull(template);
		Constraint ret = null;
		String templateId = normalize(template.getId());
		String prefix = normalize(ConstraintParserConstant.T_CONSTRAINT);
		if (templateId.startsWith(prefix)) {
			String constraintId = normalize(templateId.substring(prefix
					.length()));
			ConstraintParser constraintParser = getConstraintParser(constraintId);
			if (constraintParser != null) {
				ret = constraintParser.parse(template);
			}
		}
		return ret;
	}

	public ConstraintParser getConstraintParser(String str) {
		return this.mapOfParsers.get(str);
	}

	public String normalize(String str) {
		String ret = "";
		if (str != null) {
			ret = str.trim().toLowerCase()
					.replace(ParserConstant.UNDERSCORE, ParserConstant.SPACE);
			if (ret.length() > 0) {
				ret = ret.substring(0, 1).toUpperCase() + ret.substring(1);
			}
		}
		return ret;
	}

	private void register(String str, ConstraintParser parser) {
		this.mapOfParsers.put(normalize(str), parser);
	}

	private void registerIds() {
		register(ConstraintParserConstant.C_SINGLE_VALUE,
				new ConstraintSingleValueParser());
		register(ConstraintParserConstant.C_UNIQUE_VALUE,
				new ConstraintUniqueValueParser());
		register(ConstraintParserConstant.C_FORMAT,
				new ConstraintFormatParser());
		register(ConstraintParserConstant.C_ONE_OF, new ConstraintOneOfParser());
		register(ConstraintParserConstant.C_SYMMETRIC,
				new ConstraintSymmetricParser());
		register(ConstraintParserConstant.C_INVERSE,
				new ConstraintInverseParser());
		register(ConstraintParserConstant.C_EXISTING_FILE,
				new ConstraintExistingFileParser());
		register(ConstraintParserConstant.C_TARGET_REQUIRED_CLAIM,
				new ConstraintTargetRequiredClaimParser());
		register(ConstraintParserConstant.C_ITEM, new ConstraintItemParser());
		register(ConstraintParserConstant.C_TYPE, new ConstraintTypeParser());
		register(ConstraintParserConstant.C_VALUE_TYPE,
				new ConstraintValueTypeParser());
		register(ConstraintParserConstant.C_RANGE, new ConstraintRangeParser());
		register(ConstraintParserConstant.C_MULTI_VALUE,
				new ConstraintMultiValueParser());
		register(ConstraintParserConstant.C_CONFLICTS_WITH,
				new ConstraintConflictsWithParser());
	}

}
