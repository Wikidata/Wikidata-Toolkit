package org.wikidata.wdtk.storage.datastructure.impl;

/*
 * #%L
 * Wikidata Toolkit Storage
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

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link BitVectorIterator}.
 * 
 * @author Julian Mendez
 * 
 */
public class BitVectorIteratorTest {

	int seed;

	@Before
	public void setUp() throws Exception {
	}

	boolean getPseudoRandomBoolean() {
		this.seed = (0x4650 * (this.seed & 0xFFFF)) + (this.seed >> 0x10);
		return ((this.seed & 1) == 1);
	}

	@Test
	public void testVectorWithRegularPattern() {
		{
			BitVectorImpl bv = new BitVectorImpl();
			for (int i = 0; i < 0x100; i++) {
				boolean value = (i % 3) == 0;
				bv.addBit(value);
			}

			{
				Iterator<Boolean> it = bv.iterator();
				Assert.assertTrue(it.hasNext());
				int i = 0;
				while (it.hasNext()) {
					boolean expectedValue = ((i % 3) == 0);
					boolean value = it.next();
					Assert.assertEquals(expectedValue, value);
					i++;
				}
			}
		}

		{
			BitVectorImpl bv = new BitVectorImpl();
			for (int i = 0; i < 0x100; i++) {
				boolean value = (i % 7) == 0;
				bv.addBit(value);
			}

			{
				int i = 0;
				for (boolean value : bv) {
					boolean expectedValue = (i % 7) == 0;
					Assert.assertEquals(expectedValue, value);
					i++;
				}
			}
		}

	}

	@Test
	public void testVectorWithPseudoRandomValues() {
		BitVectorImpl bv = new BitVectorImpl();

		this.seed = 0x1234;
		for (int i = 0; i < 0x1000; i++) {
			boolean value = getPseudoRandomBoolean();
			bv.addBit(value);
		}

		this.seed = 0x1234;
		int i = 0;
		for (boolean value : bv) {
			boolean expectedValue = getPseudoRandomBoolean();
			Assert.assertEquals(expectedValue, value);
			i++;
		}

		Assert.assertEquals(i, 0x1000);
	}

}
