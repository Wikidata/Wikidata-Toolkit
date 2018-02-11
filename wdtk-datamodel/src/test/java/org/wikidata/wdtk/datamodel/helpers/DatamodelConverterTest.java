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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImplTest;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

/**
 * Test for special aspectes of {@link DatamodelConverter}. Regular operation is
 * tested elsewhere already.
 *
 * @author Markus Kroetzsch
 *
 */
public class DatamodelConverterTest {

	class BrokenItemIdValue implements ItemIdValue {

		@Override
		public String getEntityType() {
			return ItemIdValue.ET_ITEM;
		}

		@Override
		public String getId() {
			return null; // illegal; should cause errors elsewhere
		}

		@Override
		public String getSiteIri() {
			return Datamodel.SITE_WIKIDATA;
		}

		@Override
		public String getIri() {
			return null;
		}

		@Override
		public <T> T accept(ValueVisitor<T> valueVisitor) {
			return valueVisitor.visit(this);
		}

	}

	private Statement getBrokenStatement() {
		Snak brokenSnak = Datamodel.makeValueSnak(
				DataObjectFactoryImplTest.getTestPropertyIdValue(5),
				new BrokenItemIdValue());
		Claim brokenClaim = Datamodel.makeClaim(
				DataObjectFactoryImplTest.getTestItemIdValue(2), brokenSnak,
				Collections.<SnakGroup> emptyList());
		return Datamodel
				.makeStatement(brokenClaim,
						Collections.<Reference> emptyList(),
						StatementRank.NORMAL, "id");
	}

	/**
	 * Tests that statement groups that contain a single statement which cannot
	 * be copied are removed.
	 */
	@Test
	public void testSingleBrokenStatement() {

		StatementGroup sg1 = Datamodel.makeStatementGroup(Collections
				.singletonList(getBrokenStatement()));
		StatementGroup sg2 = DataObjectFactoryImplTest.getTestStatementGroup(2,
				5, 1, EntityIdValue.ET_ITEM);

		List<StatementGroup> brokenSgs = new ArrayList<>();
		brokenSgs.add(sg1);
		brokenSgs.add(sg2);
		List<StatementGroup> fixedSgs = new ArrayList<>();
		fixedSgs.add(sg2);

		ItemDocument brokenId = Datamodel.makeItemDocument(
				DataObjectFactoryImplTest.getTestItemIdValue(2),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(), brokenSgs,
				Collections.<String, SiteLink> emptyMap());

		ItemDocument fixedId = Datamodel.makeItemDocument(
				DataObjectFactoryImplTest.getTestItemIdValue(2),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(), fixedSgs,
				Collections.<String, SiteLink> emptyMap());

		DatamodelConverter dmc = new DatamodelConverter(
				new DataObjectFactoryImpl());

		assertEquals(fixedId, dmc.copy(brokenId));
	}

	/**
	 * Tests that statement groups that contain several statements, one of which
	 * cannot be copied, are reduced to the working statements.
	 */
	@Test
	public void testBrokenStatement() {

		List<Statement> brokenSg1Statements = new ArrayList<>();
		brokenSg1Statements.add(DataObjectFactoryImplTest.getTestStatement(2,
				5, 1, EntityIdValue.ET_ITEM));
		brokenSg1Statements.add(getBrokenStatement());
		brokenSg1Statements.add(DataObjectFactoryImplTest.getTestStatement(2,
				5, 2, EntityIdValue.ET_ITEM));
		StatementGroup brokenSg1 = Datamodel
				.makeStatementGroup(brokenSg1Statements);

		List<Statement> fixedSg1Statements = new ArrayList<>();
		fixedSg1Statements.add(DataObjectFactoryImplTest.getTestStatement(2, 5,
				1, EntityIdValue.ET_ITEM));
		fixedSg1Statements.add(DataObjectFactoryImplTest.getTestStatement(2, 5,
				2, EntityIdValue.ET_ITEM));
		StatementGroup fixedSg1 = Datamodel
				.makeStatementGroup(fixedSg1Statements);

		StatementGroup sg2 = DataObjectFactoryImplTest.getTestStatementGroup(2,
				5, 1, EntityIdValue.ET_ITEM);

		List<StatementGroup> brokenSgs = new ArrayList<>();
		brokenSgs.add(brokenSg1);
		brokenSgs.add(sg2);
		List<StatementGroup> fixedSgs = new ArrayList<>();
		fixedSgs.add(fixedSg1);
		fixedSgs.add(sg2);

		ItemDocument brokenId = Datamodel.makeItemDocument(
				DataObjectFactoryImplTest.getTestItemIdValue(2),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(), brokenSgs,
				Collections.<String, SiteLink> emptyMap());

		ItemDocument fixedId = Datamodel.makeItemDocument(
				DataObjectFactoryImplTest.getTestItemIdValue(2),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(), fixedSgs,
				Collections.<String, SiteLink> emptyMap());

		DatamodelConverter dmc = new DatamodelConverter(
				new DataObjectFactoryImpl());

		assertEquals(fixedId, dmc.copy(brokenId));
	}
}
