package org.wikidata.wdtk.datamodel.json;

/*
 * #%L
 * Wikidata Toolkit Data Model
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

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.testing.MockStringContentFactory;

public class JsonSerializerTest {

	final String START_DOCUMENT = "{\"entities\": {\n";
	final String END_DOCUMENT = "}}";

	final TestObjectFactory factory = new TestObjectFactory();

	ByteArrayOutputStream out;
	JsonSerializer serializer;

	/**
	 * Loads the resource file with fileName and returns the content as a
	 * {@link String} object.
	 * 
	 * @param fileName
	 * @return textual content of the file
	 * @throws IOException
	 */
	public String getResourceFromFile(String fileName) throws IOException {
		return MockStringContentFactory.getStringFromUrl(this.getClass()
				.getResource("/" + fileName));
	}

	@Before
	public void setUp() throws Exception {
		out = new ByteArrayOutputStream();
		serializer = new JsonSerializer(out);
	}

	@Test
	public void testStartSerialisation() {
		serializer.startSerialization();
		assertEquals(out.toString(), START_DOCUMENT);
	}

	@Test
	public void testFinishSerialisation() {
		serializer.finishSerialization();
		assertEquals(out.toString(), END_DOCUMENT);
	}

	@Test
	public void testProcessItemDocument() throws IOException {
		serializer.processItemDocument(factory.createItemDocument());
		assertEquals(getResourceFromFile("ItemDocumentEntry.txt"),
				out.toString()); // not very clear because of json-order-problem
	}

	@Test
	public void testProcessPropertyDocument() throws IOException {
		serializer.processPropertyDocument(factory
				.createEmptyPropertyDocument());
		serializer.processPropertyDocument(factory
				.createEmptyPropertyDocument());
		assertEquals(getResourceFromFile("PropertyDocumentEntries.txt"),
				out.toString()); // not very clear because of json-order-problem
	}

}
