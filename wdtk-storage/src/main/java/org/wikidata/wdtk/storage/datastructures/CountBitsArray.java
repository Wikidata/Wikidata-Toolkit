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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

/**
 * This class keeps the count of occurrences of <code>true</code> values in a
 * bit vector. This implementation divides the bit vector in blocks of equal
 * size. It keeps an array with the count of <code>true</code> values present in
 * each block.
 * <p>
 * For example, given the bit vector: 10010 (0 is <code>false</code>, 1 is
 * <code>true</code>), with a block size of 2, the array contains: [1, 2, 2].
 * The first block contains 1 <code>true</code> value, the second block contains
 * 1 more <code>true</code> value, in total 2. The third block is incomplete,
 * since it has only one bit, and it does not contain more <code>true</code>
 * values.
 * <p>
 * For efficiency reasons, this class assumes that the bit vector is unmodified.
 * Any modification of the bit vector needs to be notified in
 * {@link FindPositionArray#update()}.
 * 
 * @see RankedBitVectorImpl
 * 
 * @author Julian Mendez
 */
class CountBitsArray {

	/**
	 * The bit vector, which is assumed unmodified.
	 */
	final BitVector bitVector;

	/**
	 * The size of each block.
	 */
	final int blockSize;

	/**
	 * This array contains the number of <code>true</code> values found in each
	 * block.
	 */
	long[] countArray;

	/**
	 * If this value is <code>true</code>, there is a new bit vector and the
	 * array needs to be updated.
	 */
	boolean hasChanged;

	/**
	 * Creates a count array with a given block size.
	 * 
	 * @param blockSize
	 *            block size; this value must be a positive number
	 * @throws IllegalArgumentException
	 *             if the block size is not a positive number
	 */
	public CountBitsArray(BitVector bitVector, int blockSize) {
		if (blockSize < 1) {
			throw new IllegalArgumentException(
					"The block size must be a positive number. The received value was: "
							+ blockSize + ".");
		}
		this.bitVector = bitVector;
		this.hasChanged = true;
		this.blockSize = blockSize;
	}

	/**
	 * Returns the number of occurrences of <i>bit</i> up to <i>position</i>.
	 * 
	 * @return number of occurrences of <i>bit</i> up to <i>position</i>
	 */
	public long countBits(boolean bit, long position) {
		updateCount();
		int blockNumber = (int) (position / this.blockSize);
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
	 * Returns the block size.
	 * 
	 * @return the block size
	 */
	int getBlockSize() {
		return this.blockSize;
	}

	/**
	 * Returns a list of Long that contains the indices of positions computed
	 * according to the given bit vector.
	 * 
	 * @return a list of Long that contains the indices of positions computed
	 *         according to the given bit vector
	 */
	List<Long> getCountList() {
		List<Long> ret = new ArrayList<>();
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

	@Override
	public String toString() {
		updateCount();
		return Arrays.toString(this.countArray);
	}

	/**
	 * Notifies this object that the bit vector has changed, and therefore, the
	 * computed internal array must be updated.
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
			this.countArray = ArrayUtils.toPrimitive(getCountList().toArray(
					new Long[0]));
			this.hasChanged = false;
		}
	}

}
