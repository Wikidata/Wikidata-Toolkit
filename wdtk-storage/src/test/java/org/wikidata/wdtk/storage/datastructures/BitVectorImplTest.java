package org.wikidata.wdtk.storage.datastructures;

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
import org.junit.Test;

/**
 * Test class for {@link BitVectorImpl}.
 * 
 * @author Julian Mendez
 * 
 */
public class BitVectorImplTest {

	/**
	 * Asserts that two bit vectors are equal, and also that the first bit
	 * vector is equal to itself.
	 * 
	 * @param bv0
	 *            one bit vector
	 * @param bv1
	 *            another bit vector
	 */
	void assertEqualsForBitVector(BitVector bv0, BitVector bv1) {
		Assert.assertEquals(bv0, bv0);
		Assert.assertEquals(bv0, bv1);
		Assert.assertEquals(bv1, bv0);
		Assert.assertEquals(bv0.hashCode(), bv1.hashCode());
	}

	@Test
	public void testAdd() {
		BitVectorImpl bv = new BitVectorImpl();
		Assert.assertEquals(0, bv.size());

		bv.addBit(true);
		Assert.assertEquals(1, bv.size());
		Assert.assertEquals(true, bv.getBit(0));

		bv.addBit(false);
		Assert.assertEquals(2, bv.size());
		Assert.assertEquals(false, bv.getBit(1));

		bv.addBit(false);
		Assert.assertEquals(3, bv.size());
		Assert.assertEquals(false, bv.getBit(2));

		for (int i = 3; i < 0x1000; i++) {
			boolean value = (i % 3) == 0;
			bv.addBit(value);
			Assert.assertEquals(value, bv.getBit(i));
		}
	}

	@Test
	public void testEmptyBitVector() {
		BitVectorImpl bv0 = new BitVectorImpl();
		BitVector bv1 = new BitVectorImpl();
		assertEqualsForBitVector(bv0, bv1);

		BitVectorImpl bv2 = new BitVectorImpl(0);
		assertEqualsForBitVector(bv1, bv2);
	}

	@Test
	public void testEqualityAndCopyConstructor() {
		int aLargeNumber = 0x100000;
		BitVectorImpl bv0 = new BitVectorImpl();
		Assert.assertEquals(bv0, bv0);
		Assert.assertNotEquals(bv0, new Object());
		BitVectorImpl bv1 = new BitVectorImpl();

		PseudorandomBooleanGenerator generator = new PseudorandomBooleanGenerator(
				0x1234);
		for (int i = 0; i < aLargeNumber; i++) {
			boolean value = generator.getPseudorandomBoolean();
			bv0.addBit(value);
			bv1.addBit(value);
		}
		assertEqualsForBitVector(bv0, bv1);

		BitVectorImpl bv2 = new BitVectorImpl(bv1);
		assertEqualsForBitVector(bv0, bv2);

		bv1.setBit(0x12345, false);
		bv2.setBit(0x12345, true);

		Assert.assertNotEquals(bv1, bv2);
		Assert.assertNotEquals(bv2, bv1);

		RankedBitVectorImpl bv3 = new RankedBitVectorImpl(bv2);
		Assert.assertNotEquals(bv1, bv3);
		Assert.assertNotEquals(bv3, bv1);
	}

	@Test
	public void testGetBit() {
		long word = 0;

		for (byte i = 0; i < 0x40; i++) {
			Assert.assertFalse(BitVectorImpl.getBitInWord(i, word));
		}

		word = 0x0810F;

		Assert.assertTrue(BitVectorImpl.getBitInWord((byte) 0, word));
		Assert.assertTrue(BitVectorImpl.getBitInWord((byte) 1, word));
		Assert.assertTrue(BitVectorImpl.getBitInWord((byte) 2, word));
		Assert.assertTrue(BitVectorImpl.getBitInWord((byte) 3, word));
		Assert.assertFalse(BitVectorImpl.getBitInWord((byte) 4, word));
		Assert.assertFalse(BitVectorImpl.getBitInWord((byte) 5, word));
		Assert.assertFalse(BitVectorImpl.getBitInWord((byte) 6, word));
		Assert.assertFalse(BitVectorImpl.getBitInWord((byte) 7, word));
		Assert.assertTrue(BitVectorImpl.getBitInWord((byte) 8, word));
		Assert.assertFalse(BitVectorImpl.getBitInWord((byte) 9, word));
		Assert.assertFalse(BitVectorImpl.getBitInWord((byte) 10, word));
		Assert.assertFalse(BitVectorImpl.getBitInWord((byte) 11, word));
		Assert.assertFalse(BitVectorImpl.getBitInWord((byte) 12, word));
		Assert.assertFalse(BitVectorImpl.getBitInWord((byte) 13, word));
		Assert.assertFalse(BitVectorImpl.getBitInWord((byte) 14, word));
		Assert.assertTrue(BitVectorImpl.getBitInWord((byte) 15, word));
		Assert.assertFalse(BitVectorImpl.getBitInWord((byte) 16, word));

	}

	@Test
	public void testHashCode() {
		{
			BitVectorImpl bv = new BitVectorImpl();
			Assert.assertEquals(0, bv.hashCode());

			bv.addBit(false);
			Assert.assertEquals(1, bv.hashCode());
		}
		{
			BitVectorImpl bv = new BitVectorImpl();
			Assert.assertEquals(0, bv.hashCode());

			bv.addBit(true);
			Assert.assertEquals(0x20, bv.hashCode());
		}
	}

	@Test
	public void testGetOutOfRange() {
		Assert.assertEquals(false, new BitVectorImpl().getBit(1));
		Assert.assertEquals(false, new BitVectorImpl().getBit(Long.MAX_VALUE));
	}

	@Test
	public void testSetOutOfRange() {
		BitVectorImpl bv = new BitVectorImpl();
		Assert.assertEquals(0, bv.size());
		bv.setBit(41, true);
		Assert.assertEquals(42, bv.size());
		Assert.assertEquals(false, bv.getBit(40));
		Assert.assertEquals(true, bv.getBit(41));
		Assert.assertEquals(false, bv.getBit(42));
		Assert.assertEquals(false, bv.getBit(43));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidInitialSize() {
		new BitVectorImpl(-1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testInvalidPositionSizeGet00() {
		(new BitVectorImpl()).getBit(-1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testInvalidPositionSizeGet01() {
		BitVectorImpl.getBitInWord((byte) -1, 0);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testInvalidPositionSizeGet02() {
		BitVectorImpl.getBitInWord((byte) 0x40, 0);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testInvalidPositionSizeSet00() {
		BitVectorImpl.setBitInWord((byte) -1, true, 0);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testInvalidPositionSizeSet01() {
		BitVectorImpl.setBitInWord((byte) 0x40, false, 0);
	}

	@Test
	public void testSetBit() {
		long word = 0;

		for (byte i = 0; i < 0x40; i++) {
			word = BitVectorImpl.setBitInWord(i, true, word);
		}

		for (byte i = 0; i < 0x40; i++) {
			Assert.assertTrue(BitVectorImpl.getBitInWord(i, word));
		}

		for (byte i = 0; i < 0x40; i++) {
			word = BitVectorImpl.setBitInWord(i, false, word);
		}

		for (byte i = 0; i < 0x40; i++) {
			Assert.assertFalse(BitVectorImpl.getBitInWord(i, word));
		}

		word = 0x0362;
		for (byte i = 0; i < 0x40; i++) {
			boolean value = BitVectorImpl.getBitInWord(i, word);
			word = BitVectorImpl.setBitInWord(i, value, word);
			Assert.assertEquals(value, BitVectorImpl.getBitInWord(i, word));

			value = !value;
			word = BitVectorImpl.setBitInWord(i, value, word);
			Assert.assertEquals(value, BitVectorImpl.getBitInWord(i, word));

			value = !value;
			word = BitVectorImpl.setBitInWord(i, value, word);
			Assert.assertEquals(value, BitVectorImpl.getBitInWord(i, word));
		}

		Assert.assertEquals(0x0362, word);

	}

	@Test
	public void testSize() {
		{
			BitVectorImpl bv = new BitVectorImpl(0x100);
			Assert.assertEquals(0x100, bv.size());
			bv.addBit(false);
			bv.addBit(true);
			Assert.assertEquals(0x102, bv.size());
		}

		{
			BitVectorImpl bv = new BitVectorImpl();
			Assert.assertEquals(0, bv.size());
			for (int i = 0; i < 0x300; i++) {
				bv.addBit((i % 5) == 0);
				Assert.assertEquals(i + 1, bv.size());
			}
		}
	}

	@Test
	public void testToString() {
		BitVectorImpl bv = new BitVectorImpl();
		for (int i = 0; i < 0x10; i++) {
			boolean value = (i % 3) == 0;
			bv.addBit(value);
		}
		Assert.assertEquals("1001001001001001", bv.toString());

		for (int i = 0; i < 0x10; i++) {
			boolean value = (i % 2) == 0;
			bv.addBit(value);
		}
		Assert.assertEquals("10010010010010011010101010101010", bv.toString());

		for (int i = 0; i < 0x20; i++) {
			bv.setBit(i, bv.getBit(i));
		}
		Assert.assertEquals("10010010010010011010101010101010", bv.toString());

		for (int i = 0; i < 0x20; i++) {
			bv.setBit(i, !bv.getBit(i));
		}
		Assert.assertEquals("01101101101101100101010101010101", bv.toString());

	}

	@Test
	public void testWordToString() {
		long word = 0;
		Assert.assertEquals(
				"0000000000000000000000000000000000000000000000000000000000000000",
				BitVectorImpl.wordToString(word));
		word = -1;
		Assert.assertEquals(
				"1111111111111111111111111111111111111111111111111111111111111111",
				BitVectorImpl.wordToString(word));

		word = 0x362;
		Assert.assertEquals(
				"0100011011000000000000000000000000000000000000000000000000000000",
				BitVectorImpl.wordToString(word));
	}

}
