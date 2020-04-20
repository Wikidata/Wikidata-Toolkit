package org.wikidata.wdtk.datamodel.helpers;

/*-
 * #%L
 * Wikidata Toolkit Data Model
 * %%
 * Copyright (C) 2014 - 2020 Wikidata Toolkit Developers
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
import java.io.InputStream;
import java.util.Collections;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.LexemeDocument;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;


public class JsonDeserializerTest {
	
	public JsonDeserializer SUT = new JsonDeserializer(Datamodel.SITE_WIKIDATA);
	public JsonDeserializer SUTcommons = new JsonDeserializer(Datamodel.SITE_WIKIMEDIA_COMMONS);
	
	protected String loadJson(String filename) throws IOException {
		InputStream stream = JsonDeserializerTest.class.getClassLoader()
				.getResourceAsStream("JsonDeserializer/"+filename);
		return IOUtils.toString(stream);
	}

	@Test
	public void testLoadItemDocument() throws IOException {
		ItemDocument doc = SUT.deserializeItemDocument(loadJson("item.json"));
		Assert.assertEquals(doc.getEntityId(), Datamodel.makeWikidataItemIdValue("Q34987"));
	}
	
	@Test
	public void testLoadPropertyDocument() throws IOException {
		PropertyDocument doc = SUT.deserializePropertyDocument(loadJson("property.json"));
		Assert.assertEquals(doc.getEntityId(), Datamodel.makeWikidataPropertyIdValue("P3467"));
	}
	
	@Test
	public void testLoadLexemeDocument() throws IOException {
		LexemeDocument doc = SUT.deserializeLexemeDocument(loadJson("lexeme.json"));
		Assert.assertEquals(doc.getEntityId(), Datamodel.makeWikidataLexemeIdValue("L3872"));
		Assert.assertEquals(doc.getForm(Datamodel.makeWikidataFormIdValue("L3872-F2")).getStatementGroups(), Collections.emptyList());
	}
	
	@Test
	public void testLoadMediaInfoDocument() throws IOException {
		MediaInfoDocument doc = SUTcommons.deserializeMediaInfoDocument(loadJson("mediainfo.json"));
		Assert.assertEquals(doc.getEntityId(), Datamodel.makeWikimediaCommonsMediaInfoIdValue("M74698470"));
	}
	
	@Test
	public void testDeserializeEntityDocument() throws IOException {
		EntityDocument doc = SUT.deserializeEntityDocument(loadJson("property.json"));
		Assert.assertEquals(doc.getEntityId(), Datamodel.makeWikidataPropertyIdValue("P3467"));
	}
}
