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

import java.io.IOException;

import org.junit.Test;
import org.wikidata.wdtk.dumpfiles.constraint.builder.ConstraintMainBuilder;
import org.wikidata.wdtk.dumpfiles.constraint.format.Owl2FunctionalRendererFormat;
import org.wikidata.wdtk.dumpfiles.constraint.model.Constraint;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintConflictsWithTest;
import org.wikidata.wdtk.dumpfiles.constraint.template.Template;
import org.wikidata.wdtk.dumpfiles.constraint.template.TemplateParser;

/**
 *
 * @author Julian Mendez
 *
 */
public class ConstraintConflictsWithRendererTest extends
		ConstraintRendererTestSuperclass {

	@Override
	public Constraint getConstraint() {
		TemplateParser parser = new TemplateParser();
		ConstraintMainBuilder constraintBuilder = new ConstraintMainBuilder();
		String constraintStr = ConstraintConflictsWithTest.TEMPLATE_STR_MANY_PROP_MANY_ITEM;
		Template template = parser.parse(constraintStr);
		Constraint constraint = constraintBuilder.parse(
				getPropertyIdValue("P569"), template);
		return constraint;
	}

	@Test
	public void testRenderConstraint() throws IOException {
		ConstraintConflictsWithRenderer renderer = new ConstraintConflictsWithRenderer(
				new Owl2FunctionalRendererFormat(getOutputStream()));
		testRenderConstraint(renderer);
	}

}
