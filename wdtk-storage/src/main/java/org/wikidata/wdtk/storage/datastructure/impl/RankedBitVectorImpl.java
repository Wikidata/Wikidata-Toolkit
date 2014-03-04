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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

	/**
	 * Constructor of a ranked bit vector of size 0.
	 */
	public RankedBitVectorImpl() {
		this.bitVector = new BitVectorImpl();
		this.countArray = new CountArray();
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
			this.countArray = new CountArray(
					((RankedBitVectorImpl) bitVector).countArray.getBlockSize());
		} else {
			this.countArray = new CountArray();
		}
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
		this.countArray = new CountArray();
	}

	/**
	 * Constructor of a ranked bit vector of size <i>initialSize</i> and block
	 * size <i>blockSize</i>. The bit vector contains <code>false</code> at all
	 * indexes.
	 * 
	 * @param initialSize
	 *            initial size of this ranked bit vector
	 * @param blockSize
	 *            block size of this ranked bit vector
	 */
	public RankedBitVectorImpl(long initialSize, int blockSize) {
		this.bitVector = new BitVectorImpl(initialSize);
		this.countArray = new CountArray(blockSize);
	}

	@Override
	public boolean addBit(boolean bit) {
		boolean ret = this.bitVector.addBit(bit);
		this.countArray.updateCount(this.bitVector.size() - 1);
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
		throw new UnsupportedOperationException();
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
		boolean oldBit = getBit(position);
		if (oldBit != bit) {
			this.bitVector.setBit(position, bit);
			if (bit) {
				this.countArray.modifyCount(position, 1);
			} else {
				this.countArray.modifyCount(position, -1);
			}
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

	/**
	 * This class keeps the count of occurrences of <code>true</code> values in
	 * a bit vector.
	 * 
	 * @author Julian Mendez
	 */
	class CountArray {

		final int blockSize;

		/**
		 * This array contains the number of <code>true</code> values found in
		 * each block.
		 */
		final List<Long> count = new ArrayList<Long>();

		/**
		 * Creates a block array with a default size.
		 */
		CountArray() {
			this.blockSize = 0x10;
			updateCount(0);
		}

		/**
		 * Creates a count array with a give block size.
		 * 
		 * @param blockSize
		 *            block size
		 */
		CountArray(int blockSize) {
			this.blockSize = blockSize;
			updateCount(0);
		}

		long countBits(boolean bit, long position) {
			int blockNumber = getBlockNumber(position);
			long mark = ((long) blockNumber) * this.blockSize;
			long trueValues = 0;
			if (blockNumber > 0) {
				trueValues = this.count.get(blockNumber - 1);
			}
			for (long index = mark; index <= position; index++) {
				trueValues += RankedBitVectorImpl.this.bitVector.getBit(index) ? 1
						: 0;
			}
			return bit ? trueValues : ((position + 1) - trueValues);
		}

		/**
		 * Returns the block number for a given position in the bit vector.
		 * 
		 * @param positionInBitVector
		 *            position in bit vector
		 * @return the block number for a given position in the bit vector
		 */
		int getBlockNumber(long positionInBitVector) {
			return (int) (positionInBitVector / this.blockSize);
		}

		/**
		 * Returns the block size.
		 * 
		 * @return the block size
		 */
		int getBlockSize() {
			return this.blockSize;
		}

		/**
		 * Modifies the count by a delta starting from a given position.
		 * 
		 * @param startingPosition
		 *            starting position
		 * @param delta
		 *            delta
		 */
		void modifyCount(long startingPosition, int delta) {
			int blockNumber = getBlockNumber(startingPosition);
			for (int index = blockNumber; index < this.count.size(); index++) {
				this.count.set(index, this.count.get(index) + delta);
			}

		}

		/**
		 * This method updates the whole count starting from a given position
		 * <i>startingPosition</i>. This method assumes that all the previous
		 * positions are updated.
		 * 
		 * @param startingPosition
		 *            starting position to update the count
		 * @param bit
		 *            bit
		 */
		void updateCount(long startingPosition) {
			for (long index = startingPosition; index < RankedBitVectorImpl.this.bitVector
					.size(); index++) {
				int positionInCount = getBlockNumber(index);
				if (positionInCount >= this.count.size()) {
					long lastValue = 0;
					if (this.count.size() > 0) {
						lastValue = this.count.get(this.count.size() - 1);
					}
					this.count.add(lastValue);
				}
				if (RankedBitVectorImpl.this.bitVector.getBit(index)) {
					this.count.set(positionInCount,
							this.count.get(positionInCount) + 1);
				}
			}
		}

	}

}
