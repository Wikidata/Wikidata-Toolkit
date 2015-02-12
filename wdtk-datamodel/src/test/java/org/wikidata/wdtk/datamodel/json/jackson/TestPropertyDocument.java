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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestPropertyDocument {

	ObjectMapper mapper = new ObjectMapper();

	@Test
	public void testFullDocumentSetup() {
		JacksonPropertyDocument fullDocument = new JacksonPropertyDocument();
		fullDocument.setJsonId(JsonTestData.getTestPropertyId().getId());
		fullDocument.setAliases(JsonTestData.getTestAliases());
		fullDocument.setDescriptions(JsonTestData.getTestMltvMap());
		fullDocument.setLabels(JsonTestData.getTestMltvMap());
		fullDocument.setJsonDatatype("quantity");

		assertEquals(fullDocument.getAliases(), JsonTestData.getTestAliases());
		assertEquals(fullDocument.getDescriptions(),
				JsonTestData.getTestMltvMap());
		assertEquals(fullDocument.getLabels(), JsonTestData.getTestMltvMap());
		assertEquals(fullDocument.getPropertyId(),
				JsonTestData.getTestPropertyId());
		assertEquals(fullDocument.getEntityId(),
				JsonTestData.getTestPropertyId());
		assertEquals(fullDocument.getDatatype(),
				Datamodel.makeDatatypeIdValue(DatatypeIdValue.DT_QUANTITY));
		assertEquals(fullDocument.getPropertyId().getId(),
				fullDocument.getJsonId());
	}

}
