package org.wikidata.wdtk.dumpfiles;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;
import org.wikidata.wdtk.dumpfiles.TestHelpers.JsonFetcher;
import org.wikidata.wdtk.dumpfiles.TestHelpers.TestObjectFactory;

/**
 * Tests the JsonConverter-class from the dumpfile-module.
 * 
 * @author Fredo Erxleben
 * 
 */
public class JsonConverterTest {

	private static String baseIri = "foo";
	private static DataObjectFactory factory = new DataObjectFactoryImpl();
	private static JsonFetcher fetcher = new JsonFetcher();
	private JsonConverter classUnderTest = new JsonConverter(baseIri, factory);

	private JSONObject fetchTestObject(String ressource) {

		try {
			return fetcher.getJsonObjectForResource(ressource);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("JSON parsing failed");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Reading file failed");
		}
		return null;
	}

	/**
	 * Used to allow multiple test case JSONObjects in one file, written down as
	 * an JSONArray.
	 * 
	 * @param ressource
	 * @return
	 */
	private List<JSONObject> fetchTestObjects(String ressource) {

		List<JSONObject> result = new LinkedList<>();

		try {
			JSONArray array = fetcher.getJsonArrayForResource(ressource);
			for (int i = 0; i < array.length(); i++) {
				result.add(array.getJSONObject(i));
			}

		} catch (JSONException e) {
			e.printStackTrace();
			fail("JSON parsing failed");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Reading file failed");
		}
		return result;
	}

	@Test
	public void testGetPropertyIdValue() {

		PropertyIdValue shouldBe = factory
				.getPropertyIdValue("P12345", baseIri);

		// 1: PropertyId with uppercase letter
		PropertyIdValue pid = this.classUnderTest.getPropertyIdValue("P12345");
		assertEquals(shouldBe, pid);

		// 2: PropertyId with lowercase letter
		pid = this.classUnderTest.getPropertyIdValue("p12345");
		assertEquals(shouldBe, pid);

		// 3: test malformed PropertyId
		try {
			pid = this.classUnderTest.getPropertyIdValue("12345");
			fail("Should throw exception");
		} catch (JSONException e) {
		} catch (Exception e) {
			fail("Wrong exception thrown");
		}
	}

	@Test
	public void testGetItemIdValue() {

		ItemIdValue shouldBe = factory.getItemIdValue("Q12345", baseIri);

		// 1: ItemId with uppercase letter
		ItemIdValue qid = this.classUnderTest.getItemIdValue("Q12345");
		assertEquals(shouldBe, qid);

		// 2: ItemId with lowercase letter
		qid = this.classUnderTest.getItemIdValue("q12345");
		assertEquals(shouldBe, qid);

		// 3: test malformed ItemId
		try {
			qid = this.classUnderTest.getItemIdValue("");
			fail("Should throw exception");
		} catch (JSONException e) {
		} catch (Exception e) {
			fail("Wrong exception thrown");
		}
	}

	@Test
	public void testGetDataTypeIri() {

		DatatypeIdValue idValue;
		idValue = this.classUnderTest.getDatatypeIdValue("wikibase-item");
		assertEquals(idValue,
				factory.getDatatypeIdValue(DatatypeIdValue.DT_ITEM));

		idValue = this.classUnderTest.getDatatypeIdValue("string");
		assertEquals(idValue,
				factory.getDatatypeIdValue(DatatypeIdValue.DT_STRING));

		idValue = this.classUnderTest.getDatatypeIdValue("url");
		assertEquals(idValue,
				factory.getDatatypeIdValue(DatatypeIdValue.DT_URL));

		idValue = this.classUnderTest.getDatatypeIdValue("commonsMedia");
		assertEquals(idValue,
				factory.getDatatypeIdValue(DatatypeIdValue.DT_COMMONS_MEDIA));

		idValue = this.classUnderTest.getDatatypeIdValue("time");
		assertEquals(idValue,
				factory.getDatatypeIdValue(DatatypeIdValue.DT_TIME));

		idValue = this.classUnderTest.getDatatypeIdValue("globe-coordinate");
		assertEquals(
				idValue,
				factory.getDatatypeIdValue(DatatypeIdValue.DT_GLOBE_COORDINATES));

		idValue = this.classUnderTest.getDatatypeIdValue("quantity");
		assertEquals(idValue,
				factory.getDatatypeIdValue(DatatypeIdValue.DT_QUANTITY));

		try {
			idValue = this.classUnderTest.getDatatypeIdValue("unknown");
			fail("Should throw exception");
		} catch (JSONException e) {
		} catch (Exception e) {
			fail("Wrong exception thrown");
		}
	}

	@Test
	public void testGetStatemantRank() {

		StatementRank rank;

		rank = this.classUnderTest.getStatementRank(0);
		assertEquals(rank, StatementRank.DEPRECATED);

		rank = this.classUnderTest.getStatementRank(1);
		assertEquals(rank, StatementRank.NORMAL);

		rank = this.classUnderTest.getStatementRank(2);
		assertEquals(rank, StatementRank.PREFERRED);

		try {
			rank = this.classUnderTest.getStatementRank(3);
			fail("Should throw exception");
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			fail("Wrong exception thrown");
		}
	}

	@Test
	public void testGetQuantityValue() {

		JSONObject testObject = this.fetchTestObject("QuantityValue.json");

		QuantityValue value = this.classUnderTest.getQuantityValue(testObject);
		QuantityValue shouldBe = factory.getQuantityValue(
				new BigDecimal(34196), new BigDecimal(34195), new BigDecimal(
						34197));

		assertEquals(value, shouldBe);
	}

	@Test
	public void testGetTimeValue() {

		JSONObject testObject = this.fetchTestObject("TimeValue.json");

		TimeValue value = this.classUnderTest.getTimeValue(testObject);
		TimeValue shouldBe = factory.getTimeValue(2012L, (byte) 6, (byte) 30,
				(byte) 0, (byte) 0, (byte) 0, (byte) 11, (byte) 0, (byte) 0,
				(byte) 0, "http://www.wikidata.org/entity/Q1985727");

		assertEquals(value, shouldBe);
	}

	@Test
	public void testGetGlobeCoordinatesValue() {

		// TODO improve: test arc-based precisions
		// TODO improve: test default precision

		List<JSONObject> testObjects = this
				.fetchTestObjects("GlobeCoordinatesValue.json");

		// 1: default case
		GlobeCoordinatesValue value = this.classUnderTest
				.getGlobeCoordinatesValue(testObjects.get(0));
		GlobeCoordinatesValue shouldBe = factory.getGlobeCoordinatesValue(
				(long) (51.835 * GlobeCoordinatesValue.PREC_DEGREE),
				(long) (10.785277777778 * GlobeCoordinatesValue.PREC_DEGREE),
				GlobeCoordinatesValue.PREC_MILLI_DEGREE,
				"http://www.wikidata.org/entity/Q2");

		assertEquals(value, shouldBe);

		// 2: different precisions

		// 2a) 10 degrees
		value = this.classUnderTest
				.getGlobeCoordinatesValue(testObjects.get(1));
		shouldBe = TestObjectFactory
				.createGlobalCoordinatesValue(GlobeCoordinatesValue.PREC_TEN_DEGREE);
		assertEquals(value, shouldBe);

		// 2b) 1 degrees
		value = this.classUnderTest
				.getGlobeCoordinatesValue(testObjects.get(2));
		shouldBe = TestObjectFactory
				.createGlobalCoordinatesValue(GlobeCoordinatesValue.PREC_DEGREE);
		assertEquals(value, shouldBe);

		// 2c) 0.1 degrees
		value = this.classUnderTest
				.getGlobeCoordinatesValue(testObjects.get(3));
		shouldBe = TestObjectFactory
				.createGlobalCoordinatesValue(GlobeCoordinatesValue.PREC_DECI_DEGREE);
		assertEquals(value, shouldBe);

		// 2d) 0.01 degrees
		value = this.classUnderTest
				.getGlobeCoordinatesValue(testObjects.get(4));
		shouldBe = TestObjectFactory
				.createGlobalCoordinatesValue(GlobeCoordinatesValue.PREC_CENTI_DEGREE);
		assertEquals(value, shouldBe);

		// 2e) 0.001 degrees
		value = this.classUnderTest
				.getGlobeCoordinatesValue(testObjects.get(5));
		shouldBe = TestObjectFactory
				.createGlobalCoordinatesValue(GlobeCoordinatesValue.PREC_MILLI_DEGREE);
		assertEquals(value, shouldBe);

		// 2f) 0.000'1 degrees
		value = this.classUnderTest
				.getGlobeCoordinatesValue(testObjects.get(6));
		shouldBe = TestObjectFactory
				.createGlobalCoordinatesValue(GlobeCoordinatesValue.PREC_HUNDRED_MICRO_DEGREE);
		assertEquals(value, shouldBe);

		// 2g) 0.000'01 degrees
		value = this.classUnderTest
				.getGlobeCoordinatesValue(testObjects.get(7));
		shouldBe = TestObjectFactory
				.createGlobalCoordinatesValue(GlobeCoordinatesValue.PREC_TEN_MICRO_DEGREE);
		assertEquals(value, shouldBe);
		
		// 2h) 0.000'001 degrees
		value = this.classUnderTest
				.getGlobeCoordinatesValue(testObjects.get(8));
		shouldBe = TestObjectFactory
				.createGlobalCoordinatesValue(GlobeCoordinatesValue.PREC_MICRO_DEGREE);
		assertEquals(value, shouldBe);
	}

	@Test
	public void testGetSnak() {

		PropertyIdValue pid = factory.getPropertyIdValue("P40", baseIri);

		// 1: noValue snak
		JSONArray jsonArray = new JSONArray("[\"novalue\",40]");

		Snak snak = this.classUnderTest.getSnak(jsonArray);
		assertEquals(snak, factory.getNoValueSnak(pid));

		// 2: someValue snak
		jsonArray = new JSONArray("[\"somevalue\",40]");

		snak = this.classUnderTest.getSnak(jsonArray);
		assertEquals(snak, factory.getSomeValueSnak(pid));

		// 3: value snak
		// in seperate test

		// 4: unknown value
		// TODO

	}

	@Test
	public void testGetValueSnak() {
		PropertyIdValue pid = factory.getPropertyIdValue("P40", baseIri);
		String prefix = "[\"value\",40,";

		// 1: string value
		StringValue stringValue = factory.getStringValue("foo");
		JSONArray jsonArray = new JSONArray(prefix + "string,"
				+ stringValue.getString() + "]");

		ValueSnak snak = this.classUnderTest.getValueSnak(jsonArray);
		assertEquals(snak, factory.getValueSnak(pid, stringValue));

		// 2: TimeValue
		JSONObject timeValue = fetchTestObject("TestObject_TimeValue.json");

		jsonArray = new JSONArray(prefix + "time," + timeValue.toString() + "]");

		snak = this.classUnderTest.getValueSnak(jsonArray);
		assertEquals(snak,
				factory.getValueSnak(pid, TestObjectFactory.createTimeValue()));

	}

	@Test
	public void testConvertToPropertyDocument() {

		JSONObject testObject = this.fetchTestObject("Property_Empty.json");

		PropertyDocument document = this.classUnderTest
				.convertToPropertyDocument(testObject, "P1");
		assertEquals(document,
				TestObjectFactory.createEmptyPropertyDocument(baseIri));
	}

	@Test
	public void testConvertToItemDocument() {

		JSONObject testObject = this.fetchTestObject("Item_Empty.json");

		ItemDocument document = this.classUnderTest.convertToItemDocument(
				testObject, "Q1");
		assertEquals(document,
				TestObjectFactory.createEmptyItemDocument(baseIri));
	}
}
