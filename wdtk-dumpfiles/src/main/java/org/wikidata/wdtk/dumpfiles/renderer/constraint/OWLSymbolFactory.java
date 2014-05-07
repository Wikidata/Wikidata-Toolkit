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

/**
 * 
 * @author Julian Mendez
 * 
 */
public class OWLSymbolFactory {

	public String a_s(PropertyIdValue property) {
		return property.toString() + "_s";
	}

	public String a_v(PropertyIdValue property) {
		return property.toString() + "_v";
	}

	public String aItem(ItemIdValue item) {
		return item.toString();
	}

	public String aRp(PropertyIdValue property) {
		return property.toString() + "rp";
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
		return makeFunction(ConstraintRendererConstant.DataIntersectionOf,
				makePair(arg0, arg1));
	}

	public String aDataPropertyRange(String arg0, String arg1) {
		return makeFunction(ConstraintRendererConstant.DataPropertyRange,
				makePair(arg0, arg1));
	}

	public String aDataSomeValuesFrom(String arg0, String arg1) {
		return makeFunction(ConstraintRendererConstant.DataSomeValuesFrom,
				makePair(arg0, arg1));
	}

	public String aDatatype(String arg) {
		return makeFunction(ConstraintRendererConstant.Datatype, arg);
	}

	public String aDatatypeDefinition(String arg0, String arg1) {
		return makeFunction(ConstraintRendererConstant.DatatypeDefinition,
				makePair(arg0, arg1));
	}

	public String aDatatypeRestriction(String arg0, String arg1, String arg2) {
		return makeFunction(ConstraintRendererConstant.DatatypeRestriction,
				makePair(arg0, makePair(arg1, arg2)));
	}

	public String aDeclaration(String arg) {
		return makeFunction(ConstraintRendererConstant.Declaration, arg);
	}

	public String aDisjointClasses(String arg0, String arg1) {
		return makeFunction(ConstraintRendererConstant.DisjointClasses,
				makePair(arg0, arg1));
	}

	public String aFunctionalObjectProperty(String arg) {
		return makeFunction(
				ConstraintRendererConstant.FunctionalObjectProperty, arg);
	}

	public String aHasKey(String arg0, String arg1, String arg2) {
		return makeFunction(
				ConstraintRendererConstant.HasKey,
				makePair(
						arg0,
						makePair(makeFunction("", arg1), makeFunction("", arg2))));
	}

	public String aInverseFunctionalObjectProperty(String arg) {
		return makeFunction(
				ConstraintRendererConstant.InverseFunctionalObjectProperty, arg);
	}

	public String aLiteral(String value, String type) {
		return ConstraintRendererConstant.C_QUOTATION_MARK + value
				+ ConstraintRendererConstant.C_QUOTATION_MARK
				+ ConstraintRendererConstant.C_CARET
				+ ConstraintRendererConstant.C_CARET + type;
	}

	public String aObjectComplementOf(String arg) {
		return makeFunction(ConstraintRendererConstant.ObjectComplementOf, arg);
	}

	public String aObjectExactCardinality(String arg0, String arg1) {
		return makeFunction(ConstraintRendererConstant.ObjectExactCardinality,
				makePair(arg0, arg1));
	}

	public String aObjectOneOf(ItemIdValue q) {
		return makeFunction(ConstraintRendererConstant.ObjectOneOf, aItem(q));
	}

	public String aObjectOneOf(List<ItemIdValue> list) {
		return makeFunction(ConstraintRendererConstant.ObjectOneOf,
				makeList(list));
	}

	public String aObjectPropertyDomain(String arg0, String arg1) {
		return makeFunction(ConstraintRendererConstant.ObjectPropertyDomain,
				makePair(arg0, arg1));
	}

	public String aObjectPropertyRange(String arg0, String arg1) {
		return makeFunction(ConstraintRendererConstant.ObjectPropertyRange,
				makePair(arg0, arg1));
	}

	public String aObjectSomeValuesFrom(String arg0, String arg1) {
		return makeFunction(ConstraintRendererConstant.ObjectSomeValuesFrom,
				makePair(arg0, arg1));
	}

	public String aObjectUnionOf(String arg0, String arg1) {
		return makeFunction(ConstraintRendererConstant.ObjectUnionOf,
				makePair(arg0, arg1));
	}

	public String aSubClassOf(String arg0, String arg1) {
		return makeFunction(ConstraintRendererConstant.SubClassOf,
				makePair(arg0, arg1));
	}

	public String owlThing() {
		return ConstraintRendererConstant.owl_Thing;
	}

	public String xsdDateTime() {
		return ConstraintRendererConstant.xsd_dateTime;
	}

	public String xsdDecimal() {
		return ConstraintRendererConstant.xsd_decimal;
	}

	public String xsdMaxInclusive() {
		return ConstraintRendererConstant.xsd_maxInclusive;
	}

	public String xsdMinInclusive() {
		return ConstraintRendererConstant.xsd_minInclusive;
	}

	public String xsdPattern() {
		return ConstraintRendererConstant.xsd_pattern;
	}

	public String xsdString() {
		return ConstraintRendererConstant.xsd_string;
	}

}
