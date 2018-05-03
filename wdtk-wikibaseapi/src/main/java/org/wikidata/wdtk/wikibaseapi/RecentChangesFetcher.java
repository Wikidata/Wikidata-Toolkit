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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple class to fetch recent changes
 * 
 * @author Markus Damm
 *
 */
public class RecentChangesFetcher {

	static final Logger logger = LoggerFactory
			.getLogger(WikibaseDataFetcher.class);

	/**
	 * Connection to WikiBase API
	 */
	static ApiConnection apiConnection;

	/**
	 * Creates an object to fetch recent changes of Wikidata
	 */
	public RecentChangesFetcher() {
		this(ApiConnection.URL_WIKIDATA_API);
	}

	/**
	 * Creates an object to fetch recent changes
	 * 
	 * @param apiBaseUrl
	 *            base URI to the API, e.g.,
	 *            "https://www.wikidata.org/w/api.php/"
	 */
	public RecentChangesFetcher(String apiBaseUrl) {
		RecentChangesFetcher.apiConnection = new ApiConnection(apiBaseUrl);
	}

	/**
	 * Fetches IOStream from RSS recent changes feed and returns a set of recent
	 * changes.
	 * 
	 * @return a set of recent changes from the recent changes feed
	 * @throws IOException
	 *             if an error occured while connecting to Wikibase API
	 */
	public Set<RecentChange> getRecentChanges() {
		return getRecentChanges(getParameters());
	}

	/**
	 * Fetches IOStream from RSS recent changes feed and no recent change is
	 * before the date.
	 * 
	 * @param from
	 *            earliest possible date for a recent change
	 * @return a set of recent changes from the recent changes feed
	 */
	public Set<RecentChange> getRecentChanges(Date from) {
		return getRecentChanges(getParameters(from));
	}
	
	Set<RecentChange> getRecentChanges(Map<String,String> parameters){
		Set<RecentChange> recentChanges = new TreeSet<>();
		try {
			recentChanges = parseInputStream(apiConnection.sendRequest("POST",
					parameters));
		} catch (IOException e) {
			logger.error("Could not retrieve data from "
					+ apiConnection.apiBaseUrl
					+ apiConnection.getQueryString(parameters) + ". Error:\n"
					+ e.toString());
		}
		return recentChanges;
	}

	/**
	 * Parses a given RSS feed as an InputSteam and returns the recent changes.
	 * The InputStream will be closed at the end of this method.
	 * 
	 * @param inputStream
	 *            given RSS recent changes feed
	 * @return set of recent changes
	 * @throws IOException
	 *             if an error occurred while parsing the RSS feed
	 */
	Set<RecentChange> parseInputStream(InputStream inputStream)
			throws IOException {
		Set<RecentChange> changes = new TreeSet<>();

		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = bufferedReader.readLine();
		while (line != null) {
			if (line.contains("<item>")) {
				changes.add(parseItem(bufferedReader, line));
			}
			line = bufferedReader.readLine();
		}
		bufferedReader.close();
		inputStream.close();
		return changes;
	}

	/**
	 * Builds a map of parameters for a recent changes request
	 * 
	 * @return map of parameters for a recent changes request
	 */
	Map<String, String> getParameters() {
		Map<String, String> params = new HashMap<>();
		params.put("action", "feedrecentchanges");
		params.put("format", "json");
		params.put("feedformat", "rss");

		return params;
	}

	/**
	 * Builds a map of parameters for a recent changes request and adds the
	 * parameter "from" which defines the earliest possible date of a recent
	 * change
	 * 
	 * @param from
	 *            earliest date for a recent change that are requested
	 * @return map of parameters for a recent changes request
	 */
	Map<String, String> getParameters(Date from) {
		Map<String, String> params = getParameters();
		params.put("from", new SimpleDateFormat("yyyyMMddHHmmss").format(from));

		return params;
	}

	/**
	 * parses the name of the property from the item string of the recent
	 * changes feed
	 * 
	 * @param itemString
	 *            substring for an item of the recent changes feed
	 * @return name of the property
	 */
	String parsePropertyNameFromItemString(String itemString) {
		String endString = "</title>";
		int start = 10;
		int end = itemString.indexOf(endString);
		String propertyName = itemString.substring(start, end);
		return propertyName;
	}

	/**
	 * Parses the date and time of the change of a property from the item string
	 * of the recent changes feed
	 * 
	 * @param itemString
	 *            substring for an item of the recent changes feed
	 * @return date and time for the recent change
	 */
	Date parseTimeFromItemString(String itemString) {
		int start = 17;
		int end = 41;
		String timeString = itemString.substring(start, end);
		Date date = null;
		try {
			date = new SimpleDateFormat("dd MMM yyyy HH:mm:ss Z",
					Locale.ENGLISH).parse(timeString);
		}

		catch (ParseException e) {
			logger.error("Could not parse date from string \"" + itemString
					+ "\". Error:\n" + e.toString());
		}
		return date;
	}

	/**
	 * Parses the name or the IP address of the change of a property from the
	 * item string of the recent changes feed
	 * 
	 * @param itemString
	 *            substring for an item of the recent changes feed
	 * @return name of the author (if user is registered) or the IP address (if
	 *         user is not registered)
	 */
	String parseAuthorFromItemString(String itemString) {
		String endString = "</dc:creator>";
		int start = 66;
		int end = itemString.indexOf(endString);
		return itemString.substring(start, end);
	}

	/**
	 * Parses an item inside the <item>-tag.
	 * 
	 * @param bufferedReader
	 *            reader that reads the InputStream
	 * @param line
	 *            last line from the InputStream that has been read
	 * @return RecentChange that equals the item
	 */
	RecentChange parseItem(BufferedReader bufferedReader, String line) {
		String propertyName = null;
		String author = null;
		Date date = null;
		while (!line.contains("</item>")) {
			try {
				line = bufferedReader.readLine();
				if (line.contains("<title>")) {
					propertyName = parsePropertyNameFromItemString(line);
				}
				if (line.contains("<pubDate>")) {
					date = parseTimeFromItemString(line);
				}
				if (line.contains("<dc:creator>")) {
					author = parseAuthorFromItemString(line);
				}
			} catch (IOException e) {
				logger.error("Could not parse data from "
						+ apiConnection.apiBaseUrl + ". Error:\n"
						+ e.toString());
			}
		}
		if (propertyName == null || author == null || date == null) {
			// This should actually not happen
			throw new NullPointerException();
		}
		return new RecentChange(propertyName, date, author);
	}
}
