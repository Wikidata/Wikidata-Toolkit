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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
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

	final BufferedWriter writer;

	public Owl2FunctionalRendererFormat(OutputStream output) {
		this.writer = new BufferedWriter(new OutputStreamWriter(output));
	}

	public Owl2FunctionalRendererFormat(Writer writer) {
		this.writer = new BufferedWriter(writer);
	}

	private void add(BNode bnode) {
		try {
			this.writer.write(bnode.toString());
			this.writer.newLine();
			this.writer.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String addQuotationMarks(String value) {
		return Owl2FunctionalConstant.C_QUOTATION_MARK + value
				+ Owl2FunctionalConstant.C_QUOTATION_MARK;
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
		add(new StringBNode(Owl2FunctionalConstant.OWL_START));
	}

	@Override
	public void finish() {
		add(new StringBNode(Owl2FunctionalConstant.OWL_END));
		this.declaredEntities.clear();
	}

	@Override
	public URI getItem(ItemIdValue item) {
		return factory.createURI(Owl2FunctionalConstant.C_LT + item.getIri()
				+ Owl2FunctionalConstant.C_GT);
	}

	@Override
	public URI getProperty(PropertyIdValue property) {
		return factory.createURI(Owl2FunctionalConstant.C_LT
				+ property.getIri() + Owl2FunctionalConstant.C_GT);
	}

	@Override
	public URI getPs(PropertyIdValue property) {
		return factory.createURI(Owl2FunctionalConstant.C_LT
				+ Vocabulary
						.getPropertyUri(property, PropertyContext.STATEMENT)
				+ Owl2FunctionalConstant.C_GT);
	}

	@Override
	public URI getPv(PropertyIdValue property) {
		return factory.createURI(Owl2FunctionalConstant.C_LT
				+ Vocabulary.getPropertyUri(property, PropertyContext.VALUE)
				+ Owl2FunctionalConstant.C_GT);
	}

	@Override
	public URI getRp(PropertyIdValue property) {
		return factory.createURI(Owl2FunctionalConstant.C_LT
				+ property.getIri() + Owl2FunctionalConstant.AUX
				+ Owl2FunctionalConstant.C_GT);
	}

	@Override
	public URI wbTimeValue() {
		return factory.createURI(Owl2FunctionalConstant.C_LT
				+ Owl2FunctionalConstant.WB_TIME_VALUE
				+ Owl2FunctionalConstant.C_GT);
	}

	@Override
	public URI wbQuantityValue() {
		return factory.createURI(Owl2FunctionalConstant.C_LT
				+ Owl2FunctionalConstant.WB_QUANTITY_VALUE
				+ Owl2FunctionalConstant.C_GT);
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
	public BNode getDataSomeValuesFrom(URI dataPropertyExpression,
			Resource dataRange) {
		return makeFunction(Owl2FunctionalConstant.DATA_SOME_VALUES_FROM,
				makePair(dataPropertyExpression, dataRange));
	}

	@Override
	public BNode getDatatypeRestriction(URI datatype, URI constrainingFacet,
			Resource restrictionValue) {
		return makeFunction(
				Owl2FunctionalConstant.DATATYPE_RESTRICTION,
				makePair(
						datatype,
						makePair(constrainingFacet,
								getLiteral(restrictionValue, datatype))));
	}

	BNode getLiteral(Resource value, Resource type) {
		return new StringBNode(addQuotationMarks(value.stringValue())
				+ Owl2FunctionalConstant.C_CARET
				+ Owl2FunctionalConstant.C_CARET + type);
	}

	@Override
	public BNode getObjectComplementOf(Resource classExpression) {
		return makeFunction(Owl2FunctionalConstant.OBJECT_COMPLEMENT_OF,
				classExpression);
	}

	@Override
	public BNode getObjectExactCardinality(int nonNegativeInteger,
			Resource objectPropertyExpression) {
		return makeFunction(
				Owl2FunctionalConstant.OBJECT_EXACT_CARDINALITY,
				makePair(new StringResource("" + nonNegativeInteger),
						objectPropertyExpression));
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
	public BNode getObjectSomeValuesFrom(Resource objectPropertyExpression,
			Resource classExpression) {
		return makeFunction(Owl2FunctionalConstant.OBJECT_SOME_VALUES_FROM,
				makePair(objectPropertyExpression, classExpression));
	}

	@Override
	public BNode getObjectUnionOf(Resource classExpression0,
			Resource classExpression1) {
		return makeFunction(Owl2FunctionalConstant.OBJECT_UNION_OF,
				makePair(classExpression0, classExpression1));
	}

	@Override
	public boolean addAnnotationAssertionComment(URI annotationSubject,
			String annotationValue) {
		BNode bnode = makeFunction(
				Owl2FunctionalConstant.ANNOTATION_ASSERTION,
				makePair(
						new StringResource(Owl2FunctionalConstant.RDFS_COMMENT),
						makePair(annotationSubject,
								addQuotationMarks(annotationValue))));
		add(bnode);
		return true;
	}

	@Override
	public boolean addDataPropertyRange(URI dataPropertyExpression,
			Resource dataRange) {
		BNode bnode = makeFunction(Owl2FunctionalConstant.DATA_PROPERTY_RANGE,
				makePair(dataPropertyExpression, dataRange));
		add(bnode);
		return true;
	}

	@Override
	public boolean addDatatypeDefinition(URI dataPropertyExpression,
			Resource dataRange) {
		BNode bnode = makeFunction(Owl2FunctionalConstant.DATATYPE_DEFINITION,
				makePair(dataPropertyExpression, dataRange));
		add(bnode);
		return true;
	}

	@Override
	public boolean addDeclarationAnnotationProperty(URI datatype) {
		if (this.declaredEntities.contains(datatype)) {
			return false;
		}
		BNode bnode = makeFunction(
				Owl2FunctionalConstant.DECLARATION,
				makeFunction(Owl2FunctionalConstant.ANNOTATION_PROPERTY,
						datatype));
		add(bnode);
		this.declaredEntities.add(datatype);
		return true;
	}

	@Override
	public boolean addDeclarationClass(URI clss) {
		if (this.declaredEntities.contains(clss)) {
			return false;
		}
		BNode bnode = makeFunction(Owl2FunctionalConstant.DECLARATION,
				makeFunction(Owl2FunctionalConstant.CLASS, clss));
		add(bnode);
		this.declaredEntities.add(clss);
		return true;
	}

	@Override
	public boolean addDeclarationDatatype(URI datatype) {
		if (this.declaredEntities.contains(datatype)) {
			return false;
		}
		BNode bnode = makeFunction(Owl2FunctionalConstant.DECLARATION,
				makeFunction(Owl2FunctionalConstant.DATATYPE, datatype));
		add(bnode);
		this.declaredEntities.add(datatype);
		return true;
	}

	@Override
	public boolean addDeclarationDatatypeProperty(URI datatypeProperty) {
		if (this.declaredEntities.contains(datatypeProperty)) {
			return false;
		}
		BNode bnode = makeFunction(
				Owl2FunctionalConstant.DECLARATION,
				makeFunction(Owl2FunctionalConstant.DATATYPE_PROPERTY,
						datatypeProperty));
		add(bnode);
		this.declaredEntities.add(datatypeProperty);
		return true;
	}

	@Override
	public boolean addDeclarationNamedIndividual(URI namedIndividual) {
		if (this.declaredEntities.contains(namedIndividual)) {
			return false;
		}
		BNode bnode = makeFunction(
				Owl2FunctionalConstant.DECLARATION,
				makeFunction(Owl2FunctionalConstant.NAMED_INDIVIDUAL,
						namedIndividual));
		add(bnode);
		this.declaredEntities.add(namedIndividual);
		return true;
	}

	@Override
	public boolean addDeclarationObjectProperty(URI objectProperty) {
		if (this.declaredEntities.contains(objectProperty)) {
			return false;
		}
		BNode bnode = makeFunction(
				Owl2FunctionalConstant.DECLARATION,
				makeFunction(Owl2FunctionalConstant.OBJECT_PROPERTY,
						objectProperty));
		add(bnode);
		this.declaredEntities.add(objectProperty);
		return true;
	}

	@Override
	public boolean addDisjointClasses(Resource classExpression0,
			Resource classExpression1) {
		BNode bnode = makeFunction(Owl2FunctionalConstant.DISJOINT_CLASSES,
				makePair(classExpression0, classExpression1));
		add(bnode);
		return true;
	}

	@Override
	public boolean addFunctionalObjectProperty(Resource objectPropertyExpression) {
		BNode bnode = makeFunction(
				Owl2FunctionalConstant.FUNCTIONAL_OBJECT_PROPERTY,
				objectPropertyExpression);
		add(bnode);
		return true;
	}

	@Override
	public boolean addHasKey(Resource classExpression,
			Resource objectPropertyExpression) {
		BNode bnode = makeFunction(
				Owl2FunctionalConstant.HAS_KEY,
				makePair(
						classExpression,
						makePair(makeFunction("", objectPropertyExpression),
								makeFunction("", ""))));
		add(bnode);
		return true;
	}

	@Override
	public boolean addInverseFunctionalObjectProperty(
			Resource objectPropertyExpression) {
		BNode bnode = makeFunction(
				Owl2FunctionalConstant.INVERSE_FUNCTIONAL_OBJECT_PROPERTY,
				objectPropertyExpression);
		add(bnode);
		return true;
	}

	@Override
	public boolean addObjectPropertyDomain(Resource objectPropertyExpression,
			Resource classExpression) {
		BNode bnode = makeFunction(
				Owl2FunctionalConstant.OBJECT_PROPERTY_DOMAIN,
				makePair(objectPropertyExpression, classExpression));
		add(bnode);
		return true;
	}

	@Override
	public boolean addObjectPropertyRange(Resource objectPropertyExpression,
			Resource classExpression) {
		BNode bnode = makeFunction(
				Owl2FunctionalConstant.OBJECT_PROPERTY_RANGE,
				makePair(objectPropertyExpression, classExpression));
		add(bnode);
		return true;

	}

	@Override
	public boolean addSubClassOf(Resource subClassExpression,
			Resource superClassExpression) {
		BNode bnode = makeFunction(Owl2FunctionalConstant.SUB_CLASS_OF,
				makePair(subClassExpression, superClassExpression));
		add(bnode);
		return true;
	}

}
