package org.wikidata.wdtk.dumpfiles.constraint.renderer;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;
import org.wikidata.wdtk.dumpfiles.constraint.builder.ConstraintMainBuilder;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintCommonsLink;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintDiffWithinRange;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintInverse;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintQualifier;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintQualifiers;
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
	 * This test has the single purpose of testing that, even if
	 * <code>null</code> objects are used, the constraints that cannot be
	 * rendered are plainly ignored.
	 */
	@Test
	public void testEmptyConstraints() {
		ConstraintMainRenderer mainRenderer = new ConstraintMainRenderer(null);
		Assert.assertTrue(mainRenderer.visit((ConstraintSymmetric) null));
		Assert.assertTrue(mainRenderer.visit((ConstraintInverse) null));
		Assert.assertTrue(mainRenderer.visit((ConstraintCommonsLink) null));
		Assert.assertTrue(mainRenderer.visit((ConstraintDiffWithinRange) null));
		Assert.assertTrue(mainRenderer.visit((ConstraintQualifiers) null));
		Assert.assertTrue(mainRenderer.visit((ConstraintQualifier) null));
	}

	private Set<String> getNoRepetition(List<String> list) {
		Set<String> set = new TreeSet<String>();
		set.addAll(list);
		Assert.assertEquals(list.size(), set.size());
		return set;
	}

	@Test
	public void testKnownConstraints() {
		Set<String> knownConstraintsSet = getNoRepetition((new ConstraintMainBuilder())
				.getConstraintIds());
		Set<String> allConstraintsSet = new TreeSet<String>();
		allConstraintsSet.addAll(getNoRepetition(ConstraintMainRenderer
				.getAcceptedConstraints()));
		allConstraintsSet.addAll((getNoRepetition(ConstraintMainRenderer
				.getRewrittenConstraints())));
		allConstraintsSet.addAll((getNoRepetition(ConstraintMainRenderer
				.getIgnoredConstraints())));
		Assert.assertEquals(knownConstraintsSet, allConstraintsSet);
	}

}
