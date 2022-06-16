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

import org.junit.Test;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.*;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class DatamodelFilterTest {

	@Test
	public void testEmptyLanguageFilterForItem() {
		DocumentDataFilter documentDataFilter = new DocumentDataFilter();
		documentDataFilter.setLanguageFilter(Collections.emptySet());
		DatamodelFilter filter = new DatamodelFilter(new DataObjectFactoryImpl(), documentDataFilter);

		ItemDocument itemDocument = Datamodel.makeItemDocument(
				Datamodel.makeWikidataItemIdValue("Q42"),
				Arrays.asList(
						Datamodel.makeMonolingualTextValue("Label de", "de"),
						Datamodel.makeMonolingualTextValue("Label en", "en"),
						Datamodel.makeMonolingualTextValue("Label he", "he")
				),
				Arrays.asList(
						Datamodel.makeMonolingualTextValue("Desc en", "en"),
						Datamodel.makeMonolingualTextValue("Desc he", "he")
				),
				Arrays.asList(
						Datamodel.makeMonolingualTextValue("Alias en", "en"),
						Datamodel.makeMonolingualTextValue("Alias de1", "de"),
						Datamodel.makeMonolingualTextValue("Alias de2", "de")
				),
				Collections.emptyList(),
				Collections.emptyMap()
		);

		ItemDocument itemDocumentFiltered = Datamodel.makeItemDocument(Datamodel.makeWikidataItemIdValue("Q42"),
				Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList(), Collections.emptyMap()
		);

		assertEquals(itemDocumentFiltered, filter.filter(itemDocument));
	}

	@Test
	public void testLanguageFilterForItem() {
		Set<String> languageFilter = new HashSet<>();
		languageFilter.add("de");
		languageFilter.add("he");
		DocumentDataFilter documentDataFilter = new DocumentDataFilter();
		documentDataFilter.setLanguageFilter(languageFilter);
		DatamodelFilter filter = new DatamodelFilter(new DataObjectFactoryImpl(), documentDataFilter);

		ItemDocument itemDocument = Datamodel.makeItemDocument(
				Datamodel.makeWikidataItemIdValue("Q42"),
				Arrays.asList(
						Datamodel.makeMonolingualTextValue("Label de", "de"),
						Datamodel.makeMonolingualTextValue("Label en", "en"),
						Datamodel.makeMonolingualTextValue("Label he", "he")
				),
				Arrays.asList(
						Datamodel.makeMonolingualTextValue("Desc en", "en"),
						Datamodel.makeMonolingualTextValue("Desc he", "he")
				),
				Arrays.asList(
						Datamodel.makeMonolingualTextValue("Alias en", "en"),
						Datamodel.makeMonolingualTextValue("Alias de1", "de"),
						Datamodel.makeMonolingualTextValue("Alias de2", "de")
				),
				Collections.emptyList(),
				Collections.emptyMap()
		);

		ItemDocument itemDocumentFiltered = Datamodel.makeItemDocument(
				Datamodel.makeWikidataItemIdValue("Q42"),
				Arrays.asList(
						Datamodel.makeMonolingualTextValue("Label de", "de"),
						Datamodel.makeMonolingualTextValue("Label he", "he")
				),
				Collections.singletonList(
						Datamodel.makeMonolingualTextValue("Desc he", "he")
				),
				Arrays.asList(
						Datamodel.makeMonolingualTextValue("Alias de1", "de"),
						Datamodel.makeMonolingualTextValue("Alias de2", "de")
				),
				Collections.emptyList(),
				Collections.emptyMap()
		);

		assertEquals(itemDocumentFiltered, filter.filter(itemDocument));
	}

	@Test
	public void testLanguageFilterForProperty() {
		Set<String> languageFilter = new HashSet<>();
		languageFilter.add("de");
		languageFilter.add("he");
		DocumentDataFilter documentDataFilter = new DocumentDataFilter();
		documentDataFilter.setLanguageFilter(languageFilter);
		DatamodelFilter filter = new DatamodelFilter(new DataObjectFactoryImpl(), documentDataFilter);

		PropertyDocument propertyDocument = Datamodel.makePropertyDocument(
				Datamodel.makeWikidataPropertyIdValue("P42"),
				Arrays.asList(
						Datamodel.makeMonolingualTextValue("Label de", "de"),
						Datamodel.makeMonolingualTextValue("Label en", "en"),
						Datamodel.makeMonolingualTextValue("Label he", "he")
				),
				Arrays.asList(
						Datamodel.makeMonolingualTextValue("Desc en", "en"),
						Datamodel.makeMonolingualTextValue("Desc he", "he")
				),
				Arrays.asList(
						Datamodel.makeMonolingualTextValue("Alias en", "en"),
						Datamodel.makeMonolingualTextValue("Alias de1", "de"),
						Datamodel.makeMonolingualTextValue("Alias de2", "de")
				),
				Collections.emptyList(),
				Datamodel.makeDatatypeIdValue(DatatypeIdValue.DT_STRING)
		);

		PropertyDocument propertyDocumentFiltered = Datamodel.makePropertyDocument(
				Datamodel.makeWikidataPropertyIdValue("P42"),
				Arrays.asList(
						Datamodel.makeMonolingualTextValue("Label de", "de"),
						Datamodel.makeMonolingualTextValue("Label he", "he")
				),
				Collections.singletonList(
						Datamodel.makeMonolingualTextValue("Desc he", "he")
				),
				Arrays.asList(
						Datamodel.makeMonolingualTextValue("Alias de1", "de"),
						Datamodel.makeMonolingualTextValue("Alias de2", "de")
				),
				Collections.emptyList(),
				Datamodel.makeDatatypeIdValue(DatatypeIdValue.DT_STRING)
		);

		assertEquals(propertyDocumentFiltered, filter.filter(propertyDocument));
	}

	@Test
	public void testLanguageFilterForMediaInfo() {
		Set<String> languageFilter = new HashSet<>();
		languageFilter.add("de");
		languageFilter.add("he");
		DocumentDataFilter documentDataFilter = new DocumentDataFilter();
		documentDataFilter.setLanguageFilter(languageFilter);
		DatamodelFilter filter = new DatamodelFilter(new DataObjectFactoryImpl(), documentDataFilter);

		MediaInfoDocument mediaInfoDocument = Datamodel.makeMediaInfoDocument(
				Datamodel.makeWikimediaCommonsMediaInfoIdValue("M42"),
				Arrays.asList(
						Datamodel.makeMonolingualTextValue("Label de", "de"),
						Datamodel.makeMonolingualTextValue("Label en", "en"),
						Datamodel.makeMonolingualTextValue("Label he", "he")
				),
				Collections.emptyList()
		);

		MediaInfoDocument mediaInfoDocumentFiltered = Datamodel.makeMediaInfoDocument(
				Datamodel.makeWikimediaCommonsMediaInfoIdValue("M42"),
				Arrays.asList(
						Datamodel.makeMonolingualTextValue("Label de", "de"),
						Datamodel.makeMonolingualTextValue("Label he", "he")
				),
				Collections.emptyList()
		);

		assertEquals(mediaInfoDocumentFiltered, filter.filter(mediaInfoDocument));
	}

	/**
	 * Creates a statement group using the given property. The subject of the
	 * statement group will be Wikidata's Q42.
	 *
	 * @param propertyIdValue the property to use for the main snak of the claim of the
	 *                        statements in this statement group
	 * @return the new statement group
	 */
	private StatementGroup makeTestStatementGroup(
			PropertyIdValue propertyIdValue, EntityIdValue subjectIdValue) {
		Statement statement = Datamodel.makeStatement(
				subjectIdValue,
				Datamodel.makeSomeValueSnak(propertyIdValue),
				Collections.emptyList(), Collections.emptyList(),
				StatementRank.NORMAL, "statement-id-" + propertyIdValue.getId());
		return Datamodel.makeStatementGroup(Collections.singletonList(statement));
	}

	@Test
	public void testEmptyPropertyFilterForItem() {
		ItemIdValue s = Datamodel.makeWikidataItemIdValue("Q42");
		PropertyIdValue p1 = Datamodel.makeWikidataPropertyIdValue("P1");
		PropertyIdValue p2 = Datamodel.makeWikidataPropertyIdValue("P2");
		PropertyIdValue p3 = Datamodel.makeWikidataPropertyIdValue("P3");
		PropertyIdValue p4 = Datamodel.makeWikidataPropertyIdValue("P4");

		DocumentDataFilter documentDataFilter = new DocumentDataFilter();
		documentDataFilter.setPropertyFilter(Collections.emptySet());
		DatamodelFilter filter = new DatamodelFilter(new DataObjectFactoryImpl(), documentDataFilter);

		ItemDocument itemDocument = Datamodel.makeItemDocument(
				s,
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				Arrays.asList(
						makeTestStatementGroup(p1, s),
						makeTestStatementGroup(p2, s),
						makeTestStatementGroup(p3, s),
						makeTestStatementGroup(p4, s)
				),
				Collections.emptyMap()
		);

		ItemDocument itemDocumentFiltered = Datamodel.makeItemDocument(s,
				Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList(), Collections.emptyMap()
		);

		assertEquals(itemDocumentFiltered, filter.filter(itemDocument));
	}

	@Test
	public void testPropertyFilterForItem() {
		ItemIdValue s = Datamodel.makeWikidataItemIdValue("Q42");
		PropertyIdValue p1 = Datamodel.makeWikidataPropertyIdValue("P1");
		PropertyIdValue p2 = Datamodel.makeWikidataPropertyIdValue("P2");
		PropertyIdValue p3 = Datamodel.makeWikidataPropertyIdValue("P3");
		PropertyIdValue p4 = Datamodel.makeWikidataPropertyIdValue("P4");

		Set<PropertyIdValue> propertyFilter = new HashSet<>();
		propertyFilter.add(p1);
		propertyFilter.add(p3);
		DocumentDataFilter documentDataFilter = new DocumentDataFilter();
		documentDataFilter.setPropertyFilter(propertyFilter);
		DatamodelFilter filter = new DatamodelFilter(new DataObjectFactoryImpl(), documentDataFilter);

		ItemDocument itemDocument = Datamodel.makeItemDocument(
				s,
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				Arrays.asList(
						makeTestStatementGroup(p1, s),
						makeTestStatementGroup(p2, s),
						makeTestStatementGroup(p3, s),
						makeTestStatementGroup(p4, s)
				),
				Collections.emptyMap()
		);

		ItemDocument itemDocumentFiltered = Datamodel.makeItemDocument(
				s,
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				Arrays.asList(
						makeTestStatementGroup(p1, s),
						makeTestStatementGroup(p3, s)
				),
				Collections.emptyMap()
		);

		assertEquals(itemDocumentFiltered, filter.filter(itemDocument));
	}

	@Test
	public void testPropertyFilterForProperty() {
		PropertyIdValue s = Datamodel.makeWikidataPropertyIdValue("P42");
		PropertyIdValue p1 = Datamodel.makeWikidataPropertyIdValue("P1");
		PropertyIdValue p2 = Datamodel.makeWikidataPropertyIdValue("P2");
		PropertyIdValue p3 = Datamodel.makeWikidataPropertyIdValue("P3");
		PropertyIdValue p4 = Datamodel.makeWikidataPropertyIdValue("P4");

		Set<PropertyIdValue> propertyFilter = new HashSet<>();
		propertyFilter.add(p1);
		propertyFilter.add(p3);
		DocumentDataFilter documentDataFilter = new DocumentDataFilter();
		documentDataFilter.setPropertyFilter(propertyFilter);
		DatamodelFilter filter = new DatamodelFilter(new DataObjectFactoryImpl(), documentDataFilter);

		PropertyDocument propertyDocument = Datamodel.makePropertyDocument(
				s,
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				Arrays.asList(
						makeTestStatementGroup(p1, s),
						makeTestStatementGroup(p2, s),
						makeTestStatementGroup(p3, s),
						makeTestStatementGroup(p4, s)
				),
				Datamodel.makeDatatypeIdValue(DatatypeIdValue.DT_STRING)
		);

		PropertyDocument propertyDocumentFiltered = Datamodel.makePropertyDocument(
				s,
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				Arrays.asList(
						makeTestStatementGroup(p1, s),
						makeTestStatementGroup(p3, s)
				),
				Datamodel.makeDatatypeIdValue(DatatypeIdValue.DT_STRING)
		);

		assertEquals(propertyDocumentFiltered, filter.filter(propertyDocument));
	}

	@Test
	public void testPropertyFilterForLexeme() {
		LexemeIdValue l = Datamodel.makeWikidataLexemeIdValue("L42");
		FormIdValue f = Datamodel.makeWikidataFormIdValue("L42-F1");
		SenseIdValue s = Datamodel.makeWikidataSenseIdValue("L42-S1");
		PropertyIdValue p1 = Datamodel.makeWikidataPropertyIdValue("P1");
		PropertyIdValue p2 = Datamodel.makeWikidataPropertyIdValue("P2");
		PropertyIdValue p3 = Datamodel.makeWikidataPropertyIdValue("P3");
		PropertyIdValue p4 = Datamodel.makeWikidataPropertyIdValue("P4");

		Set<PropertyIdValue> propertyFilter = new HashSet<>();
		propertyFilter.add(p1);
		propertyFilter.add(p3);
		DocumentDataFilter documentDataFilter = new DocumentDataFilter();
		documentDataFilter.setPropertyFilter(propertyFilter);
		DatamodelFilter filter = new DatamodelFilter(new DataObjectFactoryImpl(), documentDataFilter);

		LexemeDocument lexemeDocument = Datamodel.makeLexemeDocument(
				l,
				Datamodel.makeWikidataItemIdValue("Q1"),
				Datamodel.makeWikidataItemIdValue("Q1"),
				Collections.singletonList(Datamodel.makeMonolingualTextValue("foo", "en")),
				Arrays.asList(
						makeTestStatementGroup(p1, l),
						makeTestStatementGroup(p2, l),
						makeTestStatementGroup(p3, l),
						makeTestStatementGroup(p4, l)
				),
				Collections.singletonList(Datamodel.makeFormDocument(
						f,
						Collections.singletonList(Datamodel.makeMonolingualTextValue("foo", "en")),
						Collections.emptyList(),
						Arrays.asList(
								makeTestStatementGroup(p1, f),
								makeTestStatementGroup(p2, f),
								makeTestStatementGroup(p3, f)
						)
				)),
				Collections.singletonList(Datamodel.makeSenseDocument(
						s,
						Collections.singletonList(Datamodel.makeMonolingualTextValue("foo", "en")),
						Arrays.asList(
								makeTestStatementGroup(p1, s),
								makeTestStatementGroup(p2, s),
								makeTestStatementGroup(p3, s)
						)
				))
		);

		LexemeDocument lexemeDocumentFiltered = Datamodel.makeLexemeDocument(
				l,
				Datamodel.makeWikidataItemIdValue("Q1"),
				Datamodel.makeWikidataItemIdValue("Q1"),
				Collections.singletonList(Datamodel.makeMonolingualTextValue("foo", "en")),
				Arrays.asList(
						makeTestStatementGroup(p1, l),
						makeTestStatementGroup(p3, l)
				),
				Collections.singletonList(Datamodel.makeFormDocument(
						f,
						Collections.singletonList(Datamodel.makeMonolingualTextValue("foo", "en")),
						Collections.emptyList(),
						Arrays.asList(
								makeTestStatementGroup(p1, f),
								makeTestStatementGroup(p3, f)
						)
				)),
				Collections.singletonList(Datamodel.makeSenseDocument(
						s,
						Collections.singletonList(Datamodel.makeMonolingualTextValue("foo", "en")),
						Arrays.asList(
								makeTestStatementGroup(p1, s),
								makeTestStatementGroup(p3, s)
						)
				))
		);

		assertEquals(lexemeDocumentFiltered, filter.filter(lexemeDocument));
	}

	@Test
	public void testPropertyFilterForMediaInfo() {
		MediaInfoIdValue s = Datamodel.makeWikimediaCommonsMediaInfoIdValue("M42");
		PropertyIdValue p1 = Datamodel.makeWikidataPropertyIdValue("P1");
		PropertyIdValue p2 = Datamodel.makeWikidataPropertyIdValue("P2");
		PropertyIdValue p3 = Datamodel.makeWikidataPropertyIdValue("P3");
		PropertyIdValue p4 = Datamodel.makeWikidataPropertyIdValue("P4");

		Set<PropertyIdValue> propertyFilter = new HashSet<>();
		propertyFilter.add(p1);
		propertyFilter.add(p3);
		DocumentDataFilter documentDataFilter = new DocumentDataFilter();
		documentDataFilter.setPropertyFilter(propertyFilter);
		DatamodelFilter filter = new DatamodelFilter(new DataObjectFactoryImpl(), documentDataFilter);

		MediaInfoDocument mediaInfoDocument = Datamodel.makeMediaInfoDocument(
				s,
				Collections.emptyList(),
				Arrays.asList(
						makeTestStatementGroup(p1, s),
						makeTestStatementGroup(p2, s),
						makeTestStatementGroup(p3, s),
						makeTestStatementGroup(p4, s)
				)
		);

		MediaInfoDocument mediaInfoDocumentFiltered = Datamodel.makeMediaInfoDocument(
				s,
				Collections.emptyList(),
				Arrays.asList(
						makeTestStatementGroup(p1, s),
						makeTestStatementGroup(p3, s)
				)
		);

		assertEquals(mediaInfoDocumentFiltered, filter.filter(mediaInfoDocument));
	}

	@Test
	public void testEmptySiteLinkFilterForItem() {
		SiteLink s1 = Datamodel.makeSiteLink("Title 1", "site1", Collections.emptyList());
		SiteLink s2 = Datamodel.makeSiteLink("Title 2", "site2", Collections.emptyList());
		SiteLink s3 = Datamodel.makeSiteLink("Title 3", "site3", Collections.emptyList());
		SiteLink s4 = Datamodel.makeSiteLink("Title 4", "site4", Collections.emptyList());

		DocumentDataFilter documentDataFilter = new DocumentDataFilter();
		documentDataFilter.setSiteLinkFilter(Collections.emptySet());
		DatamodelFilter filter = new DatamodelFilter(new DataObjectFactoryImpl(), documentDataFilter);

		Map<String, SiteLink> siteLinks = new HashMap<>();
		siteLinks.put(s1.getSiteKey(), s1);
		siteLinks.put(s2.getSiteKey(), s2);
		siteLinks.put(s3.getSiteKey(), s3);
		siteLinks.put(s4.getSiteKey(), s4);

		ItemDocument itemDocument = Datamodel.makeItemDocument(
				Datamodel.makeWikidataItemIdValue("Q42"),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				siteLinks
		);

		ItemDocument itemDocumentFiltered = Datamodel.makeItemDocument(Datamodel.makeWikidataItemIdValue("Q42"),
				Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList(), Collections.emptyMap()
		);

		assertEquals(itemDocumentFiltered, filter.filter(itemDocument));
	}


	@Test
	public void testSiteLinkFilterForItem() {
		SiteLink s1 = Datamodel.makeSiteLink("Title 1", "site1", Collections.emptyList());
		SiteLink s2 = Datamodel.makeSiteLink("Title 2", "site2", Collections.emptyList());
		SiteLink s3 = Datamodel.makeSiteLink("Title 3", "site3", Collections.emptyList());
		SiteLink s4 = Datamodel.makeSiteLink("Title 4", "site4", Collections.emptyList());

		Set<String> siteLinkFilter = new HashSet<>();
		siteLinkFilter.add("site2");
		siteLinkFilter.add("site4");
		DocumentDataFilter documentDataFilter = new DocumentDataFilter();
		documentDataFilter.setSiteLinkFilter(siteLinkFilter);
		DatamodelFilter filter = new DatamodelFilter(new DataObjectFactoryImpl(), documentDataFilter);

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
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				siteLinks
		);

		ItemDocument itemDocumentFiltered = Datamodel.makeItemDocument(
				Datamodel.makeWikidataItemIdValue("Q42"),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				siteLinksFiltered
		);

		assertEquals(itemDocumentFiltered, filter.filter(itemDocument));
	}
}
