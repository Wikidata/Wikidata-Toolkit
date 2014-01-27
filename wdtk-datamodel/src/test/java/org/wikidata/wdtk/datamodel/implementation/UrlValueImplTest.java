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
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.UrlValue;

public class UrlValueImplTest {

	UrlValue u1;
	UrlValue u2;

	@Before
	public void setUp() throws Exception {
		u1 = new UrlValueImpl("http://example.org/");
		u2 = new UrlValueImpl("http://example.org/");
	}

	@Test
	public void urlIsCorrect() {
		assertEquals(u1.getIri(), "http://example.org/");
		assertEquals(u1.getUrl(), "http://example.org/");
	}

	@Test
	public void urlValueEqualityBasedOnContent() {
		UrlValue u3 = new UrlValueImpl("http://example.com/");

		assertEquals(u1, u1);
		assertEquals(u1, u2);
		assertThat(u1, not(equalTo(u3)));
		assertThat(u1, not(equalTo(null)));
		assertFalse(u1.equals(this));
	}

	@Test
	public void urlValueHashBasedOnContent() {
		assertEquals(u1.hashCode(), u2.hashCode());
	}

	@Test(expected = NullPointerException.class)
	public void urlValueUrlNotNull() {
		new UrlValueImpl(null);
	}

}
