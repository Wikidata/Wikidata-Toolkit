package org.wikidata.wdtk.storage.datastructures;

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

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link BitVectorIterator}.
 * 
 * @author Julian Mendez
 * 
 */
public class BitVectorIteratorTest {

	@Test
	public void testHashCode() {
		Iterator<Boolean> it = (new BitVectorImpl()).iterator();
		Assert.assertEquals(0, it.hashCode());
	}

	@Test(expected = NoSuchElementException.class)
	public void testNoSuchElementException() {
		new BitVectorImpl().iterator().next();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testUnsupportedOperationException() {
		new BitVectorImpl().iterator().remove();
	}

	@Test
	public void testVectorWithPseudoRandomValues() {
		BitVectorImpl bv0 = new BitVectorImpl();
		BitVectorImpl bv1 = new BitVectorImpl();

		Iterator<Boolean> it = bv0.iterator();
		Assert.assertEquals(it, it);
		Assert.assertEquals(bv0.iterator(), bv1.iterator());
		Assert.assertNotEquals(bv0.iterator(), Collections.emptyIterator());

		PseudorandomBooleanGenerator generator0 = new PseudorandomBooleanGenerator(
				0x1234);
		for (int i = 0; i < 0x1000; i++) {
			boolean value = generator0.getPseudorandomBoolean();
			bv0.addBit(value);
			bv1.addBit(value);
		}

		PseudorandomBooleanGenerator generator1 = new PseudorandomBooleanGenerator(
				0x1234);
		int i = 0;
		for (boolean value : bv0) {
			boolean expectedValue = generator1.getPseudorandomBoolean();
			Assert.assertEquals(expectedValue, value);
			i++;
		}

		Assert.assertEquals(i, 0x1000);
		Assert.assertEquals(bv0.iterator(), bv1.iterator());
		Assert.assertNotEquals(bv0.iterator(), (new BitVectorImpl()).iterator());
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

}
