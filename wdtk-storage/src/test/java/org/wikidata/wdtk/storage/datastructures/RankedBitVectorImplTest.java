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

package org.wikidata.wdtk.storage.datastructures;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

/**
 * Test class for {@link RankedBitVectorImpl}.
 * 
 * @author Julian Mendez
 * 
 */
public class RankedBitVectorImplTest {

	/**
	 * Asserts that for every position in a bit vector,
	 * {@link RankedBitVector#countBits(boolean, long)} works as expected.
	 * 
	 * @param bv
	 *            bit vector
	 */
	void assertCorrectCount(RankedBitVector bv) {
		for (long index = 0; index < bv.size(); index++) {
			assertCorrectCount(bv, index);
		}
	}

	/**
	 * Asserts that {@link RankedBitVector#countBits(boolean, long)} works as
	 * expected at a particular position.
	 * 
	 * @param bv
	 *            bit vector
	 * @param position
	 *            position
	 */
	void assertCorrectCount(RankedBitVector bv, long position) {
		long expectedCountBitsFalse = countBits(bv, false, position);
		long computedCountBitsFalse = bv.countBits(false, position);
		assertEquals(expectedCountBitsFalse, computedCountBitsFalse);

		long expectedCountBitsTrue = countBits(bv, true, position);
		long computedCountBitsTrue = bv.countBits(true, position);
		assertEquals(expectedCountBitsTrue, computedCountBitsTrue);
	}

	/**
	 * Asserts that for every number of occurrences of a bit value in a bit
	 * vector, {@link RankedBitVector#findPosition(boolean, long)} works as
	 * expected.
	 * 
	 * @param bv
	 *            bit vector
	 */
	void assertCorrectFindPosition(RankedBitVector bv) {
		for (long index = 0; index < bv.size(); index++) {
			assertCorrectFindPosition(bv, index);
		}
	}

	/**
	 * Asserts that {@link RankedBitVector#findPosition(boolean, long)} works as
	 * expected considering the given number of occurrences of a bit value.
	 * 
	 * @param bv
	 *            bit vector
	 * @param nOccurrences
	 *            number of occurrences
	 */
	void assertCorrectFindPosition(RankedBitVector bv, long nOccurrences) {
		long expectedFindPositionFalse = findPosition(bv, false, nOccurrences);
		long computedFindPositionFalse = bv.findPosition(false, nOccurrences);
		assertEquals(expectedFindPositionFalse,
				computedFindPositionFalse);

		long expectedFindPositionTrue = findPosition(bv, true, nOccurrences);
		long computedFindPositionTrue = bv.findPosition(true, nOccurrences);
		assertEquals(expectedFindPositionTrue, computedFindPositionTrue);
	}

	/**
	 * Asserts that two ranked bit vectors are equal, and also that the first
	 * bit vector is equal to itself.
	 * 
	 * @param bv0
	 *            one bit vector
	 * @param bv1
	 *            another bit vector
	 */
	void assertEqualsForBitVector(RankedBitVector bv0, RankedBitVector bv1) {
		assertEquals(bv0, bv0);
		assertEquals(bv0, bv1);
		assertEquals(bv1, bv0);
		assertEquals(bv0.hashCode(), bv1.hashCode());
	}

	/**
	 * Returns the expected value of
	 * {@link RankedBitVector#countBits(boolean, long)}.
	 * 
	 * @param bv
	 *            bit vector
	 * @param bit
	 *            bit value
	 * @param position
	 *            position
	 * @return the expected value of
	 *         {@link RankedBitVector#countBits(boolean, long)}
	 */
	long countBits(BitVector bv, boolean bit, long position) {
		long ret = 0;
		for (long index = 0; index <= position; index++) {
			if (bv.getBit(index) == bit) {
				ret++;
			}
		}
		return ret;
	}

	/**
	 * Returns the expected value of
	 * {@link RankedBitVector#findPosition(boolean, long)}.
	 * 
	 * @param bv
	 *            bit vector
	 * @param bit
	 *            bit value
	 * @param nOccurrences
	 *            number of occurrences
	 * @return the expected value of
	 *         {@link RankedBitVector#findPosition(boolean, long)}
	 */
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
		assertEquals(0, bv.size());

		bv.addBit(true);
		assertEquals(1, bv.size());
		assertTrue(bv.getBit(0));

		bv.addBit(false);
		assertEquals(2, bv.size());
		assertFalse(bv.getBit(1));

		bv.addBit(false);
		assertEquals(3, bv.size());
		assertFalse(bv.getBit(2));

		for (int i = 3; i < 0x1000; i++) {
			boolean value = (i % 3) == 0;
			bv.addBit(value);
			assertEquals(value, bv.getBit(i));
			assertCorrectCount(bv, i);
		}
	}

	@Test
	public void testCountBits() {
		final long aLargeNumber = 0x100000;

		PseudorandomBooleanGenerator generator = new PseudorandomBooleanGenerator(
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
		assertEquals(0, bv0.size());
		assertCorrectCount(bv0);
		assertCorrectFindPosition(bv0);
		assertNotEquals(bv0, new Object());
		assertEquals(bv0, new BitVectorImpl());

		RankedBitVector bv1 = new RankedBitVectorImpl();
		RankedBitVectorImpl bv2 = new RankedBitVectorImpl(0);
		assertEqualsForBitVector(bv1, bv2);
		assertCorrectCount(bv2);
		assertCorrectFindPosition(bv2);
	}

	@Test
	public void testEqualityAndCopyConstructor() {
		final long aLargeNumber = 0x100000;

		RankedBitVectorImpl bv0 = new RankedBitVectorImpl();
		RankedBitVectorImpl bv1 = new RankedBitVectorImpl();

		PseudorandomBooleanGenerator generator = new PseudorandomBooleanGenerator(
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
		assertEquals(0, bv.size());

		bv.addBit(true);

		assertEquals(RankedBitVector.NOT_FOUND, bv.findPosition(true, 0));
		assertEquals(0, bv.findPosition(true, 1));

		bv.addBit(true);
		bv.addBit(false);
		bv.addBit(true);
		bv.addBit(false);
		bv.addBit(false);
		bv.addBit(false);
		bv.addBit(true);

		assertEquals(RankedBitVector.NOT_FOUND,
				bv.findPosition(false, 0));
		assertEquals(RankedBitVector.NOT_FOUND, bv.findPosition(true, 0));
		assertEquals(0, bv.findPosition(true, 1));
		assertEquals(1, bv.findPosition(true, 2));
		assertEquals(2, bv.findPosition(false, 1));
		assertEquals(3, bv.findPosition(true, 3));
		assertEquals(4, bv.findPosition(false, 2));
		assertEquals(5, bv.findPosition(false, 3));
		assertEquals(6, bv.findPosition(false, 4));
		assertEquals(7, bv.findPosition(true, 4));
		assertEquals(RankedBitVector.NOT_FOUND,
				bv.findPosition(false, 5));
		assertEquals(RankedBitVector.NOT_FOUND, bv.findPosition(true, 5));
	}

	@Test
	public void testInvalidInitialSizes0() {
		assertThrows(IllegalArgumentException.class, () -> new RankedBitVectorImpl(1, 0, 0x40));
	}

	@Test
	public void testInvalidInitialSizes1() {
		assertThrows(IllegalArgumentException.class, () -> new RankedBitVectorImpl(1, 2, 0x3F));
	}

	@Test
	public void testInvalidInitialSizes2() {
		assertThrows(IllegalArgumentException.class, () -> new CountBitsArray(new BitVectorImpl(), 0));
	}

	@Test
	public void testInvalidInitialSizes3() {
		assertThrows(IllegalArgumentException.class, () -> new FindPositionArray(0, new BitVectorImpl(), true));
	}

	@Test
	public void testInvalidInitialSizes4() {
		assertThrows(IllegalArgumentException.class, () -> new FindPositionArray(new BitVectorImpl(), true, 0x3F));
	}

	@Test
	public void testInvalidInitialSizes5() {
		assertThrows(IllegalArgumentException.class, () -> new RankedBitVectorImpl(-1));
	}

	@Test
	public void testIterator() {
		RankedBitVectorImpl bv = new RankedBitVectorImpl(new BitVectorImpl());
		PseudorandomBooleanGenerator generator = new PseudorandomBooleanGenerator(
				0x7531);
		assertEquals(0, bv.size());
		for (int i = 0; i < 0x300; i++) {
			bv.addBit(generator.getPseudorandomBoolean());
		}
		Iterator<Boolean> it = bv.iterator();
		for (int i = 0; i < 0x300; i++) {
			boolean value = it.next();
			assertEquals(bv.getBit(i), value);
		}
		assertFalse(it.hasNext());
	}

	@Test
	public void testSize0() {
		RankedBitVectorImpl bv = new RankedBitVectorImpl(0x100);
		assertEquals(0x100, bv.size());
		bv.addBit(false);
		bv.addBit(true);
		assertEquals(0x102, bv.size());
		assertCorrectCount(bv);
		assertCorrectFindPosition(bv);
	}

	@Test
	public void testSize1() {
		RankedBitVectorImpl bv = new RankedBitVectorImpl();
		assertEquals(0, bv.size());
		for (int i = 0; i < 0x300; i++) {
			bv.addBit((i % 5) == 0);
			assertEquals(i + 1, bv.size());
		}
		assertCorrectCount(bv);
		assertCorrectFindPosition(bv);
	}

	@Test
	public void testToString() {
		RankedBitVectorImpl bv = new RankedBitVectorImpl();
		for (int i = 0; i < 0x10; i++) {
			boolean value = (i % 3) == 0;
			bv.addBit(value);
		}
		assertEquals("1001001001001001", bv.toString());
		assertCorrectCount(bv);
		assertCorrectFindPosition(bv);

		for (int i = 0; i < 0x10; i++) {
			boolean value = (i % 2) == 0;
			bv.addBit(value);
		}
		assertEquals("10010010010010011010101010101010", bv.toString());
		assertCorrectCount(bv);
		assertCorrectFindPosition(bv);

		for (int i = 0; i < 0x20; i++) {
			bv.setBit(i, bv.getBit(i));
		}
		assertEquals("10010010010010011010101010101010", bv.toString());
		assertCorrectCount(bv);
		assertCorrectFindPosition(bv);

		for (int i = 0; i < 0x20; i++) {
			bv.setBit(i, !bv.getBit(i));
		}
		assertEquals("01101101101101100101010101010101", bv.toString());
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
		assertEquals("[1, 3, 3, 4]", cba.toString());

		FindPositionArray fpa = new FindPositionArray(2, bv, false);
		assertEquals("[-1, 4, 6]", fpa.toString());

		assertEquals(RankedBitVector.NOT_FOUND, fpa.findPosition(0));
		assertEquals(4, fpa.findPosition(2));
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
