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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.json.jackson.JacksonTermedStatementDocument;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Java implementation for wbeditentities actions.
 *
 * @author Michael Guenther
 *
 */
public class EditEntityAction {

	/*
	 * TODO:
	 *
	 * Should getEditToken throw IOExceptions or catch them?
	 *
	 * replace NeedTokenException through separate exception to distinguish
	 * login from edit tokens?
	 */

	static final Logger logger = LoggerFactory
			.getLogger(EditEntityAction.class);

	/**
	 * Connection to an Wikibase API.
	 */
	final ApiConnection connection;

	/**
	 * The IRI that identifies the site that the data is from.
	 */
	final String siteIri; // TODO look if this is unnecessary - probably not

	/**
	 * Mapper object used for deserializing JSON data.
	 */
	final ObjectMapper mapper = new ObjectMapper();

	String editToken = "";

	/**
	 * Creates an object to modify data on a Wikibase site. The API is used to
	 * request the changes. The site URI is necessary since it is not contained
	 * in the data retrieved from the API.
	 *
	 * @param connection
	 *            {@link ApiConnection} Object to send the requests
	 * @param siteUri
	 *            the URI identifying the site that is accessed (usually the
	 *            prefix of entity URIs), e.g.,
	 *            "http://www.wikidata.org/entity/"
	 */
	public EditEntityAction(ApiConnection connection, String siteUri) {
		this.connection = connection;
		this.siteIri = siteUri;
	}

	public EntityDocument wbeditentity(String id, String site, String title,
			String data, String wbNew, boolean bot, boolean clear)
			throws NeedTokenException, NoLoginException, IOException {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("action", "wbeditentity");
		if (id != null) {
			parameters.put("id", id);
		}
		if (site != null) {
			parameters.put("site", site);
		}
		if (title != null) {
			parameters.put("title", title);
		}

		if (data != null) {
			parameters.put("data", data);
		}
		if (wbNew != null) {
			parameters.put("new", wbNew);
		}
		if (bot == true) {
			parameters.put("bot", "");
		}
		if (clear == true) {
			parameters.put("clear", "");
		}
		if ((this.editToken == null) || (this.editToken.equals(""))) {
			requestEditToken();
			if ((this.editToken == null) || (this.editToken.equals(""))) {
				throw new NeedTokenException(
						"wbeditentities function needs a token parameter.");
			} else {
				parameters.put("token", this.editToken);
			}
		} else {
			parameters.put("token", this.editToken);
		}
		parameters.put("format", "json");
		InputStream response;
		try {
			response = this.connection.sendRequest("POST", parameters);
			JsonNode root;
			root = mapper.readTree(response);
			if (root.path("error").path("code").asText().equals("notoken")) {
				throw new NeedTokenException("Edit token is not valid!");
			}
			if (connection.parseErrorsAndWarnings(root)) {
				if (root.has("item")) {
					return parseJsonResponse(root.path("item"));
				}
				if (root.has("property")) { // not testable because of missing
											// permissions - probably better
											// obmit the case.
					return parseJsonResponse(root.path("property"));
				}
				if (root.has("entity")) {
					return parseJsonResponse(root.path("entity"));
				}
				return null; // TODO throw exception?
			} else {
				// TODO errors in the response -> error handling - either throw
				// IOException or simply return null
				return null;
			}
		} catch (IOException e) {
			logger.error("Could not retrieve data: " + e.toString());
			return null;
		}
	}

	/**
	 * Sets an new edit token retrieved from the API. Make sure that an user is
	 * logged in.
	 *
	 * @throws NoLoginException
	 *             To get an edit token it is necessary to be logged in. If this
	 *             is not the case a {@link NoLoginException} is thrown.
	 * @throws IOException
	 *             if it is not possible to send a request or retrieve an result
	 *             from the API or the result contains errors or the edit token
	 *             is simply missing.
	 */
	public void requestEditToken() throws NoLoginException, IOException {
		Map<String, String> params = new HashMap<String, String>();

		if (connection.loggedIn == false) {
			throw new NoLoginException(
					"To retrieve an edit token it is necessary to be logged in.");
		}

		params.put("action", "query");
		params.put("meta", "tokens");
		params.put("format", "json");
		InputStream response = this.connection.sendRequest("POST", params);
		JsonNode root;

		root = mapper.readTree(response);
		if (connection.parseErrorsAndWarnings(root)) {
			JsonNode tokenNode = root.path("query").path("tokens")
					.path("csrftoken");
			if (tokenNode.isMissingNode() == false) {
				String token = tokenNode.textValue();
				if (token.equals("")) {
					throw new IOException("Token is empty.");
				} else {
					this.editToken = token;
				}
			} else {
				throw new IOException("No Token in the response");
			}
		} else {
			throw new IOException("Errors in the API response.");
		}

	}

	private EntityDocument parseJsonResponse(JsonNode entityNode) {
		try {
			JacksonTermedStatementDocument ed = mapper.treeToValue(entityNode,
					JacksonTermedStatementDocument.class);
			ed.setSiteIri(this.siteIri);

			return ed;
		} catch (JsonProcessingException e) {
			logger.error("Error when reading JSON for entity "
					+ entityNode.path("id").asText("UNKNOWN") + ": "
					+ e.toString());
		}
		return null;
	}

}
