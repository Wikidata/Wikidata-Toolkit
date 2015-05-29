package org.wikidata.wdtk.wikibaseapi;


import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.util.WebResourceFetcher;
import org.wikidata.wdtk.util.WebResourceFetcherImpl;

import java.util.Scanner;

/**
 * Simple class to fetch recent changes 
 * 
 * @author Markus Damm
 *
 */
public class RecentChangesFetcher{
	
	static final Logger logger = LoggerFactory
			.getLogger(WikibaseDataFetcher.class);
	
	/**
	 * URL for the recent changes feed of wikidata.org.
	 */
	final static String WIKIDATA_RDF_FEED_URL = 
			"http://www.wikidata.org/w/api.php?action=feedrecentchanges&format=json&feedformat=rss";
	
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
	public RecentChangesFetcher(){
		this(WIKIDATA_RDF_FEED_URL);
	}
	
	/**
	 * Creates an object to fetch recent changes 
	 * @param rdfURL
	 */
	public RecentChangesFetcher(String rdfURL){
		this.rdfURL = rdfURL;
	}
	
	
	/*
	 * Fetches the IOStream and returns the String with the last changes.
	 */
	public String getStringFromIOStream(){
		String result = null;
		try{
			InputStream inputStream = this.webResourceFetcher.getInputStreamForUrl(rdfURL);
			Scanner scanner = new Scanner(inputStream);
			result = scanner.useDelimiter("\\Z").next();
		    scanner.close();
			inputStream.close();
		}
		catch (IOException e){
			logger.error("Could not retrieve data from " + rdfURL + ". Error:\n"
					+ e.toString());
		}
		return result;
	}
}