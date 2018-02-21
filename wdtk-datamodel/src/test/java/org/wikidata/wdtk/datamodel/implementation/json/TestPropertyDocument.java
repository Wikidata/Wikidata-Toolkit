package org.wikidata.wdtk.datamodel.implementation.json;

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

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.implementation.PropertyDocumentImpl;
import org.wikidata.wdtk.datamodel.implementation.json.JacksonPreStatement;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestPropertyDocument {

	ObjectMapper mapper = new ObjectMapper();

	@Test
	public void testFullDocumentSetup() {
		PropertyDocumentImpl fullDocument = new PropertyDocumentImpl(
				JsonTestData.getTestPropertyId().getId(),
				JsonTestData.getTestMltvMap(),
				JsonTestData.getTestMltvMap(),
				JsonTestData.getTestAliases(),
				Collections.<String, List<JacksonPreStatement>>emptyMap(),
				"quantity",
				0, JsonTestData.getTestItemId().getSiteIri());

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
