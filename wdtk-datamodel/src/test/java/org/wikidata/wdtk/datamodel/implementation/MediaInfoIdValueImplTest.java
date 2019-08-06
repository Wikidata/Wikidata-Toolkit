package org.wikidata.wdtk.datamodel.implementation;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.DatamodelMapper;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class MediaInfoIdValueImplTest {

	private final ObjectMapper mapper = new DatamodelMapper(Datamodel.SITE_WIKIMEDIA_COMMONS);

	private final MediaInfoIdValueImpl mediaInfo1 = new MediaInfoIdValueImpl("M42", "http://commons.wikimedia.org/entity/");
	private final MediaInfoIdValueImpl mediaInfo2 = new MediaInfoIdValueImpl("M42", "http://commons.wikimedia.org/entity/");
	private final MediaInfoIdValueImpl mediaInfo3 = new MediaInfoIdValueImpl("M57", "http://commons.wikimedia.org/entity/");
	private final MediaInfoIdValueImpl mediaInfo4 = new MediaInfoIdValueImpl("M42", "http://www.example.org/entity/");
	private final String JSON_MEDIA_INFO_ID_VALUE = "{\"type\":\"wikibase-entityid\",\"value\":{\"entity-type\":\"mediainfo\",\"numeric-id\":42,\"id\":\"M42\"}}";
	private final String JSON_MEDIA_INFO_ID_VALUE_WITHOUT_NUMERICAL_ID = "{\"type\":\"wikibase-entityid\",\"value\":{\"id\":\"M42\"}}";

	@Test
	public void entityTypeIsMediaInfo() {
		assertEquals(mediaInfo1.getEntityType(), EntityIdValue.ET_MEDIA_INFO);
	}

	@Test
	public void iriIsCorrect() {
		assertEquals(mediaInfo1.getIri(), "http://commons.wikimedia.org/entity/M42");
		assertEquals(mediaInfo4.getIri(), "http://www.example.org/entity/M42");
	}

	@Test
	public void siteIriIsCorrect() {
		assertEquals(mediaInfo1.getSiteIri(), "http://commons.wikimedia.org/entity/");
	}

	@Test
	public void idIsCorrect() {
		assertEquals(mediaInfo1.getId(), "M42");
	}

	@Test
	public void equalityBasedOnContent() {
		assertEquals(mediaInfo1, mediaInfo1);
		assertEquals(mediaInfo1, mediaInfo2);
		assertNotEquals(mediaInfo1, mediaInfo3);
		assertNotEquals(mediaInfo1, mediaInfo4);
		assertNotEquals(mediaInfo1, null);
		assertNotEquals(mediaInfo1, this);
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(mediaInfo1.hashCode(), mediaInfo2.hashCode());
	}

	@Test(expected = RuntimeException.class)
	public void idValidatedForFirstLetter() {
		new MediaInfoIdValueImpl("Q12345", "http://commons.wikimedia.org/entity/");
	}

	@Test(expected = IllegalArgumentException.class)
	public void idValidatedForNumber() {
		new MediaInfoIdValueImpl("L34d23", "http://commons.wikimedia.org/entity/");
	}

	@Test(expected = IllegalArgumentException.class)
	public void idValidatedForLength() {
		new MediaInfoIdValueImpl("M", "http://commons.wikimedia.org/entity/");
	}

	@Test(expected = RuntimeException.class)
	public void idNotNull() {
		new MediaInfoIdValueImpl((String)null, "http://commons.wikimedia.org/entity/");
	}

	@Test(expected = NullPointerException.class)
	public void baseIriNotNull() {
		new MediaInfoIdValueImpl("M42", null);
	}

	@Test
	public void testToJson() throws JsonProcessingException {
		JsonComparator.compareJsonStrings(JSON_MEDIA_INFO_ID_VALUE, mapper.writeValueAsString(mediaInfo1));
	}

	@Test
	public void testToJava() throws IOException {
		assertEquals(mediaInfo1, mapper.readValue(JSON_MEDIA_INFO_ID_VALUE, ValueImpl.class));
	}

	@Test
	public void testToJavaWithoutNumericalID() throws IOException {
		assertEquals(mediaInfo1, mapper.readValue(JSON_MEDIA_INFO_ID_VALUE_WITHOUT_NUMERICAL_ID, ValueImpl.class));
	}

}
