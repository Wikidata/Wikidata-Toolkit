package org.wikidata.wdtk.clt;

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

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.testing.MockStringContentFactory;

public class ConversionPropertiesTest {

	final static String[] TEST_ARGS = new String[] { "-s", "-d", "/somewhere/",
			"-f", "rdf", "-r", "TERMS", "-n", "-e", ".bz2", "-l",
			"dumps/wikidata/" };
	final static String FILE_TEST_CONFIG = "testConf.ini";
	final ConversionProperties properties = new ConversionProperties(TEST_ARGS);

	@Before
	public void setUp() throws Exception {
	}

	String getResource(String fileName) throws IOException {
		return MockStringContentFactory
				.getStringFromUrl(ConversionPropertiesTest.class
						.getResource("/" + fileName));
	}

	Boolean compareConfigurations(ConversionConfiguration conf1,
			ConversionConfiguration conf2) {
		Boolean result = true;
		if (conf1.getOfflineMode().equals(conf2.getOfflineMode()) != true) {
			result = false;
		}
		if (conf1.getStdout().equals(conf2.getStdout()) != true) {
			result = false;
		}
		if (conf1.getCompressionExtension().equals(
				conf2.getCompressionExtension()) != true) {
			result = false;
		}
		if (conf1.getDumplocation().equals(conf2.getDumplocation()) != true) {
			result = false;
		}
		if (conf1.getOutputDestination().equals(conf2.getOutputDestination()) != true) {
			result = false;
		}
		if (conf1.getOutputFormat().equals(conf2.getOutputFormat()) != true) {
			result = false;
		}
		if (conf1.getRdfdump().equals(conf2.getRdfdump()) != true) {
			result = false;
		}
		return result;
	}

	@Test
	public void testOptions() throws IOException {
		HelpFormatter formatter = new HelpFormatter();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(out);
		formatter.printHelp(writer, 1000, "ConversionClient", "",
				properties.getOptions(), 0, 3, "");
		writer.close();
		assertEquals(out.toString(), getResource("help.txt"));
	}

	@Test
	public void testReadOutConfigFile() throws IOException {
		List<ConversionConfiguration> configurations = properties
				.readOutConfigFile("src/test/resources/testConf.ini");

		ConversionConfiguration comparison = new ConversionConfiguration();

		comparison.setStdout(true);
		comparison.setOfflineMode(false);
		comparison.setOutputFormat("rdf");
		comparison.setCompressionExtension(".gz");
		comparison.setRdfdump("ALL_EXACT_DATA");
		comparison.setOutputDestination("/tmp/");
		comparison.setDumplocation("dumps/wikidata/");

		assertTrue(compareConfigurations(configurations.get(1), comparison));
	}

	@Test
	public void testGetProperties() throws ParseException, IOException {
		List<ConversionConfiguration> propertyList = properties.getProperties();
		ConversionConfiguration comparison = new ConversionConfiguration();
		comparison.setCompressionExtension(".bz2");
		comparison.setDumplocation("dumps/wikidata/");
		comparison.setOfflineMode(true);
		comparison.setOutputDestination("/somewhere/");
		comparison.setOutputFormat("rdf");
		comparison.setRdfdump("TERMS");
		comparison.setStdout(true);
		assertTrue(compareConfigurations(propertyList.get(0), comparison));
	}

}
