package org.wikidata.wdtk.datamodel.implementation;

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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeId;

public class DatatypeIdImplTest {

	@Test
	public void datatypeEqualityBasedOnContent() {
		DatatypeIdImpl d1 = new DatatypeIdImpl(DatatypeId.DT_ITEM);
		DatatypeIdImpl d2 = new DatatypeIdImpl("http://www.wikidata.org/ontology#propertyTypeItem");
		DatatypeIdImpl d3 = new DatatypeIdImpl(DatatypeId.DT_TIME);
		
		assertEquals(d1, d2);
		assertThat(d1, not(equalTo(d3)));
	}

}
