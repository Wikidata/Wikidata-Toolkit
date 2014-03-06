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
import org.wikidata.wdtk.storage.datastructure.intf.RankedBitVector;

/**
 * Default implementation of {@link RankedBitVector}. This implementation
 * divides the bit vector in blocks of equal size. It keeps an array with the
 * count of <code>true</code> values present in each block. <br />
 * For example, given the bit vector: 10010, with a block size of 2, the array
 * contains: [1, 2, 2]. The first block contains 1 <code>true</code> value, the
 * second block contains 1 more <code>true</code> value, in total 2. The third
 * block is incomplete, since it has only one bit, and it does not contain more
 * <code>true</code> values.
 * 
 * @author Julian Mendez
 */
public class RankedBitVectorImpl implements RankedBitVector, Iterable<Boolean> {

	final BitVectorImpl bitVector;

	final CountArray countArray;

	final FindPositionArray findPositionOfFalse;

	final FindPositionArray findPositionOfTrue;

	boolean isFindPositionUpdated = false;

	/**
	 * Constructor of a ranked bit vector of size 0.
	 */
	public RankedBitVectorImpl() {
		this.bitVector = new BitVectorImpl();
		this.countArray = new CountArray(this.bitVector);
		this.findPositionOfFalse = new FindPositionArray(this.bitVector, false);
		this.findPositionOfTrue = new FindPositionArray(this.bitVector, true);
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
			this.countArray = new CountArray(this.bitVector,
					((RankedBitVectorImpl) bitVector).countArray.getBlockSize());
		} else {
			this.countArray = new CountArray(this.bitVector);
		}
		this.findPositionOfFalse = new FindPositionArray(this.bitVector, false);
		this.findPositionOfTrue = new FindPositionArray(this.bitVector, true);

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
		this.countArray = new CountArray(this.bitVector);
		this.findPositionOfFalse = new FindPositionArray(this.bitVector, false);
		this.findPositionOfTrue = new FindPositionArray(this.bitVector, true);
	}

	/**
	 * Constructor of a ranked bit vector of size <i>initialSize</i> and block
	 * size <i>blockSize</i>. The bit vector contains <code>false</code> at all
	 * indexes.
	 * 
	 * @param initialSize
	 *            initial size of this ranked bit vector
	 * @param countBlockSize
	 *            block size to count number of occurrences of a value
	 * @param findPositionBlockSize
	 *            block size to find the position of the <i>n</i>-th occurrence
	 *            of a value
	 */
	public RankedBitVectorImpl(long initialSize, int countBlockSize,
			int findPositionBlockSize) {
		this.bitVector = new BitVectorImpl(initialSize);
		this.countArray = new CountArray(this.bitVector, countBlockSize);
		this.findPositionOfFalse = new FindPositionArray(this.bitVector, false,
				findPositionBlockSize);
		this.findPositionOfTrue = new FindPositionArray(this.bitVector, true,
				findPositionBlockSize);
	}

	@Override
	public boolean addBit(boolean bit) {
		this.isFindPositionUpdated = false;
		boolean ret = this.bitVector.addBit(bit);
		long lastPosition = this.bitVector.size() - 1;
		this.countArray.updateCount(lastPosition);
		return ret;
	}

	@Override
	public long countBits(boolean bit, long position) {
		return this.countArray.countBits(bit, position);
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
		updateIfNeeded();
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

	@Override
	public void setBit(long position, boolean bit) {
		this.isFindPositionUpdated = false;
		boolean oldBit = getBit(position);
		if (oldBit != bit) {
			this.bitVector.setBit(position, bit);
			this.countArray.modifyCount(position, bit ? 1 : -1);
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

	void updateIfNeeded() {
		if (!this.isFindPositionUpdated) {
			this.findPositionOfFalse.updateCount();
			this.findPositionOfTrue.updateCount();
			this.isFindPositionUpdated = true;
		}
	}

}
