package org.wikidata.wdtk.storage.db;

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

class LazyOuterObjectIterator<Inner, Outer> implements Iterator<Outer> {

	final Iterator<Inner> rawValueIterator;
	final InnerToOuterObjectConverter<Inner, ? extends Outer> converter;

	public LazyOuterObjectIterator(Iterator<Inner> rawValueIterator,
			InnerToOuterObjectConverter<Inner, ? extends Outer> converter) {
		this.rawValueIterator = rawValueIterator;
		this.converter = converter;
	}

	@Override
	public boolean hasNext() {
		return this.rawValueIterator.hasNext();
	}

	@Override
	public Outer next() {
		Inner inner = this.rawValueIterator.next();
		return converter.getOuterObject(inner);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}