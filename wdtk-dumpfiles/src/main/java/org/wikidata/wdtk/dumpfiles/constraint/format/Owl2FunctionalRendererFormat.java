package org.wikidata.wdtk.dumpfiles.constraint.format;

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

	final Set<Resource> inverseFunctionalObjectProperties = new HashSet<Resource>();

	final BufferedWriter writer;

	int auxiliaryEntityCounter = 0;

	public Owl2FunctionalRendererFormat(OutputStream output) {
		this(new OutputStreamWriter(output));
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

	private String escapeBackslash(String value) {
		return value.replaceAll(Owl2FunctionalConstant.C_BACKSLASH
				+ Owl2FunctionalConstant.C_BACKSLASH,
				Owl2FunctionalConstant.C_BACKSLASH
						+ Owl2FunctionalConstant.C_BACKSLASH
						+ Owl2FunctionalConstant.C_BACKSLASH
						+ Owl2FunctionalConstant.C_BACKSLASH);
	}

	private URI createURI(String value) {
		return factory.createURI(Owl2FunctionalConstant.C_LT + value
				+ Owl2FunctionalConstant.C_GT);
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

	private String makeListInt(List<Integer> list) {
		StringBuilder ret = new StringBuilder();
		for (Integer number : list) {
			BNode literal = getLiteral(number);
			ret.append(literal.stringValue());
			ret.append(Owl2FunctionalConstant.C_SPACE);
		}
		return ret.toString();
	}

	private String makeListStr(List<String> list) {
		StringBuilder ret = new StringBuilder();
		for (String str : list) {
			BNode literal = getLiteral(str);
			ret.append(literal.stringValue());
			ret.append(Owl2FunctionalConstant.C_SPACE);
		}
		return ret.toString();
	}

	boolean addPrefix(String prefixName, URI value) {
		BNode bnode = new StringBNode(Owl2FunctionalConstant.PREFIX
				+ Owl2FunctionalConstant.C_PAR_A + prefixName
				+ Owl2FunctionalConstant.C_COLON_EQUALS + value
				+ Owl2FunctionalConstant.C_PAR_B);
		add(bnode);
		return true;
	}

	public boolean addNamespaceDeclarations() {
		addPrefix("", createURI(Owl2FunctionalConstant.PREFIX_CONSTRAINTS));
		addPrefix(Owl2FunctionalConstant.OWL,
				createURI(Owl2FunctionalConstant.PREFIX_OWL));
		addPrefix(Owl2FunctionalConstant.RDF,
				createURI(Owl2FunctionalConstant.PREFIX_RDF));
		addPrefix(Owl2FunctionalConstant.XML,
				createURI(Owl2FunctionalConstant.PREFIX_XML));
		addPrefix(Owl2FunctionalConstant.XSD,
				createURI(Owl2FunctionalConstant.PREFIX_XSD));
		addPrefix(Owl2FunctionalConstant.RDFS,
				createURI(Owl2FunctionalConstant.PREFIX_RDFS));
		addPrefix(Owl2FunctionalConstant.WO,
				createURI(Owl2FunctionalConstant.PREFIX_WBONTO));
		addPrefix(Owl2FunctionalConstant.ID,
				createURI(Owl2FunctionalConstant.PREFIX_WIKIDATA));
		return true;
	}

	@Override
	public void start() {
		addNamespaceDeclarations();
		add(new StringBNode(""));
		add(new StringBNode(""));
		add(new StringBNode(Owl2FunctionalConstant.ONTOLOGY
				+ Owl2FunctionalConstant.C_PAR_A
				+ createURI(Owl2FunctionalConstant.PREFIX_ONTOLOGY)));
		add(new StringBNode(""));
	}

	@Override
	public void finish() {
		add(new StringBNode(""));
		add(new StringBNode(Owl2FunctionalConstant.C_PAR_B));
		add(new StringBNode(""));
		this.declaredEntities.clear();
		this.inverseFunctionalObjectProperties.clear();
	}

	@Override
	public URI getItem(ItemIdValue item) {
		return createURI(item.getIri());
	}

	@Override
	public URI getProperty(PropertyIdValue property) {
		return createURI(property.getIri());
	}

	@Override
	public URI getDaux(PropertyIdValue property) {
		URI ret = createURI(property.getIri() + Owl2FunctionalConstant.AUX
				+ this.auxiliaryEntityCounter);
		this.auxiliaryEntityCounter++;
		return ret;
	}

	@Override
	public URI getPs(PropertyIdValue property) {
		return createURI(Vocabulary.getPropertyUri(property,
				PropertyContext.STATEMENT));
	}

	@Override
	public URI getPv(PropertyIdValue property) {
		return createURI(Vocabulary.getPropertyUri(property,
				PropertyContext.VALUE));
	}

	@Override
	public URI wbTimeValue() {
		return createURI(Owl2FunctionalConstant.WB_TIME_VALUE);
	}

	@Override
	public URI wbQuantityValue() {
		return createURI(Owl2FunctionalConstant.WB_QUANTITY_VALUE);
	}

	@Override
	public URI owlThing() {
		return createURI(Owl2FunctionalConstant.OWL_THING);
	}

	@Override
	public URI xsdDateTime() {
		return createURI(Owl2FunctionalConstant.XSD_DATE_TIME);
	}

	@Override
	public URI xsdDecimal() {
		return createURI(Owl2FunctionalConstant.XSD_DECIMAL);
	}

	@Override
	public URI xsdInteger() {
		return createURI(Owl2FunctionalConstant.XSD_INTEGER);
	}

	@Override
	public URI xsdMaxInclusive() {
		return createURI(Owl2FunctionalConstant.XSD_MAX_INCLUSIVE);
	}

	@Override
	public URI xsdMinInclusive() {
		return createURI(Owl2FunctionalConstant.XSD_MIN_INCLUSIVE);
	}

	@Override
	public URI xsdPattern() {
		return createURI(Owl2FunctionalConstant.XSD_PATTERN);
	}

	@Override
	public URI xsdString() {
		return createURI(Owl2FunctionalConstant.XSD_STRING);
	}

	@Override
	public BNode getDataIntersectionOf(Resource dataRange0, Resource dataRange1) {
		return makeFunction(Owl2FunctionalConstant.DATA_INTERSECTION_OF,
				makePair(dataRange0, dataRange1));
	}

	@Override
	public BNode getDataOneOf(Integer literal) {
		return makeFunction(Owl2FunctionalConstant.DATA_ONE_OF,
				getLiteral(literal));
	}

	@Override
	public BNode getDataOneOf(String literal) {
		return makeFunction(Owl2FunctionalConstant.DATA_ONE_OF,
				getLiteral(literal));
	}

	@Override
	public BNode getDataOneOfInt(List<Integer> listOfLiterals) {
		return makeFunction(Owl2FunctionalConstant.DATA_ONE_OF,
				makeListInt(listOfLiterals));
	}

	@Override
	public BNode getDataOneOfStr(List<String> listOfLiterals) {
		return makeFunction(Owl2FunctionalConstant.DATA_ONE_OF,
				makeListStr(listOfLiterals));
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

	BNode getLiteral(Integer literal) {
		return getLiteral(new StringResource("" + literal), xsdInteger());
	}

	BNode getLiteral(String literal) {
		return getLiteral(
				new StringResource(StringResource.escapeChars(literal)),
				xsdString());
	}

	BNode getLiteral(Resource value, Resource type) {
		return new StringBNode(
				addQuotationMarks(escapeBackslash(value.stringValue()))
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
				makeFunction(Owl2FunctionalConstant.DATA_PROPERTY,
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
			Resource objectPropertyExpression, Resource dataPropertyExpression) {

		Resource objectProperty = (objectPropertyExpression == null) ? new StringResource(
				"") : objectPropertyExpression;

		Resource dataProperty = (dataPropertyExpression == null) ? new StringResource(
				"") : dataPropertyExpression;

		BNode bnode = makeFunction(
				Owl2FunctionalConstant.HAS_KEY,
				makePair(
						classExpression,
						makePair(makeFunction("", objectProperty),
								makeFunction("", dataProperty))));
		add(bnode);
		return true;
	}

	@Override
	public boolean addInverseFunctionalObjectProperty(
			Resource objectPropertyExpression) {
		if (this.inverseFunctionalObjectProperties
				.contains(objectPropertyExpression)) {
			return false;
		}
		BNode bnode = makeFunction(
				Owl2FunctionalConstant.INVERSE_FUNCTIONAL_OBJECT_PROPERTY,
				objectPropertyExpression);
		add(bnode);
		this.inverseFunctionalObjectProperties.add(objectPropertyExpression);
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

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
