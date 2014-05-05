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

import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;

public class OWLSymbolFactory {

	public String aPs(PropertyIdValue property) {
		return "ps";
	}

	public String aPv(PropertyIdValue property) {
		return "pv";
	}

	public String aPa(PropertyIdValue property) {
		return "pa";
	}

	private String parentheses(String object, String arg) {
		return object + ConstraintRendererConstant.C_PAR_A
				+ ConstraintRendererConstant.C_SPACE + arg
				+ ConstraintRendererConstant.C_SPACE
				+ ConstraintRendererConstant.C_PAR_B;
	}

	public String pair(String arg0, String arg1) {
		return arg0 + ConstraintRendererConstant.C_SPACE + arg1;
	}

	public String aDatatype(String arg) {
		return parentheses(ConstraintRendererConstant.Datatype, arg);
	}

	public String aDatatypeDefinition(String arg) {
		return parentheses(ConstraintRendererConstant.DatatypeDefinition, arg);
	}

	public String aDatatypeRestriction(String arg) {
		return parentheses(ConstraintRendererConstant.DatatypeRestriction, arg);
	}

	public String aDeclaration(String arg) {
		return parentheses(ConstraintRendererConstant.Declaration, arg);
	}

	public String aFunctionalObjectProperty(String arg) {
		return parentheses(ConstraintRendererConstant.FunctionalObjectProperty,
				arg);
	}

	public String aHasKey(String arg0, String arg1) {
		return parentheses(ConstraintRendererConstant.HasKey, pair(arg0, arg1));
	}

	public String aInverseFunctionalObjectProperty(String arg) {
		return parentheses(
				ConstraintRendererConstant.InverseFunctionalObjectProperty, arg);
	}

	public String aObjectComplementOf(String arg) {
		return parentheses(ConstraintRendererConstant.ObjectComplementOf, arg);
	}

	public String aObjectExactCardinality(String arg0, String arg1) {
		return parentheses(ConstraintRendererConstant.ObjectExactCardinality,
				pair(arg0, arg1));
	}

	public String aObjectPropertyDomain(String arg0, String arg1) {
		return parentheses(ConstraintRendererConstant.ObjectPropertyDomain,
				pair(arg0, arg1));
	}

	public String aObjectPropertyRange(String arg0, String arg1) {
		return parentheses(ConstraintRendererConstant.ObjectPropertyRange,
				pair(arg0, arg1));
	}

	public String aObjectSomeValuesFrom(String arg0, String arg1) {
		return parentheses(ConstraintRendererConstant.ObjectSomeValuesFrom,
				pair(arg0, arg1));
	}

	public String aObjectUnionOf(String arg0, String arg1) {
		return parentheses(ConstraintRendererConstant.ObjectUnionOf,
				pair(arg0, arg1));
	}

	public String aSubClassOf(String arg0, String arg1) {
		return parentheses(ConstraintRendererConstant.SubClassOf,
				pair(arg0, arg1));
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
