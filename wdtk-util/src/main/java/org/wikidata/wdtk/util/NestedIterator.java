package org.wikidata.wdtk.util;

/*
 * #%L
 * Wikidata Toolkit Utilities
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

/**
 * Given an iterable of iterables of T, this class simulates an iterator of T.
 * For example, it can be used to iterate over every element in a list of lists
 * of T.
 * <p>
 * This implementation does not support the removal of elements.
 * 
 * @author Markus Kroetzsch
 * 
 * @param <T>
 */
public class NestedIterator<T> implements Iterator<T> {

	Iterator<? extends Iterable<T>> outerIterator;
	Iterator<T> innerIterator;

	/**
	 * Constructor.
	 * 
	 * @param iterableOfIterables
	 *            the nested iterable to iterate over
	 */
	public NestedIterator(Iterable<? extends Iterable<T>> iterableOfIterables) {
		this.outerIterator = iterableOfIterables.iterator();
		advanceOuterIterator();
	}

	@Override
	public boolean hasNext() {
		return this.innerIterator != null;
	}

	@Override
	public T next() {
		if (this.innerIterator == null) {
			throw new NoSuchElementException();
		}
		T result = this.innerIterator.next();
		if (!this.innerIterator.hasNext()) {
			advanceOuterIterator();
		}
		return result;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	private void advanceOuterIterator() {
		while ((this.innerIterator == null || !this.innerIterator.hasNext())
				&& this.outerIterator.hasNext()) {
			this.innerIterator = this.outerIterator.next().iterator();
		}
		if (this.innerIterator != null && !this.innerIterator.hasNext()) {
			this.innerIterator = null;
		}
	}

}
