package org.wikidata.wdtk.dumpfiles.constraint.format;

import org.junit.Assert;
import org.junit.Test;

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

/**
 *
 * @author Julian Mendez
 *
 */
public class StringResourceTest {

	@Test
	public void testStringValueAndGetID() {
		StringResource node0 = new StringResource("");
		Assert.assertEquals("", node0.stringValue());
		StringResource node1 = new StringResource("Resource");
		Assert.assertEquals("Resource", node1.stringValue());
	}

	@Test
	public void testEqualityAndHashCode() {
		StringResource node0 = new StringResource("Resource");
		StringResource node1 = new StringResource("Resource");
		Assert.assertEquals(node0, node0);
		Assert.assertEquals(node0, node1);
		Assert.assertNotEquals(node0, null);
		Assert.assertNotEquals(node0, new Object());
	}

	@Test
	public void testToString() {
		StringResource node0 = new StringResource("Resource");
		Assert.assertEquals("Resource", node0.toString());
	}

}
