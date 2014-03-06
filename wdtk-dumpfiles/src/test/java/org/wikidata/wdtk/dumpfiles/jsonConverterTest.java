package org.wikidata.wdtk.dumpfiles;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;
import org.wikidata.wdtk.datamodel.interfaces.Value;

/**
 * The test setup uses several files containing JSON. These files are read by
 * the org.json-parser into sample objects to be converted.
 * 
 * @author Fredo Erxleben
 * 
 */
public class jsonConverterTest {

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
		assert testCase.getResult().equals(emptyPropertyDocument) : "Converted and expected empty property documents did not match";
	}

	@Test
	public void testEmptyItem() throws JSONException {
		// create the empty property test case
		ItemDocument emptyItemDocument = this.createEmptyItemDocument();
		ItemTestCase testCase = this.generateItemTestCase("EmptyItem.json",
				emptyItemDocument);

		testCase.convert();
		assertEquals( testCase.getResult(),emptyItemDocument);
	}

	@Test
	public void testBasicItem() throws JSONException {
		// create the empty property test case
		ItemDocument basicItemDocument = this.createBasicItemDocument();
		ItemTestCase testCase = this.generateItemTestCase("BasicItem.json",
				basicItemDocument);

		testCase.convert();
		ItemDocument result = testCase.getResult();
		
		assertEquals(result.getEntityId(), basicItemDocument.getEntityId());
		assertEquals(result.getItemId(), basicItemDocument.getItemId());
		assertEquals(result.getDescriptions(), basicItemDocument.getDescriptions());
		assertEquals(result.getAliases(), basicItemDocument.getAliases());
		assertEquals(result.getLabels(), basicItemDocument.getLabels());
		assertEquals(result.getSiteLinks(), basicItemDocument.getSiteLinks());
		assertEquals(result.getStatementGroups(), basicItemDocument.getStatementGroups());		
		assertEquals(result, basicItemDocument);
	}

	@Test
	public void testRealItems() throws JSONException {
		List<ItemTestCase> testCases = new LinkedList<>();

		testCases.add(this.generateItemTestCase("Chicago.json", null));
		testCases.add(this.generateItemTestCase("Haaften.json", null));

		for (ItemTestCase t : testCases) {
			t.convert();
		}
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

	private ItemDocument createBasicItemDocument() {

		ItemIdValue itemIdValue = this.factory.getItemIdValue("Q1", baseIri);

		List<MonolingualTextValue> labels = new LinkedList<>();
		labels.add(this.factory.getMonolingualTextValue("test", "en"));

		List<MonolingualTextValue> descriptions = new LinkedList<>();
		descriptions.add(this.factory.getMonolingualTextValue("this is a test",
				"en"));

		List<MonolingualTextValue> aliases = new LinkedList<>();
		aliases.add(this.factory.getMonolingualTextValue("TEST", "en"));
		aliases.add(this.factory.getMonolingualTextValue("Test", "en"));

		List<StatementGroup> statementGroups = new LinkedList<>();
		List<Statement> statements = new LinkedList<>();

		PropertyIdValue propertyId = this.factory.getPropertyIdValue("P1",
				baseIri);
		Value value = this.factory.getItemIdValue("Q1", baseIri);
		Snak mainSnak = factory.getValueSnak(propertyId, value);
		List<? extends Snak> qualifiers = new LinkedList<>();
		Claim claim = this.factory.getClaim(itemIdValue, mainSnak, qualifiers);

		List<? extends Reference> references = new LinkedList<>();
		StatementRank rank = StatementRank.NORMAL;
		String statementId = "foo";
		statements.add(this.factory.getStatement(claim, references, rank,
				statementId));

		statementGroups.add(this.factory.getStatementGroup(statements));

		Map<String, SiteLink> siteLinks = new HashMap<>();
		List<String> badges = new LinkedList<>();
		String siteKey = "enwiki";
		String title = "test";
		siteLinks.put("enwiki",
				this.factory.getSiteLink(title, siteKey, baseIri, badges));

		ItemDocument document = this.factory.getItemDocument(itemIdValue,
				labels, descriptions, aliases, statementGroups, siteLinks);
		return document;
	}

	private ItemDocument createEmptyItemDocument() {

		ItemIdValue itemIdValue = this.factory.getItemIdValue("Q1", baseIri);
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
	private ItemTestCase generateItemTestCase(String fileName,
			ItemDocument expectation) {
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
