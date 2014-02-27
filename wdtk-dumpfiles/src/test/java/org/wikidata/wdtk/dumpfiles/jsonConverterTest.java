package org.wikidata.wdtk.dumpfiles;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;

/**
 * The test setup uses several files containing JSON. These files are read by
 * the org.json-parser into sample objects to be converted.
 * 
 * @author Fredo Erxleben
 * 
 */
public class jsonConverterTest {

	// TODO test empty property document
	// TODO test empty item document
	// TODO reduce replication in test files

	private String sampleFilesBasePath = "src/test/resources/testSamples/";
	private JsonConverter unitUnderTest;
	private String baseIri = "test";
	private DataObjectFactory factory = new DataObjectFactoryImpl();
	private List<TestCase> testCases = new LinkedList<>();

	@Before
	public void setUp() {
		this.unitUnderTest = new JsonConverter(this.baseIri);

		// create the empty property test case
		PropertyDocument expectation = this.createEmptyPropertyDocument();
		this.addPropertyTestCase("EmptyProperty.json", expectation);

	}

	@Test
	public void compareWithExpectation() {
		for (TestCase t : this.testCases) {
			
			// show the json
			JSONObject json = t.getJson();
			
			@SuppressWarnings("unchecked")
			Iterator<String> key = json.keys();
			
			while(key.hasNext()){
				try {
					System.out.println(json.get(key.next()));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			// convert
			try {
				t.convert();
			} catch (JSONException e) {
				System.err.println("Conversion error!");
				System.err.println(t.toString());
				e.printStackTrace();
				continue;
			}

			assert t.resultMatchesExpected() : "Result did not match expectation.\n"
					+ t.toString();
		}
	}

	private PropertyDocument createEmptyPropertyDocument() {

		PropertyIdValue propertyId = this.factory.getPropertyIdValue("P1",
				this.baseIri);
		List<MonolingualTextValue> labels = new LinkedList<>();
		List<MonolingualTextValue> descriptions = new LinkedList<>();
		List<MonolingualTextValue> aliases = new LinkedList<>();
		DatatypeIdValue datatypeId = this.factory
				.getDatatypeIdValue("globe-coordinate");
		PropertyDocument document = this.factory.getPropertyDocument(
				propertyId, labels, descriptions, aliases, datatypeId);
		return document;
	}

	/**
	 * A helper for setting up ItemTestCases
	 * 
	 * @param fileName
	 *            the file name only, no path information. The file is supposed
	 *            to be in the "resources/testSamples/"-directory.
	 */
	private void addItemTestCase(String fileName, ItemDocument expectation) {
		String relativeFilePath = this.sampleFilesBasePath + fileName;

		ItemTestCase testCase = new ItemTestCase(relativeFilePath,
				this.unitUnderTest);

		if (expectation != null) {
			testCase.setExpected(expectation);
		}

		this.testCases.add(testCase);

	}

	/**
	 * A helper for setting up PropertyTestCases
	 * 
	 * @param fileName
	 *            the file name only, no path information. The file is supposed
	 *            to be in the "resources/testSamples/"-directory.
	 */
	private void addPropertyTestCase(String fileName,
			PropertyDocument expectation) {
		String relativeFilePath = this.sampleFilesBasePath + fileName;

		PropertyTestCase testCase = new PropertyTestCase(relativeFilePath,
				this.unitUnderTest);

		if (expectation != null) {
			testCase.setExpected(expectation);
		}

		this.testCases.add(testCase);

	}

	@Test
	public void itemDocumentConversion() {
		// TODO complete

	}

}
