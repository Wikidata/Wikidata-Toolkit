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
 * <li>rank: number of occurrences of a bit at a certain position</li>
 * <li>select: position of the <i>n</i>-th occurrence of a certain bit</li>
 * </ul>
 * 
 * @author Julian Mendez
 */
public interface RankedBitVector extends BitVector {

	/**
	 * This is the &quot;rank&quot; method of bit vectors.
	 * 
	 * @return number of occurrences of <i>bit</i> at <i>position</i>.
	 */
	long countBits(boolean bit, long position);

	/**
	 * This is the &quot;select&quot; method of bit vectors.
	 * 
	 * @return position of the <i>n</i>-th occurrence (<i>nOccurrence</i>) of
	 *         <i>bit</i>.
	 */
	long findPosition(boolean bit, long nOccurrence);

}
