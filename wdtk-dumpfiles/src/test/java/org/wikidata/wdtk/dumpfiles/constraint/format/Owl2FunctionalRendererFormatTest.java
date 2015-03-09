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

import org.junit.Assert;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 * 
 * Test class for {@link Owl2FunctionalRendererFormat}.
 *
 * @author Julian Mendez
 *
 */
public class Owl2FunctionalRendererFormatTest {

	final Owl2FunctionalRendererFormat formatWithExceptions = new Owl2FunctionalRendererFormat(
			new WriterWithExceptions());
	final StringResource resource = new StringResource("");
	final URI uri = new URIImpl("http://example.org/#testURI");

	public Owl2FunctionalRendererFormatTest() {
	}

	private String toStr(URI uri) {
		String ret = null;
		if (uri == null) {
			Assert.fail();
		} else if (uri.stringValue().length() < 2) {
			Assert.fail();
		} else {
			String str = uri.stringValue();
			String a = str.substring(0, 1);
			String b = str.substring(str.length() - 1, str.length());
			Assert.assertEquals("<", a);
			Assert.assertEquals(">", b);
			ret = str.substring(1, str.length() - 1);
		}
		return ret;
	}

	@Test
	public void testWbTimeValue() {
		Assert.assertEquals(Owl2FunctionalConstant.WB_TIME_VALUE,
				toStr(formatWithExceptions.wbTimeValue()));
	}

	@Test
	public void testWbQuantityValue() {
		Assert.assertEquals(Owl2FunctionalConstant.WB_QUANTITY_VALUE,
				toStr(formatWithExceptions.wbQuantityValue()));
	}

	@Test
	public void testOwlThing() {
		Assert.assertEquals(Owl2FunctionalConstant.OWL_THING,
				toStr(formatWithExceptions.owlThing()));
	}

	@Test
	public void testXsdDateTime() {
		Assert.assertEquals(Owl2FunctionalConstant.XSD_DATE_TIME,
				toStr(formatWithExceptions.xsdDateTime()));
	}

	@Test
	public void testXsdDecimal() {
		Assert.assertEquals(Owl2FunctionalConstant.XSD_DECIMAL,
				toStr(formatWithExceptions.xsdDecimal()));
	}

	@Test
	public void testXsdInteger() {
		Assert.assertEquals(Owl2FunctionalConstant.XSD_INTEGER,
				toStr(formatWithExceptions.xsdInteger()));
	}

	@Test
	public void testXsdMaxInclusive() {
		Assert.assertEquals(Owl2FunctionalConstant.XSD_MAX_INCLUSIVE,
				toStr(formatWithExceptions.xsdMaxInclusive()));
	}

	@Test
	public void testXsdMinInclusive() {
		Assert.assertEquals(Owl2FunctionalConstant.XSD_MIN_INCLUSIVE,
				toStr(formatWithExceptions.xsdMinInclusive()));
	}

	@Test
	public void testXsdPattern() {
		Assert.assertEquals(Owl2FunctionalConstant.XSD_PATTERN,
				toStr(formatWithExceptions.xsdPattern()));
	}

	@Test
	public void testXsdString() {
		Assert.assertEquals(Owl2FunctionalConstant.XSD_STRING,
				toStr(formatWithExceptions.xsdString()));
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
