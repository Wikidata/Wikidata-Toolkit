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

/**
 * Default implementation of {@link RankedBitVector}. This implementation uses
 * auxiliary classes to have efficient performance for the methods of a ranked
 * bit vector. Hence, {@link #countBits(boolean, long)} uses an instance of
 * {@link CountBitsArray} and {@link #findPosition(boolean, long)} uses two
 * instances of {@link FindPositionArray}.
 * 
 * @see CountBitsArray
 * 
 * @see FindPositionArray
 * 
 * @author Julian Mendez
 */
public class RankedBitVectorImpl implements RankedBitVector, Iterable<Boolean> {

	static final int defaultCountBitsBlockSize = 0x400;

	static final int defaultFindPositionBlockSize = 0x2000;

	final BitVectorImpl bitVector;

	final CountBitsArray countBitsArray;

	final FindPositionArray findPositionOfFalse;

	final FindPositionArray findPositionOfTrue;

	/**
	 * Constructor of a ranked bit vector of size 0.
	 */
	public RankedBitVectorImpl() {
		this.bitVector = new BitVectorImpl();
		this.countBitsArray = new CountBitsArray(this.bitVector,
				defaultCountBitsBlockSize);
		this.findPositionOfFalse = new FindPositionArray(this.bitVector, false,
				defaultFindPositionBlockSize);
		this.findPositionOfTrue = new FindPositionArray(this.bitVector, true,
				defaultFindPositionBlockSize);
	}

	/**
	 * Copy constructor of a ranked bit vector.
	 * 
	 * @param bitVector
	 *            bit vector
	 */
	public RankedBitVectorImpl(BitVector bitVector) {
		this.bitVector = new BitVectorImpl(bitVector);
		if (bitVector instanceof RankedBitVectorImpl) {
			this.countBitsArray = new CountBitsArray(this.bitVector,
					((RankedBitVectorImpl) bitVector).countBitsArray
							.getBlockSize());
		} else {
			this.countBitsArray = new CountBitsArray(this.bitVector,
					defaultCountBitsBlockSize);
		}
		this.findPositionOfFalse = new FindPositionArray(this.bitVector, false,
				defaultFindPositionBlockSize);
		this.findPositionOfTrue = new FindPositionArray(this.bitVector, true,
				defaultFindPositionBlockSize);

	}

	/**
	 * Constructor of a ranked bit vector of size <i>initialSize</i>. The bit
	 * vector contains <code>false</code> at all indexes.
	 * 
	 * @param initialSize
	 *            initial size of this ranked bit vector
	 */
	public RankedBitVectorImpl(long initialSize) {
		this.bitVector = new BitVectorImpl(initialSize);
		this.countBitsArray = new CountBitsArray(this.bitVector,
				defaultCountBitsBlockSize);
		this.findPositionOfFalse = new FindPositionArray(this.bitVector, false,
				defaultFindPositionBlockSize);
		this.findPositionOfTrue = new FindPositionArray(this.bitVector, true,
				defaultFindPositionBlockSize);
	}

	/**
	 * Constructor of a ranked bit vector of size <i>initialSize</i> and block
	 * size <i>blockSize</i>. The bit vector contains <code>false</code> at all
	 * indexes.
	 * 
	 * @param initialSize
	 *            initial size of this ranked bit vector
	 * @param countBlockSize
	 *            block size to count number of occurrences of a value; this
	 *            value must be a positive number
	 * @param findPositionBlockSize
	 *            block size to find the position of the <i>n</i>-th occurrence
	 *            of a value; this value must be greater than or equal to 64
	 * @throws IllegalArgumentException
	 *             if any of the block sizes is too small
	 */
	public RankedBitVectorImpl(long initialSize, int countBlockSize,
			int findPositionBlockSize) {
		this.bitVector = new BitVectorImpl(initialSize);
		this.countBitsArray = new CountBitsArray(this.bitVector, countBlockSize);
		this.findPositionOfFalse = new FindPositionArray(this.bitVector, false,
				findPositionBlockSize);
		this.findPositionOfTrue = new FindPositionArray(this.bitVector, true,
				findPositionBlockSize);
	}

	@Override
	public boolean addBit(boolean bit) {
		boolean ret = this.bitVector.addBit(bit);
		notifyObservers();
		return ret;
	}

	@Override
	public long countBits(boolean bit, long position) {
		return this.countBitsArray.countBits(bit, position);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof BitVector)) {
			return false;
		}
		return this.bitVector.equals(obj);
	}

	@Override
	public long findPosition(boolean bit, long nOccurrence) {
		if (nOccurrence <= 0) {
			return NOT_FOUND;
		}
		return bit ? this.findPositionOfTrue.findPosition(nOccurrence)
				: this.findPositionOfFalse.findPosition(nOccurrence);
	}

	@Override
	public boolean getBit(long position) {
		return this.bitVector.getBit(position);
	}

	@Override
	public int hashCode() {
		return this.bitVector.hashCode();
	}

	@Override
	public Iterator<Boolean> iterator() {
		return this.bitVector.iterator();
	}

	void notifyObservers() {
		this.countBitsArray.update();
		this.findPositionOfFalse.update();
		this.findPositionOfTrue.update();
	}

	@Override
	public void setBit(long position, boolean bit) {
		boolean oldBit = getBit(position);
		if (oldBit != bit) {
			this.bitVector.setBit(position, bit);
			notifyObservers();
		}
	}

	@Override
	public long size() {
		return this.bitVector.size();
	}

	@Override
	public String toString() {
		return this.bitVector.toString();
	}

}
