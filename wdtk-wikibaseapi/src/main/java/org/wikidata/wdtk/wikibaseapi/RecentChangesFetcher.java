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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.util.WebResourceFetcher;
import org.wikidata.wdtk.util.WebResourceFetcherImpl;

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
	 * URL for the recent changes feed of wikidata.org.
	 */
	final static String WIKIDATA_RDF_FEED_URL = "http://www.wikidata.org/w/api.php?action=feedrecentchanges&format=json&feedformat=rss";

	/**
	 * The URL where the recent changes feed can be found.
	 */
	final String rdfURL;

	/**
	 * Object used to make web requests. Package-private so that it can be
	 * overwritten with a mock object in tests.
	 */
	WebResourceFetcher webResourceFetcher = new WebResourceFetcherImpl();

	/**
	 * Creates an object to fetch recent changes of Wikidata
	 */
	public RecentChangesFetcher() {
		this(WIKIDATA_RDF_FEED_URL);
	}

	/**
	 * Creates an object to fetch recent changes
	 * 
	 * @param rdfURL
	 *                URL of the RDF feed
	 */
	public RecentChangesFetcher(String rdfURL) {
		this.rdfURL = rdfURL;
	}

	/**
	 * Fetches IOStream from RDF feed and separates it into different items
	 * 
	 * @return Different items of the recent changes feed or null if it does
	 *         not exist
	 */
	public String[] getStringFromIOStream() {
		String[] result = null;
		try {
			InputStream inputStream = this.webResourceFetcher
					.getInputStreamForUrl(rdfURL);
			Scanner scanner = new Scanner(inputStream);
			String rdfString = scanner.useDelimiter("\\Z").next();
			result = rdfString.split("<item>");
			scanner.close();
			inputStream.close();
		} catch (IOException e) {
			logger.error("Could not retrieve data from " + rdfURL
					+ ". Error:\n" + e.toString());
		}
		return result;
	}

	/**
	 * parses a substring of the recent changes feed and collects all
	 * property names
	 * 
	 * @param items
	 *                substring for an item of the recent changes feed
	 * @return set of all property names that were changed. Hence multiple
	 *         changes appear only once.
	 */
	public Set<String> parseItemStrings(String[] items) {
		Set<String> propertyNames = new HashSet<>();
		boolean firstEntry = true;
		for (String item : items) {
			if (firstEntry) {
				firstEntry = false;
			} else {
				String propertyName = parsePropertyNameFromItemString(item);
				propertyNames.add(propertyName);
			}
		}
		return propertyNames;
	}

	/**
	 * parses the name of the property from the item string of the recent
	 * changes feed
	 * 
	 * @param itemString
	 *                substring for an item of the recent changes feed
	 * @return name of the property
	 */
	public String parsePropertyNameFromItemString(String itemString) {
		String startString = "<title>";
		String endString = "</title>";
		int start = itemString.indexOf(startString)
				+ startString.length();
		int end = itemString.indexOf(endString);
		String propertyName = itemString.substring(start, end);
		return propertyName;
	}

	/**
	 * parses the date and time of the change of a property from the item
	 * string of the recent changes feed
	 * 
	 * @param itemString
	 *                substring for an item of the recent changes feed
	 * @return date and time for the recent change
	 */
	public Date parseTimeFromItemString(String itemString) {
		String startString = "<pubDate>";
		String endString = "</pubDate>";
		int start = itemString.indexOf(startString)
				+ startString.length();
		int end = itemString.indexOf(endString);
		String timeString = itemString.substring(start, end);
		Date date = null;
		try {
			date = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss Z",
					Locale.ENGLISH).parse(timeString);
		}

		catch (ParseException e) {
			logger.error("Could not parse date from string \""
					+ itemString + "\". Error:\n"
					+ e.toString());
		}
		return date;
	}


	/**
	 * parses the name or the IP address of the change of a property from
	 * the item string of the recent changes feed
	 * 
	 * @param itemString
	 *                substring for an item of the recent changes feed
	 * @return name of the author (if user is registered) or the IP address
	 *         (if user is not registered)
	 */
	public String parseAuthorFromItemString(String itemString) {
		String startString = "<dc:creator>";
		String endString = "</dc:creator>";
		int start = itemString.indexOf(startString)
				+ startString.length();
		int end = itemString.indexOf(endString);
		return itemString.substring(start, end);
	}
	
	/**
	 * method to call for getting recent changes
	 * 
	 * @return Set of recent Changes
	 */
	public Set<String> getRecentChanges() {
		String[] recentChangeString = getStringFromIOStream();
		Set<String> recentChanges = parseItemStrings(recentChangeString);
		return recentChanges;
	}
}