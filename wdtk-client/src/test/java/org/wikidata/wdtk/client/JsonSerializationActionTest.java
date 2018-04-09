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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.DatamodelMapper;
import org.wikidata.wdtk.datamodel.implementation.EntityDocumentImpl;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.testing.MockDirectoryManager;
import org.wikidata.wdtk.util.CompressionType;
import org.wikidata.wdtk.util.DirectoryManagerFactory;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

public class JsonSerializationActionTest {

	@Test
	public void testDefaults() {
		String[] args = new String[] { "-a", "json" };
		DumpProcessingOutputAction action = DumpProcessingOutputActionTest
				.getActionFromArgs(args);

		assertTrue(action instanceof JsonSerializationAction);
		assertFalse(action.needsSites());
		assertTrue(action.isReady());
		assertEquals(action.getActionName(), "JsonSerializationAction");
	}

	@Test
	public void testJsonOutput() throws IOException {
		String[] args = new String[] { "-a", "json", "-o",
				"/path/to/output.json" };

		DirectoryManagerFactory
				.setDirectoryManagerClass(MockDirectoryManager.class);

		ClientConfiguration config = new ClientConfiguration(args);
		JsonSerializationAction jsa = (JsonSerializationAction) config
				.getActions().get(0);

		ItemIdValue subject1 = Datamodel.makeWikidataItemIdValue("Q42");
		ItemIdValue subject2 = Datamodel.makeWikidataItemIdValue("Q43");
		MonolingualTextValue mtv1 = Datamodel.makeMonolingualTextValue("Test1",
				"en");
		MonolingualTextValue mtv2 = Datamodel.makeMonolingualTextValue("Test2",
				"fr");

		ItemDocument id1 = Datamodel.makeItemDocument(subject1,
				Arrays.asList(mtv1, mtv2), Arrays.asList(mtv1),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<StatementGroup> emptyList(),
				Collections.<String, SiteLink> emptyMap());

		ItemDocument id2 = Datamodel.makeItemDocument(subject2,
				Collections.<MonolingualTextValue> emptyList(),
				Arrays.asList(mtv2),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<StatementGroup> emptyList(),
				Collections.<String, SiteLink> emptyMap());

		PropertyDocument pd1 = Datamodel
				.makePropertyDocument(
						Datamodel.makeWikidataPropertyIdValue("P31"),
						Arrays.asList(mtv1),
						Collections.<MonolingualTextValue> emptyList(),
						Arrays.asList(mtv1),
						Collections.emptyList(),
						Datamodel
								.makeDatatypeIdValue(DatatypeIdValue.DT_MONOLINGUAL_TEXT));

		jsa.open();
		jsa.processItemDocument(id1);
		jsa.processPropertyDocument(pd1);
		jsa.processItemDocument(id2);
		jsa.close();

		MockDirectoryManager mdm = new MockDirectoryManager(
				Paths.get("/path/to/"), false);

		ObjectMapper mapper = new DatamodelMapper(Datamodel.SITE_WIKIDATA);
		ObjectReader documentReader = mapper
				.readerFor(EntityDocumentImpl.class);
		MappingIterator<EntityDocument> documentIterator = documentReader
				.readValues(mdm.getInputStreamForFile("output.json",
						CompressionType.NONE));

		List<EntityDocument> results = new ArrayList<>();
		while (documentIterator.hasNextValue()) {
			EntityDocument document = documentIterator.nextValue();
			results.add(document);
		}
		documentIterator.close();

		assertEquals(3, results.size());
		assertEquals(id1, results.get(0));
		assertEquals(pd1, results.get(1));
		assertEquals(id2, results.get(2));

	}

	@Test
	public void testJsonGzipOutput() throws IOException {
		String[] args = new String[] { "-a", "json", "-o",
				"/path/to/output.json", "-z", "gz" };

		DirectoryManagerFactory
				.setDirectoryManagerClass(MockDirectoryManager.class);

		ClientConfiguration config = new ClientConfiguration(args);
		JsonSerializationAction jsa = (JsonSerializationAction) config
				.getActions().get(0);

		ItemIdValue subject1 = Datamodel.makeWikidataItemIdValue("Q42");
		MonolingualTextValue mtv1 = Datamodel.makeMonolingualTextValue("Test1",
				"en");
		MonolingualTextValue mtv2 = Datamodel.makeMonolingualTextValue("Test2",
				"fr");

		ItemDocument id1 = Datamodel.makeItemDocument(subject1,
				Arrays.asList(mtv1, mtv2), Arrays.asList(mtv1),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<StatementGroup> emptyList(),
				Collections.<String, SiteLink> emptyMap());

		jsa.open();
		jsa.processItemDocument(id1);
		jsa.close();

		MockDirectoryManager mdm = new MockDirectoryManager(
				Paths.get("/path/to/"), false);

		ObjectMapper mapper = new DatamodelMapper(Datamodel.SITE_WIKIDATA);
		ObjectReader documentReader = mapper.readerFor(EntityDocumentImpl.class);
		MappingIterator<EntityDocument> documentIterator = documentReader
				.readValues(mdm.getInputStreamForFile("output.json.gz",
						CompressionType.GZIP));

		List<EntityDocument> results = new ArrayList<>();
		while (documentIterator.hasNextValue()) {
			EntityDocument document = documentIterator.nextValue();
			results.add(document);
		}
		documentIterator.close();

		assertEquals(1, results.size());
		assertEquals(id1, results.get(0));
	}

	@Test
	public void testJsonBz2Output() throws IOException {
		String[] args = new String[] { "-a", "json", "-o", "output.json", "-z",
				"bz2" };

		DirectoryManagerFactory
				.setDirectoryManagerClass(MockDirectoryManager.class);

		ClientConfiguration config = new ClientConfiguration(args);
		JsonSerializationAction jsa = (JsonSerializationAction) config
				.getActions().get(0);

		ItemIdValue subject1 = Datamodel.makeWikidataItemIdValue("Q42");
		MonolingualTextValue mtv1 = Datamodel.makeMonolingualTextValue("Test1",
				"en");
		MonolingualTextValue mtv2 = Datamodel.makeMonolingualTextValue("Test2",
				"fr");

		ItemDocument id1 = Datamodel.makeItemDocument(subject1,
				Arrays.asList(mtv1, mtv2), Arrays.asList(mtv1),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<StatementGroup> emptyList(),
				Collections.<String, SiteLink> emptyMap());

		jsa.open();
		jsa.processItemDocument(id1);
		jsa.close();

		MockDirectoryManager mdm = new MockDirectoryManager(Paths.get("."),
				false);

		ObjectMapper mapper = new DatamodelMapper(Datamodel.SITE_WIKIDATA);
		ObjectReader documentReader = mapper.readerFor(EntityDocumentImpl.class);
		MappingIterator<EntityDocument> documentIterator = documentReader
				.readValues(mdm.getInputStreamForFile("output.json.bz2",
						CompressionType.BZ2));

		List<EntityDocument> results = new ArrayList<>();
		while (documentIterator.hasNextValue()) {
			EntityDocument document = documentIterator.nextValue();
			results.add(document);
		}
		documentIterator.close();

		assertEquals(1, results.size());
		assertEquals(id1, results.get(0));
	}
}
