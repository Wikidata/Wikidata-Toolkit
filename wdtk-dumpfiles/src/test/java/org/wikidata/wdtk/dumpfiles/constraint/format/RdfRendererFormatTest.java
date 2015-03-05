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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.wikidata.wdtk.dumpfiles.constraint.renderer.ConstraintRendererTestHelper;

/**
 * 
 * @author Julian Mendez
 *
 */
public class RdfRendererFormatTest {

	static final String PREFIX = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<rdf:RDF" + "  xmlns:owl=\"http://www.w3.org/2002/07/owl#\""
			+ "  xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""
			+ "  xmlns:xml=\"http://www.w3.org/XML/1998/namespace\""
			+ "  xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\""
			+ "  xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\""
			+ "  xmlns:wo=\"http://www.wikidata.org/ontology#\""
			+ "  xmlns:id=\"http://www.wikidata.org/entity/\">";

	static final String SUFFIX = "</rdf:RDF>";

	static final String EXPECTED_ADD_HAS_KEY_1 = PREFIX
			+ "<rdf:Description rdf:about=\"http://www.w3.org/2002/07/owl#Thing\">"
			+ "  <owl:hasKey rdf:nodeID=\"node19flfl1t9x15\"/>"
			+ "</rdf:Description>"
			+ "<rdf:Description rdf:nodeID=\"node19flfl1t9x15\">"
			+ "  <rdf:first rdf:resource=\"http://example.org/#testObjectProperty\"/>"
			+ "  <rdf:rest rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#nil\"/>"
			+ "</rdf:Description>" + SUFFIX;

	static final String EXPECTED_ADD_HAS_KEY_2 = PREFIX
			+ "<rdf:Description rdf:about=\"http://www.w3.org/2002/07/owl#Thing\">"
			+ "  <owl:hasKey rdf:nodeID=\"node19flfvkg3x16\"/>"
			+ "</rdf:Description>"
			+ "<rdf:Description rdf:nodeID=\"node19flfvkg3x16\">"
			+ "  <rdf:first rdf:resource=\"http://example.org/#testDataProperty\"/>"
			+ "  <rdf:rest rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#nil\"/>"
			+ "</rdf:Description>" + SUFFIX;

	static final String EXPECTED_ADD_HAS_KEY_3 = PREFIX
			+ "<rdf:Description rdf:about=\"http://www.w3.org/2002/07/owl#Thing\">"
			+ "  <owl:hasKey rdf:nodeID=\"node19flg3r4sx17\"/>"
			+ "</rdf:Description>"
			+ "<rdf:Description rdf:nodeID=\"node19flg3r4sx17\">"
			+ "  <rdf:first rdf:resource=\"http://example.org/#testObjectProperty\"/>"
			+ "  <rdf:rest rdf:nodeID=\"node19flg3r4sx18\"/>"
			+ "</rdf:Description>"
			+ "<rdf:Description rdf:nodeID=\"node19flg3r4sx18\">"
			+ "  <rdf:first rdf:resource=\"http://example.org/#testDataProperty\"/>"
			+ "  <rdf:rest rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#nil\"/>"
			+ "</rdf:Description>" + SUFFIX;

	final Set<Resource> declaredEntities = new HashSet<Resource>();
	final Set<Resource> inverseFunctionalObjectProperties = new HashSet<Resource>();
	final RdfWriterWithExceptions rdfWriter = new RdfWriterWithExceptions();
	final RdfRendererFormat formatWithExceptions = new RdfRendererFormat(
			new RdfWriterWithExceptions());
	final StringResource resource = new StringResource("");
	final URI uri = new URIImpl("http://example.org/#testURI");
	final URI objectProperty = new URIImpl(
			"http://example.org/#testObjectProperty");
	final URI dataProperty = new URIImpl("http://example.org/#testDataProperty");
	final int literal = 0;

	public RdfRendererFormatTest() {
	}

	@Test
	public void testWbTimeValue() {
		Assert.assertEquals(RdfUriConstant.WB_TIME_VALUE,
				formatWithExceptions.wbTimeValue());
	}

	@Test
	public void testWbQuantityValue() {
		Assert.assertEquals(RdfUriConstant.WB_QUANTITY_VALUE,
				formatWithExceptions.wbQuantityValue());
	}

	@Test
	public void testOwlThing() {
		Assert.assertEquals(RdfUriConstant.OWL_THING,
				formatWithExceptions.owlThing());
	}

	@Test
	public void testXsdDateTime() {
		Assert.assertEquals(RdfUriConstant.XSD_DATE_TIME,
				formatWithExceptions.xsdDateTime());
	}

	@Test
	public void testXsdDecimal() {
		Assert.assertEquals(RdfUriConstant.XSD_DECIMAL,
				formatWithExceptions.xsdDecimal());
	}

	@Test
	public void testXsdInteger() {
		Assert.assertEquals(RdfUriConstant.XSD_INTEGER,
				formatWithExceptions.xsdInteger());
	}

	@Test
	public void testXsdMaxInclusive() {
		Assert.assertEquals(RdfUriConstant.XSD_MAX_INCLUSIVE,
				formatWithExceptions.xsdMaxInclusive());
	}

	@Test
	public void testXsdMinInclusive() {
		Assert.assertEquals(RdfUriConstant.XSD_MIN_INCLUSIVE,
				formatWithExceptions.xsdMinInclusive());
	}

	@Test
	public void testXsdPattern() {
		Assert.assertEquals(RdfUriConstant.XSD_PATTERN,
				formatWithExceptions.xsdPattern());
	}

	@Test
	public void testXsdString() {
		Assert.assertEquals(RdfUriConstant.XSD_STRING,
				formatWithExceptions.xsdString());
	}

	@Test(expected = RuntimeException.class)
	public void testGetDataIntersectionOf() {
		formatWithExceptions.getDataIntersectionOf(resource, resource);
		Assert.fail();
	}

	@Test(expected = RuntimeException.class)
	public void testGetDataOneOf() {
		formatWithExceptions.getDataOneOf(literal);
		Assert.fail();
	}

	@Test(expected = RuntimeException.class)
	public void testGetDataOneOfList() {
		formatWithExceptions.getDataOneOf(new ArrayList<Integer>());
		Assert.fail();
	}

	@Test(expected = RuntimeException.class)
	public void testGetDataSomeValuesFromt() {
		formatWithExceptions.getDataSomeValuesFrom(uri, resource);
		Assert.fail();
	}

	@Test(expected = RuntimeException.class)
	public void testGetDatatypeRestriction() {
		formatWithExceptions.getDatatypeRestriction(uri, uri, resource);
		Assert.fail();
	}

	@Test(expected = RuntimeException.class)
	public void testGetObjectComplementOf() {
		formatWithExceptions.getObjectComplementOf(resource);
		Assert.fail();
	}

	@Test(expected = RuntimeException.class)
	public void testGetObjectExactCardinality() {
		formatWithExceptions.getObjectExactCardinality(0, resource);
		Assert.fail();
	}

	@Test(expected = RuntimeException.class)
	public void testGetObjectOneOf() {
		formatWithExceptions.getObjectOneOf(resource);
		Assert.fail();
	}

	@Test(expected = RuntimeException.class)
	public void testGetObjectOneOfList() {
		formatWithExceptions.getObjectOneOf(new ArrayList<Resource>());
		Assert.fail();
	}

	@Test(expected = RuntimeException.class)
	public void testGetObjectSomeValuesFrom() {
		formatWithExceptions.getObjectSomeValuesFrom(resource, resource);
		Assert.fail();
	}

	@Test(expected = RuntimeException.class)
	public void testAddDataPropertyRange() {
		formatWithExceptions.addDataPropertyRange(uri, resource);
		Assert.fail();
	}

	@Test(expected = RuntimeException.class)
	public void testAddDatatypeDefinition() {
		formatWithExceptions.addDatatypeDefinition(uri, resource);
		Assert.fail();
	}

	@Test(expected = RuntimeException.class)
	public void testAddDeclarationDatatype() {
		formatWithExceptions.addDeclarationDatatype(uri);
		Assert.fail();
	}

	@Test
	public void testAddDeclarationDatatype2() {
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		RdfRendererFormat format = new RdfRendererFormat(byteArray);
		format.start();
		Assert.assertTrue(format.addDeclarationDatatype(format.owlThing()));
		Assert.assertFalse(format.addDeclarationDatatype(format.owlThing()));
		format.finish();
	}

	@Test(expected = RuntimeException.class)
	public void testAddDeclarationDatatypeProperty() {
		formatWithExceptions.addDeclarationDatatypeProperty(uri);
		Assert.fail();
	}

	@Test
	public void testAddDeclarationDatatypeProperty2() {
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		RdfRendererFormat format = new RdfRendererFormat(byteArray);
		format.start();
		Assert.assertTrue(format.addDeclarationDatatypeProperty(format
				.owlThing()));
		Assert.assertFalse(format.addDeclarationDatatypeProperty(format
				.owlThing()));
		format.finish();
	}

	@Test(expected = RuntimeException.class)
	public void testAddDeclarationNamedIndividual() {
		formatWithExceptions.addDeclarationNamedIndividual(uri);
		Assert.fail();
	}

	@Test
	public void testAddDeclarationNamedIndividual2() {
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		RdfRendererFormat format = new RdfRendererFormat(byteArray);
		format.start();
		Assert.assertTrue(format.addDeclarationNamedIndividual(format
				.owlThing()));
		Assert.assertFalse(format.addDeclarationNamedIndividual(format
				.owlThing()));
		format.finish();
	}

	@Test(expected = RuntimeException.class)
	public void testAddDeclarationObjectProperty() {
		formatWithExceptions.addDeclarationObjectProperty(uri);
		Assert.fail();
	}

	@Test(expected = RuntimeException.class)
	public void testAddDisjointClasses() {
		formatWithExceptions.addDisjointClasses(resource, resource);
		Assert.fail();
	}

	@Test(expected = RuntimeException.class)
	public void testAddFunctionalObjectProperty() {
		formatWithExceptions.addFunctionalObjectProperty(resource);
		Assert.fail();
	}

	@Test(expected = RuntimeException.class)
	public void testAddHasKey() {
		formatWithExceptions.addHasKey(resource, resource, resource);
		Assert.fail();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddHasKey0() {
		RdfRendererFormat format = new RdfRendererFormat(System.out);
		format.addHasKey(resource, null, null);
		Assert.fail();
	}

	@Test
	public void testAddHasKey1() throws RDFParseException, RDFHandlerException,
			IOException {
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		RdfRendererFormat format = new RdfRendererFormat(byteArray);
		format.start();
		format.addHasKey(format.owlThing(), objectProperty, null);
		format.finish();
		Model expected = ConstraintRendererTestHelper
				.parseRdf(EXPECTED_ADD_HAS_KEY_1);
		String obtainedStr = new String(byteArray.toByteArray());
		Model obtained = ConstraintRendererTestHelper.parseRdf(obtainedStr);
		Assert.assertEquals(expected, obtained);
	}

	@Test
	public void testAddHasKey2() throws RDFParseException, RDFHandlerException,
			IOException {
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		RdfRendererFormat format = new RdfRendererFormat(byteArray);
		format.start();
		format.addHasKey(format.owlThing(), null, dataProperty);
		format.finish();
		Model expected = ConstraintRendererTestHelper
				.parseRdf(EXPECTED_ADD_HAS_KEY_2);
		String obtainedStr = new String(byteArray.toByteArray());
		Model obtained = ConstraintRendererTestHelper.parseRdf(obtainedStr);
		Assert.assertEquals(expected, obtained);
	}

	@Test
	public void testAddHasKey3() throws RDFParseException, RDFHandlerException,
			IOException {
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		RdfRendererFormat format = new RdfRendererFormat(byteArray);
		format.start();
		format.addHasKey(format.owlThing(), objectProperty, dataProperty);
		format.finish();
		Model expected = ConstraintRendererTestHelper
				.parseRdf(EXPECTED_ADD_HAS_KEY_3);
		String obtainedStr = new String(byteArray.toByteArray());
		Model obtained = ConstraintRendererTestHelper.parseRdf(obtainedStr);
		Assert.assertEquals(expected, obtained);
	}

	@Test(expected = RuntimeException.class)
	public void testAddInverseFunctionalObjectProperty() {
		formatWithExceptions.addInverseFunctionalObjectProperty(resource);
		Assert.fail();
	}

	@Test
	public void testAddInverseFunctionalObjectProperty2() {
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		RdfRendererFormat format = new RdfRendererFormat(byteArray);
		format.start();
		Assert.assertTrue(format.addInverseFunctionalObjectProperty(format
				.owlThing()));
		Assert.assertFalse(format.addInverseFunctionalObjectProperty(format
				.owlThing()));
		format.finish();
	}

	@Test(expected = RuntimeException.class)
	public void testAddObjectPropertyDomain() {
		formatWithExceptions.addObjectPropertyDomain(resource, resource);
		Assert.fail();
	}

	@Test(expected = RuntimeException.class)
	public void testAddObjectPropertyRange() {
		formatWithExceptions.addObjectPropertyRange(resource, resource);
		Assert.fail();
	}

	@Test(expected = RuntimeException.class)
	public void testAddSubClassOf() {
		formatWithExceptions.addSubClassOf(resource, resource);
		Assert.fail();
	}

	@Test(expected = RuntimeException.class)
	public void testAddNamespaceDeclarations() {
		formatWithExceptions.addNamespaceDeclarations();
		Assert.fail();
	}

	@Test(expected = RuntimeException.class)
	public void testStart() {
		formatWithExceptions.start();
		Assert.fail();
	}

	@Test(expected = RuntimeException.class)
	public void testFinish() {
		formatWithExceptions.finish();
		Assert.fail();
	}

}
