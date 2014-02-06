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

import org.wikidata.wdtk.storage.datastructure.intf.BitVector;

/**
 * Default implementation of {@link BitVector}. This implementation contains an
 * array of <b>long</b>, and each <b>long</b> stores 64 bits. When more space is
 * needed, the internal array grows exponentially.
 * 
 * @author Julian Mendez
 */
public class BitVectorImpl implements BitVector, Iterable<Boolean> {

	static final int GROWTH_FACTOR = 2;
	static final int LG_WORD_SIZE = 6;
	static final int MINIMUM_ARRAY_SIZE = 1;
	static final int WORD_MASK = 0x3F;
	static final int WORD_SIZE = 0x40;

	long[] array;
	int hashCode;
	long size;
	boolean undefinedHashCode = true;

	/**
	 * Constructor of a bit vector of size 0.
	 * 
	 */
	public BitVectorImpl() {
		this.array = new long[MINIMUM_ARRAY_SIZE];
	}

	/**
	 * Copy constructor of a bit vector. A
	 * 
	 */
	public BitVectorImpl(BitVector vector) {
		if (vector == null) {
			throw new IllegalArgumentException(
					"Null argument. Bit vector cannot be null.");
		}

		if (vector instanceof BitVectorImpl) {
			BitVectorImpl other = (BitVectorImpl) vector;
			this.array = new long[other.array.length];
			System.arraycopy(other.array, 0, this.array, 0, other.array.length);
		} else {
			this.array = new long[getMinimumArraySize(vector.size())];
			for (long index = 0; index < this.size; index++) {
				setBit(index, vector.getBit(index));
			}
		}
		this.size = vector.size();
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

		this.array = new long[getMinimumArraySize(initialSize)];
		this.size = 0;
	}

	/**
	 * @param bit
	 *            bit
	 * @param word
	 *            word
	 * @return the value in a specific <i>bit</i> of a <i>word</i>.
	 */
	static boolean getBitInWord(byte bit, long word) {
		if ((bit < 0) || (bit >= WORD_SIZE)) {
			throw new IndexOutOfBoundsException();
		}
		return ((word >> bit) & 1) == 1;
	}

	/**
	 * @param bitVectorSize
	 *            bit vector sizes
	 * @return the minimum array size for a bit vector of <i>bitVectorSize</i>.
	 */
	static int getMinimumArraySize(long bitVectorSize) {
		return Math.max(MINIMUM_ARRAY_SIZE,
				(int) (bitVectorSize >> LG_WORD_SIZE) + 1);
	}

	/**
	 * @param bit
	 *            bit
	 * @param word
	 *            word
	 * @param value
	 *            value
	 * @return the resulting word of setting a <i>value</i> in a specific
	 *         <i>bit</i> of a <i>word</i>.
	 */
	static long setBitInWord(byte bit, boolean value, long word) {
		if (getBitInWord(bit, word) == value) {
			return word;
		} else {
			return word ^ (((long) 1) << bit);
		}
	}

	/**
	 * @param word
	 *            word to be rendered
	 * @return a string representation of a <i>word</i> with the least
	 *         significant bit first.
	 */
	static String toString(long word) {
		String binaryDigits = String.format("%" + WORD_SIZE + "s",
				Long.toBinaryString(word)).replace(' ', '0');
		return (new StringBuilder(binaryDigits)).reverse().toString();
	}

	@Override
	public boolean add(boolean element) {
		this.undefinedHashCode = true;
		this.size++;
		if (((this.size >> LG_WORD_SIZE) + 1) > this.array.length) {
			resizeArray(GROWTH_FACTOR * this.array.length);
		}
		setBit(this.size - 1, element);
		return true;
	}

	/**
	 * @param position
	 *            position
	 * @throws IndexOutOfBoundsException
	 *             if the position is out of bounds.
	 */
	void assertRange(long position) throws IndexOutOfBoundsException {
		if ((position < 0) || (position >= this.size)) {
			throw new IndexOutOfBoundsException("Position " + position
					+ " is out of bounds.");
		}
	}

	/**
	 * @return a hash code for the current bit vector.
	 */
	int computeHashCode() {
		int ret = (int) this.size;
		int arraySize = (int) (this.size >> LG_WORD_SIZE);
		for (int i = 0; i < arraySize; i++) {
			ret += (0x1F * this.array[i]);
		}
		return ret;
	}

	@Override
	public boolean equals(Object obj) {
		boolean ret = (this == obj);
		if (!ret && (obj instanceof BitVector)) {
			BitVector other = (BitVector) obj;
			ret = (this.size == other.size());
			if (ret) {
				long comparisonFirstPos = 0;
				if (other instanceof BitVectorImpl) {
					BitVectorImpl otherBitVectorImpl = (BitVectorImpl) other;
					int arraySize = (int) (this.size >> LG_WORD_SIZE);
					for (int i = 0; ret && (i < arraySize); i++) {
						ret = ret
								&& (this.array[i] == otherBitVectorImpl.array[i]);
					}
					comparisonFirstPos = ((long) arraySize << LG_WORD_SIZE);
				}
				for (long i = comparisonFirstPos; ret && (i < this.size); i++) {
					ret = ret && (getBit(i) == other.getBit(i));
				}
			}
		}
		return ret;
	}

	@Override
	public boolean getBit(long position) {
		assertRange(position);
		int arrayPos = (int) (position >> LG_WORD_SIZE);
		byte wordPos = (byte) (position & WORD_MASK);
		return getBitInWord(wordPos, this.array[arrayPos]);
	}

	@Override
	public int hashCode() {
		if (this.undefinedHashCode) {
			this.hashCode = computeHashCode();
			this.undefinedHashCode = false;
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
		System.arraycopy(this.array, 0, newArray, 0, this.array.length);
		this.array = newArray;
	}

	@Override
	public void setBit(long position, boolean value) {
		assertRange(position);
		this.undefinedHashCode = true;
		int arrayPos = (int) (position >> LG_WORD_SIZE);
		byte wordPos = (byte) (position & WORD_MASK);
		this.array[arrayPos] = setBitInWord(wordPos, value,
				this.array[arrayPos]);
	}

	@Override
	public long size() {
		return this.size;
	}

	@Override
	public String toString() {
		StringBuffer sbuf = new StringBuffer();
		for (long position = 0; position < this.size;) {
			if ((position + WORD_SIZE) < this.size) {
				int arrayPos = (int) (position >> LG_WORD_SIZE);
				sbuf.append(toString(this.array[arrayPos]));
				position += WORD_SIZE;
			} else {
				sbuf.append(getBit(position) ? "1" : "0");
				position++;
			}
		}
		return sbuf.toString();
	}

}
