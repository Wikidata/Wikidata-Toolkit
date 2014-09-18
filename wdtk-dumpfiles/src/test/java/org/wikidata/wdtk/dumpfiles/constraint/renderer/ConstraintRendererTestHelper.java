package org.wikidata.wdtk.dumpfiles.constraint.renderer;

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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.openrdf.model.Model;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.constraint.builder.ConstraintMainBuilder;
import org.wikidata.wdtk.dumpfiles.constraint.format.Owl2FunctionalRendererFormat;
import org.wikidata.wdtk.dumpfiles.constraint.format.RdfRendererFormat;
import org.wikidata.wdtk.dumpfiles.constraint.format.RendererFormat;
import org.wikidata.wdtk.dumpfiles.constraint.model.Constraint;
import org.wikidata.wdtk.dumpfiles.constraint.template.Template;
import org.wikidata.wdtk.dumpfiles.constraint.template.TemplateParser;
import org.wikidata.wdtk.testing.MockStringContentFactory;

/**
 * This class is a test helper for every unit test of renderer of constraints.
 *
 * @author Julian Mendez
 *
 */
public class ConstraintRendererTestHelper {

	public static final String OWL_EXT = ".owl";
	public static final String OWL_PATH = "constraint/owl/";
	public static final String OWLPART_EXT = ".owl";
	public static final String OWLPART_PATH = "constraint/owlpart/";
	public static final String RDF_EXT = ".rdf";
	public static final String RDF_PATH = "constraint/rdf/";

	public static String getResourceFromFile(String fileName)
			throws IOException {
		return MockStringContentFactory
				.getStringFromUrl(ConstraintRendererTestHelper.class
						.getResource("/" + fileName));
	}

	public static Model parseRdf(String rdfResource) throws RDFParseException,
			RDFHandlerException, IOException {
		InputStream inStream = new ByteArrayInputStream(rdfResource.getBytes());
		RDFParser parser = Rio.createParser(RDFFormat.RDFXML);
		Model graph = new LinkedHashModel();
		parser.setRDFHandler(new StatementCollector(graph));
		parser.parse(inStream, "http://test/");
		return graph;
	}

	final ByteArrayOutputStream output = new ByteArrayOutputStream();

	String fileName = getClass().getSimpleName().replace("Constraint", "")
			.replace("Renderer", "").replace("Test", "").toLowerCase();

	public Constraint getConstraint(String propertyId, String templateStr) {
		TemplateParser parser = new TemplateParser();
		ConstraintMainBuilder constraintBuilder = new ConstraintMainBuilder();
		Template template = parser.parse(templateStr);
		Constraint constraint = constraintBuilder.parse(
				getPropertyIdValue(propertyId), template);
		return constraint;
	}

	public ConstraintRendererTestHelper() {
	}

	public ConstraintRendererTestHelper(String fileName) {
		this.fileName = fileName;
	}

	public PropertyIdValue getPropertyIdValue(String propertyName) {
		return (new DataObjectFactoryImpl()).getPropertyIdValue(propertyName,
				ConstraintMainBuilder.PREFIX_WIKIDATA);
	}

	public ByteArrayOutputStream getOutputStream() {
		return this.output;
	}

	public void serializeConstraint(RendererFormat rendererFormat,
			Constraint constraint) {
		ConstraintMainRenderer renderer = new ConstraintMainRenderer(
				rendererFormat);
		rendererFormat.start();
		constraint.accept(renderer);
		rendererFormat.finish();
	}

	public void testRenderConstraint(ConstraintRenderer renderer,
			Constraint constraint) throws IOException {
		this.output.reset();
		renderer.renderConstraint(constraint);
		String expected = getResourceFromFile(OWLPART_PATH + this.fileName
				+ OWLPART_EXT);
		String obtained = this.output.toString();
		Assert.assertEquals(expected, obtained);
	}

	public void testRdfRenderer(Constraint constraint)
			throws RDFParseException, RDFHandlerException, IOException {
		this.output.reset();
		serializeConstraint(new RdfRendererFormat(this.output), constraint);
		String expectedStr = getResourceFromFile(RDF_PATH + this.fileName
				+ RDF_EXT);
		Model expected = parseRdf(expectedStr);
		String obtainedStr = this.output.toString();
		Model obtained = parseRdf(obtainedStr);
		Assert.assertEquals(expected, obtained);
	}

	public void testOwl2FunctionalRenderer(Constraint constraint)
			throws IOException {
		this.output.reset();
		serializeConstraint(new Owl2FunctionalRendererFormat(this.output),
				constraint);
		String expected = getResourceFromFile(OWL_PATH + this.fileName
				+ OWL_EXT);
		String obtained = this.output.toString();
		Assert.assertEquals(expected, obtained);
	}

}
