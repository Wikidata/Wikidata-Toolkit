package org.wikidata.wdtk.storage.datastructure.intf;

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
 * 
 */
public interface BitVector {

	/**
	 * Appends an <i>element</i> to this bit vector.
	 * 
	 * @return <code>true</code> if the element was successfully added.
	 */
	boolean add(boolean element);

	/**
	 * This is the &quot;rank&quot; method of bit vectors.
	 * 
	 * @return number of occurrences of <i>bit</i> at <i>position</i>.
	 */
	long count(boolean bit, long position);

	/**
	 * This is the &quot;select&quot; method of bit vectors.
	 * 
	 * @return position of the n-th occurrence (<i>nOccurrence</i>) of
	 *         <i>bit</i>.
	 */
	long find(boolean bit, long nOccurrence);

	/**
	 * This is the &quot;access&quot; method of bit vectors.
	 * 
	 * @return value of a bit at <i>position</i>.
	 */
	boolean get(long position);

	/**
	 * @return an iterator for this bit vector.
	 */
	Iterator<BitVector> iterator();

	/**
	 * Sets a <i>value</i> at a particular <i>position</i>.
	 * 
	 * @param position
	 *            position
	 * @param value
	 *            value
	 */
	void set(long position, boolean value);

	/**
	 * @return size of this bit vector.
	 */
	long size();

}
