package org.wikidata.wdtk.examples.benchmark.storage.datastructure;

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

import org.wikidata.wdtk.storage.datastructure.impl.BitVectorImpl;
import org.wikidata.wdtk.storage.datastructure.intf.BitVector;

/**
 * Example class for {@link BitVector}.
 * 
 * @author Julian Mendez
 * 
 */
public class BitVectorExample {

	/**
	 * Shows the examples
	 * 
	 * @param args
	 *            command line arguments (they are ignored)
	 */
	public static void main(String[] args) {
		BitVectorExample instance = new BitVectorExample();
		System.out.println(instance.createExample());
	}

	/**
	 * Constructor of example class for bit vector.
	 */
	public BitVectorExample() {
	}

	/**
	 * Returns a bit vector with a regular pattern.
	 * 
	 * @param size
	 *            size of the bit vector
	 * @return a bit vector with a regular pattern.
	 */
	public BitVector createBitVectorWithRegularPattern(long size) {
		BitVectorImpl bv = new BitVectorImpl();
		for (int index = 3; index < size; index++) {
			boolean value = (index % 3) == 0;
			bv.addBit(value);
		}
		return bv;
	}

	/**
	 * Returns a string created using a bit vector.
	 * 
	 * @return a string created using a bit vector
	 */
	public String createExample() {
		StringBuilder str = new StringBuilder();
		BitVector bv = createPseudorandomBitVector(0x20);
		str.append("\nExample of 32-bit bit vector with pseudorandom-generated values\n");
		str.append(bv.toString());
		bv = createBitVectorWithRegularPattern(0x20);
		str.append("\n\nExample of 32-bit bit vector with values following a regular pattern\n");
		str.append(bv.toString());
		str.append("\n");
		return str.toString();
	}

	/**
	 * Returns a bit vector which cells contain pseudorandom-generated values.
	 * 
	 * @param size
	 *            size of the bit vector
	 * @return a bit vector which cells contain pseudorandom-generated values
	 */
	public BitVector createPseudorandomBitVector(long size) {
		PseudorandomNumberGenerator rand = new PseudorandomNumberGenerator(
				0x1234);
		BitVectorImpl bv = new BitVectorImpl();
		for (long index = 0; index < size; index++) {
			boolean value = rand.getPseudorandomBoolean();
			bv.addBit(value);
		}
		return bv;
	}

}
