package org.wikidata.wdtk.storage.serialization;

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

public class IntIntArrayIterator implements Iterator<int[]> {

	int pos;
	final int[][] array;
	int[] nextArray;

	public IntIntArrayIterator(int[][] array) {
		this.array = array;
		this.pos = 0;
		findNextArray();
	}

	private void findNextArray() {
		this.nextArray = null;
		while (this.nextArray == null && this.pos < this.array.length) {
			this.nextArray = this.array[this.pos];
			this.pos++;
		}
	}

	@Override
	public boolean hasNext() {
		return this.nextArray != null;
	}

	@Override
	public int[] next() {
		int[] current = this.nextArray;
		findNextArray();
		return current;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
