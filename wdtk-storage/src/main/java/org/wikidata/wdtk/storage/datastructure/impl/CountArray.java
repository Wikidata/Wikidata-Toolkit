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
import java.util.List;

import org.wikidata.wdtk.storage.datastructure.intf.BitVector;

/**
 * This class keeps the count of occurrences of <code>true</code> values in a
 * bit vector.
 * 
 * @author Julian Mendez
 * @see RankedBitVectorImpl
 */
class CountArray {

	final BitVector bitVector;

	final int blockSize;

	/**
	 * This array contains the number of <code>true</code> values found in each
	 * block.
	 */
	final List<Long> count = new ArrayList<Long>();

	/**
	 * Creates a block array with a default size.
	 */
	CountArray(BitVector bitVector) {
		this.bitVector = bitVector;
		this.blockSize = 0x10;
		updateCount(0);
	}

	/**
	 * Creates a count array with a give block size.
	 * 
	 * @param blockSize
	 *            block size
	 */
	CountArray(BitVector bitVector, int blockSize) {
		this.bitVector = bitVector;
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
			trueValues += this.bitVector.getBit(index) ? 1 : 0;
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
		for (long index = startingPosition; index < this.bitVector.size(); index++) {
			int positionInCount = getBlockNumber(index);
			if (positionInCount >= this.count.size()) {
				long lastValue = 0;
				if (this.count.size() > 0) {
					lastValue = this.count.get(this.count.size() - 1);
				}
				this.count.add(lastValue);
			}
			if (this.bitVector.getBit(index)) {
				this.count.set(positionInCount,
						this.count.get(positionInCount) + 1);
			}
		}
	}

}
