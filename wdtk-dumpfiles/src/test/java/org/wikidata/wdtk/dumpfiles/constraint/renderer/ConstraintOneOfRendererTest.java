package org.wikidata.wdtk.dumpfiles.constraint.renderer;

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
import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.constraint.builder.ConstraintMainBuilder;
import org.wikidata.wdtk.dumpfiles.constraint.format.Owl2FunctionalRendererFormat;
import org.wikidata.wdtk.dumpfiles.constraint.format.RdfRendererFormat;
import org.wikidata.wdtk.dumpfiles.constraint.model.Constraint;
import org.wikidata.wdtk.dumpfiles.constraint.template.Template;
import org.wikidata.wdtk.dumpfiles.constraint.template.TemplateParser;

/**
 * Test class for {@link ConstraintOneOfRenderer}
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintOneOfRendererTest {

	PropertyIdValue getPropertyIdValue(String propertyName) {
		return (new DataObjectFactoryImpl()).getPropertyIdValue(propertyName,
				ConstraintMainBuilder.PREFIX_WIKIDATA);
	}

	public Constraint getConstraint1088() {
		TemplateParser parser = new TemplateParser();
		ConstraintMainBuilder constraintBuilder = new ConstraintMainBuilder();
		String constraintStr = "{{Constraint:One of|values=1, 2, 3, 4, 5, 6, 7, 8, 9, 10}}";
		Template template = parser.parse(constraintStr);
		Constraint constraint = constraintBuilder.parse(
				getPropertyIdValue("P1088"), template);
		return constraint;
	}

	public Constraint getConstraint1123() {
		TemplateParser parser = new TemplateParser();
		ConstraintMainBuilder constraintBuilder = new ConstraintMainBuilder();
		String constraintStr = "{{Constraint:One of|values=-1, 1}}";
		Template template = parser.parse(constraintStr);
		Constraint constraint = constraintBuilder.parse(
				getPropertyIdValue("P1123"), template);
		return constraint;
	}

	@Test
	public void testProp1088FSS() {
		StringWriter output = new StringWriter();
		ConstraintMainRenderer renderer = new ConstraintMainRenderer(
				new Owl2FunctionalRendererFormat(output));
		Constraint constraint = getConstraint1088();
		constraint.accept(renderer);

		String expected = "Declaration( ObjectProperty( <http://www.wikidata.org/entity/P1088s> ) )\n"
				+ "InverseFunctionalObjectProperty( <http://www.wikidata.org/entity/P1088s> )\n"
				+ "Declaration( DataProperty( <http://www.wikidata.org/entity/P1088v> ) )\n"
				+ "DataPropertyRange( <http://www.wikidata.org/entity/P1088v> DataOneOf( 1 2 3 4 5 6 7 8 9 10  ) )\n";

		Assert.assertEquals(expected, output.toString());
	}

	@Test
	public void testProp1088RDF() {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		RdfRendererFormat rendererFormat = new RdfRendererFormat(output);
		ConstraintMainRenderer renderer = new ConstraintMainRenderer(
				rendererFormat);
		rendererFormat.start();
		Constraint constraint = getConstraint1088();
		constraint.accept(renderer);
		rendererFormat.finish();

		// TODO
		// System.out.println(output.toString());
	}

	@Test
	public void testProp1123FSS() {
		Constraint constraint = getConstraint1123();
		StringWriter output = new StringWriter();
		ConstraintMainRenderer renderer = new ConstraintMainRenderer(
				new Owl2FunctionalRendererFormat(output));
		constraint.accept(renderer);

		String expected = "Declaration( ObjectProperty( <http://www.wikidata.org/entity/P1123s> ) )\n"
				+ "InverseFunctionalObjectProperty( <http://www.wikidata.org/entity/P1123s> )\n"
				+ "Declaration( DataProperty( <http://www.wikidata.org/entity/P1123v> ) )\n"
				+ "DataPropertyRange( <http://www.wikidata.org/entity/P1123v> DataOneOf( -1 1  ) )\n";

		Assert.assertEquals(expected, output.toString());
	}

	@Test
	public void testProp1123RDF() {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		RdfRendererFormat rendererFormat = new RdfRendererFormat(output);
		ConstraintMainRenderer renderer = new ConstraintMainRenderer(
				rendererFormat);
		rendererFormat.start();
		Constraint constraint = getConstraint1123();
		constraint.accept(renderer);
		rendererFormat.finish();

		// TODO
		// System.out.println(output.toString());
	}

}
