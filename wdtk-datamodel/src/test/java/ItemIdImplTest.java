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
import org.wikidata.wdtk.datamodel.implementation.ItemIdImpl;
import org.wikidata.wdtk.datamodel.interfaces.EntityId;

import junit.framework.TestCase;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class ItemIdImplTest extends TestCase {

	private ItemIdImpl item1;
	private ItemIdImpl item2;
	private ItemIdImpl item3;
	private ItemIdImpl item4;

	protected void setUp() {
		item1 = new ItemIdImpl("Q42", "http://www.wikidata.org/entity/");
		item2 = new ItemIdImpl("Q42", "http://www.wikidata.org/entity/");
		item3 = new ItemIdImpl("Q57", "http://www.wikidata.org/entity/");
		item4 = new ItemIdImpl("Q42", "http://www.example.org/entity/");
	}

	public void testItemType() {
		assertEquals(item1.getEntityType(), EntityId.EntityType.ITEM);
	}

	public void testItemIri() {
		assertEquals(item1.getIri(), "http://www.wikidata.org/entity/Q42");
		assertEquals(item4.getIri(), "http://www.example.org/entity/Q42");
	}

	public void testItemId() {
		assertEquals(item1.getId(), "Q42");
	}

	public void testItemEquality() {
		assertEquals(item1, item2);
		assertThat(item1, not(equalTo(item3)));
		assertThat(item1, not(equalTo(item4)));
	}

}
