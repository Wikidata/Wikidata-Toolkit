package org.wikidata.wdtk.dumpfiles.TestHelpers;

import java.io.IOException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;
import org.wikidata.wdtk.testing.MockStringContentFactory;

/**
 * A simple helper class to get JSON objects from a ressource file.
 * @author Fredo Erxleben
 *
 */
public class JsonFetcher {
	// NOTE: in the case of switching to fasterxml.jackson this class might be 
	// outfitted with more methods to accomodate different ways of in- / output
	
	private static final String SAMPLE_FILES_BASE_PATH = "/testSamples/";
	
	/**
	 * Returns a JSON object for the JSON stored in the given resource.
	 * 
	 * @param resourceName
	 *            a file name without any path information
	 * @return the JSONObject
	 * @throws IOException
	 * @throws JSONException
	 */
	public JSONObject getJsonObjectForResource(String resourceName)
			throws IOException, JSONException {
		
		URL resourceUrl = this.getClass().getResource(
				JsonFetcher.SAMPLE_FILES_BASE_PATH + resourceName);
		String jsonString = MockStringContentFactory
				.getStringFromUrl(resourceUrl);
		return new JSONObject(jsonString);
	}

}
