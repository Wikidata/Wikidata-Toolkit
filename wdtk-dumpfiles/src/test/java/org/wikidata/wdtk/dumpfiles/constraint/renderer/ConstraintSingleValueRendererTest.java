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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Model;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.constraint.builder.ConstraintMainBuilder;
import org.wikidata.wdtk.dumpfiles.constraint.format.Owl2FunctionalRendererFormat;
import org.wikidata.wdtk.dumpfiles.constraint.format.RdfRendererFormat;
import org.wikidata.wdtk.dumpfiles.constraint.format.RendererFormat;
import org.wikidata.wdtk.dumpfiles.constraint.model.Constraint;
import org.wikidata.wdtk.dumpfiles.constraint.template.Template;
import org.wikidata.wdtk.dumpfiles.constraint.template.TemplateParser;

/**
 * 
 * @author Julian Mendez
 *
 */
public class ConstraintSingleValueRendererTest {

	ByteArrayOutputStream output;

	final String fileName = "single-value";

	PropertyIdValue getPropertyIdValue(String propertyName) {
		return (new DataObjectFactoryImpl()).getPropertyIdValue(propertyName,
				ConstraintMainBuilder.PREFIX_WIKIDATA);
	}

	@Before
	public void setUp() throws Exception {
		this.output = new ByteArrayOutputStream();
	}

	public Constraint getConstraint() {
		TemplateParser parser = new TemplateParser();
		ConstraintMainBuilder constraintBuilder = new ConstraintMainBuilder();
		String constraintStr = "{{Constraint:Single value}}";
		Template template = parser.parse(constraintStr);
		Constraint constraint = constraintBuilder.parse(
				getPropertyIdValue("P30"), template);
		return constraint;
	}

	public void serializeConstraint(RendererFormat rendererFormat) {
		ConstraintMainRenderer renderer = new ConstraintMainRenderer(
				rendererFormat);
		rendererFormat.start();
		getConstraint().accept(renderer);
		rendererFormat.finish();
	}

	@Test
	public void testRdfRenderer() throws RDFParseException,
			RDFHandlerException, IOException {
		serializeConstraint(new RdfRendererFormat(this.output));
		Model expected = RdfTestHelpers.parseRdf(RdfTestHelpers
				.getResourceFromFile(RdfTestHelpers.RDF_PATH + fileName
						+ RdfTestHelpers.RDF_EXT));
		Model obtained = RdfTestHelpers.parseRdf(this.output.toString());
		Assert.assertEquals(expected, obtained);
	}

	@Test
	public void testOwl2FunctionalRenderer() throws IOException {
		serializeConstraint(new Owl2FunctionalRendererFormat(this.output));
		String expected = RdfTestHelpers
				.getResourceFromFile(RdfTestHelpers.OWL_PATH + fileName
						+ RdfTestHelpers.OWL_EXT);
		String obtained = this.output.toString();
		Assert.assertEquals(expected, obtained);
	}

	@Test
	public void testRenderConstraint() throws IOException {
		ConstraintSingleValueRenderer renderer = new ConstraintSingleValueRenderer(
				new Owl2FunctionalRendererFormat(this.output));

		renderer.render((PropertyIdValue) null);
		Assert.assertEquals("", output.toString());

		renderer.renderConstraint(getConstraint());
		String expected = RdfTestHelpers
				.getResourceFromFile(RdfTestHelpers.OWLPART_PATH + fileName
						+ RdfTestHelpers.OWLPART_EXT);
		String obtained = this.output.toString();
		Assert.assertEquals(expected, obtained);
	}

}
