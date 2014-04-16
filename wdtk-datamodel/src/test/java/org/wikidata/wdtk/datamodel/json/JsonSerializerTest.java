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

import org.junit.Before;
import org.junit.Test;

public class JsonSerializerTest {

	final String START_DOCUMENT = "{\"entities\": {";
	final String END_DOCUMENT = "}}";

	ByteArrayOutputStream out;
	JsonProcessor processor;
	JsonSerializer serializer;

	@Before
	public void setUp() throws Exception {
		out = new ByteArrayOutputStream();
		processor = new JsonProcessor(out);
		serializer = new JsonSerializer(processor);
	}

	@Test
	public void testStartSerialisation() {
		serializer.startSerialisation();
		assertEquals(out.toString(), START_DOCUMENT);
	}

	@Test
	public void testFinishSerialisation() {
		serializer.finishSerialisation();
		assertEquals(out.toString(), END_DOCUMENT);
	}

}
