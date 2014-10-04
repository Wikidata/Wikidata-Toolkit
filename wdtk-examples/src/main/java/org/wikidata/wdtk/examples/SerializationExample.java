package org.wikidata.wdtk.examples;

/*
 * #%L
 * Wikidata Toolkit Examples
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

import java.io.IOException;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.wikidata.wdtk.datamodel.json.JsonSerializer;

/**
 * This class shows how convert data from wikidata.org to the JSON format as
 * used in the Wikibase API. The compressed output will be written into a file
 * named WikidataDump.json.bz2. You can find it in the example directory after
 * you ran the example code.
 *
 * @author Michael Günther
 *
 */
public class SerializationExample {

	public static void main(String[] args) throws IOException {

		// Define where log messages go
		ExampleHelpers.configureLogging();

		// Print information about this program
		printDocumentation();

		// Write the output to a BZip2-compressed file
		try (BZip2CompressorOutputStream out = new BZip2CompressorOutputStream(
				ExampleHelpers
						.openExampleFileOuputStream("WikidataDump.json.bz2"))) {
			// Create an object for managing the serialization process
			JsonSerializer jsonSerializer = new JsonSerializer(out);
			// Set up the serializer and write headers
			jsonSerializer.start();
			ExampleHelpers.processEntitiesFromWikidataDump(jsonSerializer);
			// Finish the serialization
			jsonSerializer.close();

			System.out.println("Finished serialization.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Print some basic documentation about this program.
	 */
	private static void printDocumentation() {
		System.out
				.println("********************************************************************");
		System.out.println("*** Wikidata Toolkit: JSON Serialization Example");
		System.out.println("*** ");
		System.out
				.println("*** This program will download dumps from Wikidata and serialize");
		System.out
				.println("*** the data in Wikidata's JSON format. The output is stored in a");
		System.out
				.println("*** compressed file. See source code for further details.");
		System.out
				.println("********************************************************************");
	}
}
