package org.wikidata.wdtk.dumpfiles;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

/**
 * The test setup uses several files containing JSON. These files are read by
 * the org.json-parser into sample objects to be converted.
 * 
 * @author Fredo Erxleben
 * 
 */
public class jsonConverterTest {

	private List<File> sampleFiles = new LinkedList<>();
	private List<JSONObject> testSamples = new LinkedList<>();

	@Before
	public void setUp() {
		// TODO complete
		// load test case files
		// convert them to JSONObject

	}

	@Test
	public void itemDocumentConversion() {
		// TODO complete

	}

}
