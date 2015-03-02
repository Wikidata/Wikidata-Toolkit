package org.wikidata.wdtk.client;

/*
 * #%L
 * Wikidata Toolkit Command-line Tool
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.rdf.RdfSerializer;

public class ClientConfigurationTest {

	@Test
	public void testReadConfigFile() throws IOException {
		String configFile = "src/test/resources/testConf.ini";
		String[] args = new String[] { "-c", configFile };
		ClientConfiguration config = new ClientConfiguration(args);

		assertTrue(config.getOfflineMode());
		assertTrue(config.isQuiet());
		assertEquals(config.getDumpLocation(), "dumps/wikidata/");
		assertEquals(Collections.<String> emptySet(),
				config.getFilterSiteKeys());
		assertEquals(Collections.<PropertyIdValue> singleton(Datamodel
				.makeWikidataPropertyIdValue("P31")),
				config.getFilterProperties());
		Set<String> langFilters = new HashSet<>();
		langFilters.add("fr");
		langFilters.add("zh");
		assertEquals(langFilters, config.getFilterLanguages());

		assertEquals(config.getActions().size(), 2);
		assertTrue(config.getActions().get(0) instanceof RdfSerializationAction);
		assertTrue(config.getActions().get(1) instanceof JsonSerializationAction);
		RdfSerializationAction rdfAction = (RdfSerializationAction) config
				.getActions().get(0);
		JsonSerializationAction jsonAction = (JsonSerializationAction) config
				.getActions().get(1);

		assertTrue(rdfAction.useStdOut);
		assertEquals(rdfAction.compressionType,
				DumpProcessingOutputAction.COMPRESS_GZIP);
		assertEquals(rdfAction.outputDestination, "/tmp/wikidata-items.nt");
		assertEquals(rdfAction.tasks, RdfSerializer.TASK_ITEMS
				| RdfSerializer.TASK_STATEMENTS | RdfSerializer.TASK_TERMS);

		assertFalse(jsonAction.useStdOut);
		assertEquals(jsonAction.compressionType,
				DumpProcessingOutputAction.COMPRESS_BZ2);
		assertEquals(jsonAction.outputDestination, "/tmp/wikidata-dump.json");
	}

	@Test
	public void testDefaultArguments() {
		String[] args = new String[] {};
		ClientConfiguration config = new ClientConfiguration(args);
		assertFalse(config.getOfflineMode());
		assertEquals(config.getDumpLocation(), null);
		assertEquals(config.getFilterLanguages(), null);
		assertEquals(config.getFilterSiteKeys(), null);
		assertEquals(config.getFilterProperties(), null);
		assertFalse(config.isQuiet());
	}

	@Test
	public void testUnknownAction() {
		String[] args = new String[] { "-a", "notImplemented" };
		ClientConfiguration config = new ClientConfiguration(args);
		assertEquals(config.getActions().size(), 0);
	}

	@Test
	public void testUnknownArguments() {
		String[] args = new String[] { "--unknown", "-foo" };
		ClientConfiguration config = new ClientConfiguration(args);
		assertFalse(config.getOfflineMode());
		assertEquals(config.getDumpLocation(), null);
		assertFalse(config.isQuiet());
	}

	@Test
	public void testDumpLocationArgumentsShort() {
		String[] args = new String[] { "-d", "dumps/wikidata/" };
		ClientConfiguration config = new ClientConfiguration(args);
		assertEquals(config.getDumpLocation(), "dumps/wikidata/");
	}

	@Test
	public void testDumpLocationArgumentsLong() {
		String[] args = new String[] { "--dumps", "dumps/wikidata/" };
		ClientConfiguration config = new ClientConfiguration(args);
		assertEquals(config.getDumpLocation(), "dumps/wikidata/");
	}

	@Test
	public void testOfflineModeArgumentsShort() {
		String[] args = new String[] { "-n" };
		ClientConfiguration config = new ClientConfiguration(args);
		assertTrue(config.getOfflineMode());
	}

	@Test
	public void testOfflineModeArgumentsLong() {
		String[] args = new String[] { "--offline" };
		ClientConfiguration config = new ClientConfiguration(args);
		assertTrue(config.getOfflineMode());
	}

	@Test
	public void testStdOutOutputArgumentsShort() {
		String[] args = new String[] { "-a", "json", "-s" };
		ClientConfiguration config = new ClientConfiguration(args);
		assertTrue(config.isQuiet());
	}

	@Test
	public void testStdOutOutputArgumentsLong() {
		String[] args = new String[] { "--action", "json", "--stdout" };
		ClientConfiguration config = new ClientConfiguration(args);
		assertTrue(config.isQuiet());
	}

	@Test
	public void testQuietArgumentsShort() {
		String[] args = new String[] { "-q" };
		ClientConfiguration config = new ClientConfiguration(args);
		assertTrue(config.isQuiet());
	}

	@Test
	public void testQuietArgumentsLong() {
		String[] args = new String[] { "--quiet" };
		ClientConfiguration config = new ClientConfiguration(args);
		assertTrue(config.isQuiet());
	}

	@Test
	public void testLanguageFilterArguments() {
		String[] args = new String[] { "--fLang", "en,de" };
		ClientConfiguration config = new ClientConfiguration(args);

		Set<String> langFilters = new HashSet<>();
		langFilters.add("en");
		langFilters.add("de");

		assertEquals(langFilters, config.getFilterLanguages());
	}

	@Test
	public void testLanguageFilterArgumentsEmpty() {
		String[] args = new String[] { "--fLang", "-" };
		ClientConfiguration config = new ClientConfiguration(args);

		Set<String> langFilters = new HashSet<>();

		assertEquals(langFilters, config.getFilterLanguages());
	}

	@Test
	public void testSiteLinkFilterArguments() {
		String[] args = new String[] { "--fSite", "fawiki,dewiki" };
		ClientConfiguration config = new ClientConfiguration(args);

		Set<String> siteFilters = new HashSet<>();
		siteFilters.add("fawiki");
		siteFilters.add("dewiki");

		assertEquals(siteFilters, config.getFilterSiteKeys());
	}

	@Test
	public void testSiteLinkFilterArgumentsEmpty() {
		String[] args = new String[] { "--fSite", "-" };
		ClientConfiguration config = new ClientConfiguration(args);

		Set<String> siteFilters = new HashSet<>();

		assertEquals(siteFilters, config.getFilterSiteKeys());
	}

	@Test
	public void testPropertyFilterArguments() {
		String[] args = new String[] { "--fProp", "P100,P31" };
		ClientConfiguration config = new ClientConfiguration(args);

		Set<PropertyIdValue> propFilters = new HashSet<>();
		propFilters.add(Datamodel.makeWikidataPropertyIdValue("P31"));
		propFilters.add(Datamodel.makeWikidataPropertyIdValue("P100"));

		assertEquals(propFilters, config.getFilterProperties());
	}

	@Test
	public void testPropertyFilterArgumentsEmpty() {
		String[] args = new String[] { "--fProp", "-" };
		ClientConfiguration config = new ClientConfiguration(args);

		Set<PropertyIdValue> propFilters = new HashSet<>();

		assertEquals(propFilters, config.getFilterProperties());
	}

}
