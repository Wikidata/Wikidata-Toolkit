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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.util.WebResourceFetcherImpl;
import org.wikidata.wdtk.wikibaseapi.apierrors.MaxlagErrorException;
import org.wikidata.wdtk.wikibaseapi.apierrors.AssertUserFailedException;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Class to build up and hold a connection to a Wikibase API.
 *
 * @author Michael Guenther
 * @author Antonin Delpeuch
 * @author Lu Liu
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class ApiConnection {

	static final Logger logger = LoggerFactory.getLogger(ApiConnection.class);

	/**
	 * URL of the API of wikidata.org.
	 */
	public final static String URL_WIKIDATA_API = "https://www.wikidata.org/w/api.php";
	/**
	 * URL of the API of test.wikidata.org.
	 */
	public final static String URL_TEST_WIKIDATA_API = "https://test.wikidata.org/w/api.php";

	/**
	 * URL of the API of commons.wikimedia.org.
	 */
	public final static String URL_WIKIMEDIA_COMMONS_API = "https://commons.wikimedia.org/w/api.php";

	/**
	 * Name of the HTTP parameter to submit an action to the API.
	 */
	protected final static String PARAM_ACTION = "action";

	/**
	 * Name of the HTTP parameter to submit the requested result format to the
	 * API.
	 */
	protected final static String PARAM_FORMAT = "format";

	/**
	 * MediaWiki assert parameter to ensure we are editing while logged in.
	 */
	protected static final String ASSERT_PARAMETER = "assert";

	protected static final MediaType URLENCODED_MEDIA_TYPE = MediaType.parse("application/x-www-form-urlencoded");

	/**
	 * URL to access the Wikibase API.
	 */
	protected final String apiBaseUrl;

	/**
	 * True after successful login.
	 */
	protected boolean loggedIn = false;

	/**
	 * User name used to log in.
	 */
	protected String username = "";

	/**
	 * Map of requested tokens.
	 */
	protected final Map<String, String> tokens;

	/**
	 * Maximum time to wait for when establishing a connection, in milliseconds.
	 * For negative values, no timeout is set.
	 */
	protected int connectTimeout = -1;

	/**
	 * Maximum time to wait for a server response once the connection was established.
	 * For negative values, no timeout is set.
	 */
	protected int readTimeout = -1;

	/**
	 * Http client used for making requests.
	 */
	private OkHttpClient client;

	/**
	 * Mapper object used for deserializing JSON data.
	 */
	private final ObjectMapper mapper = new ObjectMapper();

	/**
	 * Creates an object to manage a connection to the Web API of a Wikibase
	 * site.
	 *
	 * @param apiBaseUrl
	 *            base URI to the API, e.g.,
	 *            "https://www.wikidata.org/w/api.php/"
	 */
	public ApiConnection(String apiBaseUrl) {
		this(apiBaseUrl, null);
	}

	/**
	 * Creates an object to manage a connection to the Web API of a Wikibase
	 * site.
	 *
	 * @param apiBaseUrl
	 *            base URI to the API, e.g.,
	 *            "https://www.wikidata.org/w/api.php/"
	 * @param tokens
	 * 	      CSRF tokens already acquired by the connection
	 */
	public ApiConnection(String apiBaseUrl, Map<String, String> tokens) {
		this.apiBaseUrl = apiBaseUrl;
		this.tokens = tokens != null ? tokens : new HashMap<>();
	}

	/**
	 * Subclasses can customized their own {@link OkHttpClient.Builder} instances.
	 *
	 * An example:
	 * <pre>
	 * 	    return new OkHttpClient.Builder()
	 * 		        .connectTimeout(5, TimeUnit.MILLISECONDS)
	 * 		        .readTimeout(5, TimeUnit.MILLISECONDS)
	 * 		        .cookieJar(...);
	 * </pre>
	 */
	protected abstract OkHttpClient.Builder getClientBuilder();

	/**
	 * Getter for the apiBaseUrl.
	 */
	@JsonProperty("baseUrl")
	public String getApiBaseUrl() {
		return apiBaseUrl;
	}

	/**
	 * Returns true if a user is logged in. This does not perform
	 * any request to the server: it just returns our own internal state.
	 * To check if our authentication credentials are still considered
	 * valid by the remote server, use {@link ApiConnection#checkCredentials()}.
	 *
	 * @return true if the connection is in a logged in state
	 */
	@JsonProperty("loggedIn")
	public boolean isLoggedIn() {
		return loggedIn;
	}

	/**
	 * Checks that the credentials are still valid for the
	 * user currently logged in. This can fail if (for instance)
	 * the cookies expired, or were invalidated by a logout from
	 * a different client.
	 *
	 * This method queries the API and throws {@link AssertUserFailedException}
	 * if the check failed. This does not update the state of the connection
	 * object.
	 * @throws MediaWikiApiErrorException
	 * @throws IOException
	 */
	public void checkCredentials() throws IOException, MediaWikiApiErrorException {
		Map<String,String> parameters = new HashMap<>();
		parameters.put("action", "query");
		sendJsonRequest("POST", parameters);
	}

	/**
	 * Returns the username of the user who is currently logged in. If there is
	 * no user logged in the result is an empty string.
	 *
	 * @return name of the logged in user
	 */
	@JsonProperty("username")
	public String getCurrentUser() {
		return username;
	}

	/**
	 * Returns the map of tokens (such as csrf token and login token) currently used in this connection.
	 */
	@JsonProperty("tokens")
	public Map<String, String> getTokens() {
		return Collections.unmodifiableMap(tokens);
	}

	/**
	 * Sets the maximum time to wait for when establishing a connection, in milliseconds.
	 * For negative values, no timeout is set.
	 *
	 * @see HttpURLConnection#setConnectTimeout
	 */
	public void setConnectTimeout(int timeout) {
		connectTimeout = timeout;
		client = null;
	}

	/**
	 * Sets the maximum time to wait for a server response once the connection was established, in milliseconds.
	 * For negative values, no timeout is set.
	 *
	 * @see HttpURLConnection#setReadTimeout
	 */
	public void setReadTimeout(int timeout) {
		readTimeout = timeout;
		client = null;
	}

	/**
	 * Maximum time to wait for when establishing a connection, in milliseconds.
	 * For negative values, no timeout is set, which is the default behaviour (for
	 * backwards compatibility).
	 *
	 * @see HttpURLConnection#getConnectTimeout
	 */
	@JsonProperty("connectTimeout")
	public int getConnectTimeout() {
		return connectTimeout;
	}

	/**
	 * Maximum time to wait for a server response once the connection was established.
	 * For negative values, no timeout is set, which is the default behaviour (for backwards
	 * compatibility).
	 *
	 * @see HttpURLConnection#getReadTimeout
	 */
	@JsonProperty("readTimeout")
	public int getReadTimeout() {
		return readTimeout;
	}

	/**
	 * Logs the current user out.
	 *
	 * @throws IOException
	 * @throws MediaWikiApiErrorException
	 */
	public abstract void logout() throws IOException, MediaWikiApiErrorException;

	/**
	 * Return a token of given type.
	 * @param tokenType The kind of token to retrieve like "csrf" or "login"
	 * @return a token
	 * @throws MediaWikiApiErrorException
	 *     if MediaWiki returned an error
	 * @throws IOException
	 *     if a network error occurred
	 */
	String getOrFetchToken(String tokenType) throws IOException, MediaWikiApiErrorException {
		if (tokens.containsKey(tokenType)) {
			return tokens.get(tokenType);
		}
		String value = fetchToken(tokenType);
		tokens.put(tokenType, value);
		// TODO if fetchToken raises an exception, we could try to recover here:
		// (1) Check if we are still logged in; maybe log in again
		// (2) If there is another error, maybe just run the operation again
		return value;
	}

	/**
	 * Remove fetched value of given token.
	 */
	void clearToken(String tokenType) {
		tokens.remove(tokenType);
	}

	/**
	 * Executes a API query action to get a new token.
	 * The method only executes the action, without doing any
	 * checks first. If errors occur, they are logged and null is returned.
	 *
	 * @param tokenType The kind of token to retrieve like "csrf" or "login"
	 * @return newly retrieved token
	 * @throws IOException
	 *     if a network error occurred
	 * @throws MediaWikiApiErrorException
	 *     if MediaWiki returned an error when fetching the token
	 */
	private String fetchToken(String tokenType) throws IOException, MediaWikiApiErrorException {
		Map<String, String> params = new HashMap<>();
		params.put(ApiConnection.PARAM_ACTION, "query");
		params.put("meta", "tokens");
		params.put("type", tokenType);

		JsonNode root = this.sendJsonRequest("POST", params);
		return root.path("query").path("tokens").path(tokenType + "token").textValue();
	}

	/**
	 * Sends a request to the API with the given parameters and the given
	 * request method and returns the result JSON tree. It automatically fills the
	 * cookie map with cookies in the result header after the request.
	 * It logs the request warnings and adds makes sure that "format": "json"
	 * parameter is set.
	 *
	 * @param requestMethod
	 *            either POST or GET
	 * @param parameters
	 *            Maps parameter keys to values. Out of this map the function
	 *            will create a query string for the request.
	 * @return API result
	 * @throws IOException
	 * @throws MediaWikiApiErrorException if the API returns an error
	 */
	public JsonNode sendJsonRequest(String requestMethod, Map<String,String> parameters) throws IOException, MediaWikiApiErrorException {
		parameters.put(ApiConnection.PARAM_FORMAT, "json");
		if (loggedIn) {
			parameters.put(ApiConnection.ASSERT_PARAMETER, "user");
		}
		try (InputStream response = sendRequest(requestMethod, parameters)) {
			JsonNode root = this.mapper.readTree(response);
			this.checkErrors(root);
			this.logWarnings(root);
			return root;
		}
	}

	/**
	 * Sends a request to the API with the given parameters and the given
	 * request method and returns the result string. It automatically fills the
	 * cookie map with cookies in the result header after the request.
	 *
	 * Warning: You probably want to use ApiConnection.sendJsonRequest
	 * that execute the request using JSON content format,
	 * throws the errors and logs the warnings.
	 *
	 * @param requestMethod
	 *            either POST or GET
	 * @param parameters
	 *            Maps parameter keys to values. Out of this map the function
	 *            will create a query string for the request.
	 * @return API result
	 * @throws IOException
	 */
	public InputStream sendRequest(String requestMethod,
			Map<String, String> parameters) throws IOException {
		Request request;
		String queryString = getQueryString(parameters);
		if ("GET".equalsIgnoreCase(requestMethod)) {
			request = new Request.Builder().url(apiBaseUrl + "?" + queryString).build();
		} else if ("POST".equalsIgnoreCase(requestMethod)) {
			request = new Request.Builder().url(apiBaseUrl).post(RequestBody.create(URLENCODED_MEDIA_TYPE, queryString)).build();
		} else {
			throw new IllegalArgumentException("Expected the requestMethod to be either GET or POST, but got " + requestMethod);
		}

		if (client == null) {
			buildClient();
		}
		Response response = client.newCall(request).execute();
		return Objects.requireNonNull(response.body()).byteStream();
	}

	private void buildClient() {
		OkHttpClient.Builder builder = getClientBuilder();
		if (connectTimeout >= 0) {
			builder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
		}
		if (readTimeout >= 0) {
			builder.readTimeout(readTimeout, TimeUnit.MILLISECONDS);
		}
		client = builder.build();
	}

	/**
	 * Checks if an API response contains an error and throws a suitable
	 * exception in this case.
	 *
	 * @param root
	 *            root node of the JSON result
	 * @throws MediaWikiApiErrorException
	 */
	protected void checkErrors(JsonNode root) throws MediaWikiApiErrorException {
		if (root.has("error")) {
			JsonNode errorNode = root.path("error");
			String code = errorNode.path("code").asText("UNKNOWN");
			String info = errorNode.path("info").asText("No details provided");
			// Special case for the maxlag error since we also want to return
			// the lag value in the exception thrown
			if (errorNode.has("lag") && MediaWikiApiErrorHandler.ERROR_MAXLAG.equals(code)) {
				double lag = errorNode.path("lag").asDouble();
				throw new MaxlagErrorException(info, lag);
			} else {
				MediaWikiApiErrorHandler.throwMediaWikiApiErrorException(code, info);
			}
		}
	}

	/**
	 * Extracts and logs any warnings that are returned in an API response.
	 *
	 * @param root
	 *            root node of the JSON result
	 */
	protected void logWarnings(JsonNode root) {
		for (String warning : getWarnings(root)) {
			logger.warn("API warning " + warning);
		}
	}

	/**
	 * Extracts warnings that are returned in an API response.
	 *
	 * @param root
	 *            root node of the JSON result
	 */
	List<String> getWarnings(JsonNode root) {
		ArrayList<String> warnings = new ArrayList<>();

		if (root.has("warnings")) {
			JsonNode warningNode = root.path("warnings");
			Iterator<Map.Entry<String, JsonNode>> moduleIterator = warningNode
					.fields();
			while (moduleIterator.hasNext()) {
				Map.Entry<String, JsonNode> moduleNode = moduleIterator.next();
				Iterator<JsonNode> moduleOutputIterator = moduleNode.getValue()
						.elements();
				while (moduleOutputIterator.hasNext()) {
					JsonNode moduleOutputNode = moduleOutputIterator.next();
					if (moduleOutputNode.isTextual()) {
						warnings.add("[" + moduleNode.getKey() + "]: "
								+ moduleOutputNode.textValue());
					} else if (moduleOutputNode.isArray()) {
						Iterator<JsonNode> messageIterator = moduleOutputNode
								.elements();
						while (messageIterator.hasNext()) {
							JsonNode messageNode = messageIterator.next();
							warnings.add("["
									+ moduleNode.getKey()
									+ "]: "
									+ messageNode.path("html").path("*")
											.asText(messageNode.toString()));
						}
					} else {
						warnings.add("["
								+ moduleNode.getKey()
								+ "]: "
								+ "Warning was not understood. Please report this to Wikidata Toolkit. JSON source: "
								+ moduleOutputNode.toString());
					}
				}

			}
		}

		return warnings;
	}

	/**
	 * Returns the query string of a URL from a parameter list.
	 *
	 * @param params
	 *            Map with parameters
	 * @return query string
	 */
	String getQueryString(Map<String, String> params) {
		StringBuilder builder = new StringBuilder();
		try {
			boolean first = true;
			for (Map.Entry<String,String> entry : params.entrySet()) {
				if (first) {
					first = false;
				} else {
					builder.append("&");
				}
				builder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
				builder.append("=");
				builder.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(
					"Your Java version does not support UTF-8 encoding.");
		}

		return builder.toString();
	}

	/**
	 * Builds a string that serializes a list of objects separated by the pipe
	 * character. The toString methods are used to turn objects into strings.
	 * This operation is commonly used to build parameter lists for API
	 * requests.
	 *
	 * @param objects
	 *            the objects to implode
	 * @return string of imploded objects
	 */
	public static String implodeObjects(Iterable<?> objects) {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (Object o : objects) {
			if (first) {
				first = false;
			} else {
				builder.append("|");
			}
			builder.append(o.toString());
		}
		return builder.toString();
	}

}
