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
import org.wikidata.wdtk.storage.datastructure.intf.RankedBitVector;

/**
 * This class keeps the positions where the <i>n</i>-th <i>bit</i> value can be
 * found, where <i>bit</i> can be <code>true</code> or <code>false</code> .
 * 
 * @author Julian Mendez
 * @see RankedBitVectorImpl
 */
class FindPositionArray {

	final boolean bit;

	final BitVector bitVector;

	final int blockSize;

	long[] positionArray;

	FindPositionArray(BitVector bitVector, boolean bit) {
		this.bitVector = bitVector;
		this.bit = bit;
		this.blockSize = 0x10;
		updateCount();
	}

	FindPositionArray(BitVector bitVector, boolean bit, int blockSize) {
		this.bitVector = bitVector;
		this.bit = bit;
		this.blockSize = blockSize;
		updateCount();
	}

	/**
	 * Returns the position for a given number of occurrences or NOT_FOUND if
	 * this value is not found.
	 * 
	 * @param nOccurrence
	 *            number of occurrences
	 * @return the position for a given number of occurrences or NOT_FOUND if
	 *         this value is not found
	 */
	long findPosition(long nOccurrence) {
		long ret = RankedBitVector.NOT_FOUND;
		if (nOccurrence > 0) {
			int findPos = getBlockNumber(nOccurrence);
			if (findPos < this.positionArray.length) {
				long pos0 = this.positionArray[findPos];
				if (pos0 != RankedBitVector.NOT_FOUND) {
					long leftOccurrences = nOccurrence
							- (findPos * this.blockSize);
					if (leftOccurrences == 0) {
						ret = pos0;
					} else {
						for (long index = (pos0 + ((pos0 > 0) ? 1 : 0)); (leftOccurrences > 0)
								&& (index < this.bitVector.size()); index++) {
							if (this.bitVector.getBit(index) == this.bit) {
								leftOccurrences--;
							}
							if (leftOccurrences == 0) {
								ret = index;
							}
						}
					}
				}
			}
		}
		return ret;
	}

	/**
	 * Returns the block number for a given number of occurrences.
	 * 
	 * @param nOccurrences
	 *            nOccurrences
	 * @return the block number for a given number of occurrences
	 */
	int getBlockNumber(long nOccurrences) {
		return (int) (nOccurrences / this.blockSize);
	}

	List<Long> getPositionList() {
		List<Long> ret = new ArrayList<Long>();
		ret.add((long) 0);
		long count = 0;
		for (long index = 0; index < this.bitVector.size(); index++) {
			if (this.bitVector.getBit(index) == this.bit) {
				count++;
			}
			if (count >= this.blockSize) {
				count = 0;
				ret.add(index);
			}
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

	void updateCount() {
		this.positionArray = toArray(getPositionList());
	}

}
