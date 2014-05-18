package org.wikidata.wdtk.dumpfiles.renderer.format;

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

import java.util.List;

import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.rdf.PropertyContext;
import org.wikidata.wdtk.rdf.Vocabulary;

/**
 * 
 * @author Julian Mendez
 * 
 */
public class Owl2FunctionalRendererFormat implements RendererFormat {

	public Owl2FunctionalRendererFormat() {
	}

	@Override
	public String a_s(PropertyIdValue property) {
		return Owl2FunctionalConstant.C_LT
				+ Vocabulary
						.getPropertyUri(property, PropertyContext.STATEMENT)
				+ Owl2FunctionalConstant.C_GT;
	}

	@Override
	public String a_v(PropertyIdValue property) {
		return Owl2FunctionalConstant.C_LT
				+ Vocabulary.getPropertyUri(property, PropertyContext.VALUE)
				+ Owl2FunctionalConstant.C_GT;
	}

	@Override
	public String aItem(ItemIdValue item) {
		return Owl2FunctionalConstant.C_LT + item.getIri()
				+ Owl2FunctionalConstant.C_GT;
	}

	@Override
	public String aRp(PropertyIdValue property) {
		return Owl2FunctionalConstant.C_LT + property.getIri() + "aux"
				+ Owl2FunctionalConstant.C_GT;
	}

	private String makeFunction(String object, String arg) {
		return object + Owl2FunctionalConstant.C_PAR_A
				+ Owl2FunctionalConstant.C_SPACE + arg
				+ Owl2FunctionalConstant.C_SPACE
				+ Owl2FunctionalConstant.C_PAR_B;
	}

	@Override
	public String makePair(String arg0, String arg1) {
		return arg0 + Owl2FunctionalConstant.C_SPACE + arg1;
	}

	@Override
	public String makeList(List<ItemIdValue> list) {
		StringBuilder ret = new StringBuilder();
		for (ItemIdValue q : list) {
			ret.append(aItem(q));
			ret.append(Owl2FunctionalConstant.C_SPACE);
		}
		return ret.toString();
	}

	@Override
	public String aDataIntersectionOf(String arg0, String arg1) {
		return makeFunction(Owl2FunctionalConstant.DATA_INTERSECTION_OF,
				makePair(arg0, arg1));
	}

	@Override
	public String aDataPropertyRange(String arg0, String arg1) {
		return makeFunction(Owl2FunctionalConstant.DATA_PROPERTY_RANGE,
				makePair(arg0, arg1));
	}

	@Override
	public String aDataSomeValuesFrom(String arg0, String arg1) {
		return makeFunction(Owl2FunctionalConstant.DATA_SOME_VALUES_FROM,
				makePair(arg0, arg1));
	}

	@Override
	public String aDatatype(String arg) {
		return makeFunction(Owl2FunctionalConstant.DATATYPE, arg);
	}

	@Override
	public String aDatatypeDefinition(String arg0, String arg1) {
		return makeFunction(Owl2FunctionalConstant.DATATYPE_DEFINITION,
				makePair(arg0, arg1));
	}

	@Override
	public String aDatatypeRestriction(String arg0, String arg1, String arg2) {
		return makeFunction(Owl2FunctionalConstant.DATATYPE_RESTRICTION,
				makePair(arg0, makePair(arg1, arg2)));
	}

	@Override
	public String aDeclaration(String arg) {
		return makeFunction(Owl2FunctionalConstant.DECLARATION, arg);
	}

	@Override
	public String aDisjointClasses(String arg0, String arg1) {
		return makeFunction(Owl2FunctionalConstant.DISJOINT_CLASSES,
				makePair(arg0, arg1));
	}

	@Override
	public String aFunctionalObjectProperty(String arg) {
		return makeFunction(Owl2FunctionalConstant.FUNCTIONAL_OBJECT_PROPERTY,
				arg);
	}

	@Override
	public String aHasKey(String arg0, String arg1, String arg2) {
		return makeFunction(
				Owl2FunctionalConstant.HAS_KEY,
				makePair(
						arg0,
						makePair(makeFunction("", arg1), makeFunction("", arg2))));
	}

	@Override
	public String aInverseFunctionalObjectProperty(String arg) {
		return makeFunction(
				Owl2FunctionalConstant.INVERSE_FUNCTIONAL_OBJECT_PROPERTY, arg);
	}

	@Override
	public String aLiteral(String value, String type) {
		return Owl2FunctionalConstant.C_QUOTATION_MARK + value
				+ Owl2FunctionalConstant.C_QUOTATION_MARK
				+ Owl2FunctionalConstant.C_CARET
				+ Owl2FunctionalConstant.C_CARET + type;
	}

	@Override
	public String aObjectComplementOf(String arg) {
		return makeFunction(Owl2FunctionalConstant.OBJECT_COMPLEMENT_OF, arg);
	}

	@Override
	public String aObjectExactCardinality(String arg0, String arg1) {
		return makeFunction(Owl2FunctionalConstant.OBJECT_EXACT_CARDINALITY,
				makePair(arg0, arg1));
	}

	@Override
	public String aObjectOneOf(ItemIdValue q) {
		return makeFunction(Owl2FunctionalConstant.OBJECT_ONE_OF, aItem(q));
	}

	@Override
	public String aObjectOneOf(List<ItemIdValue> list) {
		return makeFunction(Owl2FunctionalConstant.OBJECT_ONE_OF,
				makeList(list));
	}

	@Override
	public String aObjectPropertyDomain(String arg0, String arg1) {
		return makeFunction(Owl2FunctionalConstant.OBJECT_PROPERTY_DOMAIN,
				makePair(arg0, arg1));
	}

	@Override
	public String aObjectPropertyRange(String arg0, String arg1) {
		return makeFunction(Owl2FunctionalConstant.OBJECT_PROPERTY_RANGE,
				makePair(arg0, arg1));
	}

	@Override
	public String aObjectSomeValuesFrom(String arg0, String arg1) {
		return makeFunction(Owl2FunctionalConstant.OBJECT_SOME_VALUES_FROM,
				makePair(arg0, arg1));
	}

	@Override
	public String aObjectUnionOf(String arg0, String arg1) {
		return makeFunction(Owl2FunctionalConstant.OBJECT_UNION_OF,
				makePair(arg0, arg1));
	}

	@Override
	public String aSubClassOf(String arg0, String arg1) {
		return makeFunction(Owl2FunctionalConstant.SUB_CLASS_OF,
				makePair(arg0, arg1));
	}

	@Override
	public String owlThing() {
		return Owl2FunctionalConstant.OWL_THING;
	}

	@Override
	public String xsdDateTime() {
		return Owl2FunctionalConstant.XSD_DATE_TIME;
	}

	@Override
	public String xsdDecimal() {
		return Owl2FunctionalConstant.XSD_DECIMAL;
	}

	@Override
	public String xsdMaxInclusive() {
		return Owl2FunctionalConstant.XSD_MAX_INCLUSIVE;
	}

	@Override
	public String xsdMinInclusive() {
		return Owl2FunctionalConstant.XSD_MIN_INCLUSIVE;
	}

	@Override
	public String xsdPattern() {
		return Owl2FunctionalConstant.XSD_PATTERN;
	}

	@Override
	public String xsdString() {
		return Owl2FunctionalConstant.XSD_STRING;
	}

}
