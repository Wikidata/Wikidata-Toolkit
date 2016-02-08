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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.wikidata.wdtk.datamodel.interfaces.Sites;
import org.wikidata.wdtk.dumpfiles.DumpContentType;
import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.dumpfiles.MwDumpFile;
import org.wikidata.wdtk.testing.MockDirectoryManager;
import org.wikidata.wdtk.util.CompressionType;
import org.wikidata.wdtk.util.DirectoryManagerFactory;

public class ClientTest {

	DumpProcessingController mockDpc;

	@Before
	public void setup() throws IOException {
		mockDpc = Mockito.mock(DumpProcessingController.class);

		MwDumpFile mockDump = Mockito.mock(MwDumpFile.class);
		Mockito.when(mockDump.getProjectName()).thenReturn("wikidata");
		Mockito.when(mockDump.getDateStamp()).thenReturn("20150303");

		Mockito.when(mockDpc.getMostRecentDump(DumpContentType.JSON))
				.thenReturn(mockDump);

		Sites mockSites = Mockito.mock(Sites.class);
		Mockito.when(mockDpc.getSitesInformation()).thenReturn(mockSites);
	}

	@Test
	public void testDefaultLoggingConfig() throws ParseException, IOException {
		String[] args = new String[] {};
		Client client = new Client(mockDpc, args);
		client.performActions(); // print help

		assertEquals(Level.INFO, Client.consoleAppender.getThreshold());
		assertEquals(Level.WARN, Client.errorAppender.getThreshold());
	}

	@Test
	public void testQuietStdOutLoggingConfig() throws ParseException,
			IOException {
		String[] args = new String[] { "-a", "json", "-s" };
		new Client(mockDpc, args);

		assertEquals(Level.OFF, Client.consoleAppender.getThreshold());
		assertEquals(Level.WARN, Client.errorAppender.getThreshold());
	}

	@Test
	public void testQuietLoggingConfig() throws ParseException, IOException {
		String[] TEST_ARGS = new String[] { "-a", "json", "-q" };
		new Client(mockDpc, TEST_ARGS);

		assertEquals(Level.OFF, Client.consoleAppender.getThreshold());
		assertEquals(Level.WARN, Client.errorAppender.getThreshold());
	}

	@Test
	public void testJsonOutput() {
		String[] args = { "-a", "json", "-o", "output/wikidata.json" };
		ClientConfiguration configuration = new ClientConfiguration(args);
		DumpProcessingAction action = configuration.actions.get(0);
		action.open();
		action.close();
		assertTrue(action
				.getReport()
				.matches(
						"Finished serialization of \\d+ EntityDocuments in file output/wikidata.json"));
	}

	@Test
	public void testRdfOutput() {
		String[] args = { "-a", "rdf", "-o", "output/wikidata.rdf" };
		ClientConfiguration configuration = new ClientConfiguration(args);
		DumpProcessingAction action = configuration.actions.get(0);
		action.open();
		action.close();
		assertTrue(action
				.getReport()
				.matches(
						"Finished serialization of \\d+ RDF triples in file output/wikidata.rdf"));
	}

	public void testNonReadyActionWithDumps() throws ParseException,
			IOException {
		String[] args = new String[] { "-a", "rdf", "--dumps", "/tmp" };
		Client client = new Client(mockDpc, args);
		client.performActions(); // print help

		Mockito.verify(mockDpc, Mockito.never()).processDump(
				Mockito.<MwDumpFile> any());
		Mockito.verify(mockDpc, Mockito.never()).getSitesInformation();
		Mockito.verify(mockDpc).setDownloadDirectory("/tmp");
	}

	@Test
	public void testSitesAction() throws ParseException, IOException {
		String[] args = new String[] { "-a", "rdf", "--rdftasks",
				"items,labels" };
		Client client = new Client(mockDpc, args);
		client.performActions();

		Mockito.verify(mockDpc).processDump(Mockito.<MwDumpFile> any());
		Mockito.verify(mockDpc).getSitesInformation();
	}

	@Test
	public void testSetDumpsDirectoryException() throws ParseException,
			IOException {
		Mockito.doThrow(new IOException("Mock exception for testing."))
				.when(mockDpc).setDownloadDirectory(Mockito.anyString());

		String[] args = new String[] { "-a", "rdf", "--rdftasks",
				"items,labels", "--dumps", "/tmp/" };
		Client client = new Client(mockDpc, args);
		client.performActions(); // print help

		Mockito.verify(mockDpc, Mockito.never()).processDump(
				Mockito.<MwDumpFile> any());
		Mockito.verify(mockDpc, Mockito.never()).getSitesInformation();
	}

	@Test
	public void testSitesActionException() throws ParseException, IOException {
		Mockito.doThrow(new IOException()).when(mockDpc).getSitesInformation();

		String[] args = new String[] { "-a", "rdf", "--rdftasks",
				"items,labels" };
		Client client = new Client(mockDpc, args);
		client.performActions(); // print help

		Mockito.verify(mockDpc, Mockito.never()).processDump(
				Mockito.<MwDumpFile> any());
		Mockito.verify(mockDpc).getSitesInformation();
	}

	@Test
	public void testNonSitesAction() throws ParseException, IOException {
		String[] args = new String[] { "-a", "json", "-q" };
		Client client = new Client(mockDpc, args);
		client.performActions(); // print help

		Mockito.verify(mockDpc).processDump(Mockito.<MwDumpFile> any());
		Mockito.verify(mockDpc, Mockito.never()).getSitesInformation();
	}

	@Test
	public void testWriteReport() throws IOException {
		DirectoryManagerFactory
				.setDirectoryManagerClass(MockDirectoryManager.class);

		MockDirectoryManager mdm = new MockDirectoryManager(
				Paths.get("/output/"), false);

		String[] args = {"-n", "-a", "rdf", "--rdftasks", "aliases", "-o",
				"/output/wikidata.rdf", "-r", "/output/report.txt" };

		Client client = new Client(mockDpc, args);
		DumpProcessingAction action = client.clientConfiguration.actions.get(0);
		action.open();
		action.close();
		client.writeReport();
		assertTrue(IOUtils
				.toString(
						mdm.getInputStreamForFile("report.txt",
								CompressionType.NONE))
				.matches(
						"RdfSerializationAction: Finished serialization of \\d+ RDF triples in file /output/wikidata.rdf"
								+ System.lineSeparator()));

	}

	@Test
	public void testInsertDumpInformation() throws IOException {
		DirectoryManagerFactory
				.setDirectoryManagerClass(MockDirectoryManager.class);

		MockDirectoryManager mdm = new MockDirectoryManager(
				Paths.get("/output/"), false);

		String[] args = { "-n", "-a", "rdf", "-o", "/output/wikidata.rdf",
				"--rdftasks", "aliases", "-r", "/output/report-{DATE}.txt" };

		Client client = new Client(mockDpc, args);
		client.performActions();

		assertTrue(mdm.hasFile("/output/report-"
				+ client.clientConfiguration.getDateStamp() + ".txt"));
	}

	@Test
	public void testNonExistingLocalDump() {
		String[] args = { "-f", "./asfjl.json" };
		Client client = new Client(mockDpc, args);
		client.performActions();

		Mockito.verify(mockDpc, Mockito.never()).processDump(
				Mockito.<MwDumpFile> any());
	}
}
