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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

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
	final ConversionProperties properties = new ConversionProperties();

	public ConversionPropertiesTest() throws ParseException, IOException {
		properties.handleArguments(TEST_ARGS);
	}

	@Before
	public void setUp() throws Exception {
	}

	String getResource(String fileName) throws IOException {
		return MockStringContentFactory
				.getStringFromUrl(ConversionPropertiesTest.class
						.getResource("/" + fileName));
	}

	Boolean compareOutputConfigurations(OutputConfiguration conf1,
			OutputConfiguration conf2) {
		Boolean result = true;
		if (conf1.getUseStdout().equals(conf2.getUseStdout()) != true) {
			result = false;
		}
		if (conf1.getCompressionExtension().equals(
				conf2.getCompressionExtension()) != true) {
			result = false;
		}
		if (conf1.getOutputDestination().equals(conf2.getOutputDestination()) != true) {
			result = false;
		}
		if (conf1.getOutputFormat().equals(conf2.getOutputFormat()) != true) {
			result = false;
		}
		if (conf1 instanceof RdfConfiguration) {
			if (!(conf2 instanceof RdfConfiguration)) {
				return false;
			} else {
				if (((RdfConfiguration) conf1).getRdfdump().equals(
						((RdfConfiguration) conf2).getRdfdump()) != true) {
					result = false;
				}
			}
		}
		return result;
	}

	Boolean compareConversionProperties(ConversionProperties props1,
			ConversionProperties props2) {
		Boolean result = true;
		if (props1.getOfflineMode().equals(props2.getOfflineMode()) != true) {
			result = false;
		}
		if (props1.getDumplocation().equals(props2.getDumplocation()) != true) {
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
				ConversionProperties.options, 0, 3, "");
		writer.close();
		assertEquals(out.toString(), getResource("help.txt"));
	}

	@Test
	public void testReadConfigFile() throws IOException {
		ConversionProperties conversionProperties = new ConversionProperties();
		List<OutputConfiguration> configurations = conversionProperties
				.readConfigFile("src/test/resources/testConf.ini");
		ConversionProperties propsComparison = new ConversionProperties();

		propsComparison.setDumplocation("dumps/wikidata/");
		propsComparison.setOfflineMode(false);

		RdfConfiguration confComparison = new RdfConfiguration(propsComparison);

		confComparison.setUseStdout(true);
		confComparison.setCompressionExtension(".gz");
		confComparison.setRdfdump("ALL_EXACT_DATA");
		confComparison.setOutputDestination("/tmp/");

		assertTrue(compareOutputConfigurations(configurations.get(0),
				confComparison));
		assertTrue(compareConversionProperties(conversionProperties,
				propsComparison));
	}

	@Test
	public void testHandleArguments() throws ParseException, IOException {
		List<OutputConfiguration> configurations = properties
				.handleArguments(TEST_ARGS);
		ConversionProperties propsComparison = new ConversionProperties();
		RdfConfiguration confComparison = new RdfConfiguration(properties);
		confComparison.setCompressionExtension(".bz2");
		propsComparison.setDumplocation("dumps/wikidata/");
		propsComparison.setOfflineMode(true);
		confComparison.setOutputDestination("/somewhere/");
		confComparison.setRdfdump("TERMS");
		confComparison.setUseStdout(true);
		assertTrue(compareOutputConfigurations(configurations.get(0),
				confComparison));
		assertTrue(compareConversionProperties(properties, propsComparison));
	}

}
