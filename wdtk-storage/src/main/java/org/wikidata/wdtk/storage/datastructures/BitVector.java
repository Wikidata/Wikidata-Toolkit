package org.wikidata.wdtk.storage.datastructures;

import java.util.Iterator;

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

/**
 * Interface for a bit vector.
 * 
 * @author Julian Mendez
 */
public interface BitVector {

	/**
	 * Returns <code>true</code> if and only if the specified object is also a
	 * BitVector and both contain the same bits in the same order.
	 * 
	 * @param o
	 *            the object to be compared with this BitVector
	 * 
	 * @return <code>true</code> if and only if the specified object is also a
	 *         BitVector and both contain the same bits in the same order
	 */
	@Override
	boolean equals(Object o);

	/**
	 * This is the &quot;access&quot; method of bit vectors.
	 * 
	 * @return value of a bit at <i>position</i>
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if the position is out of range
	 */
	boolean getBit(long position);

	/**
	 * @return size of this bit vector
	 */
	long size();

	/**
	 * @return an iterator for this bit vector
	 */
	Iterator<Boolean> iterator();

	/**
	 * Appends a <i>bit</i> to this bit vector.
	 * 
	 * @return <code>true</code> if the element was successfully added
	 */
	boolean addBit(boolean bit);

	/**
	 * Sets a <i>bit</i> at a particular <i>position</i>.
	 * 
	 * @param position
	 *            position
	 * @param bit
	 *            bit
	 * @throws IndexOutOfBoundsException
	 *             if the position is out of range
	 */
	void setBit(long position, boolean bit);

}
