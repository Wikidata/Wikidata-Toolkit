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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;

public class MonoloingualTextValueImplTest {

	MonolingualTextValue s1;
	MonolingualTextValue s2;

	@Before
	public void setUp() throws Exception {
		s1 = new MonolingualTextValueImpl("some string", "en");
		s2 = new MonolingualTextValueImpl("some string", "en");
	}

	@Test
	public void dataIsCorrect() {
		assertEquals(s1.getText(), "some string");
		assertEquals(s1.getLanguageCode(), "en");
	}

	@Test
	public void equalityBasedOnContent() {
		MonolingualTextValue s3 = new MonolingualTextValueImpl(
				"another string", "en");
		MonolingualTextValue s4 = new MonolingualTextValueImpl("some string",
				"en-GB");

		assertEquals(s1, s1);
		assertEquals(s1, s2);
		assertThat(s1, not(equalTo(s3)));
		assertThat(s1, not(equalTo(s4)));
		assertThat(s1, not(equalTo(null)));
		assertFalse(s1.equals(this));
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(s1.hashCode(), s2.hashCode());
	}

	@Test(expected = NullPointerException.class)
	public void textNotNull() {
		new MonolingualTextValueImpl(null, "en");
	}

	@Test(expected = NullPointerException.class)
	public void languageCodeNotNull() {
		new MonolingualTextValueImpl("some text", null);
	}

}
