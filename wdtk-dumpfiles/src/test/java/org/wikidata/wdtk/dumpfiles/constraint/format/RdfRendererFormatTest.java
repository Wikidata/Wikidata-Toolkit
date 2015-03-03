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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;

/**
 * 
 * @author Julian Mendez
 *
 */
public class RdfRendererFormatTest {

	final Set<Resource> declaredEntities = new HashSet<Resource>();
	final Set<Resource> inverseFunctionalObjectProperties = new HashSet<Resource>();
	final RdfWriterWithExceptions rdfWriter = new RdfWriterWithExceptions();
	final RdfRendererFormat formatWithExceptions = new RdfRendererFormat(
			new RdfWriterWithExceptions());
	final StringResource resource = new StringResource("");
	final URI uri = null;
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

	@Test(expected = RuntimeException.class)
	public void testAddDeclarationDatatypeProperty() {
		formatWithExceptions.addDeclarationDatatypeProperty(uri);
		Assert.fail();
	}

	@Test(expected = RuntimeException.class)
	public void testAddDeclarationNamedIndividual() {
		formatWithExceptions.addDeclarationNamedIndividual(uri);
		Assert.fail();
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

	@Test(expected = RuntimeException.class)
	public void testAddInverseFunctionalObjectProperty() {
		formatWithExceptions.addInverseFunctionalObjectProperty(resource);
		Assert.fail();
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
