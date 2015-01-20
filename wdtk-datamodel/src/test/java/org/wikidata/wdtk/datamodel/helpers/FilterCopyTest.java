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
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;

public class FilterCopyTest {

	/**
	 * Returns an item document for Wikidata's Q42 that has no data.
	 *
	 * @return empty item document
	 */
	protected ItemDocument getEmptyItemDocument() {
		return Datamodel.makeItemDocument(
				Datamodel.makeWikidataItemIdValue("Q42"),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<StatementGroup> emptyList(),
				Collections.<String, SiteLink> emptyMap());
	}

	@Test
	public void testEmptyLanguageFilter() {
		DatamodelConverter datamodelConverter = new DatamodelConverter(
				new DataObjectFactoryImpl());
		datamodelConverter.setOptionLanguageFilter(Collections
				.<String> emptySet());

		List<MonolingualTextValue> labels = new ArrayList<>();
		labels.add(Datamodel.makeMonolingualTextValue("Label de", "de"));
		labels.add(Datamodel.makeMonolingualTextValue("Label en", "en"));
		labels.add(Datamodel.makeMonolingualTextValue("Label he", "he"));
		List<MonolingualTextValue> descriptions = new ArrayList<>();
		descriptions.add(Datamodel.makeMonolingualTextValue("Desc en", "en"));
		descriptions.add(Datamodel.makeMonolingualTextValue("Desc he", "he"));
		List<MonolingualTextValue> aliases = new ArrayList<>();
		aliases.add(Datamodel.makeMonolingualTextValue("Alias en", "en"));
		aliases.add(Datamodel.makeMonolingualTextValue("Alias de1", "de"));
		aliases.add(Datamodel.makeMonolingualTextValue("Alias de2", "de"));

		ItemDocument itemDocument = Datamodel.makeItemDocument(
				Datamodel.makeWikidataItemIdValue("Q42"), labels, descriptions,
				aliases, Collections.<StatementGroup> emptyList(),
				Collections.<String, SiteLink> emptyMap());

		ItemDocument itemDocumentFiltered = getEmptyItemDocument();

		assertEquals(itemDocumentFiltered,
				datamodelConverter.copy(itemDocument));

		datamodelConverter.setOptionDeepCopy(false);
		assertEquals(itemDocumentFiltered,
				datamodelConverter.copy(itemDocument));
	}

	@Test
	public void testLanguageFilter() {
		Set<String> languageFilter = new HashSet<>();
		languageFilter.add("de");
		languageFilter.add("he");

		DatamodelConverter datamodelConverter = new DatamodelConverter(
				new DataObjectFactoryImpl());
		datamodelConverter.setOptionLanguageFilter(languageFilter);

		List<MonolingualTextValue> labels = new ArrayList<>();
		labels.add(Datamodel.makeMonolingualTextValue("Label de", "de"));
		labels.add(Datamodel.makeMonolingualTextValue("Label en", "en"));
		labels.add(Datamodel.makeMonolingualTextValue("Label he", "he"));
		List<MonolingualTextValue> descriptions = new ArrayList<>();
		descriptions.add(Datamodel.makeMonolingualTextValue("Desc en", "en"));
		descriptions.add(Datamodel.makeMonolingualTextValue("Desc he", "he"));
		List<MonolingualTextValue> aliases = new ArrayList<>();
		aliases.add(Datamodel.makeMonolingualTextValue("Alias en", "en"));
		aliases.add(Datamodel.makeMonolingualTextValue("Alias de1", "de"));
		aliases.add(Datamodel.makeMonolingualTextValue("Alias de2", "de"));

		ItemDocument itemDocument = Datamodel.makeItemDocument(
				Datamodel.makeWikidataItemIdValue("Q42"), labels, descriptions,
				aliases, Collections.<StatementGroup> emptyList(),
				Collections.<String, SiteLink> emptyMap());

		List<MonolingualTextValue> labelsFiltered = new ArrayList<>();
		labelsFiltered
				.add(Datamodel.makeMonolingualTextValue("Label de", "de"));
		labelsFiltered
				.add(Datamodel.makeMonolingualTextValue("Label he", "he"));
		List<MonolingualTextValue> descriptionsFiltered = new ArrayList<>();
		descriptionsFiltered.add(Datamodel.makeMonolingualTextValue("Desc he",
				"he"));
		List<MonolingualTextValue> aliasesFiltered = new ArrayList<>();
		aliasesFiltered.add(Datamodel.makeMonolingualTextValue("Alias de1",
				"de"));
		aliasesFiltered.add(Datamodel.makeMonolingualTextValue("Alias de2",
				"de"));

		ItemDocument itemDocumentFiltered = Datamodel.makeItemDocument(
				Datamodel.makeWikidataItemIdValue("Q42"), labelsFiltered,
				descriptionsFiltered, aliasesFiltered,
				Collections.<StatementGroup> emptyList(),
				Collections.<String, SiteLink> emptyMap());

		assertEquals(languageFilter,
				datamodelConverter.getOptionLanguageFilter());
		assertEquals(itemDocumentFiltered,
				datamodelConverter.copy(itemDocument));

		datamodelConverter.setOptionDeepCopy(false);
		assertEquals(itemDocumentFiltered,
				datamodelConverter.copy(itemDocument));
	}

	/**
	 * Creates a statement group using the given property. The subject of the
	 * statement group will be Wikidata's Q42.
	 *
	 * @param propertyIdValue
	 *            the property to use for the main snak of the claim of the
	 *            statements in this statement group
	 * @return the new statement group
	 */
	protected StatementGroup makeTestStatementGroup(
			PropertyIdValue propertyIdValue) {
		Claim claim = Datamodel.makeClaim(
				Datamodel.makeWikidataItemIdValue("Q42"),
				Datamodel.makeSomeValueSnak(propertyIdValue),
				Collections.<SnakGroup> emptyList());
		Statement statement = Datamodel.makeStatement(claim,
				Collections.<Reference> emptyList(), StatementRank.NORMAL,
				"statement-id-" + propertyIdValue.getId());
		return Datamodel.makeStatementGroup(Collections
				.singletonList(statement));
	}

	@Test
	public void testPropertyFilter() {
		PropertyIdValue p1 = Datamodel.makeWikidataPropertyIdValue("P1");
		PropertyIdValue p2 = Datamodel.makeWikidataPropertyIdValue("P2");
		PropertyIdValue p3 = Datamodel.makeWikidataPropertyIdValue("P3");
		PropertyIdValue p4 = Datamodel.makeWikidataPropertyIdValue("P4");

		Set<PropertyIdValue> propertyFilter = new HashSet<>();
		propertyFilter.add(p1);
		propertyFilter.add(p3);

		DatamodelConverter datamodelConverter = new DatamodelConverter(
				new DataObjectFactoryImpl());
		datamodelConverter.setOptionPropertyFilter(propertyFilter);

		List<StatementGroup> statementGroups = new ArrayList<>();
		statementGroups.add(makeTestStatementGroup(p1));
		statementGroups.add(makeTestStatementGroup(p2));
		statementGroups.add(makeTestStatementGroup(p3));
		statementGroups.add(makeTestStatementGroup(p4));

		List<StatementGroup> statementGroupsFiltered = new ArrayList<>();
		statementGroupsFiltered.add(makeTestStatementGroup(p1));
		statementGroupsFiltered.add(makeTestStatementGroup(p3));

		ItemDocument itemDocument = Datamodel.makeItemDocument(
				Datamodel.makeWikidataItemIdValue("Q42"),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				statementGroups, Collections.<String, SiteLink> emptyMap());

		ItemDocument itemDocumentFiltered = Datamodel.makeItemDocument(
				Datamodel.makeWikidataItemIdValue("Q42"),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				statementGroupsFiltered,
				Collections.<String, SiteLink> emptyMap());

		assertEquals(propertyFilter,
				datamodelConverter.getOptionPropertyFilter());
		assertEquals(itemDocumentFiltered,
				datamodelConverter.copy(itemDocument));

		datamodelConverter.setOptionDeepCopy(false);
		assertEquals(itemDocumentFiltered,
				datamodelConverter.copy(itemDocument));
	}

	@Test
	public void testEmptyPropertyFilter() {
		PropertyIdValue p1 = Datamodel.makeWikidataPropertyIdValue("P1");
		PropertyIdValue p2 = Datamodel.makeWikidataPropertyIdValue("P2");
		PropertyIdValue p3 = Datamodel.makeWikidataPropertyIdValue("P3");
		PropertyIdValue p4 = Datamodel.makeWikidataPropertyIdValue("P4");

		DatamodelConverter datamodelConverter = new DatamodelConverter(
				new DataObjectFactoryImpl());
		datamodelConverter.setOptionPropertyFilter(Collections
				.<PropertyIdValue> emptySet());

		List<StatementGroup> statementGroups = new ArrayList<>();
		statementGroups.add(makeTestStatementGroup(p1));
		statementGroups.add(makeTestStatementGroup(p2));
		statementGroups.add(makeTestStatementGroup(p3));
		statementGroups.add(makeTestStatementGroup(p4));

		ItemDocument itemDocument = Datamodel.makeItemDocument(
				Datamodel.makeWikidataItemIdValue("Q42"),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				statementGroups, Collections.<String, SiteLink> emptyMap());

		ItemDocument itemDocumentFiltered = getEmptyItemDocument();

		assertEquals(itemDocumentFiltered,
				datamodelConverter.copy(itemDocument));

		datamodelConverter.setOptionDeepCopy(false);
		assertEquals(itemDocumentFiltered,
				datamodelConverter.copy(itemDocument));
	}

	@Test
	public void testSiteLinkFilter() {
		SiteLink s1 = Datamodel.makeSiteLink("Title 1", "site1",
				Collections.<String> emptyList());
		SiteLink s2 = Datamodel.makeSiteLink("Title 2", "site2",
				Collections.<String> emptyList());
		SiteLink s3 = Datamodel.makeSiteLink("Title 3", "site3",
				Collections.<String> emptyList());
		SiteLink s4 = Datamodel.makeSiteLink("Title 4", "site4",
				Collections.<String> emptyList());

		Set<String> siteLinkFilter = new HashSet<>();
		siteLinkFilter.add("site2");
		siteLinkFilter.add("site4");

		DatamodelConverter datamodelConverter = new DatamodelConverter(
				new DataObjectFactoryImpl());
		datamodelConverter.setOptionSiteLinkFilter(siteLinkFilter);

		Map<String, SiteLink> siteLinks = new HashMap<>();
		siteLinks.put(s1.getSiteKey(), s1);
		siteLinks.put(s2.getSiteKey(), s2);
		siteLinks.put(s3.getSiteKey(), s3);
		siteLinks.put(s4.getSiteKey(), s4);

		Map<String, SiteLink> siteLinksFiltered = new HashMap<>();
		siteLinksFiltered.put(s2.getSiteKey(), s2);
		siteLinksFiltered.put(s4.getSiteKey(), s4);

		ItemDocument itemDocument = Datamodel.makeItemDocument(
				Datamodel.makeWikidataItemIdValue("Q42"),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<StatementGroup> emptyList(), siteLinks);

		ItemDocument itemDocumentFiltered = Datamodel.makeItemDocument(
				Datamodel.makeWikidataItemIdValue("Q42"),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<StatementGroup> emptyList(), siteLinksFiltered);

		assertEquals(siteLinkFilter,
				datamodelConverter.getOptionSiteLinkFilter());
		assertEquals(itemDocumentFiltered,
				datamodelConverter.copy(itemDocument));

		datamodelConverter.setOptionDeepCopy(false);
		assertEquals(itemDocumentFiltered,
				datamodelConverter.copy(itemDocument));
	}

	@Test
	public void testEmptySiteLinkFilter() {
		SiteLink s1 = Datamodel.makeSiteLink("Title 1", "site1",
				Collections.<String> emptyList());
		SiteLink s2 = Datamodel.makeSiteLink("Title 2", "site2",
				Collections.<String> emptyList());
		SiteLink s3 = Datamodel.makeSiteLink("Title 3", "site3",
				Collections.<String> emptyList());
		SiteLink s4 = Datamodel.makeSiteLink("Title 4", "site4",
				Collections.<String> emptyList());

		DatamodelConverter datamodelConverter = new DatamodelConverter(
				new DataObjectFactoryImpl());
		datamodelConverter.setOptionSiteLinkFilter(Collections
				.<String> emptySet());

		Map<String, SiteLink> siteLinks = new HashMap<>();
		siteLinks.put(s1.getSiteKey(), s1);
		siteLinks.put(s2.getSiteKey(), s2);
		siteLinks.put(s3.getSiteKey(), s3);
		siteLinks.put(s4.getSiteKey(), s4);

		Map<String, SiteLink> siteLinksFiltered = new HashMap<>();
		siteLinksFiltered.put(s2.getSiteKey(), s2);
		siteLinksFiltered.put(s4.getSiteKey(), s4);

		ItemDocument itemDocument = Datamodel.makeItemDocument(
				Datamodel.makeWikidataItemIdValue("Q42"),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<StatementGroup> emptyList(), siteLinks);

		ItemDocument itemDocumentFiltered = getEmptyItemDocument();

		assertEquals(itemDocumentFiltered,
				datamodelConverter.copy(itemDocument));

		datamodelConverter.setOptionDeepCopy(false);
		assertEquals(itemDocumentFiltered,
				datamodelConverter.copy(itemDocument));
	}

	@Test
	public void testDeepCopyReferencesFilter() {
		DatamodelConverter datamodelConverter = new DatamodelConverter(
				new DataObjectFactoryImpl());
		datamodelConverter.setOptionDeepCopyReferences(false);

		PropertyIdValue propertyIdValue = Datamodel
				.makeWikidataPropertyIdValue("P1");
		Snak snak = Datamodel.makeSomeValueSnak(propertyIdValue);
		Claim claim = Datamodel.makeClaim(
				Datamodel.makeWikidataItemIdValue("Q42"), snak,
				Collections.<SnakGroup> emptyList());

		SnakGroup snakGroup = Datamodel.makeSnakGroup(Collections
				.singletonList(snak));
		Reference reference = Datamodel.makeReference(Collections
				.singletonList(snakGroup));

		Statement statement = Datamodel.makeStatement(claim,
				Collections.singletonList(reference), StatementRank.NORMAL,
				"statement-id-" + propertyIdValue.getId());
		StatementGroup statementGroup = Datamodel
				.makeStatementGroup(Collections.singletonList(statement));
		ItemDocument itemDocument = Datamodel.makeItemDocument(
				Datamodel.makeWikidataItemIdValue("Q42"),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.singletonList(statementGroup),
				Collections.<String, SiteLink> emptyMap());

		Statement statementFiltered = Datamodel.makeStatement(claim,
				Collections.<Reference> emptyList(), StatementRank.NORMAL,
				"statement-id-" + propertyIdValue.getId());
		StatementGroup statementGroupFiltered = Datamodel
				.makeStatementGroup(Collections
						.singletonList(statementFiltered));
		ItemDocument itemDocumentFiltered = Datamodel.makeItemDocument(
				Datamodel.makeWikidataItemIdValue("Q42"),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.singletonList(statementGroupFiltered),
				Collections.<String, SiteLink> emptyMap());

		assertFalse(datamodelConverter.hasOptionDeepCopyReferences());
		assertEquals(itemDocumentFiltered,
				datamodelConverter.copy(itemDocument));

		datamodelConverter.setOptionDeepCopy(false);
		assertEquals(itemDocument, datamodelConverter.copy(itemDocument));
	}
}
