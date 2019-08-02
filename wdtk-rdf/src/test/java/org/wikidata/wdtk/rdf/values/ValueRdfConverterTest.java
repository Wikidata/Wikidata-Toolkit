package org.wikidata.wdtk.rdf.values;

/*
 * #%L
 * Wikidata Toolkit RDF
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.wikidata.wdtk.datamodel.helpers.DatamodelMapper;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.implementation.EntityIdValueImpl;
import org.wikidata.wdtk.datamodel.implementation.UnsupportedEntityIdValueImpl;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.UnsupportedEntityIdValue;
import org.wikidata.wdtk.rdf.MockPropertyRegister;
import org.wikidata.wdtk.rdf.OwlDeclarationBuffer;
import org.wikidata.wdtk.rdf.PropertyRegister;
import org.wikidata.wdtk.rdf.RdfTestHelpers;
import org.wikidata.wdtk.rdf.RdfWriter;

public class ValueRdfConverterTest {

	ByteArrayOutputStream out;
	RdfWriter rdfWriter;
	OwlDeclarationBuffer rdfConversionBuffer;
	PropertyRegister propertyRegister = new MockPropertyRegister();
	DatamodelMapper mapper = new DatamodelMapper("http://www.wikidata.org/entity/");

	DataObjectFactory objectFactory = new DataObjectFactoryImpl();
	ValueFactory rdfFactory = SimpleValueFactory.getInstance();

	Resource resource = rdfFactory.createIRI("http://test.org/");

	@Before
	public void setUp() throws Exception {
		this.out = new ByteArrayOutputStream();
		this.rdfWriter = new RdfWriter(RDFFormat.TURTLE, this.out);
		this.rdfConversionBuffer = new OwlDeclarationBuffer();
		this.rdfWriter.start();
	}

	@Test
	public void testWriteQuantityValue() throws RDFHandlerException,
			RDFParseException, IOException {
		QuantityValueConverter valueConverter = new QuantityValueConverter(
				this.rdfWriter, this.propertyRegister, this.rdfConversionBuffer);

		QuantityValue value = this.objectFactory.getQuantityValue(
				new BigDecimal(100), new BigDecimal(100), new BigDecimal(100));
		PropertyIdValue propertyIdValue = objectFactory.getPropertyIdValue(
				"P1081", "http://www.wikidata.org/entity/");
		Value valueURI = valueConverter.getRdfValue(value, propertyIdValue,
				false);
		valueConverter.writeValue(value, (Resource) valueURI);
		this.rdfWriter.finish();
		Model model = RdfTestHelpers.parseRdf(this.out.toString());
		assertEquals(model, RdfTestHelpers.parseRdf(RdfTestHelpers
				.getResourceFromFile("QuantityValue.rdf")));
	}

	@Test
	public void testWriteUnboundedQuantityValue() throws RDFHandlerException,
			RDFParseException, IOException {
		QuantityValueConverter valueConverter = new QuantityValueConverter(
				this.rdfWriter, this.propertyRegister, this.rdfConversionBuffer);

		QuantityValue value = this.objectFactory.getQuantityValue(new BigDecimal(100));
		PropertyIdValue propertyIdValue = objectFactory.getPropertyIdValue(
				"P1081", "http://www.wikidata.org/entity/");
		Value valueURI = valueConverter.getRdfValue(value, propertyIdValue,
				false);
		valueConverter.writeValue(value, (Resource) valueURI);
		this.rdfWriter.finish();
		Model model = RdfTestHelpers.parseRdf(this.out.toString());
		assertEquals(model, RdfTestHelpers.parseRdf(RdfTestHelpers
				.getResourceFromFile("UnboundedQuantityValue.rdf")));
	}

	@Test
	public void testWriteMonolingualTextValue() throws RDFHandlerException {
		MonolingualTextValueConverter valueConverter = new MonolingualTextValueConverter(
				this.rdfWriter, this.propertyRegister, this.rdfConversionBuffer);

		MonolingualTextValue value = this.objectFactory
				.getMonolingualTextValue("中华人民共和国", "zh-hans");
		PropertyIdValue propertyIdValue = this.objectFactory
				.getPropertyIdValue("P1448", "http://www.wikidata.org/entity/");
		Value valueURI = valueConverter.getRdfValue(value, propertyIdValue,
				true);
		this.rdfWriter.finish();

		assertEquals(valueURI.toString(), "\"中华人民共和国\"@zh-Hans");
	}

	@Test
	public void testWriteGlobeCoordinatesValue() throws RDFHandlerException,
			RDFParseException, IOException {
		GlobeCoordinatesValueConverter valueConverter = new GlobeCoordinatesValueConverter(
				this.rdfWriter, this.propertyRegister, this.rdfConversionBuffer);

		GlobeCoordinatesValue value = this.objectFactory
				.getGlobeCoordinatesValue(51.033333333333, 13.733333333333,
						(GlobeCoordinatesValue.PREC_DECI_DEGREE),
						"http://www.wikidata.org/entity/Q2");
		PropertyIdValue propertyIdValue = objectFactory.getPropertyIdValue(
				"P625", "http://www.wikidata.org/entity/");
		Value valueURI = valueConverter.getRdfValue(value, propertyIdValue,
				false);
		valueConverter.writeValue(value, (Resource) valueURI);
		this.rdfWriter.finish();
		Model model = RdfTestHelpers.parseRdf(this.out.toString());
		assertEquals(model, RdfTestHelpers.parseRdf(RdfTestHelpers
				.getResourceFromFile("GlobeCoordinatesValue.rdf")));
	}

	@Test
	public void testWriteTimeValue() throws RDFHandlerException,
			RDFParseException, IOException {
		TimeValueConverter valueConverter = new TimeValueConverter(
				this.rdfWriter, this.propertyRegister, this.rdfConversionBuffer);

		TimeValue value = objectFactory.getTimeValue(2008, (byte) 1, (byte) 1,
				(byte) 0, (byte) 0, (byte) 0, (byte) 9, 0, 0, 0,
				"http://www.wikidata.org/entity/Q1985727");
		PropertyIdValue propertyIdValue = objectFactory.getPropertyIdValue(
				"P569", "http://www.wikidata.org/entity/");
		Value valueURI = valueConverter.getRdfValue(value, propertyIdValue,
				false);
		valueConverter.writeValue(value, (Resource) valueURI);
		this.rdfWriter.finish();
		Model model = RdfTestHelpers.parseRdf(this.out.toString());
		assertEquals(model, RdfTestHelpers.parseRdf(RdfTestHelpers
				.getResourceFromFile("TimeValue.rdf")));
	}
	
	@Test
	public void testWriteUnsupportedEntityIdValue() throws RDFHandlerException,
			RDFParseException, IOException {
		AnyValueConverter valueConverter = new AnyValueConverter(
				this.rdfWriter, this.rdfConversionBuffer, this.propertyRegister);

		UnsupportedEntityIdValue value = mapper.readValue(
				"{\"type\":\"wikibase-entityid\",\"value\":{\"entity-type\":\"funky\",\"id\":\"Z343\"}}",
				UnsupportedEntityIdValueImpl.class);
		PropertyIdValue propertyIdValue = objectFactory.getPropertyIdValue(
				"P569", "http://www.wikidata.org/entity/");
		Value valueURI = valueConverter.getRdfValue(value, propertyIdValue,
				false);
		this.rdfWriter.finish();
		assertTrue(valueURI instanceof BNode);
	}

}
