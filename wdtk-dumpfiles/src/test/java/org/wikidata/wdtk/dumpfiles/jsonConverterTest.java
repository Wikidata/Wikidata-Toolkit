package org.wikidata.wdtk.dumpfiles;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;

/**
 * The test setup uses several files containing JSON. These files are read by
 * the org.json-parser into sample objects to be converted.
 * 
 * @author Fredo Erxleben
 * 
 */
public class jsonConverterTest {

	// TODO reduce replication in test files

	private String sampleFilesBasePath = "src/test/resources/testSamples/";
	private static JsonConverter unitUnderTest;
	private static String baseIri = "test";
	private DataObjectFactory factory = new DataObjectFactoryImpl();

	@BeforeClass
	public static void setUp() {
		unitUnderTest = new JsonConverter(baseIri);
	}

	@Test
	public void testEmptyProperty() throws JSONException {
		// create the empty property test case
		PropertyDocument emptyPropertyDocument = this
				.createEmptyPropertyDocument();
		PropertyTestCase testCase = this.generatePropertyTestCase(
				"EmptyProperty.json", emptyPropertyDocument);

		testCase.convert();
		assert testCase.getResult().equals(emptyPropertyDocument) : 
			"Converted and expected empty property documents did not match";
	}
	
	@Test
	public void testEmptyItem() throws JSONException {
		// create the empty property test case
		ItemDocument emptyItemDocument = this
				.createEmptyItemDocument();
		ItemTestCase testCase = this.generateItemTestCase(
				"EmptyItem.json", emptyItemDocument);

		testCase.convert();
		assert testCase.getResult().equals(emptyItemDocument) : 
			"Converted and expected empty property documents did not match";
	}
	

	/**
	 * Sets up an empty property document in the WDTK data model.
	 * 
	 * @return
	 */
	private PropertyDocument createEmptyPropertyDocument() {

		PropertyIdValue propertyId = this.factory.getPropertyIdValue("P1",
				baseIri);
		List<MonolingualTextValue> labels = new LinkedList<>();
		List<MonolingualTextValue> descriptions = new LinkedList<>();
		List<MonolingualTextValue> aliases = new LinkedList<>();
		DatatypeIdValue datatypeId = this.factory
				.getDatatypeIdValue("globe-coordinate");
		PropertyDocument document = this.factory.getPropertyDocument(
				propertyId, labels, descriptions, aliases, datatypeId);
		return document;
	}

	private ItemDocument createEmptyItemDocument() {

		ItemIdValue itemIdValue = this.factory.getItemIdValue("Q1",
				baseIri);
		List<MonolingualTextValue> labels = new LinkedList<>();
		List<MonolingualTextValue> descriptions = new LinkedList<>();
		List<MonolingualTextValue> aliases = new LinkedList<>();
		List<StatementGroup> statementGroups = new LinkedList<>();
		Map<String, SiteLink> siteLinks = new HashMap<>();
		ItemDocument document = this.factory.getItemDocument(itemIdValue,
				labels, descriptions, aliases, statementGroups, siteLinks);
		return document;
	}

	/**
	 * A helper for setting up ItemTestCases
	 * 
	 * @param fileName
	 *            the file name only, no path information. The file is supposed
	 *            to be in the "resources/testSamples/"-directory.
	 */
	private ItemTestCase generateItemTestCase(String fileName, ItemDocument expectation) {
		String relativeFilePath = this.sampleFilesBasePath + fileName;

		ItemTestCase testCase = new ItemTestCase(relativeFilePath,
				unitUnderTest);

		if (expectation != null) {
			testCase.setExpected(expectation);
		}

		return testCase;

	}

	/**
	 * A helper for setting up PropertyTestCases
	 * 
	 * @param fileName
	 *            the file name only, no path information. The file is supposed
	 *            to be in the "resources/testSamples/"-directory.
	 */
	private PropertyTestCase generatePropertyTestCase(String fileName,
			PropertyDocument expectation) {
		String relativeFilePath = this.sampleFilesBasePath + fileName;

		PropertyTestCase testCase = new PropertyTestCase(relativeFilePath,
				unitUnderTest);

		if (expectation != null) {
			testCase.setExpected(expectation);
		}

		return testCase;

	}

}
