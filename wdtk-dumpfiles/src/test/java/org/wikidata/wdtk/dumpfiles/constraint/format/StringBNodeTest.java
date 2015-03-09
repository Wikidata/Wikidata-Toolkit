package org.wikidata.wdtk.dumpfiles.constraint.format;

/*
 * #%L
 * Wikidata Toolkit Dump File Handling
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

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * Test class for {@link StringBNode}.
 * 
 * @author Julian Mendez
 *
 */
public class StringBNodeTest {

	public StringBNodeTest() {
	}

	@Test
	public void testStringValueAndGetID() {
		StringBNode node0 = new StringBNode("");
		Assert.assertEquals("", node0.stringValue());
		Assert.assertNotEquals("", node0.getID());
		StringBNode node1 = new StringBNode("BNode");
		Assert.assertEquals("BNode", node1.stringValue());
		Assert.assertNotEquals("", node1.getID());
	}

	@Test
	public void testEqualityAndHashCode() {
		StringBNode node0 = new StringBNode("BNode");
		Assert.assertEquals(node0, node0);
		Assert.assertNotEquals(node0, null);
		Assert.assertNotEquals(node0, new Object());
		StringBNode node1 = new StringBNode(node0);
		Assert.assertEquals(node0, node1);
		Assert.assertEquals(node0.hashCode(), node1.hashCode());
		StringBNode node2 = new StringBNode("BNode");
		Assert.assertNotEquals(node0, node2);
	}

	@Test
	public void testToString() {
		StringBNode node0 = new StringBNode("BNode");
		Assert.assertEquals("BNode", node0.toString());
	}

}
