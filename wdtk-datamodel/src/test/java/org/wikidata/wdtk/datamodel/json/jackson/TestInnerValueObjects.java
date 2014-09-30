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
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonInnerEntityId;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonInnerMonolingualText;

/**
 * This class tests the inner objects lying behind the …ValueImpl-classes.
 *
 * @author Fredo Erxleben
 *
 */
public class TestInnerValueObjects {

	private static String itemType = "item";
	private static String wrongType = "wrongType";

	private JacksonInnerEntityId testEntityId;
	private JacksonInnerMonolingualText testMonolingualText;

	@Before
	public void setupTestEntityIds() {
		this.testEntityId = new JacksonInnerEntityId(itemType, 1);
	}

	@Before
	public void setupTestMonolingualText() {
		this.testMonolingualText = new JacksonInnerMonolingualText("en",
				"foobar");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEntityIdConstructor() {
		new JacksonInnerEntityId(wrongType, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEntityIdSetter() {
		JacksonInnerEntityId emptyId = new JacksonInnerEntityId();
		emptyId.setNumericId(1);
		emptyId.setEntityType(itemType); // should work
		emptyId.setEntityType(wrongType); // should fail
	}

	@Test
	public void testEntityIdMethods() {
		assertEquals("Q1", this.testEntityId.getStringId());
		assertEquals(this.testEntityId.getNumericId(), 1);

		// test equals
		assertEquals(this.testEntityId, new JacksonInnerEntityId("item", 1));
		assertEquals(this.testEntityId, this.testEntityId);
		assertFalse(this.testEntityId.equals(new Object()));
		assertFalse(this.testEntityId
				.equals(new JacksonInnerEntityId("item", 2)));

	}

	@Test
	public void testMonolingualTextMethods() {
		assertEquals(this.testMonolingualText.getLanguage(), "en");
		assertEquals(this.testMonolingualText.getText(), "foobar");

		// test equals
		assertEquals(this.testMonolingualText, new JacksonInnerMonolingualText(
				"en", "foobar"));
		assertEquals(this.testMonolingualText, this.testMonolingualText);
		assertFalse(this.testMonolingualText.equals(new Object()));
		assertFalse(this.testMonolingualText
				.equals(new JacksonInnerMonolingualText("en", "barfoo")));
		assertFalse(this.testMonolingualText
				.equals(new JacksonInnerMonolingualText("de", "foobar")));

	}
}
