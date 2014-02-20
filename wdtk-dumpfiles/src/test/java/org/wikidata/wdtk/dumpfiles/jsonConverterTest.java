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

	private String sampleFilesBasePath = "ressources/testSamples/";
	private List<File> sampleFiles = new LinkedList<>();
	private List<JSONObject> testSamples = new LinkedList<>();

	@Before
	public void setUp() {

		// select the samples
		this.addSample("Haaften.json");
		this.addSample("Chicago.json");

		// load test case files
		// convert them to JSONObject

	}

	/**
	 * A helper for fetching the samples for the test setup
	 * 
	 * @param fileName
	 *            the file name only, no path information. The file is supposed
	 *            to be in the "resources/testSamples/"-directory.
	 */
	private void addSample(String fileName) {
		String relativeFilePath = this.sampleFilesBasePath + fileName;
		File sampleFile = new File(relativeFilePath);

		if (sampleFile.exists()) {
			this.sampleFiles.add(sampleFile);
		}

	}

	@Test
	public void itemDocumentConversion() {
		// TODO complete

	}

}
