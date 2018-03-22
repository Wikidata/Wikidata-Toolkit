package org.wikidata.wdtk.datamodel.implementation.json;

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

import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.implementation.TermImpl;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.implementation.SiteLinkImpl;
import org.wikidata.wdtk.datamodel.interfaces.*;

import java.util.*;

/**
 * Class that provides objects and strings for testing the conversion between
 * JSON and Java.
 *
 * @author Fredo Erxleben
 *
 */
public class JsonTestData {

	public static final DataObjectFactory JACKSON_OBJECT_FACTORY = new DataObjectFactoryImpl();

	// the id's used in the tests
	public static final String TEST_PROPERTY_ID = "P1";
	public static final String TEST_ITEM_ID = "Q1";
	public static final String TEST_STATEMENT_ID = "statement_foobar";

	public static final String JSON_RANK_NORMAL = "normal";

	// stand-alone descriptions of ItemDocument-parts
	public static final String JSON_ITEM_TYPE = "\"type\":\"item\"";
	public static final String JSON_NOVALUE_SNAK = "{\"snaktype\":\"novalue\",\"property\":\""
			+ TEST_PROPERTY_ID + "\"}";

	// wrapping into item document structure for dedicated tests
	public static final String JSON_WRAPPED_ITEMID = "{\"id\":\""
			+ TEST_ITEM_ID
			+ "\",\"aliases\":{},\"labels\":{},\"descriptions\":{},\"claims\":{},\"sitelinks\":{},"
			+ JSON_ITEM_TYPE + "}";

	public static final String JSON_NOVALUE_STATEMENT = "{\"type\":\"statement\",\"id\":\""
			+ TEST_STATEMENT_ID
			+ "\",\"rank\":\""
			+ JSON_RANK_NORMAL
			+ "\",\"mainsnak\":" + JSON_NOVALUE_SNAK + "}";
}
