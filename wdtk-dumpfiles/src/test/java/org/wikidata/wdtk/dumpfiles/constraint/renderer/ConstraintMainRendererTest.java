package org.wikidata.wdtk.dumpfiles.constraint.renderer;

import org.junit.Assert;
import org.junit.Test;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintCommonsLink;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintInverse;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintQualifier;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintSymmetric;

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

/**
 * Test class for {@link ConstraintMainRenderer}.
 * 
 * @author Julian Mendez
 *
 */
public class ConstraintMainRendererTest {

	public ConstraintMainRendererTest() {
	}

	/**
	 * This test has the single of purpose of testing that, even if
	 * <code>null</code> objects are used, the constraints that cannot be
	 * rendered are plainly ignored.
	 */
	@Test
	public void testEmptyConstraints() {
		ConstraintMainRenderer mainRenderer = new ConstraintMainRenderer(null);
		Assert.assertTrue(mainRenderer.visit((ConstraintSymmetric) null));
		Assert.assertTrue(mainRenderer.visit((ConstraintInverse) null));
		Assert.assertTrue(mainRenderer.visit((ConstraintCommonsLink) null));
		Assert.assertTrue(mainRenderer.visit((ConstraintQualifier) null));
	}

}
