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
 * 
 */
public class BitVectorImpl implements BitVector {

	static final int GROWTH_FACTOR = 2;
	static final int LG_WORD_SIZE = 6;
	static final int MINIMUM_ARRAY_SIZE = 1;
	static final int WORD_MASK = 0x3F;
	static final int WORD_SIZE = 0x40;

	/**
	 * @param bit
	 *            bit
	 * @param word
	 *            word
	 * @return the value in a specific <i>bit</i> of a <i>word</i>.
	 */
	static boolean getBit(int bit, long word) {
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
	static long setBit(int bit, boolean value, long word) {
		if (getBit(bit, word) == value) {
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

	private long[] array = new long[MINIMUM_ARRAY_SIZE];

	private long size;

	/**
	 * Constructor of a bit vector of size 0.
	 * 
	 */
	public BitVectorImpl() {
	}

	/**
	 * Copy constructor of a bit vector.
	 * 
	 */
	public BitVectorImpl(BitVector vector) {
		this(vector.size());
		for (long index = 0; index < this.size; index++) {
			set(index, vector.get(index));
		}
	}

	/**
	 * Constructor of a bit vector of size <i>initialSize</i>.
	 * 
	 */
	public BitVectorImpl(long initialSize) {
		if (initialSize < 0) {
			throw new IllegalArgumentException("Wrong vector size '"
					+ initialSize + "'. Vector size must be non-negative.");
		}

		this.array = new long[getMinimumArraySize(initialSize)];
		this.size = initialSize;
	}

	@Override
	public boolean add(boolean element) {
		this.size++;
		if (((this.size >> LG_WORD_SIZE) + 1) > this.array.length) {
			resizeArray();
		}
		set(this.size - 1, element);
		return true;
	}

	void assertRange(long position) throws IndexOutOfBoundsException {
		if ((position < 0) || (position >= size())) {
			throw new IndexOutOfBoundsException("Position " + position
					+ " is out of bounds.");
		}
	}

	/** This method is not implemented. */
	@Override
	public long count(boolean bit, long position) {
		// TODO
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public boolean equals(Object obj) {
		boolean ret = (this == obj);
		if (!ret && (obj instanceof BitVector)) {
			BitVector other = (BitVector) obj;
			ret = size() == other.size();
			for (long i = 0; ret && (i < size()); i++) {
				ret = ret && (get(i) == other.get(i));
			}
		}
		return ret;
	}

	/** This method is not implemented. */
	@Override
	public long find(boolean bit, long nOccurrence) {
		// TODO
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public boolean get(long position) {
		assertRange(position);
		int arrayPos = (int) (position >> LG_WORD_SIZE);
		int wordPos = (int) (position & WORD_MASK);
		return getBit(wordPos, this.array[arrayPos]);
	}

	@Override
	public int hashCode() {
		return (int) (this.size + (0x1F * this.array[0]));
	}

	/** This method is not implemented. */
	@Override
	public Iterator<BitVector> iterator() {
		// TODO
		throw new UnsupportedOperationException("Method not implemented.");
	}

	void resizeArray() {
		long[] newArray = new long[GROWTH_FACTOR * this.array.length];
		System.arraycopy(this.array, 0, newArray, 0, this.array.length);
		this.array = newArray;
	}

	@Override
	public void set(long position, boolean value) {
		assertRange(position);
		int arrayPos = (int) (position >> LG_WORD_SIZE);
		int wordPos = (int) (position & WORD_MASK);
		this.array[arrayPos] = setBit(wordPos, value, this.array[arrayPos]);
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
				sbuf.append(get(position) ? "1" : "0");
				position++;
			}
		}
		return sbuf.toString();
	}

}
