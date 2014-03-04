package org.wikidata.wdtk.storage.datastructure.intf;

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

/**
 * A ranked bit vector provides operations to compute:
 * <ul>
 * <li>rank ({@link RankedBitVector#countBits}): number of occurrences of a bit
 * at a certain position</li>
 * <li>select ({@link RankedBitVector#findPosition}): position of the
 * <i>n</i>-th occurrence of a certain bit</li>
 * </ul>
 * 
 * @author Julian Mendez
 */
public interface RankedBitVector extends BitVector {

	/**
	 * This is a distinguished value, which denotes that the position of a
	 * <i>n</i>-th occurrence of a <i>bit</i> was not found.
	 * 
	 * @see #findPosition(boolean, long)
	 */
	long NOT_FOUND = -1;

	/**
	 * This is the &quot;rank&quot; method of bit vectors.
	 * 
	 * @return number of occurrences of <i>bit</i> at <i>position</i>
	 */
	long countBits(boolean bit, long position);

	/**
	 * This is the &quot;select&quot; method of bit vectors. Returns the
	 * position for a given number of occurrences of a given bit, or NOT_FOUND
	 * is this number cannot be reached because there are not enough
	 * occurrences.
	 * 
	 * @return position of the <i>n</i>-th occurrence (<i>nOccurrence</i>) of
	 *         <i>bit</i> or NOT_FOUND if there are not enough occurrences
	 */
	long findPosition(boolean bit, long nOccurrence);

}
