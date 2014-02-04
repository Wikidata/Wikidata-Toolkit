package org.wikidata.wdtk.storage.datastructure.impl;

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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.storage.datastructure.intf.BitVector;

/**
 * Test class for {@link BitVectorImpl}.
 * 
 * @author Julian Mendez
 * 
 */
public class BitVectorImplTest {

	void assertEqualsForBitVector(BitVector bv0, BitVector bv1) {
		Assert.assertEquals(bv0, bv0);
		Assert.assertEquals(bv0, bv1);
		Assert.assertEquals(bv1, bv0);
		Assert.assertEquals(bv1, bv1);
		Assert.assertEquals(bv0.hashCode(), bv1.hashCode());
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testAdd() {
		BitVectorImpl bv = new BitVectorImpl();
		Assert.assertEquals(0, bv.size());

		bv.add(true);
		Assert.assertEquals(1, bv.size());
		Assert.assertEquals(true, bv.get(0));

		bv.add(false);
		Assert.assertEquals(2, bv.size());
		Assert.assertEquals(false, bv.get(1));

		bv.add(false);
		Assert.assertEquals(3, bv.size());
		Assert.assertEquals(false, bv.get(2));

		for (int i = 3; i < 0x1000; i++) {
			boolean value = (i % 3) == 0;
			bv.add(value);
			Assert.assertEquals(value, bv.get(i));
		}
	}

	@Test
	public void testEmptyBitVector() {
		BitVectorImpl bv0 = new BitVectorImpl();
		BitVector bv1 = new BitVectorImpl();
		assertEqualsForBitVector(bv0, bv1);

		BitVectorImpl bv2 = new BitVectorImpl(0);
		assertEqualsForBitVector(bv0, bv2);
		assertEqualsForBitVector(bv1, bv2);
	}

	@Test
	public void testEqualityAndCopyConstructor() {
		BitVectorImpl bv0 = new BitVectorImpl();
		for (int i = 0; i < 0x100000; i++) {
			boolean value = (i % 3) == 0;
			bv0.add(value);
		}

		BitVectorImpl bv1 = new BitVectorImpl();
		for (int i = 0; i < 0x100000; i++) {
			boolean value = (i % 3) == 0;
			bv1.add(value);
		}

		assertEqualsForBitVector(bv0, bv1);

		bv1.set(0x12345, false);
		Assert.assertFalse(bv0.equals(bv1));
		Assert.assertFalse(bv1.equals(bv0));

		bv1.set(0x12346, true);
		Assert.assertFalse(bv0.equals(bv1));
		Assert.assertFalse(bv1.equals(bv0));

		BitVectorImpl bv2 = new BitVectorImpl(bv1);
		for (int i = 0; i < 0x100000; i++) {
			boolean value = (i % 3) == 0;
			bv1.add(value);
		}

		bv2.set(0x12345, true);
		Assert.assertFalse(bv0.equals(bv2));
		Assert.assertFalse(bv2.equals(bv0));
		Assert.assertFalse(bv1.equals(bv2));
		Assert.assertFalse(bv2.equals(bv1));

		bv2.set(0x12346, false);
		assertEqualsForBitVector(bv0, bv2);
	}

	@Test
	public void testGetBit() {
		long word = 0;

		for (int i = 0; i < 0x40; i++) {
			Assert.assertFalse(BitVectorImpl.getBit(i, word));
		}

		word = 0x0810F;

		Assert.assertTrue(BitVectorImpl.getBit(0, word));
		Assert.assertTrue(BitVectorImpl.getBit(1, word));
		Assert.assertTrue(BitVectorImpl.getBit(2, word));
		Assert.assertTrue(BitVectorImpl.getBit(3, word));
		Assert.assertFalse(BitVectorImpl.getBit(4, word));
		Assert.assertFalse(BitVectorImpl.getBit(5, word));
		Assert.assertFalse(BitVectorImpl.getBit(6, word));
		Assert.assertFalse(BitVectorImpl.getBit(7, word));
		Assert.assertTrue(BitVectorImpl.getBit(8, word));
		Assert.assertFalse(BitVectorImpl.getBit(9, word));
		Assert.assertFalse(BitVectorImpl.getBit(10, word));
		Assert.assertFalse(BitVectorImpl.getBit(11, word));
		Assert.assertFalse(BitVectorImpl.getBit(12, word));
		Assert.assertFalse(BitVectorImpl.getBit(13, word));
		Assert.assertFalse(BitVectorImpl.getBit(14, word));
		Assert.assertTrue(BitVectorImpl.getBit(15, word));
		Assert.assertFalse(BitVectorImpl.getBit(16, word));

	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidInitialSize() {
		@SuppressWarnings("unused")
		BitVectorImpl bv = new BitVectorImpl(-1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testInvalidPositionSizeGet01() {
		BitVectorImpl.getBit(-1, 0);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testInvalidPositionSizeGet02() {
		BitVectorImpl.getBit(0x40, 0);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testInvalidPositionSizeSet01() {
		BitVectorImpl.setBit(-1, true, 0);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testInvalidPositionSizeSet02() {
		BitVectorImpl.setBit(0x40, false, 0);
	}

	@Test
	public void testSetBit() {
		long word = 0;

		for (int i = 0; i < 0x40; i++) {
			word = BitVectorImpl.setBit(i, true, word);
		}

		for (int i = 0; i < 0x40; i++) {
			Assert.assertTrue(BitVectorImpl.getBit(i, word));
		}

		for (int i = 0; i < 0x40; i++) {
			word = BitVectorImpl.setBit(i, false, word);
		}

		for (int i = 0; i < 0x40; i++) {
			Assert.assertFalse(BitVectorImpl.getBit(i, word));
		}

		word = 0x0362;
		for (int i = 0; i < 0x40; i++) {
			boolean value = BitVectorImpl.getBit(i, word);
			word = BitVectorImpl.setBit(i, value, word);
			Assert.assertEquals(value, BitVectorImpl.getBit(i, word));

			value = !value;
			word = BitVectorImpl.setBit(i, value, word);
			Assert.assertEquals(value, BitVectorImpl.getBit(i, word));

			value = !value;
			word = BitVectorImpl.setBit(i, value, word);
			Assert.assertEquals(value, BitVectorImpl.getBit(i, word));
		}

		Assert.assertEquals(0x0362, word);

	}

	@Test
	public void testToString() {
		BitVectorImpl bv = new BitVectorImpl();
		for (int i = 0; i < 0x10; i++) {
			boolean value = (i % 3) == 0;
			bv.add(value);
		}
		Assert.assertEquals("1001001001001001", bv.toString());

		for (int i = 0; i < 0x10; i++) {
			boolean value = (i % 2) == 0;
			bv.add(value);
		}
		Assert.assertEquals("10010010010010011010101010101010", bv.toString());

		for (int i = 0; i < 0x20; i++) {
			bv.set(i, bv.get(i));
		}
		Assert.assertEquals("10010010010010011010101010101010", bv.toString());

		for (int i = 0; i < 0x20; i++) {
			bv.set(i, !bv.get(i));
		}
		Assert.assertEquals("01101101101101100101010101010101", bv.toString());

	}

	@Test
	public void testWordToString() {
		long word = 0;
		Assert.assertEquals(
				"0000000000000000000000000000000000000000000000000000000000000000",
				BitVectorImpl.toString(word));
		word = -1;
		Assert.assertEquals(
				"1111111111111111111111111111111111111111111111111111111111111111",
				BitVectorImpl.toString(word));

		word = 0x362;
		Assert.assertEquals(
				"0100011011000000000000000000000000000000000000000000000000000000",
				BitVectorImpl.toString(word));
	}

}
