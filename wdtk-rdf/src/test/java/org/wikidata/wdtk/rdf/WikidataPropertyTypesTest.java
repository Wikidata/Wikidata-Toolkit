package org.wikidata.wdtk.rdf;

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

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.implementation.SitesImpl;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.testing.MockWebResourceFetcher;

public class WikidataPropertyTypesTest {

	final DataObjectFactory factory = new DataObjectFactoryImpl();

	SitesImpl sites = new SitesImpl();

	final WikidataPropertyTypes propertyTypes = new WikidataPropertyTypes();

	@Before
	public void setUp() throws Exception {
		MockWebResourceFetcher wrf = new MockWebResourceFetcher();
		propertyTypes.webResourceFetcher = wrf;
		wrf.setWebResourceContents(
				"https://www.wikidata.org/w/api.php?action=wbgetentities&ids=P10&format=json&props=datatype",
				"{\"entities\":{\"P10\":{\"id\":\"P10\",\"type\":\"property\",\"datatype\":\"commonsMedia\"}},\"success\":1}");
		wrf.setWebResourceContents(
				"https://www.wikidata.org/w/api.php?action=wbgetentities&ids=P1245&format=json&props=datatype",
				"{\"entities\":{\"P1245\":{\"id\":\"P1245\",\"type\":\"property\",\"datatype\":\"string\"}},\"success\":1}");
	}

	@Test
	public void testGetPropertyType() throws IOException, URISyntaxException {
		assertEquals(propertyTypes.getPropertyType(factory.getPropertyIdValue(
				"P1245", "base/")), DatatypeIdValue.DT_STRING);
		assertEquals(propertyTypes.getPropertyType(factory.getPropertyIdValue(
				"P10", "base/")), DatatypeIdValue.DT_COMMONS_MEDIA);

	}

	@Test
	public void testFetchPropertyType() throws IOException, URISyntaxException {

		assertEquals(propertyTypes.fetchPropertyType(factory
				.getPropertyIdValue("P10", "base/")),
				DatatypeIdValue.DT_COMMONS_MEDIA);

	}

	@Test
	public void testSetPropertyTypeFromEntityIdValue() {
		assertEquals(propertyTypes.setPropertyTypeFromEntityIdValue(
				factory.getPropertyIdValue("P1001",
						"http://www.wikidata.org/property"), factory
						.getItemIdValue("Q20",
								"http://www.wikidata.org/entity/")),
				DatatypeIdValue.DT_ITEM);
	}

	@Test
	public void testSetPropertyTypeFromStringValue() {
		assertEquals(propertyTypes.setPropertyTypeFromStringValue(
				factory.getPropertyIdValue("P1245",
						"http://www.wikidata.org/property"), factory
						.getStringValue("6763")),
				"http://www.wikidata.org/ontology#propertyTypeString");
	}

	void printList(List<String> list) {
		for (String str : list) {
			System.out.println(str);
		}
	}

	@Test
	public void testQuicksort() {
		List<String> propertyList = new ArrayList<String>();

		propertyList.add("P17");
		propertyList.add("P107");
		propertyList.add("P3");
		propertyList.add("P29");
		propertyList.add("P17");
		propertyList.add("P89");

		List<String> sortedList = new ArrayList<String>();

		sortedList.add("P3");
		sortedList.add("P17");
		sortedList.add("P17");
		sortedList.add("P29");
		sortedList.add("P89");
		sortedList.add("P107");

		propertyTypes.sortByPropertyKey(propertyList);

		assertEquals(propertyList, sortedList);

	}

	@Test
	public void testGetPropertyList() throws IOException {

		// propertyTypes.getPropertyList(System.out);
	}
}
