package org.wikidata.wdtk.wikibaseapi;

/*
 * #%L
 * Wikidata Toolkit Wikibase API
 * %%
 * Copyright (C) 2014 - 2020 Wikidata Toolkit Developers
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



import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.*;

public class OAuthApiConnectionTest {

    private static final String consumerKey = "consumer_key";
    private static final String consumerSecret = "consumer_secret";
    private static final String accessToken = "access_token";
    private static final String accessSecret = "access_secret";

    @Test
    public void testAnonymousRequest() throws IOException, MediaWikiApiErrorException, InterruptedException {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody("{\"entities\":{\"Q8\":{\"pageid\":134,\"ns\":0,\"title\":\"Q8\",\"lastrevid\":1174289176,\"modified\":\"2020-05-05T12:39:07Z\",\"type\":\"item\",\"id\":\"Q8\",\"labels\":{\"fr\":{\"language\":\"fr\",\"value\":\"bonheur\"}},\"descriptions\":{\"fr\":{\"language\":\"fr\",\"value\":\"état émotionnel\"}},\"aliases\":{\"fr\":[{\"language\":\"fr\",\"value\":\":)\"},{\"language\":\"fr\",\"value\":\"\uD83D\uDE04\"},{\"language\":\"fr\",\"value\":\"\uD83D\uDE03\"}]},\"sitelinks\":{\"enwiki\":{\"site\":\"enwiki\",\"title\":\"Happiness\",\"badges\":[]}}}},\"success\":1}"));
        server.start();
        HttpUrl apiBaseUrl = server.url("/w/api.php");
        // We don't need to login here, so we don't care about the consumer key/secret.
        ApiConnection connection = new OAuthApiConnection(apiBaseUrl.toString(), null, null);

        // We can still fetch unprotected data without logging in.
        WikibaseDataFetcher wbdf = new WikibaseDataFetcher(connection, Datamodel.SITE_WIKIDATA);
        wbdf.getFilter().setSiteLinkFilter(Collections.singleton("enwiki"));
        wbdf.getFilter().setLanguageFilter(Collections.singleton("fr"));
        wbdf.getFilter().setPropertyFilter(
                Collections.<PropertyIdValue>emptySet());
        EntityDocument q8 = wbdf.getEntityDocument("Q8");
        String result = "";
        if (q8 instanceof ItemDocument) {
            result = "The French label for entity Q8 is "
                    + ((ItemDocument) q8).getLabels().get("fr").getText()
                    + "\nand its English Wikipedia page has the title "
                    + ((ItemDocument) q8).getSiteLinks().get("enwiki")
                    .getPageTitle() + ".";
        }
        assertEquals("The French label for entity Q8 is bonheur\n" +
                "and its English Wikipedia page has the title Happiness.", result);

        RecordedRequest request = server.takeRequest();
        assertNull(request.getHeader("Authorization"));

        server.shutdown();
    }

    @Test
    public void testLoginAndLogout() throws IOException, LoginFailedException, InterruptedException {
        MockWebServer server = new MockWebServer();
        // user info
        server.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody("{\"batchcomplete\":\"\",\"query\":{\"userinfo\":{\"id\":2333,\"name\":\"foo\"}}}"));
        server.start();
        HttpUrl apiBaseUrl = server.url("w/api.php");

        OAuthApiConnection connection = new OAuthApiConnection(apiBaseUrl.toString(), consumerKey, consumerSecret);
        assertEquals("", connection.getCurrentUser());
        assertFalse(connection.isLoggedIn());
        connection.login(accessToken, accessSecret);
        assertEquals("foo", connection.getCurrentUser());
        assertTrue(connection.isLoggedIn());
        connection.logout();
        assertEquals("", connection.getCurrentUser());
        assertFalse(connection.isLoggedIn());

        RecordedRequest request = server.takeRequest();
        assertNotNull(request.getHeader("Authorization"));
        assertEquals("GET /w/api.php?meta=userinfo&format=json&action=query HTTP/1.1", request.toString());

        server.shutdown();
    }

}
