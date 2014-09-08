package org.wikidata.wdtk.storage.datastructures;

/*
 * #%L
 * Wikidata Toolkit Storage
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
import java.util.NoSuchElementException;

import org.apache.commons.lang3.Validate;

/**
 * This is an iterator for a bit vector.
 * 
 * @author Julian Mendez
 */
public class BitVectorIterator implements Iterator<Boolean> {

	final BitVector bitVector;
	int pointer = 0;

	/**
	 * Constructs an iterator for a bit vector.
	 * 
	 * @param bitVector
	 *            bit vector
	 */
	public BitVectorIterator(BitVector bitVector) {
		Validate.notNull(bitVector, "Bit vector cannot be null.");
		this.bitVector = bitVector;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof BitVectorIterator)) {
			return false;
		}
		BitVectorIterator other = (BitVectorIterator) o;
		return (this.pointer == other.pointer)
				&& this.bitVector.equals(other.bitVector);
	}

	@Override
	public int hashCode() {
		return this.pointer + (0x1F * this.bitVector.hashCode());
	}

	@Override
	public boolean hasNext() {
		return this.pointer < this.bitVector.size();
	}

	@Override
	public Boolean next() {
		if (this.pointer >= this.bitVector.size()) {
			throw new NoSuchElementException();
		}

		return this.bitVector.getBit(this.pointer++);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
