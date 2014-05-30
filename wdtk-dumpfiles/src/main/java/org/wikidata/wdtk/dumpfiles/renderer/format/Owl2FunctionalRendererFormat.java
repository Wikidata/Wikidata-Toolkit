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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	final Set<Resource> declaredEntities = new HashSet<Resource>();

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

	private String makeList(List<Resource> list) {
		StringBuilder ret = new StringBuilder();
		for (Resource q : list) {
			ret.append(q.stringValue());
			ret.append(Owl2FunctionalConstant.C_SPACE);
		}
		return ret.toString();
	}

	@Override
	public void start() {
		this.model.add(Owl2FunctionalConstant.OWL_START);
	}

	@Override
	public void finish() {
		this.model.add(Owl2FunctionalConstant.OWL_END);
		this.declaredEntities.clear();
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
	public BNode getDataIntersectionOf(Resource dataRange0, Resource dataRange1) {
		return makeFunction(Owl2FunctionalConstant.DATA_INTERSECTION_OF,
				makePair(dataRange0, dataRange1));
	}

	@Override
	public BNode getDataSomeValuesFrom(Resource dataProperty, Resource dataRange) {
		return makeFunction(Owl2FunctionalConstant.DATA_SOME_VALUES_FROM,
				makePair(dataProperty, dataRange));
	}

	@Override
	public BNode getDatatype(Resource arg) {
		return makeFunction(Owl2FunctionalConstant.DATATYPE, arg);
	}

	@Override
	public BNode getDatatypeRestriction(Resource dataType, Resource facet,
			Resource value) {
		return makeFunction(Owl2FunctionalConstant.DATATYPE_RESTRICTION,
				makePair(dataType, makePair(facet, value)));
	}

	@Override
	public BNode getLiteral(Resource value, Resource type) {
		return new StringBNode(Owl2FunctionalConstant.C_QUOTATION_MARK + value
				+ Owl2FunctionalConstant.C_QUOTATION_MARK
				+ Owl2FunctionalConstant.C_CARET
				+ Owl2FunctionalConstant.C_CARET + type);
	}

	@Override
	public BNode getObjectComplementOf(Resource clss) {
		return makeFunction(Owl2FunctionalConstant.OBJECT_COMPLEMENT_OF, clss);
	}

	@Override
	public BNode getObjectExactCardinality(int cardinality,
			Resource objectProperty) {
		return makeFunction(Owl2FunctionalConstant.OBJECT_EXACT_CARDINALITY,
				makePair(new StringResource("" + cardinality), objectProperty));
	}

	@Override
	public BNode getObjectOneOf(Resource individual) {
		return makeFunction(Owl2FunctionalConstant.OBJECT_ONE_OF, individual);
	}

	@Override
	public BNode getObjectOneOf(List<Resource> listOfIndividuals) {
		return makeFunction(Owl2FunctionalConstant.OBJECT_ONE_OF,
				makeList(listOfIndividuals));
	}

	@Override
	public BNode getObjectSomeValuesFrom(Resource property, Resource clss) {
		return makeFunction(Owl2FunctionalConstant.OBJECT_SOME_VALUES_FROM,
				makePair(property, clss));
	}

	@Override
	public BNode getObjectUnionOf(Resource class0, Resource class1) {
		return makeFunction(Owl2FunctionalConstant.OBJECT_UNION_OF,
				makePair(class0, class1));
	}

	@Override
	public boolean addAnnotationAssertionComment(Resource subject, String value) {
		StringBuilder sb = new StringBuilder();
		sb.append(Owl2FunctionalConstant.ANNOTATION_ASSERTION_A);
		sb.append(subject);
		sb.append(Owl2FunctionalConstant.ANNOTATION_ASSERTION_B);
		sb.append(value);
		sb.append(Owl2FunctionalConstant.ANNOTATION_ASSERTION_C);
		this.model.add(sb.toString());
		return true;
	}

	@Override
	public boolean addDatatypeDefinition(Resource dataProperty,
			Resource dataRange) {
		BNode bnode = makeFunction(Owl2FunctionalConstant.DATATYPE_DEFINITION,
				makePair(dataProperty, dataRange));
		this.model.add(bnode.toString());
		return true;
	}

	@Override
	public boolean addDeclarationAnnotationProperty(Resource entity) {
		if (this.declaredEntities.contains(entity)) {
			return false;
		}
		BNode bnode = makeFunction(
				Owl2FunctionalConstant.DECLARATION,
				makeFunction(Owl2FunctionalConstant.ANNOTATION_PROPERTY, entity));
		this.model.add(bnode.toString());
		this.declaredEntities.add(entity);
		return true;
	}

	@Override
	public boolean addDeclarationClass(Resource entity) {
		if (this.declaredEntities.contains(entity)) {
			return false;
		}
		BNode bnode = makeFunction(Owl2FunctionalConstant.DECLARATION,
				makeFunction(Owl2FunctionalConstant.CLASS, entity));
		this.model.add(bnode.toString());
		this.declaredEntities.add(entity);
		return true;
	}

	@Override
	public boolean addDeclarationDatatype(Resource entity) {
		if (this.declaredEntities.contains(entity)) {
			return false;
		}
		BNode bnode = makeFunction(Owl2FunctionalConstant.DECLARATION,
				makeFunction(Owl2FunctionalConstant.DATATYPE, entity));
		this.model.add(bnode.toString());
		this.declaredEntities.add(entity);
		return true;
	}

	@Override
	public boolean addDeclarationDatatypeProperty(Resource entity) {
		if (this.declaredEntities.contains(entity)) {
			return false;
		}
		BNode bnode = makeFunction(Owl2FunctionalConstant.DECLARATION,
				makeFunction(Owl2FunctionalConstant.DATATYPE_PROPERTY, entity));
		this.model.add(bnode.toString());
		this.declaredEntities.add(entity);
		return true;
	}

	@Override
	public boolean addDeclarationNamedIndividual(Resource entity) {
		if (this.declaredEntities.contains(entity)) {
			return false;
		}
		BNode bnode = makeFunction(Owl2FunctionalConstant.DECLARATION,
				makeFunction(Owl2FunctionalConstant.NAMED_INDIVIDUAL, entity));
		this.model.add(bnode.toString());
		this.declaredEntities.add(entity);
		return true;
	}

	@Override
	public boolean addDeclarationObjectProperty(Resource entity) {
		if (this.declaredEntities.contains(entity)) {
			return false;
		}
		BNode bnode = makeFunction(Owl2FunctionalConstant.DECLARATION,
				makeFunction(Owl2FunctionalConstant.OBJECT_PROPERTY, entity));
		this.model.add(bnode.toString());
		this.declaredEntities.add(entity);
		return true;
	}

	@Override
	public boolean addDisjointClasses(Resource class0, Resource class1) {
		BNode ret = makeFunction(Owl2FunctionalConstant.DISJOINT_CLASSES,
				makePair(class0, class1));
		this.model.add(ret.toString());
		return true;
	}

	@Override
	public boolean addFunctionalObjectProperty(Resource objectProperty) {
		BNode ret = makeFunction(
				Owl2FunctionalConstant.FUNCTIONAL_OBJECT_PROPERTY,
				objectProperty);
		this.model.add(ret.toString());
		return true;
	}

	@Override
	public boolean addHasKey(Resource clss, Resource objectProperty,
			Resource dataProperty) {
		BNode bnode = makeFunction(
				Owl2FunctionalConstant.HAS_KEY,
				makePair(
						clss,
						makePair(makeFunction("", objectProperty),
								makeFunction("", dataProperty))));
		this.model.add(bnode.toString());
		return true;
	}

	@Override
	public boolean addInverseFunctionalObjectProperty(Resource objectProperty) {
		BNode ret = makeFunction(
				Owl2FunctionalConstant.INVERSE_FUNCTIONAL_OBJECT_PROPERTY,
				objectProperty);
		this.model.add(ret.toString());
		return true;
	}

	@Override
	public boolean addObjectPropertyDomain(Resource objectProperty,
			Resource clss) {
		BNode bnode = makeFunction(
				Owl2FunctionalConstant.OBJECT_PROPERTY_DOMAIN,
				makePair(objectProperty, clss));
		this.model.add(bnode.toString());
		return true;
	}

	@Override
	public boolean addObjectPropertyRange(Resource objectProperty, Resource clss) {
		BNode bnode = makeFunction(
				Owl2FunctionalConstant.OBJECT_PROPERTY_RANGE,
				makePair(objectProperty, clss));
		this.model.add(bnode.toString());
		return true;

	}

	@Override
	public boolean addDataPropertyRange(Resource objectProperty, Resource clss) {
		BNode bnode = makeFunction(Owl2FunctionalConstant.DATA_PROPERTY_RANGE,
				makePair(objectProperty, clss));
		this.model.add(bnode.toString());
		return true;
	}

	@Override
	public boolean addSubClassOf(Resource subClass, Resource superClass) {
		BNode bnode = makeFunction(Owl2FunctionalConstant.SUB_CLASS_OF,
				makePair(subClass, superClass));
		this.model.add(bnode.toString());
		return true;
	}

}
