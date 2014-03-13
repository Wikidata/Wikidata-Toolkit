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

import java.util.Iterator;

import org.junit.Assert;
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

	void assertCorrectCount(RankedBitVector bv, long position) {
		{
			long expectedCount = countBits(bv, false, position);
			long computedCount = bv.countBits(false, position);
			Assert.assertEquals(expectedCount, computedCount);
		}
		{
			long expectedCount = countBits(bv, true, position);
			long computedCount = bv.countBits(true, position);
			Assert.assertEquals(expectedCount, computedCount);
		}
	}

	void assertCorrectFindPosition(RankedBitVector bv) {
		for (long index = 0; index < bv.size(); index++) {
			assertCorrectFindPosition(bv, index);
		}
	}

	void assertCorrectFindPosition(RankedBitVector bv, long nOccurrences) {
		{
			long expectedFindPosition = findPosition(bv, false, nOccurrences);
			long computedFindPosition = bv.findPosition(false, nOccurrences);
			Assert.assertEquals(expectedFindPosition, computedFindPosition);
		}
		{
			long expectedFindPosition = findPosition(bv, true, nOccurrences);
			long computedFindPosition = bv.findPosition(true, nOccurrences);
			Assert.assertEquals(expectedFindPosition, computedFindPosition);
		}
	}

	void assertEqualsForBitVector(RankedBitVector bv0, BitVector bv1) {
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

	long findPosition(BitVector bv, boolean bit, long nOccurrences) {
		if (nOccurrences == 0) {
			return RankedBitVector.NOT_FOUND;
		}
		long accumOccurrences = 0;
		for (long index = 0; index < bv.size(); index++) {
			if (bv.getBit(index) == bit) {
				accumOccurrences++;
			}
			if (accumOccurrences == nOccurrences) {
				return index;
			}
		}
		return RankedBitVector.NOT_FOUND;
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
	public void testCountBits() {
		final long aLargeNumber = 0x100000;

		PseudorandomNumberGenerator generator = new PseudorandomNumberGenerator(
				0x1234);
		RankedBitVectorImpl bv = new RankedBitVectorImpl(new BitVectorImpl());
		for (int i = 0; i < aLargeNumber; i++) {
			boolean value = generator.getPseudorandomBoolean();
			bv.addBit(value);
		}

		for (int i = 0; i < aLargeNumber; i++) {
			if ((i % 0x6785) == 0) {
				assertCorrectCount(bv, i);
			}
		}
	}

	@Test
	public void testEmptyBitVector() {
		RankedBitVectorImpl bv0 = new RankedBitVectorImpl();
		RankedBitVector bv1 = new RankedBitVectorImpl();
		assertEqualsForBitVector(bv0, bv1);
		assertCorrectCount(bv0);
		assertCorrectCount(bv1);
		assertCorrectFindPosition(bv0);
		assertCorrectFindPosition(bv1);

		RankedBitVectorImpl bv2 = new RankedBitVectorImpl(0);
		assertEqualsForBitVector(bv1, bv2);
		assertCorrectCount(bv2);
		assertCorrectFindPosition(bv2);

		Assert.assertNotEquals(bv0, new Object());
		Assert.assertEquals(bv0, new BitVectorImpl());
	}

	@Test
	public void testEqualityAndCopyConstructor() {
		final long aLargeNumber = 0x100000;

		RankedBitVectorImpl bv0 = new RankedBitVectorImpl();
		RankedBitVectorImpl bv1 = new RankedBitVectorImpl();

		PseudorandomNumberGenerator generator = new PseudorandomNumberGenerator(
				0x1234);
		for (int i = 0; i < aLargeNumber; i++) {
			boolean value = generator.getPseudorandomBoolean();
			bv0.addBit(value);
			bv1.addBit(value);
		}

		assertEqualsForBitVector(bv0, bv1);

		RankedBitVectorImpl bv2 = new RankedBitVectorImpl(bv1);
		assertEqualsForBitVector(bv0, bv2);
	}

	@Test
	public void testFindPositionBlockSize() {
		for (int x = 0x80; x >= 0x40; x--) {
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
	public void testInvalidInitialSizes0() {
		new RankedBitVectorImpl(1, 0, 0x40);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidInitialSizes1() {
		new RankedBitVectorImpl(1, 2, 0x3F);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidInitialSizes2() {
		new CountBitsArray(new BitVectorImpl(), 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidInitialSizes3() {
		new FindPositionArray(0, new BitVectorImpl(), true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidInitialSizes4() {
		new FindPositionArray(new BitVectorImpl(), true, 0x3F);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidInitialSizes5() {
		new RankedBitVectorImpl(-1);
	}

	@Test
	public void testIterator() {
		RankedBitVectorImpl bv = new RankedBitVectorImpl(new BitVectorImpl());
		Assert.assertEquals(0, bv.size());
		for (int i = 0; i < 0x300; i++) {
			bv.addBit((i % 5) == 0);
		}

		Iterator<Boolean> it = bv.iterator();
		for (int i = 0; i < 0x300; i++) {
			boolean value = it.next();
			Assert.assertEquals(bv.getBit(i), value);
		}
		Assert.assertFalse(it.hasNext());
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
			assertCorrectFindPosition(bv);
		}

		{
			RankedBitVectorImpl bv = new RankedBitVectorImpl();
			Assert.assertEquals(0, bv.size());
			for (int i = 0; i < 0x300; i++) {
				bv.addBit((i % 5) == 0);
				Assert.assertEquals(i + 1, bv.size());
			}
			assertCorrectCount(bv);
			assertCorrectFindPosition(bv);
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
		assertCorrectFindPosition(bv);

		for (int i = 0; i < 0x10; i++) {
			boolean value = (i % 2) == 0;
			bv.addBit(value);
		}
		Assert.assertEquals("10010010010010011010101010101010", bv.toString());
		assertCorrectCount(bv);
		assertCorrectFindPosition(bv);

		for (int i = 0; i < 0x20; i++) {
			bv.setBit(i, bv.getBit(i));
		}
		Assert.assertEquals("10010010010010011010101010101010", bv.toString());
		assertCorrectCount(bv);
		assertCorrectFindPosition(bv);

		for (int i = 0; i < 0x20; i++) {
			bv.setBit(i, !bv.getBit(i));
		}
		Assert.assertEquals("01101101101101100101010101010101", bv.toString());
		assertCorrectCount(bv);
		assertCorrectFindPosition(bv);

	}

	@Test
	public void testToStringOfAuxClasses() {
		BitVectorImpl bv = new BitVectorImpl();
		bv.addBit(true);
		bv.addBit(false);
		bv.addBit(true);
		bv.addBit(true);
		bv.addBit(false);
		bv.addBit(false);
		bv.addBit(false);
		bv.addBit(true);

		CountBitsArray cba = new CountBitsArray(bv, 2);
		Assert.assertEquals("[1, 3, 3, 4]", cba.toString());

		FindPositionArray fpa = new FindPositionArray(2, bv, false);
		Assert.assertEquals("[-1, 4, 6]", fpa.toString());
	}

	@Test
	public void testValidInitialSizes() {
		new RankedBitVectorImpl(1, 1, 0x40);
		new RankedBitVectorImpl(1, 2, 0x40);
		new CountBitsArray(new BitVectorImpl(), 1);
		new FindPositionArray(1, new BitVectorImpl(), true);
		new FindPositionArray(new BitVectorImpl(), true, 0x40);
		new RankedBitVectorImpl(0);
	}

}
