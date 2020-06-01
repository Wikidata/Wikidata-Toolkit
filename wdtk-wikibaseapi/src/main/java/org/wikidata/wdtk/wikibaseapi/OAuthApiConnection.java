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


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import okhttp3.*;
import org.wikidata.wdtk.wikibaseapi.apierrors.AssertUserFailedException;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;
import se.akerfeldt.okhttp.signpost.SigningInterceptor;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * A connection to the MediaWiki/Wikibase API which uses OAuth
 * for authentication.
 *
 * @author Antonin Delpeuch
 * @author Lu Liu
 */
public class OAuthApiConnection extends ApiConnection {

    private String consumerKey;
    private String consumerSecret;

    private String accessToken;
    private String accessSecret;

    private OkHttpClient client;

    private static final MediaType URLENCODED_MEDIA_TYPE = MediaType.parse("application/x-www-form-urlencoded");

    /**
     * Constructs an OAuth connection to the given MediaWiki API endpoint.
     * <p>
     * {@link ApiConnection#isLoggedIn()} will return true
     * until {@link ApiConnection#logout()} is called.
     * <p>
     * NOTICE: The constructor doesn't check if the OAuth credentials
     * (i.e., the consumer key/secret and the access token/secret) are valid.
     * Even if the credentials are valid when calling this constructor,
     * they can be revoked by the user at any time.
     * <p>
     * The validity of the credentials is automatically checked if you use
     * {@link ApiConnection#sendJsonRequest}.
     *
     * @param apiBaseUrl     the MediaWiki API endpoint, such as "https://www.wikidata.org/w/api.php"
     * @param consumerKey    the OAuth 1.0a consumer key
     * @param consumerSecret the OAuth 1.0a consumer secret
     * @param accessToken    the access token obtained via the OAuth process
     * @param accessSecret   the secret key obtained via the OAuth process
     */
    @JsonCreator
    public OAuthApiConnection(
            @JsonProperty("baseUrl") String apiBaseUrl,
            @JsonProperty("consumerKey") String consumerKey,
            @JsonProperty("consumerSecret") String consumerSecret,
            @JsonProperty("accessToken") String accessToken,
            @JsonProperty("accessSecret") String accessSecret) {
        super(apiBaseUrl, null);
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.accessToken = accessToken;
        this.accessSecret = accessSecret;
        OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(consumerKey, consumerSecret);
        consumer.setTokenWithSecret(accessToken, accessSecret);
        client = new OkHttpClient.Builder()
                .addInterceptor(new SigningInterceptor(consumer))
                .build();
        loggedIn = true;
    }

    /**
     * Sends a request to the API with the given parameters and the given
     * request method and returns the result string.
     * <p>
     * WARNING: You probably want to use {@link ApiConnection#sendJsonRequest}
     * that execute the request using JSON content format,
     * throws the errors and logs the warnings.
     *
     * @param requestMethod either POST or GET
     * @param parameters    Maps parameter keys to values. Out of this map the function
     *                      will create a query string for the request.
     * @return API result
     * @throws IOException
     */
    @Override
    public InputStream sendRequest(String requestMethod, Map<String, String> parameters) throws IOException {
        Request request;
        String queryString = getQueryString(parameters);
        if ("GET".equalsIgnoreCase(requestMethod)) {
            request = new Request.Builder().url(apiBaseUrl + "?" + queryString).build();
        } else if ("POST".equalsIgnoreCase(requestMethod)) {
            request = new Request.Builder().url(apiBaseUrl).post(RequestBody.create(URLENCODED_MEDIA_TYPE, queryString)).build();
        } else {
            throw new IllegalArgumentException("Expected the requestMethod to be either GET or POST, but got " + requestMethod);
        }

        Response response = client.newCall(request).execute();
        return Objects.requireNonNull(response.body()).byteStream();
    }

    /**
     * Forgets the OAuth credentials locally.
     * No requests will be made.
     */
    @Override
    public void logout() {
        consumerKey = null;
        consumerSecret = null;
        accessToken = null;
        accessSecret = null;
        username = "";
        loggedIn = false;
    }

    /**
     * Checks if the OAuth credentials (i.e., consumer key/secret and access token/secret) are still valid.
     * <p>
     * The OAuth credentials can be invalid if the user invoked it.
     * <p>
     * We simply call {@link ApiConnection#checkCredentials()} here.
     * Because for OAuth, the query "action=query&assert=user" returns success
     * if and only if the credentials are still valid. This behaviour is the
     * same when using username/password for logging in.
     * <p>
     * This method throws {@link AssertUserFailedException} if the check failed.
     * This does not update the state of the connection object.
     *
     * @throws MediaWikiApiErrorException if the check failed
     * @throws IOException
     */
    @Override
    public void checkCredentials() throws IOException, MediaWikiApiErrorException {
        super.checkCredentials();
    }

    @Override
    @JsonProperty("baseUrl")
    public String getApiBaseUrl() {
        return super.getApiBaseUrl();
    }

    @Override
    @JsonProperty("loggedIn")
    public boolean isLoggedIn() {
        return super.isLoggedIn();
    }

    @Override
    @JsonProperty("username")
    public String getCurrentUser() {
        if (!loggedIn) return "";
        if (username != null && !username.equals("")) return username;

        try {
            Map<String, String> params = new HashMap<>();
            params.put(PARAM_ACTION, "query");
            params.put("meta", "userinfo");
            JsonNode root = sendJsonRequest("GET", params);
            JsonNode nameNode = root.path("query").path("userinfo").path("name");
            if (nameNode.isMissingNode()) {
                throw new AssertUserFailedException("The path \"query/userinfo/name\" doesn't exist in the json response");
            }
            username = nameNode.textValue();
        } catch (IOException | MediaWikiApiErrorException e) {
            logger.warn("An error occurred when retrieving the username with OAuth credentials, the username is set to \"\" automatically: " + e.getMessage());
            username = "";
        }
        return username;
    }

    @JsonProperty("consumerKey")
    public String getConsumerKey() {
        return consumerKey;
    }

    @JsonProperty("consumerSecret")
    public String getConsumerSecret() {
        return consumerSecret;
    }

    @JsonProperty("accessToken")
    public String getAccessToken() {
        return accessToken;
    }

    @JsonProperty("accessSecret")
    public String getAccessSecret() {
        return accessSecret;
    }

    @Override
    public void setConnectTimeout(int timeout) {
        super.setConnectTimeout(timeout);
        updateTimeoutSetting();
    }

    @Override
    public void setReadTimeout(int timeout) {
        super.setReadTimeout(timeout);
        updateTimeoutSetting();
    }

    private void updateTimeoutSetting() {
        if (!loggedIn) throw new IllegalStateException("Cannot update connection settings after logging out");

        // avoid instantiating new objects if possible
        if (connectTimeout < 0 && readTimeout < 0) return;

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(consumerKey, consumerSecret);
        consumer.setTokenWithSecret(accessToken, accessSecret);
        builder.addInterceptor(new SigningInterceptor(consumer));

        if (connectTimeout >= 0) {
            builder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
        }

        if (readTimeout >= 0) {
            builder.readTimeout(readTimeout, TimeUnit.MILLISECONDS);
        }

        // rebuild the client
        client = builder.build();
    }
}
