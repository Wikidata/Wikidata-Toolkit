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


import com.fasterxml.jackson.databind.ObjectMapper;
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

    private static final String CONSUMER_KEY = "consumer_key";
    private static final String CONSUMER_SECRET = "consumer_secret";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String ACCESS_SECRET = "access_secret";

    private final ObjectMapper mapper = new ObjectMapper();

    private static final String NOT_LOGGED_IN_SERIALIZED = "{\"baseUrl\":\"http://kubernetes.docker.internal:55690/w/api.php\",\"consumerKey\":null,\"consumerSecret\":null,\"accessToken\":null,\"accessSecret\":null,\"connectTimeout\":-1,\"readTimeout\":-1,\"loggedIn\":false,\"username\":\"\"}";
    private static final String LOGGED_IN_SERIALIZED = "{\"baseUrl\":\"http://kubernetes.docker.internal:55178/w/api.php\",\"consumerKey\":\"consumer_key\",\"consumerSecret\":\"consumer_secret\",\"accessToken\":\"access_token\",\"accessSecret\":\"access_secret\",\"connectTimeout\":-1,\"readTimeout\":-1,\"loggedIn\":true,\"username\":\"foo\"}";

    @Test
    public void testAnonymousRequest() throws IOException, MediaWikiApiErrorException, InterruptedException {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody("{\"entities\":{\"Q8\":{\"pageid\":134,\"ns\":0,\"title\":\"Q8\",\"lastrevid\":1174289176,\"modified\":\"2020-05-05T12:39:07Z\",\"type\":\"item\",\"id\":\"Q8\",\"labels\":{\"fr\":{\"language\":\"fr\",\"value\":\"bonheur\"}},\"descriptions\":{\"fr\":{\"language\":\"fr\",\"value\":\"état émotionnel\"}},\"aliases\":{\"fr\":[{\"language\":\"fr\",\"value\":\":)\"},{\"language\":\"fr\",\"value\":\"\uD83D\uDE04\"},{\"language\":\"fr\",\"value\":\"\uD83D\uDE03\"}]},\"sitelinks\":{\"enwiki\":{\"site\":\"enwiki\",\"title\":\"Happiness\",\"badges\":[]}}}},\"success\":1}"));
        server.start();
        HttpUrl apiBaseUrl = server.url("/w/api.php");
        // We don't need to login here.
        ApiConnection connection = new OAuthApiConnection(apiBaseUrl.toString());
        assertFalse(connection.isLoggedIn());
        assertEquals("", connection.getCurrentUser());

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
    public void testLogout() throws IOException, LoginFailedException, InterruptedException {
        MockWebServer server = new MockWebServer();
        // user info
        server.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody("{\"batchcomplete\":\"\",\"query\":{\"userinfo\":{\"id\":2333,\"name\":\"foo\"}}}"));
        server.start();
        HttpUrl apiBaseUrl = server.url("w/api.php");

        OAuthApiConnection connection = new OAuthApiConnection(apiBaseUrl.toString(),
                CONSUMER_KEY, CONSUMER_SECRET, ACCESS_TOKEN, ACCESS_SECRET);
        assertTrue(connection.isLoggedIn());
        assertEquals("foo", connection.getCurrentUser());

        connection.logout();
        assertEquals("", connection.getCurrentUser());
        assertFalse(connection.isLoggedIn());
        assertEquals("", connection.getCurrentUser());

        RecordedRequest request = server.takeRequest();
        assertNotNull(request.getHeader("Authorization"));

        assertEquals("/w/api.php?meta=userinfo&assert=user&format=json&action=query", request.getPath());

        server.shutdown();
    }

    @Test
    public void testSerialize() throws IOException, LoginFailedException {
        MockWebServer server = new MockWebServer();
        // user info
        server.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody("{\"batchcomplete\":\"\",\"query\":{\"userinfo\":{\"id\":2333,\"name\":\"foo\"}}}"));
        server.start();
        HttpUrl apiBaseUrl = server.url("w/api.php");

        OAuthApiConnection connection = new OAuthApiConnection(apiBaseUrl.toString(),
                CONSUMER_KEY, CONSUMER_SECRET, ACCESS_TOKEN, ACCESS_SECRET);
        String jsonSerialization = mapper.writeValueAsString(connection);
        // The baseUrl field may be different because the server port is random in every run.
        // But other fields are required to be the same.
        assertEquals(LOGGED_IN_SERIALIZED.substring(LOGGED_IN_SERIALIZED.indexOf("/w/api.php")),
                jsonSerialization.substring(jsonSerialization.indexOf("/w/api.php")));
        server.shutdown();
    }

    @Test
    public void testDeserialize() throws IOException {
        OAuthApiConnection connection = mapper.readValue(LOGGED_IN_SERIALIZED, OAuthApiConnection.class);
        assertTrue(connection.isLoggedIn());
        assertEquals(CONSUMER_KEY, connection.getConsumerKey());
        assertEquals(CONSUMER_SECRET, connection.getConsumerSecret());
        assertEquals(ACCESS_TOKEN, connection.getAccessToken());
        assertEquals(ACCESS_SECRET, connection.getAccessSecret());
        assertEquals("http://kubernetes.docker.internal:55178/w/api.php", connection.getApiBaseUrl());

        // To get the username, we have to start a server at http://kubernetes.docker.internal:55178/w/api.php
        // and return the corresponding json response.
        // But we cannot control the port of the mocked server, so we just change the url here.
        MockWebServer server = new MockWebServer();
        // user info
        server.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody("{\"batchcomplete\":\"\",\"query\":{\"userinfo\":{\"id\":2333,\"name\":\"foo\"}}}"));
        server.start();
        HttpUrl apiBaseUrl = server.url("w/api.php");
        OAuthApiConnection connection1 = new OAuthApiConnection(apiBaseUrl.toString(),
                connection.getConsumerKey(), connection.getConsumerSecret(),
                connection.getAccessToken(), connection.getAccessSecret());

        assertEquals("foo", connection1.getCurrentUser());
        server.shutdown();
    }

    @Test
    public void testDeserializeNotLogin() throws IOException {
        OAuthApiConnection connection = mapper.readValue(NOT_LOGGED_IN_SERIALIZED, OAuthApiConnection.class);
        assertFalse(connection.isLoggedIn());
        assertNull(CONSUMER_KEY, connection.getConsumerKey());
        assertNull(CONSUMER_SECRET, connection.getConsumerSecret());
        assertNull(ACCESS_TOKEN, connection.getAccessToken());
        assertNull(ACCESS_SECRET, connection.getAccessSecret());
        assertEquals("http://kubernetes.docker.internal:55690/w/api.php", connection.getApiBaseUrl());
    }


    @Test
    public void testConnectionTimeout() {
        ApiConnection connection = new OAuthApiConnection(ApiConnection.URL_WIKIDATA_API,
                CONSUMER_KEY, CONSUMER_SECRET,
                ACCESS_TOKEN, ACCESS_SECRET);
        connection.setConnectTimeout(2000);
        assertEquals(2000, connection.getConnectTimeout());
        assertEquals(-1, connection.getReadTimeout());
    }

    @Test
    public void testReadTimeout() {
        ApiConnection connection = new OAuthApiConnection(ApiConnection.URL_WIKIDATA_API);
        connection.setReadTimeout(2000);
        assertEquals(-1, connection.getConnectTimeout());
        assertEquals(2000, connection.getReadTimeout());
    }

}
