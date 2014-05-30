package org.wikidata.wdtk.rdf;

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

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;

public class ValueRdfConverterTest {

	ValueRdfConverter valueConverter;

	ByteArrayOutputStream out;
	RdfWriter writer;

	DataObjectFactory objectFactory = new DataObjectFactoryImpl();
	ValueFactory rdfFactory = ValueFactoryImpl.getInstance();

	Resource resource = rdfFactory.createURI("http://test.org/");

	@Before
	public void setUp() throws Exception {
		this.out = new ByteArrayOutputStream();
		this.writer = new RdfWriter(RDFFormat.N3, this.out);
		this.valueConverter = new ValueRdfConverter(this.writer,
				new RdfConversionBuffer(), new WikidataPropertyTypes());
		this.writer.start();
	}

	@Test
	public void testWriteQuantityValue() throws RDFHandlerException,
			RDFParseException, IOException {
		QuantityValue value = this.objectFactory.getQuantityValue(
				new BigDecimal(100), new BigDecimal(100), new BigDecimal(100));
		this.valueConverter.writeQuantityValue(value, this.resource);
		this.writer.finish();
		Model model = RdfTestHelpers.parseRdf(this.out.toString());
		// System.out.println(out.toString());
		assertEquals(model, RdfTestHelpers.parseRdf(RdfTestHelpers
				.getResourceFromFile("QuantityValue.rdf")));
	}

	@Test
	public void testWriteGlobeCoordinatesValue() throws RDFHandlerException,
			RDFParseException, IOException {
		GlobeCoordinatesValue value = this.objectFactory
				.getGlobeCoordinatesValue(
						(long) (51.033333333333 * GlobeCoordinatesValue.PREC_DEGREE),
						(long) (13.733333333333 * GlobeCoordinatesValue.PREC_DEGREE),
						(long) (GlobeCoordinatesValue.PREC_ARCMINUTE),
						"http://www.wikidata.org/entity/Q2");
		this.valueConverter.writeGlobeCoordinatesValue(value, this.resource);
		this.writer.finish();
		Model model = RdfTestHelpers.parseRdf(this.out.toString());
		assertEquals(model, RdfTestHelpers.parseRdf(RdfTestHelpers
				.getResourceFromFile("GlobeCoordinatesValue.rdf")));
	}

	@Test
	public void testWriteTimeValue() throws RDFHandlerException,
			RDFParseException, IOException {
		TimeValue value = objectFactory.getTimeValue(2008, (byte) 1, (byte) 1,
				(byte) 0, (byte) 0, (byte) 0, (byte) 9, 0, 0, 0,
				"http://www.wikidata.org/entity/Q1985727");
		valueConverter.writeTimeValue(value, resource);
		this.writer.finish();
		Model model = RdfTestHelpers.parseRdf(this.out.toString());
		assertEquals(model, RdfTestHelpers.parseRdf(RdfTestHelpers
				.getResourceFromFile("TimeValue.rdf")));
	}

}
