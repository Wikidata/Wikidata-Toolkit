package org.wikidata.wdtk.datamodel.json.jackson;

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

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;


import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This is a test for a bug in the JSON. Empty aliases are wrongly serialized as
 * arrays. Once the bug is gone this class can safely be removed. Don't forget
 * to cleanup the affected implementations!
 *
 * @author Fredo Erxleben
 *
 */
public class TestAliasBug {

	ObjectMapper mapper = new ObjectMapper();

	String buggedAliasesJson = "\"aliases\":[]";
	String buggedItemJson = "{" + JsonTestData.JSON_ITEM_TYPE + ","
			+ buggedAliasesJson + "}";

	@Test
	public void testAliasesToJava() throws
			IOException {
		JacksonItemDocument result = mapper.readValue(buggedItemJson,
				JacksonItemDocument.class);

		assertNotNull(result);
		assert (result.getAliases().isEmpty());
	}
}
