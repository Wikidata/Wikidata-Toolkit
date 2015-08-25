package org.wikidata.wdtk.wikibaseapi;

/*
 * #%L
 * Wikidata Toolkit Wikibase API
 * %%
 * Copyright (C) 2014 - 2015 Wikidata Toolkit Developers
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

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.util.CompressionType;

public class GetEntitiesActionTest {

	MockApiConnection con;
	WbGetEntitiesAction action;

	@Before
	public void setUp() throws Exception {

		this.con = new MockApiConnection();
		Map<String, String> params = new HashMap<String, String>();
		params.put("action", "wbgetentities");
		params.put("format", "json");
		params.put("props",
				"datatype|labels|aliases|descriptions|claims|sitelinks");
		params.put("ids", "Q6|Q42|P31");
		this.con.setWebResourceFromPath(params, this.getClass(),
				"/wbgetentities-Q6-Q42-P31.json", CompressionType.NONE);
		this.action = new WbGetEntitiesAction(this.con, Datamodel.SITE_WIKIDATA);

	}

	@Test
	public void testWbgetEntities() {
		WbGetEntitiesProperties properties = new WbGetEntitiesProperties();
		properties.ids = "Q6|Q42|P31";
		properties.props = "datatype|labels|aliases|descriptions|claims|sitelinks";
		Map<String, EntityDocument> result1 = action.wbGetEntities(properties);

		Map<String, EntityDocument> result2 = action.wbGetEntities(
				properties.ids, null, null, properties.props, null, null);
		assertTrue(result1 != null);
		assertFalse(result1.isEmpty());
		assertTrue(result1.equals(result2));
	}

}
