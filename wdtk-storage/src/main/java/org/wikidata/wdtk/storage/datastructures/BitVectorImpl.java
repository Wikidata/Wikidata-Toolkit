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

import java.util.Iterator;

import org.apache.commons.lang3.Validate;

/**
 * Default implementation of {@link BitVector}. This implementation contains an
 * array of <b>long</b>, and each <b>long</b> stores 64 bits. When more space is
 * needed, the internal array grows exponentially. This bit vector is
 * <i>flexible</i>, which means that:
 * <ol>
 * <li>it is always possible to store a bit in any non-negative position without
 * explicitly resizing the vector,</li>
 * <li>any non-negative position outside the bit vector can be retrieved and
 * contains a <code>false</code>.</li>
 * </ol>
 *
 * @author Julian Mendez
 *
 */
public class BitVectorImpl implements BitVector, Iterable<Boolean> {

	static final int GROWTH_FACTOR = 2;
	static final int LG_WORD_SIZE = 6;
	static final int MINIMUM_ARRAY_SIZE = 1;
	static final int WORD_MASK = 0x3F;
	static final int WORD_SIZE = 0x40;

	long[] arrayOfBits;
	int hashCode;
	long size;
	boolean validHashCode = false;

	/**
	 * Constructor of a bit vector of size 0.
	 *
	 */
	public BitVectorImpl() {
		this.arrayOfBits = new long[MINIMUM_ARRAY_SIZE];
	}

	/**
	 * Copy constructor of a bit vector.
	 *
	 * @param bitVector
	 *            bit vector
	 */
	public BitVectorImpl(BitVector bitVector) {
		Validate.notNull(bitVector, "Bit vector cannot be null.");
		if (bitVector instanceof BitVectorImpl) {
			BitVectorImpl other = (BitVectorImpl) bitVector;
			this.arrayOfBits = new long[other.arrayOfBits.length];
			this.size = bitVector.size();
			System.arraycopy(other.arrayOfBits, 0, this.arrayOfBits, 0,
					other.arrayOfBits.length);
		} else {
			this.arrayOfBits = new long[getMinimumArraySize(bitVector.size())];
			this.size = bitVector.size();
			for (long index = 0; index < bitVector.size(); index++) {
				setBit(index, bitVector.getBit(index));
			}
		}
	}

	/**
	 * Constructor of a bit vector of size <i>initialSize</i>. The bit vector
	 * contains <code>false</code> at all indexes.
	 *
	 * @param initialSize
	 *            initial size of this bit vector
	 *
	 */
	public BitVectorImpl(long initialSize) {
		if (initialSize < 0) {
			throw new IllegalArgumentException("Wrong bit vector size '"
					+ initialSize + "'. Bit vector size must be non-negative.");
		}

		this.arrayOfBits = new long[getMinimumArraySize(initialSize)];
		this.size = initialSize;
	}

	/**
	 * @param position
	 *            position
	 * @param word
	 *            word
	 * @return the value of a bit at a specific <i>position</i> of a <i>word</i>
	 */
	static boolean getBitInWord(byte position, long word) {
		if ((position < 0) || (position >= WORD_SIZE)) {
			throw new IndexOutOfBoundsException();
		}
		return ((word >> position) & 1) == 1;
	}

	/**
	 * @param bitVectorSize
	 *            bit vector sizes
	 * @return the minimum array size for a bit vector of <i>bitVectorSize</i>
	 */
	static int getMinimumArraySize(long bitVectorSize) {
		return Math.max(MINIMUM_ARRAY_SIZE, getSizeInWords(bitVectorSize));
	}

	/**
	 * @param position
	 *            position
	 * @param word
	 *            word
	 * @param bit
	 *            bit
	 * @return the resulting word of setting a <i>bit</i> at a specific
	 *         <i>position</i> of a <i>word</i>
	 */
	static long setBitInWord(byte position, boolean bit, long word) {
		if (getBitInWord(position, word) == bit) {
			return word;
		} else {
			return word ^ (((long) 1) << position);
		}
	}

	/**
	 * @param word
	 *            word to be rendered
	 * @return a string representation of a <i>word</i> with the least
	 *         significant bit first
	 */
	static String wordToString(long word) {
		String binaryDigits = String.format("%" + WORD_SIZE + "s",
				Long.toBinaryString(word)).replace(' ', '0');
		return (new StringBuilder(binaryDigits)).reverse().toString();
	}

	/**
	 * @param sizeInBits
	 *            size in bits
	 * @return the size in words
	 */
	static int getSizeInWords(long sizeInBits) {
		return (int) ((sizeInBits >> LG_WORD_SIZE) + 1);
	}

	@Override
	public boolean addBit(boolean bit) {
		this.validHashCode = false;
		this.size++;
		if (getSizeInWords(this.size) > this.arrayOfBits.length) {
			resizeArray(GROWTH_FACTOR * this.arrayOfBits.length);
		}
		setBit(this.size - 1, bit);
		return true;
	}

	/**
	 * @param position
	 *            position
	 * @throws IndexOutOfBoundsException
	 *             if the position is a negative number
	 */
	void assertNonNegativePosition(long position)
			throws IndexOutOfBoundsException {
		if ((position < 0)) {
			throw new IndexOutOfBoundsException("Position " + position
					+ " is out of bounds.");
		}
	}

	/**
	 * Ensures that the bit vector is large enough to contain an element at the
	 * given position. If the bit vector needs to be enlarged, new
	 * <code>false</code> elements are added.
	 *
	 * @param position
	 *            position
	 */
	void ensureSize(long position) {
		assertNonNegativePosition(position);
		if (position >= this.size) {
			this.validHashCode = false;
			long newSize = position + 1;
			int arrayOfBitsLength = this.arrayOfBits.length;
			int sizeInWords = getSizeInWords(newSize);
			while (sizeInWords > arrayOfBitsLength) {
				arrayOfBitsLength = GROWTH_FACTOR * arrayOfBitsLength;
			}
			resizeArray(arrayOfBitsLength);
			this.size = newSize;
		}
	}

	/**
	 * @return a hash code for the current bit vector
	 */
	int computeHashCode() {
		int ret = (int) this.size;

		int arraySize = (int) (this.size >> LG_WORD_SIZE);
		for (int i = 0; i < arraySize; i++) {
			ret += (0x1F * this.arrayOfBits[i]);
		}

		long lastWordStart = (arraySize << LG_WORD_SIZE);
		long remainingBits = this.size - lastWordStart;
		long lastWord = 0;
		for (int i = 0; i < remainingBits; i++) {
			lastWord = setBitInWord((byte) i, getBit(i + lastWordStart),
					lastWord);
		}
		ret += (0x1F * lastWord);

		return ret;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof BitVector)) {
			return false;
		}
		BitVector other = (BitVector) obj;
		if (this.size != other.size()) {
			return false;
		}

		long comparisonFirstPos = 0;

		if (other instanceof BitVectorImpl) {
			// if the other bit vector has the same representation, it is
			// possible to compare their arrays of bits

			BitVectorImpl otherBitVectorImpl = (BitVectorImpl) other;
			int arraySize = (int) (this.size >> LG_WORD_SIZE);
			// only full words can be compared, because two bit
			// vectors that are equal can have different values in the unused
			// bits

			for (int i = 0; i < arraySize; i++) {
				if (this.arrayOfBits[i] != otherBitVectorImpl.arrayOfBits[i]) {
					return false;
				}
			}
			comparisonFirstPos = ((long) arraySize << LG_WORD_SIZE);
		}

		for (long i = comparisonFirstPos; i < this.size; i++) {
			// bit-by-bit comparison of the remaining bits
			if (getBit(i) != other.getBit(i)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean getBit(long position) {
		assertNonNegativePosition(position);
		if (position >= this.size) {
			return false;
		}
		int arrayPos = (int) (position >> LG_WORD_SIZE);
		byte wordPos = (byte) (position & WORD_MASK);
		return getBitInWord(wordPos, this.arrayOfBits[arrayPos]);
	}

	@Override
	public int hashCode() {
		if (!this.validHashCode) {
			this.hashCode = computeHashCode();
			this.validHashCode = true;
		}
		return this.hashCode;
	}

	@Override
	public Iterator<Boolean> iterator() {
		return new BitVectorIterator(this);
	}

	/**
	 * Resizes the array that represents this bit vector.
	 *
	 * @param newArraySize
	 *            new array size
	 */
	void resizeArray(int newArraySize) {
		long[] newArray = new long[newArraySize];
		System.arraycopy(this.arrayOfBits, 0, newArray, 0,
				Math.min(this.arrayOfBits.length, newArraySize));
		this.arrayOfBits = newArray;
	}

	@Override
	public void setBit(long position, boolean bit) {
		ensureSize(position);
		this.validHashCode = false;
		int arrayPos = (int) (position >> LG_WORD_SIZE);
		byte wordPos = (byte) (position & WORD_MASK);
		this.arrayOfBits[arrayPos] = setBitInWord(wordPos, bit,
				this.arrayOfBits[arrayPos]);
	}

	@Override
	public long size() {
		return this.size;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (long position = 0; position < this.size;) {
			sb.append(getBit(position) ? "1" : "0");
			position++;
		}
		return sb.toString();
	}

}
