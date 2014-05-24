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

import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
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

	static final ValueFactory factory = ValueFactoryImpl.getInstance();

	final List<String> model;

	public Owl2FunctionalRendererFormat(List<String> model) {
		this.model = model;
	}

	public List<String> getModel() {
		return this.model;
	}

	private BNode makeFunction(String object, Resource arg) {
		return makeFunction(object, arg.toString());
	}

	private BNode makeFunction(String object, String arg) {
		return new StringBNode(object + Owl2FunctionalConstant.C_PAR_A
				+ Owl2FunctionalConstant.C_SPACE + arg
				+ Owl2FunctionalConstant.C_SPACE
				+ Owl2FunctionalConstant.C_PAR_B);
	}

	private BNode makePair(Resource arg0, Resource arg1) {
		return makePair(arg0, arg1.toString());
	}

	private BNode makePair(Resource arg0, String arg1) {
		return makePair(arg0.toString(), arg1);
	}

	private BNode makePair(String arg0, String arg1) {
		return new StringBNode(arg0 + Owl2FunctionalConstant.C_SPACE + arg1);
	}

	private String makeList(List<ItemIdValue> list) {
		StringBuilder ret = new StringBuilder();
		for (ItemIdValue q : list) {
			ret.append(aItem(q));
			ret.append(Owl2FunctionalConstant.C_SPACE);
		}
		return ret.toString();
	}

	@Override
	public String getStart() {
		return Owl2FunctionalConstant.OWL_START;
	}

	@Override
	public String getEnd() {
		return Owl2FunctionalConstant.OWL_END;
	}

	@Override
	public URI a_s(PropertyIdValue property) {
		return factory.createURI(Owl2FunctionalConstant.C_LT
				+ Vocabulary
						.getPropertyUri(property, PropertyContext.STATEMENT)
				+ Owl2FunctionalConstant.C_GT);
	}

	@Override
	public URI a_v(PropertyIdValue property) {
		return factory.createURI(Owl2FunctionalConstant.C_LT
				+ Vocabulary.getPropertyUri(property, PropertyContext.VALUE)
				+ Owl2FunctionalConstant.C_GT);
	}

	@Override
	public URI aItem(ItemIdValue item) {
		return factory.createURI(Owl2FunctionalConstant.C_LT + item.getIri()
				+ Owl2FunctionalConstant.C_GT);
	}

	@Override
	public URI aRp(PropertyIdValue property) {
		return factory.createURI(Owl2FunctionalConstant.C_LT
				+ property.getIri() + "aux" + Owl2FunctionalConstant.C_GT);
	}

	@Override
	public URI owlThing() {
		return factory.createURI(Owl2FunctionalConstant.OWL_THING);
	}

	@Override
	public URI xsdDateTime() {
		return factory.createURI(Owl2FunctionalConstant.XSD_DATE_TIME);
	}

	@Override
	public URI xsdDecimal() {
		return factory.createURI(Owl2FunctionalConstant.XSD_DECIMAL);
	}

	@Override
	public URI xsdMaxInclusive() {
		return factory.createURI(Owl2FunctionalConstant.XSD_MAX_INCLUSIVE);
	}

	@Override
	public URI xsdMinInclusive() {
		return factory.createURI(Owl2FunctionalConstant.XSD_MIN_INCLUSIVE);
	}

	@Override
	public URI xsdPattern() {
		return factory.createURI(Owl2FunctionalConstant.XSD_PATTERN);
	}

	@Override
	public URI xsdString() {
		return factory.createURI(Owl2FunctionalConstant.XSD_STRING);
	}

	@Override
	public BNode getDataIntersectionOf(Resource arg0, Resource arg1) {
		return makeFunction(Owl2FunctionalConstant.DATA_INTERSECTION_OF,
				makePair(arg0, arg1));
	}

	@Override
	public BNode getDataSomeValuesFrom(Resource arg0, Resource arg1) {
		return makeFunction(Owl2FunctionalConstant.DATA_SOME_VALUES_FROM,
				makePair(arg0, arg1));
	}

	@Override
	public BNode getDatatype(Resource arg) {
		return makeFunction(Owl2FunctionalConstant.DATATYPE, arg);
	}

	@Override
	public BNode getDatatypeRestriction(Resource arg0, Resource arg1,
			Resource arg2) {
		return makeFunction(Owl2FunctionalConstant.DATATYPE_RESTRICTION,
				makePair(arg0, makePair(arg1, arg2)));
	}

	@Override
	public BNode getLiteral(Resource value, Resource type) {
		return new StringBNode(Owl2FunctionalConstant.C_QUOTATION_MARK + value
				+ Owl2FunctionalConstant.C_QUOTATION_MARK
				+ Owl2FunctionalConstant.C_CARET
				+ Owl2FunctionalConstant.C_CARET + type);
	}

	@Override
	public BNode getObjectComplementOf(Resource arg) {
		return makeFunction(Owl2FunctionalConstant.OBJECT_COMPLEMENT_OF, arg);
	}

	@Override
	public BNode getObjectExactCardinality(Resource arg0, Resource arg1) {
		return makeFunction(Owl2FunctionalConstant.OBJECT_EXACT_CARDINALITY,
				makePair(arg0, arg1));
	}

	@Override
	public BNode getObjectOneOf(ItemIdValue q) {
		return makeFunction(Owl2FunctionalConstant.OBJECT_ONE_OF, aItem(q));
	}

	@Override
	public BNode getObjectOneOf(List<ItemIdValue> list) {
		return makeFunction(Owl2FunctionalConstant.OBJECT_ONE_OF,
				makeList(list));
	}

	@Override
	public BNode getObjectSomeValuesFrom(Resource arg0, Resource arg1) {
		return makeFunction(Owl2FunctionalConstant.OBJECT_SOME_VALUES_FROM,
				makePair(arg0, arg1));
	}

	@Override
	public BNode getObjectUnionOf(Resource arg0, Resource arg1) {
		return makeFunction(Owl2FunctionalConstant.OBJECT_UNION_OF,
				makePair(arg0, arg1));
	}

	@Override
	public boolean addAnnotationComment(Resource key, Resource comment) {
		StringBuilder sb = new StringBuilder();
		sb.append(Owl2FunctionalConstant.ANNOTATION_ASSERTION_A);
		sb.append(key);
		sb.append(Owl2FunctionalConstant.ANNOTATION_ASSERTION_B);
		sb.append(comment);
		sb.append(Owl2FunctionalConstant.ANNOTATION_ASSERTION_C);
		this.model.add(sb.toString());
		return true;
	}

	@Override
	public boolean addDatatypeDefinition(Resource arg0, Resource arg1) {
		BNode bnode = makeFunction(Owl2FunctionalConstant.DATATYPE_DEFINITION,
				makePair(arg0, arg1));
		this.model.add(bnode.toString());
		return true;
	}

	@Override
	public boolean addDeclaration(Resource arg) {
		BNode ret = makeFunction(Owl2FunctionalConstant.DECLARATION, arg);
		this.model.add(ret.toString());
		return true;
	}

	@Override
	public boolean addDisjointClasses(Resource arg0, Resource arg1) {
		BNode ret = makeFunction(Owl2FunctionalConstant.DISJOINT_CLASSES,
				makePair(arg0, arg1));
		this.model.add(ret.toString());
		return true;
	}

	@Override
	public boolean addFunctionalObjectProperty(Resource arg) {
		BNode ret = makeFunction(
				Owl2FunctionalConstant.FUNCTIONAL_OBJECT_PROPERTY, arg);
		this.model.add(ret.toString());
		return true;
	}

	@Override
	public boolean addHasKey(Resource arg0, Resource arg1, Resource arg2) {
		BNode bnode = makeFunction(
				Owl2FunctionalConstant.HAS_KEY,
				makePair(
						arg0,
						makePair(makeFunction("", arg1), makeFunction("", arg2))));
		this.model.add(bnode.toString());
		return true;
	}

	@Override
	public boolean addInverseFunctionalObjectProperty(Resource arg) {
		BNode ret = makeFunction(
				Owl2FunctionalConstant.INVERSE_FUNCTIONAL_OBJECT_PROPERTY, arg);
		this.model.add(ret.toString());
		return true;
	}

	@Override
	public boolean addObjectPropertyDomain(Resource arg0, Resource arg1) {
		BNode bnode = makeFunction(
				Owl2FunctionalConstant.OBJECT_PROPERTY_DOMAIN,
				makePair(arg0, arg1));
		this.model.add(bnode.toString());
		return true;
	}

	@Override
	public boolean addObjectPropertyRange(Resource arg0, Resource arg1) {
		BNode bnode = makeFunction(
				Owl2FunctionalConstant.OBJECT_PROPERTY_RANGE,
				makePair(arg0, arg1));
		this.model.add(bnode.toString());
		return true;

	}

	@Override
	public boolean addDataPropertyRange(Resource arg0, Resource arg1) {
		BNode bnode = makeFunction(Owl2FunctionalConstant.DATA_PROPERTY_RANGE,
				makePair(arg0, arg1));
		this.model.add(bnode.toString());
		return true;
	}

	@Override
	public boolean addSubClassOf(Resource arg0, Resource arg1) {
		BNode bnode = makeFunction(Owl2FunctionalConstant.SUB_CLASS_OF,
				makePair(arg0, arg1));
		this.model.add(bnode.toString());
		return true;
	}

}
