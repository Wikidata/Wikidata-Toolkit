package org.wikidata.wdtk.dumpfiles.renderer.constraint;

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
public class OWLSymbolFactory {

	public String a_s(PropertyIdValue property) {
		return Vocabulary.getPropertyUri(property, PropertyContext.STATEMENT);
	}

	public String a_v(PropertyIdValue property) {
		return Vocabulary.getPropertyUri(property, PropertyContext.VALUE);
	}

	public String aItem(ItemIdValue item) {
		return item.getIri();
	}

	public String aRp(PropertyIdValue property) {
		return property.getIri() + "aux";
	}

	private String makeFunction(String object, String arg) {
		return object + ConstraintRendererConstant.C_PAR_A
				+ ConstraintRendererConstant.C_SPACE + arg
				+ ConstraintRendererConstant.C_SPACE
				+ ConstraintRendererConstant.C_PAR_B;
	}

	public String makePair(String arg0, String arg1) {
		return arg0 + ConstraintRendererConstant.C_SPACE + arg1;
	}

	public String makeList(List<ItemIdValue> list) {
		StringBuilder ret = new StringBuilder();
		for (ItemIdValue q : list) {
			ret.append(aItem(q));
			ret.append(ConstraintRendererConstant.C_SPACE);
		}
		return ret.toString();
	}

	public String aDataIntersectionOf(String arg0, String arg1) {
		return makeFunction(ConstraintRendererConstant.DATA_INTERSECTION_OF,
				makePair(arg0, arg1));
	}

	public String aDataPropertyRange(String arg0, String arg1) {
		return makeFunction(ConstraintRendererConstant.DATA_PROPERTY_RANGE,
				makePair(arg0, arg1));
	}

	public String aDataSomeValuesFrom(String arg0, String arg1) {
		return makeFunction(ConstraintRendererConstant.DATA_SOME_VALUES_FROM,
				makePair(arg0, arg1));
	}

	public String aDatatype(String arg) {
		return makeFunction(ConstraintRendererConstant.DATATYPE, arg);
	}

	public String aDatatypeDefinition(String arg0, String arg1) {
		return makeFunction(ConstraintRendererConstant.DATATYPE_DEFINITION,
				makePair(arg0, arg1));
	}

	public String aDatatypeRestriction(String arg0, String arg1, String arg2) {
		return makeFunction(ConstraintRendererConstant.DATATYPE_RESTRICTION,
				makePair(arg0, makePair(arg1, arg2)));
	}

	public String aDeclaration(String arg) {
		return makeFunction(ConstraintRendererConstant.DECLARATION, arg);
	}

	public String aDisjointClasses(String arg0, String arg1) {
		return makeFunction(ConstraintRendererConstant.DISJOINT_CLASSES,
				makePair(arg0, arg1));
	}

	public String aFunctionalObjectProperty(String arg) {
		return makeFunction(
				ConstraintRendererConstant.FUNCTIONAL_OBJECT_PROPERTY, arg);
	}

	public String aHasKey(String arg0, String arg1, String arg2) {
		return makeFunction(
				ConstraintRendererConstant.HAS_KEY,
				makePair(
						arg0,
						makePair(makeFunction("", arg1), makeFunction("", arg2))));
	}

	public String aInverseFunctionalObjectProperty(String arg) {
		return makeFunction(
				ConstraintRendererConstant.INVERSE_FUNCTIONAL_OBJECT_PROPERTY, arg);
	}

	public String aLiteral(String value, String type) {
		return ConstraintRendererConstant.C_QUOTATION_MARK + value
				+ ConstraintRendererConstant.C_QUOTATION_MARK
				+ ConstraintRendererConstant.C_CARET
				+ ConstraintRendererConstant.C_CARET + type;
	}

	public String aObjectComplementOf(String arg) {
		return makeFunction(ConstraintRendererConstant.OBJECT_COMPLEMENT_OF, arg);
	}

	public String aObjectExactCardinality(String arg0, String arg1) {
		return makeFunction(ConstraintRendererConstant.OBJECT_EXACT_CARDINALITY,
				makePair(arg0, arg1));
	}

	public String aObjectOneOf(ItemIdValue q) {
		return makeFunction(ConstraintRendererConstant.OBJECT_ONE_OF, aItem(q));
	}

	public String aObjectOneOf(List<ItemIdValue> list) {
		return makeFunction(ConstraintRendererConstant.OBJECT_ONE_OF,
				makeList(list));
	}

	public String aObjectPropertyDomain(String arg0, String arg1) {
		return makeFunction(ConstraintRendererConstant.OBJECT_PROPERTY_DOMAIN,
				makePair(arg0, arg1));
	}

	public String aObjectPropertyRange(String arg0, String arg1) {
		return makeFunction(ConstraintRendererConstant.OBJECT_PROPERTY_RANGE,
				makePair(arg0, arg1));
	}

	public String aObjectSomeValuesFrom(String arg0, String arg1) {
		return makeFunction(ConstraintRendererConstant.OBJECT_SOME_VALUES_FROM,
				makePair(arg0, arg1));
	}

	public String aObjectUnionOf(String arg0, String arg1) {
		return makeFunction(ConstraintRendererConstant.OBJECT_UNION_OF,
				makePair(arg0, arg1));
	}

	public String aSubClassOf(String arg0, String arg1) {
		return makeFunction(ConstraintRendererConstant.SUB_CLASS_OF,
				makePair(arg0, arg1));
	}

	public String owlThing() {
		return ConstraintRendererConstant.OWL_THING;
	}

	public String xsdDateTime() {
		return ConstraintRendererConstant.XSD_DATE_TIME;
	}

	public String xsdDecimal() {
		return ConstraintRendererConstant.XSD_DECIMAL;
	}

	public String xsdMaxInclusive() {
		return ConstraintRendererConstant.XSD_MAX_INCLUSIVE;
	}

	public String xsdMinInclusive() {
		return ConstraintRendererConstant.XSD_MIN_INCLUSIVE;
	}

	public String xsdPattern() {
		return ConstraintRendererConstant.XSD_PATTERN;
	}

	public String xsdString() {
		return ConstraintRendererConstant.XSD_STRING;
	}

}
