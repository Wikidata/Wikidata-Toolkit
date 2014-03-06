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
 * @see RankedBitVectorImpl
 * 
 * @author Julian Mendez
 */
class CountArray {

	BitVector bitVector;

	final int blockSize;

	/**
	 * This array contains the number of <code>true</code> values found in each
	 * block.
	 */
	long[] countArray;

	boolean hasChanged;

	/**
	 * Creates a block array with a default size.
	 */
	public CountArray(BitVector bitVector) {
		this(bitVector, 0x10);
	}

	/**
	 * Creates a count array with a give block size.
	 * 
	 * @param blockSize
	 *            block size
	 */
	public CountArray(BitVector bitVector, int blockSize) {
		this.bitVector = bitVector;
		this.hasChanged = true;
		this.blockSize = blockSize;
		updateCount();
	}

	public long countBits(boolean bit, long position) {
		updateCount();
		int blockNumber = getBlockNumber(position);
		long mark = ((long) blockNumber) * this.blockSize;
		long trueValues = 0;
		if (blockNumber > 0) {
			trueValues = this.countArray[blockNumber - 1];
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

	List<Long> getCountList() {
		List<Long> ret = new ArrayList<Long>();
		long lastValue = 0;
		int positionInBlock = 0;
		for (long index = 0; index < this.bitVector.size(); index++) {
			if (this.bitVector.getBit(index)) {
				lastValue++;
			}
			positionInBlock++;
			if (positionInBlock == this.blockSize) {
				ret.add(lastValue);
				positionInBlock = 0;
			}
		}
		if (positionInBlock > 0) {
			ret.add(lastValue);
		}
		return ret;
	}

	long[] toArray(List<Long> list) {
		long[] ret = new long[list.size()];
		int index = 0;
		for (Long element : list) {
			ret[index] = element;
			index++;
		}
		return ret;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append('[');
		if (this.countArray.length > 0) {
			str.append(this.countArray[0]);
		}
		for (int index = 1; index < this.countArray.length; index++) {
			str.append(", ");
			str.append(this.countArray[index]);
		}
		str.append(']');
		return str.toString();
	}

	public void update(BitVector bitVector) {
		this.bitVector = bitVector;
		this.hasChanged = true;
	}

	void updateCount() {
		if (this.hasChanged) {
			this.countArray = toArray(getCountList());
			this.hasChanged = false;
		}
	}

}
