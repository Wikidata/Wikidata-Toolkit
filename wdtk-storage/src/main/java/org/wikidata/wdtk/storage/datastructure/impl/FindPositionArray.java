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
 * <p>
 * This class keeps the positions where the <i>n</i>-th <i>bit</i> value can be
 * found in a bit vector (<i>bit</i> can be <code>true</code> or
 * <code>false</code>). This class uses an array to store these positions. Each
 * cell of the array covers a block in the bit vector, and to find the positions
 * in this block, the method iterates on the bit vector.
 * </p>
 * <p>
 * For example, let us suppose we have the following bit vector: 11010001 (0 is
 * <code>false</code> and 1 is <code>true</code>), with a block size of 2. For
 * the case of <code>true</code>, the array stores the position where
 * <code>true</code> is found for the zeroth occurrence, the second occurrence,
 * the forth occurrence, and so on and so forth. The first cell of the array
 * contains -1. This convention comes handy because the zeroth occurrence is
 * undefined and the first occurrence needs to be found in the following cell of
 * the bit vector, i.e. at position 0. Anyway, since the zeroth occurrence is
 * not defined, the {@link #findPosition(long)} method returns
 * {@link RankedBitVector.NOT_FOUND} for that value.
 * </p>
 * <p>
 * The array for <code>true</code> is [-1, 1, 7]. The second occurrence of
 * <code>true</code> is at position 1 in the bit vector. The forth occurrence of
 * <code>true</code> is at position 7 in the bit vector. Analogously, the array
 * for <code>false</code> is [-1, 4, 6]. The positions of <code>false</code> are
 * 4 for the second occurrence, and 6 for the forth occurrence.
 * </p>
 * <p>
 * Please observe that the blocks have the same size in number of occurrences,
 * but may cover different number of positions in the bit vector.
 * </p>
 * <p>
 * For efficiency reasons, this class assumes that the bit vector is unmodified.
 * Any modification of the bit vector needs to be notified in
 * {@link FindPositionArray#update(BitVector)}.
 * </p>
 * 
 * @see RankedBitVectorImpl
 * 
 * @author Julian Mendez
 */
class FindPositionArray {

	/**
	 * Value to be consider in the occurrences.
	 */
	final boolean bit;

	/**
	 * The bit vector, which is assumed unmodified.
	 */
	final BitVector bitVector;

	/**
	 * This is the size of each block of occurrences.
	 */
	final int blockSize;

	/**
	 * If this value is <code>true</code>, there is a new bit vector and the
	 * array needs to be updated.
	 */
	boolean hasChanged;

	/**
	 * This array contains the position represented as explained above.
	 */
	long[] positionArray;

	/**
	 * Constructs a new array.
	 * 
	 * @param bitVector
	 *            bit vector
	 * @param bit
	 *            bit
	 */
	public FindPositionArray(BitVector bitVector, boolean bit) {
		this(bitVector, bit, 0x10);
	}

	/**
	 * Constructs a new array using a given block size of occurrences.
	 * 
	 * @param bitVector
	 *            bit vector
	 * @param bit
	 *            bit
	 * @param blockSize
	 *            block size
	 */
	public FindPositionArray(BitVector bitVector, boolean bit, int blockSize) {
		this.bitVector = bitVector;
		this.hasChanged = true;
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
	public long findPosition(long nOccurrence) {
		updateCount();
		if (nOccurrence <= 0) {
			return RankedBitVector.NOT_FOUND;
		}
		int findPos = getBlockNumber(nOccurrence);
		if (findPos < this.positionArray.length) {
			long pos0 = this.positionArray[findPos];
			long leftOccurrences = nOccurrence - (findPos * this.blockSize);
			if (leftOccurrences == 0) {
				return pos0;
			} else {
				for (long index = pos0 + 1; (leftOccurrences > 0)
						&& (index < this.bitVector.size()); index++) {
					if (this.bitVector.getBit(index) == this.bit) {
						leftOccurrences--;
					}
					if (leftOccurrences == 0) {
						return index;
					}
				}
			}
		}
		return RankedBitVector.NOT_FOUND;
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

	/**
	 * Returns a list of Long that contains the indices of positions computed
	 * according to the given bit vector.
	 * 
	 * @return a list of Long that contains the indices of positions computed
	 *         according to the given bit vector
	 */
	List<Long> getPositionList() {
		List<Long> ret = new ArrayList<Long>();

		ret.add(-1L);
		/*
		 * This -1 is pointing to the previous position of the first valid
		 * position of the bit vector, which starts at index 0. Since the zeroth
		 * occurrence of a bit is undefined, the first occurrence can be at
		 * position 0, or later.
		 */

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

	/**
	 * Transforms a list of Long to an array of long.
	 * 
	 * @param list
	 *            list
	 * @return an array of long
	 */
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
		if (this.positionArray.length > 0) {
			str.append(this.positionArray[0]);
		}
		for (int index = 1; index < this.positionArray.length; index++) {
			str.append(", ");
			str.append(this.positionArray[index]);
		}
		str.append(']');
		return str.toString();
	}

	/**
	 * Notifies this object that the bit vector has changed, and therefore, the
	 * computed internal array must be updated.
	 * 
	 * @param bitVector
	 *            new bit vector
	 */
	public void update() {
		this.hasChanged = true;
	}

	/**
	 * This method updates the internal array only if the bit vector has been
	 * changed since the last update or creation of this class.
	 */
	void updateCount() {
		if (this.hasChanged) {
			this.positionArray = toArray(getPositionList());
			this.hasChanged = false;
		}
	}

}
