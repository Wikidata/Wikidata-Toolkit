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
import org.wikidata.wdtk.storage.datastructure.intf.RankedBitVector;

/**
 * Test class for {@link RankedRankedBitVectorImpl}.
 * 
 * @author Julian Mendez
 * 
 */
public class RankedBitVectorImplTest {

	void assertCorrectCount(RankedBitVector bv) {
		for (long index = 0; index < bv.size(); index++) {
			assertCorrectCount(bv, index);
		}
	}

	void assertCorrectCount(RankedBitVector bv, long index) {
		{
			long expectedCount = countBits(bv, false, index);
			Assert.assertEquals(expectedCount, bv.countBits(false, index));
		}
		{
			long expectedCount = countBits(bv, true, index);
			Assert.assertEquals(expectedCount, bv.countBits(true, index));
		}
	}

	void assertEqualsForBitVector(BitVector bv0, BitVector bv1) {
		Assert.assertEquals(bv0, bv0);
		Assert.assertEquals(bv0, bv1);
		Assert.assertEquals(bv1, bv0);
		Assert.assertEquals(bv1, bv1);
		Assert.assertEquals(bv0.hashCode(), bv1.hashCode());
	}

	long countBits(BitVector bv, boolean bit, long position) {
		long ret = 0;
		for (long index = 0; index <= position; index++) {
			if (bv.getBit(index) == bit) {
				ret++;
			}
		}
		return ret;
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testAdd() {
		RankedBitVectorImpl bv = new RankedBitVectorImpl();
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
			assertCorrectCount(bv, i);
		}
	}

	@Test
	public void testEmptyBitVector() {
		RankedBitVectorImpl bv0 = new RankedBitVectorImpl();
		RankedBitVector bv1 = new RankedBitVectorImpl();
		assertEqualsForBitVector(bv0, bv1);
		assertCorrectCount(bv0);
		assertCorrectCount(bv1);

		RankedBitVectorImpl bv2 = new RankedBitVectorImpl(0);
		assertEqualsForBitVector(bv1, bv2);
		assertCorrectCount(bv2);
	}

	@Test
	public void testEqualityAndCopyConstructor() {
		RankedBitVectorImpl bv0 = new RankedBitVectorImpl();
		for (int i = 0; i < 0x100000; i++) {
			boolean value = (i % 3) == 0;
			bv0.addBit(value);
		}

		RankedBitVectorImpl bv1 = new RankedBitVectorImpl();
		for (int i = 0; i < 0x100000; i++) {
			boolean value = (i % 3) == 0;
			bv1.addBit(value);
		}

		assertEqualsForBitVector(bv0, bv1);

		bv1.setBit(0x12345, false);
		Assert.assertFalse(bv0.equals(bv1));
		Assert.assertFalse(bv1.equals(bv0));

		bv1.setBit(0x12346, true);
		Assert.assertFalse(bv0.equals(bv1));
		Assert.assertFalse(bv1.equals(bv0));

		RankedBitVectorImpl bv2 = new RankedBitVectorImpl(bv1);
		for (int i = 0; i < 0x100000; i++) {
			boolean value = (i % 3) == 0;
			bv1.addBit(value);
		}

		bv2.setBit(0x12345, true);
		Assert.assertFalse(bv0.equals(bv2));
		Assert.assertFalse(bv2.equals(bv0));
		Assert.assertFalse(bv1.equals(bv2));
		Assert.assertFalse(bv2.equals(bv1));

		bv2.setBit(0x12346, false);
		assertEqualsForBitVector(bv0, bv2);

		for (int i = 0; i < 0x100000; i++) {
			if ((i % 0x6785) == 0) {
				assertCorrectCount(bv2, i);
			}
		}

	}

	@Test
	public void testFindPosition() {
		for (int x = 0x20; x > 0; x--) {
			testFindPositionWithBitVector(new RankedBitVectorImpl(0, 0x10, x));
		}
	}

	void testFindPositionWithBitVector(RankedBitVectorImpl bv) {
		Assert.assertEquals(0, bv.size());

		bv.addBit(true);

		Assert.assertEquals(RankedBitVector.NOT_FOUND, bv.findPosition(true, 0));
		Assert.assertEquals(0, bv.findPosition(true, 1));

		bv.addBit(true);
		bv.addBit(false);
		bv.addBit(true);
		bv.addBit(false);
		bv.addBit(false);
		bv.addBit(false);
		bv.addBit(true);

		Assert.assertEquals(RankedBitVector.NOT_FOUND,
				bv.findPosition(false, 0));
		Assert.assertEquals(RankedBitVector.NOT_FOUND, bv.findPosition(true, 0));
		Assert.assertEquals(0, bv.findPosition(true, 1));
		Assert.assertEquals(1, bv.findPosition(true, 2));
		Assert.assertEquals(2, bv.findPosition(false, 1));
		Assert.assertEquals(3, bv.findPosition(true, 3));
		Assert.assertEquals(4, bv.findPosition(false, 2));
		Assert.assertEquals(5, bv.findPosition(false, 3));
		Assert.assertEquals(6, bv.findPosition(false, 4));
		Assert.assertEquals(7, bv.findPosition(true, 4));
		Assert.assertEquals(RankedBitVector.NOT_FOUND,
				bv.findPosition(false, 5));
		Assert.assertEquals(RankedBitVector.NOT_FOUND, bv.findPosition(true, 5));

	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidInitialSize() {
		new RankedBitVectorImpl(-1);
	}

	@Test
	public void testSize() {
		{
			RankedBitVectorImpl bv = new RankedBitVectorImpl(0x100);
			Assert.assertEquals(0x100, bv.size());
			bv.addBit(false);
			bv.addBit(true);
			Assert.assertEquals(0x102, bv.size());
			assertCorrectCount(bv);
		}

		{
			RankedBitVectorImpl bv = new RankedBitVectorImpl();
			Assert.assertEquals(0, bv.size());
			for (int i = 0; i < 0x300; i++) {
				bv.addBit((i % 5) == 0);
				Assert.assertEquals(i + 1, bv.size());
			}
			assertCorrectCount(bv);
		}
	}

	@Test
	public void testToString() {
		RankedBitVectorImpl bv = new RankedBitVectorImpl();
		for (int i = 0; i < 0x10; i++) {
			boolean value = (i % 3) == 0;
			bv.addBit(value);
		}
		Assert.assertEquals("1001001001001001", bv.toString());
		assertCorrectCount(bv);

		for (int i = 0; i < 0x10; i++) {
			boolean value = (i % 2) == 0;
			bv.addBit(value);
		}
		Assert.assertEquals("10010010010010011010101010101010", bv.toString());
		assertCorrectCount(bv);

		for (int i = 0; i < 0x20; i++) {
			bv.setBit(i, bv.getBit(i));
		}
		Assert.assertEquals("10010010010010011010101010101010", bv.toString());
		assertCorrectCount(bv);

		for (int i = 0; i < 0x20; i++) {
			bv.setBit(i, !bv.getBit(i));
		}
		Assert.assertEquals("01101101101101100101010101010101", bv.toString());
		assertCorrectCount(bv);

	}

}
