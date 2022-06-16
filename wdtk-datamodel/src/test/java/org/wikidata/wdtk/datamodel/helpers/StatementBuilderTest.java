package org.wikidata.wdtk.datamodel.helpers;

/*
 * #%L
 * Wikidata Toolkit Data Model
 * %%
 * Copyright (C) 2014 - 2015 Wikidata Toolkit Developers
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

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;

public class StatementBuilderTest {

	@Test
	public void testEmptyStatement() {
		ItemIdValue i = ItemIdValue.NULL;
		PropertyIdValue p = PropertyIdValue.NULL;

		Statement stmt1 = Datamodel.makeStatement(
				i, Datamodel.makeSomeValueSnak(p),
				Collections.emptyList(), Collections.emptyList(),
				StatementRank.NORMAL, "");
		Statement stmt2 = StatementBuilder.forSubjectAndProperty(i, p).build();

		assertEquals(stmt1, stmt2);
	}

	@Test
	public void testComplexStatement() {
		ItemIdValue i = ItemIdValue.NULL;
		PropertyIdValue p = PropertyIdValue.NULL;

		Snak q1 = Datamodel.makeSomeValueSnak(p);
		Snak q2 = Datamodel.makeNoValueSnak(p);
		Snak q3 = Datamodel.makeValueSnak(p, i);
		SnakGroup sg = Datamodel.makeSnakGroup(Arrays.asList(q1, q2, q3));

		Reference r = Datamodel.makeReference(Collections.singletonList(sg));

		Statement stmt1 = Datamodel.makeStatement(i,
				Datamodel.makeValueSnak(p, i), Collections.singletonList(sg),
				Collections.singletonList(r), StatementRank.PREFERRED, "id");
		Statement stmt2 = StatementBuilder.forSubjectAndProperty(i, p)
				.withRank(StatementRank.PREFERRED).withValue(i)
				.withQualifierSomeValue(p).withQualifierNoValue(p)
				.withQualifierValue(p, i).withId("id").withReference(r).build();

		assertEquals(stmt1, stmt2);
	}

	@Test
	public void testQualifierList() {
		ItemIdValue i = ItemIdValue.NULL;
		PropertyIdValue p = PropertyIdValue.NULL;

		Snak q1 = Datamodel.makeSomeValueSnak(p);
		Snak q2 = Datamodel.makeNoValueSnak(p);
		Snak q3 = Datamodel.makeValueSnak(p, i);
		SnakGroup sg = Datamodel.makeSnakGroup(Arrays.asList(q1, q2, q3));

		Reference r = Datamodel.makeReference(Collections.singletonList(sg));

		Statement stmt1 = Datamodel.makeStatement(i,
				Datamodel.makeValueSnak(p, i), Collections.singletonList(sg),
				Collections.singletonList(r), StatementRank.PREFERRED, "id");
		Statement stmt2 = StatementBuilder.forSubjectAndProperty(i, p)
				.withRank(StatementRank.PREFERRED).withValue(i)
				.withQualifiers(stmt1.getQualifiers()).withId("id")
				.withReference(r).build();

		assertEquals(stmt1, stmt2);
	}

	@Test
	public void testReferenceList() {
		ItemIdValue i = ItemIdValue.NULL;
		PropertyIdValue p = PropertyIdValue.NULL;

		Reference r1 = ReferenceBuilder.newInstance().withSomeValue(p).build();
		Reference r2 = ReferenceBuilder.newInstance().withPropertyValue(p, i)
				.build();

		Snak q1 = Datamodel.makeSomeValueSnak(p);
		Snak q2 = Datamodel.makeNoValueSnak(p);
		Snak q3 = Datamodel.makeValueSnak(p, i);
		SnakGroup sg = Datamodel.makeSnakGroup(Arrays.asList(q1, q2, q3));

		Statement stmt1 = Datamodel.makeStatement(i,
				Datamodel.makeValueSnak(p, i), Collections.singletonList(sg),
				Arrays.asList(r1, r2), StatementRank.PREFERRED, "id");
		Statement stmt2 = StatementBuilder.forSubjectAndProperty(i, p)
				.withRank(StatementRank.PREFERRED).withValue(i)
				.withQualifierSomeValue(p).withQualifierNoValue(p)
				.withQualifierValue(p, i).withId("id")
				.withReferences(Arrays.asList(r1, r2)).build();

		assertEquals(stmt1, stmt2);
	}

	@Test
	public void testNoValueStatement() {
		ItemIdValue i = ItemIdValue.NULL;
		PropertyIdValue p = PropertyIdValue.NULL;

		Statement stmt1 = Datamodel.makeStatement(
				i, Datamodel.makeNoValueSnak(p),
				Collections.emptyList(), Collections.emptyList(),
				StatementRank.NORMAL, "");
		Statement stmt2 = StatementBuilder.forSubjectAndProperty(i, p)
				.withNoValue().build();

		assertEquals(stmt1, stmt2);
	}

	@Test
	public void testSomeValueStatement() {
		ItemIdValue i = ItemIdValue.NULL;
		PropertyIdValue p = PropertyIdValue.NULL;

		Statement stmt1 = Datamodel.makeStatement(i,
				Datamodel.makeSomeValueSnak(p),
				Collections.emptyList(), Collections.emptyList(),
				StatementRank.NORMAL, "");
		Statement stmt2 = StatementBuilder.forSubjectAndProperty(i, p)
				.withSomeValue().build();

		assertEquals(stmt1, stmt2);
	}

}
