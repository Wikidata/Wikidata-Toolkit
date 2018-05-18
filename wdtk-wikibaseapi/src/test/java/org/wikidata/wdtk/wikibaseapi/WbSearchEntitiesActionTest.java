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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.util.CompressionType;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

public class WbSearchEntitiesActionTest {

    MockBasicApiConnection con;
    WbSearchEntitiesAction action;

    @Before
    public void setUp() throws Exception {

        this.con = new MockBasicApiConnection();
        Map<String, String> params = new HashMap<String, String>();
        params.put(ApiConnection.PARAM_ACTION, "wbsearchentities");
        params.put(ApiConnection.PARAM_FORMAT, "json");
        params.put("search", "abc");
        params.put("language", "en");
        this.con.setWebResourceFromPath(params, getClass(),
                "/wbsearchentities-abc.json", CompressionType.NONE);

        params.put("search", "some search string with no results");
        this.con.setWebResourceFromPath(params, getClass(),
                "/wbsearchentities-empty.json", CompressionType.NONE);

        this.action = new WbSearchEntitiesAction(this.con, Datamodel.SITE_WIKIDATA);

    }

    @Test
    public void testWbSearchEntities() throws MediaWikiApiErrorException {
        List<WbSearchEntitiesResult> results = action.wbSearchEntities("abc",
                "en", null, null, null, null);

        assertEquals(7, results.size());

        WbSearchEntitiesResult firstResult = results.get(0);
        assertEquals("Q169889", firstResult.getEntityId());
        assertEquals(firstResult.getConceptUri(),
                "http://www.wikidata.org/entity/Q169889");
        assertEquals(firstResult.getUrl(), "//www.wikidata.org/wiki/Q169889");
        assertEquals("Q169889", firstResult.getTitle());
        assertEquals(170288, firstResult.getPageId());
        assertEquals("American Broadcasting Company", firstResult.getLabel());
        assertEquals("American broadcast television network",
                firstResult.getDescription());
        WbSearchEntitiesResult.Match match = new JacksonWbSearchEntitiesResult.JacksonMatch(
                "alias", "en", "ABC");
        assertEquals(match, firstResult.getMatch());
        List<String> aliases = new ArrayList<>();
        aliases.add("ABC");
        assertEquals(aliases, firstResult.getAliases());
    }

    @Test
    public void testWbSearchEntitiesEmpty() throws MediaWikiApiErrorException {
        List<WbSearchEntitiesResult> results = action.wbSearchEntities(
                "some search string with no results", "en", null, null, null,
                null);

        assertTrue(results.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIdsAndTitles() throws MediaWikiApiErrorException {
        action.wbSearchEntities(null, "en", null, null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIdsAndSites() throws MediaWikiApiErrorException {
        action.wbSearchEntities("abc", null, null, null, null, null);
    }
}
