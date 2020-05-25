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


import com.fasterxml.jackson.databind.JsonNode;
import okhttp3.*;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;
import se.akerfeldt.okhttp.signpost.SigningInterceptor;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * A connection to the MediaWiki/Wikibase API which uses OAuth
 * for authentication.
 *
 * @author Antonin Delpeuch
 * @author Lu Liu
 */
public class OAuthApiConnection extends ApiConnection {

    protected OkHttpOAuthConsumer consumer;

    /**
     * This client is used if the user hadn't logged in when sending a request.
     */
    protected OkHttpClient client;

    /**
     * This client is used if the user had already logged in when sending a request.
     */
    protected OkHttpClient oauthClient;

    public static final MediaType MEDIA_TYPE = MediaType.parse("application/x-www-form-urlencoded");

    /**
     * Constructs a connection to the given MediaWiki
     * API endpoint.
     *
     * @param apiBaseUrl     the MediaWiki API endpoint, such as "https://www.wikidata.org/w/api.php"
     * @param consumerKey    the OAuth 1.0a consumer key
     * @param consumerSecret the OAuth 1.0a consumer secret
     */
    public OAuthApiConnection(String apiBaseUrl, String consumerKey, String consumerSecret) {
        super(apiBaseUrl);
        consumer = new OkHttpOAuthConsumer(consumerKey, consumerSecret);
        client = new OkHttpClient.Builder().build();
    }

    /**
     * Once an access token has been obtained via the OAuth login process,
     * this registers the connection as logged in with this token.
     *
     * @param accessToken  the access token obtained after the OAuth process
     * @param accessSecret the secret key obtained after the OAuth process
     * @throws LoginFailedException if the access token/secret are not valid
     */
    public void login(String accessToken, String accessSecret) throws LoginFailedException {
        consumer.setTokenWithSecret(accessToken, accessSecret);
        oauthClient = new OkHttpClient.Builder()
                .addInterceptor(new SigningInterceptor(consumer))
                .build();

        // Try to get the user's name, if successful, we know that
        // the consumer key/secret and access token/secret are valid.
        Map<String, String> params = new HashMap<>();
        params.put(PARAM_ACTION, "query");
        params.put("meta", "userinfo");
        try {
            JsonNode root = sendJsonRequest("GET", params);
            username = root.path("query").path("userinfo").path("name").textValue();
            loggedIn = true;
        } catch (IOException | MediaWikiApiErrorException e) {
            throw new LoginFailedException(e.getMessage(), e);
        }
    }

    @Override
    public InputStream sendRequest(String requestMethod, Map<String, String> parameters) throws IOException {
        Request request;
        String queryString = getQueryString(parameters);
        if ("GET".equalsIgnoreCase(requestMethod)) {
            request = new Request.Builder().url(apiBaseUrl + "?" + queryString).build();
        } else if ("POST".equalsIgnoreCase(requestMethod)) {
            request = new Request.Builder().url(apiBaseUrl).post(RequestBody.create(MEDIA_TYPE, queryString)).build();
        } else {
            throw new IllegalArgumentException("Expected the requestMethod to be either GET or POST, but got " + requestMethod);
        }

        Response response;
        if (oauthClient == null) {
            response = client.newCall(request).execute();
        } else {
            response = oauthClient.newCall(request).execute();
        }

        return response.body().byteStream();
    }

    @Override
    public void logout() throws IOException {
        oauthClient = null;
        username = "";
        loggedIn = false;
    }

}
