package org.wikidata.wdtk.wikibaseapi;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
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
     *            URL of the RDF feed
     */
    public RecentChangesFetcher(String rdfURL) {
	this.rdfURL = rdfURL;
    }

    /**
     * Fetches IOStream from RDF feed and separates it into different items
     * 
     * @return Different items of the recent changes feed or null if it does not
     *         exist
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
     * parses a substring of the recent changes feed and collects all property
     * names
     * 
     * @param items
     *            substring for an item of the recent changes feed
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
		String propertyName = parsePropertyName(item);
		propertyNames.add(propertyName);
	    }
	}
	return propertyNames;
    }

    /**
     * parses the name of the property from the item string of
     * 
     * @param itemString
     *            substring for an item of the recent changes feed
     * @return name of the property
     */
    public String parsePropertyName(String itemString) {
	String startString = "<title>";
	String endString = "</title>";
	int start = itemString.indexOf(startString) + startString.length();
	int end = itemString.indexOf(endString);
	String propertyName = itemString.substring(start, end);
	// logger.info(propertyName);
	return propertyName;
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