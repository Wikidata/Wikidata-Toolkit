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

public class MonolingualTextValueImplTest {

	MonolingualTextValue mt1;
	MonolingualTextValue mt2;

	@Before
	public void setUp() throws Exception {
		mt1 = new MonolingualTextValueImpl("some string", "en");
		mt2 = new MonolingualTextValueImpl("some string", "en");
	}

	@Test
	public void dataIsCorrect() {
		assertEquals(mt1.getText(), "some string");
		assertEquals(mt1.getLanguageCode(), "en");
	}

	@Test
	public void equalityBasedOnContent() {
		MonolingualTextValue mtDiffString = new MonolingualTextValueImpl(
				"another string", "en");
		MonolingualTextValue mtDiffLanguageCode = new MonolingualTextValueImpl(
				"some string", "en-GB");

		assertEquals(mt1, mt1);
		assertEquals(mt1, mt2);
		assertThat(mt1, not(equalTo(mtDiffString)));
		assertThat(mt1, not(equalTo(mtDiffLanguageCode)));
		assertThat(mt1, not(equalTo(null)));
		assertFalse(mt1.equals(this));
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(mt1.hashCode(), mt2.hashCode());
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
