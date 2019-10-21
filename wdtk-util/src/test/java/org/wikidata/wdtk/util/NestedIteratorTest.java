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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;

import static org.junit.Assert.*;

public class NestedIteratorTest {

	@Test
	public void testIteration() {
		List<String> list1 = new ArrayList<>();
		list1.add("1");
		list1.add("2");
		List<String> list2 = new ArrayList<>();
		list2.add("3");
		List<String> list3 = new ArrayList<>();
		List<String> list4 = new ArrayList<>();
		list4.add("4");

		List<List<String>> listOfLists = new ArrayList<>();
		listOfLists.add(list1);
		listOfLists.add(list2);
		listOfLists.add(list3);
		listOfLists.add(list4);

		NestedIterator<String> nestedIterator = new NestedIterator<>(
				listOfLists);

		assertTrue(nestedIterator.hasNext());
		assertEquals("1", nestedIterator.next());
		assertTrue(nestedIterator.hasNext());
		assertEquals("2", nestedIterator.next());
		assertTrue(nestedIterator.hasNext());
		assertEquals("3", nestedIterator.next());
		assertTrue(nestedIterator.hasNext());
		assertEquals("4", nestedIterator.next());
		assertFalse(nestedIterator.hasNext());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void removeNotSupported() {
		NestedIterator<String> nestedIterator = new NestedIterator<>(
				Collections.singletonList(Collections.singletonList("Test")));
		nestedIterator.remove();
	}

	@Test(expected = NoSuchElementException.class)
	public void iterateBeyondInnerList() {
		NestedIterator<String> nestedIterator = new NestedIterator<>(
				Collections.singletonList(Collections.emptyList()));
		nestedIterator.next();
	}

	@Test(expected = NoSuchElementException.class)
	public void iterateBeyondOuterList() {
		NestedIterator<String> nestedIterator = new NestedIterator<>(
				Collections.emptyList());
		nestedIterator.next();
	}

}
