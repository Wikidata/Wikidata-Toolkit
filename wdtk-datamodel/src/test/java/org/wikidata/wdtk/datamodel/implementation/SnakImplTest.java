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
import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

public class SnakImplTest {

	ValueSnak vs1;
	ValueSnak vs2;
	ValueSnak vs3;
	ValueSnak vs4;
	SomeValueSnak svs1;
	SomeValueSnak svs2;
	SomeValueSnak svs3;
	NoValueSnak nvs1;
	NoValueSnak nvs2;
	NoValueSnak nvs3;

	@Before
	public void setUp() throws Exception {
		PropertyIdValue p1 = PropertyIdValueImpl.create("P42",
				"http://example.com/entity/");
		PropertyIdValue p2 = PropertyIdValueImpl.create("P43",
				"http://example.com/entity/");

		vs1 = new ValueSnakImpl(p1, p1);
		vs2 = new ValueSnakImpl(p1, p1);
		vs3 = new ValueSnakImpl(p2, p1);
		vs4 = new ValueSnakImpl(p1, p2);

		svs1 = new SomeValueSnakImpl(p1);
		svs2 = new SomeValueSnakImpl(p1);
		svs3 = new SomeValueSnakImpl(p2);

		nvs1 = new NoValueSnakImpl(p1);
		nvs2 = new NoValueSnakImpl(p1);
		nvs3 = new NoValueSnakImpl(p2);
	}

	@Test
	public void snakHashBasedOnContent() {
		assertEquals(vs1.hashCode(), vs2.hashCode());
		assertEquals(svs1.hashCode(), svs2.hashCode());
		assertEquals(nvs1.hashCode(), nvs2.hashCode());
	}

	@Test
	public void snakEqualityBasedOnType() {
		assertFalse(svs1.equals(nvs1));
		assertFalse(nvs1.equals(svs1));
		assertFalse(vs1.equals(svs1));
	}

	@Test
	public void valueSnakEqualityBasedOnContent() {
		assertEquals(vs1, vs1);
		assertEquals(vs1, vs2);
		assertThat(vs1, not(equalTo(vs3)));
		assertThat(vs1, not(equalTo(vs4)));
		assertThat(vs1, not(equalTo(null)));
	}

	@Test
	public void someValueSnakEqualityBasedOnContent() {
		assertEquals(svs1, svs1);
		assertEquals(svs1, svs2);
		assertThat(svs1, not(equalTo(svs3)));
		assertThat(svs1, not(equalTo(null)));
	}

	@Test
	public void noValueSnakEqualityBasedOnContent() {
		assertEquals(nvs1, nvs1);
		assertEquals(nvs1, nvs2);
		assertThat(nvs1, not(equalTo(nvs3)));
		assertThat(nvs1, not(equalTo(null)));
	}

	@Test(expected = NullPointerException.class)
	public void snakPropertyNotNull() {
		new SomeValueSnakImpl(null);
	}

	@Test(expected = NullPointerException.class)
	public void snakValueNotNull() {
		new ValueSnakImpl(PropertyIdValueImpl.create("P42",
				"http://example.com/entity/"), null);
	}
}
