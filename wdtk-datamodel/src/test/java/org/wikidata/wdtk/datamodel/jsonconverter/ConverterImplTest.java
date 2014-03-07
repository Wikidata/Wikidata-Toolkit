package org.wikidata.wdtk.datamodel.jsonconverter;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

public class ConverterImplTest {

	DataObjectFactory factory = new DataObjectFactoryImpl();;
	TestObjectFactory objectFactory = new TestObjectFactory();
	ConverterImpl converter = new ConverterImpl();

	@Before
	public void setUp() throws Exception {

		// TODO generate test classes (maybe from dumpfiles)
	}

	@Test
	public void testConvertItemDocumentToJson() throws JSONException {

		//System.out.println(converter.convertItemDocumentToJson(objectFactory
		//		.createEmptyItemDocument()));

		List<Statement> statements = new ArrayList<Statement>();
		List<StatementGroup> statementGroups = new ArrayList<StatementGroup>();
		Claim claim = factory.getClaim(factory.getItemIdValue("Q10", "base/"),
				factory.getNoValueSnak(factory.getPropertyIdValue("P11",
						"base/")), Collections.<Snak> emptyList());
		statements.add(factory.getStatement(claim,
				Collections.<Reference> emptyList(), StatementRank.NORMAL,
				"none"));

		statementGroups.add(factory.getStatementGroup(statements));

		statements = new ArrayList<Statement>();

		Claim claim2 = factory.getClaim(factory.getItemIdValue("Q10", "base/"),
				objectFactory.createValueSnakTime(17, "P1040"),
				objectFactory.createQualifiers());
		Claim claim3 = factory.getClaim(factory.getItemIdValue("Q10", "base/"),
				objectFactory.createValueSnakTime(3, "P1040"),
				Collections.<Snak> emptyList());

		statements.add(factory.getStatement(claim2,
				objectFactory.createReferences(3), StatementRank.NORMAL,
				"none2"));
		statements.add(factory.getStatement(claim3,
				Collections.<Reference> emptyList(), StatementRank.NORMAL,
				"none"));
		statementGroups.add(factory.getStatementGroup(statements));
		assertEquals("h", "h");
		assertEquals(converter.convertItemDocumentToJson(
				factory.getItemDocument(factory.getItemIdValue("Q10", "base/"),
						objectFactory.createLabels(),
						objectFactory.createDescriptions(),
						objectFactory.createAliases(), statementGroups,
						objectFactory.createSiteLinks())).toString(), TestRessources.ITEM_DOCUMENT_REPRES);
		assertEquals(converter.convertEntityDocumentToJson(
				factory.getItemDocument(factory.getItemIdValue("Q10", "base/"),
						objectFactory.createLabels(),
						objectFactory.createDescriptions(),
						objectFactory.createAliases(), statementGroups,
						objectFactory.createSiteLinks())).toString(), TestRessources.ITEM_DOCUMENT_REPRES);
	}

	@Test
	public void testConvertPropertyDocumentToJson() throws JSONException {
		PropertyDocument document = factory.getPropertyDocument(
				factory.getPropertyIdValue("P42", "base/"),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				factory.getDatatypeIdValue("string"));
		assertEquals(converter.convertPropertyDocumentToJson(document).toString(), TestRessources.EMPTY_PROPERTY_DOCUMENT_REPRES);
		assertEquals(converter.convertPropertyDocumentToJson(objectFactory.createEmptyPropertyDocument()).toString(), "{\"id\":\"P1\",\"title\":\"P1\",\"type\":\"property\"}");

	}

	@Test
	public void testTimeToString() {
		assertEquals(converter.timeToString((long) 1974, (byte) 3,
				(byte) 11, (byte) 12, (byte) 47, (byte) 35), "+00000001974-03-11T12:47:35Z");
		assertEquals(converter.timeToString((long) -65000000, (byte) 1,
				(byte) 1, (byte) 0, (byte) 0, (byte) 0), "-00065000000-01-01T00:00:00Z");
	}

	@Test
	public void testOrder() {
		JSONArray array = new JSONArray();
		array.put("one");
		array.put("two");
		array.put("three");
		assertEquals(array.toString(), "[\"one\",\"two\",\"three\"]");
	}
	
	@Test
	public void testMergeJSONObjects(){
		JSONObject object1 = new JSONObject();
		JSONObject object2 = new JSONObject();
		JSONObject objectMerge = new JSONObject();
		
		object1.put("keyLv1.1", 3);
		objectMerge.put("keyLv1.1", 3);
		
		object2.put("keyLv1.2", new JSONArray());
		objectMerge.put("keyLv1.2", new JSONArray());
		
		JSONObject Lv2 = new JSONObject();
		
		Lv2.put("keylv2.1", "ok");
		Lv2.put("keyLv2.2", new String());
		
		object1.put("keyLv1.3", Lv2);
		objectMerge.put("keyLv1.3", Lv2);
		
		assertEquals(converter.mergeJSONObjects(object1, object2).toString(), converter.mergeJSONObjects(object2, object1).toString());
		//assertEquals(converter.mergeJSONObjects(object1, objectMerge), objectMerge);
		assertEquals(converter.mergeJSONObjects(objectMerge, object2).toString(), objectMerge.toString());
		assertEquals(converter.mergeJSONObjects(object1, object2).toString(), objectMerge.toString());

		
	}
	
	@Test
	public void testConvertValueSnakToJson(){
		// ItemIdValue:
		ValueSnak snak = objectFactory.createValueSnakItemIdValue("P132", "Q233");
		assertEquals(converter.convertSnakToJson(snak).toString(), TestRessources.VALUE_SNAK_ITEM_ID_VALUE_REPRES);
		// String
		snak = objectFactory.createValueSnakStringValue("P132");
		assertEquals(converter.convertSnakToJson(snak).toString(), TestRessources.VALUE_SNAK_STRING_VALUE_REPRES);
		// Globe-Coordinates
		snak = objectFactory.createValueSnakCoordinatesValue("P132");
		assertEquals(converter.convertSnakToJson(snak).toString(), TestRessources.VALUE_SNAK_GLOBE_COORDINATES_VALUE_REPRES);
		// Quantity
		snak = objectFactory.createValueSnakQuantityValue("P231");
		assertEquals(converter.convertSnakToJson(snak).toString(), TestRessources.VALUE_SNAK_QUANTITY_VALUE_REPRES);
	}
	
	@Test
	public void testConvertSomeValueSnakToJson(){
		SomeValueSnak snak = objectFactory.createSomeValueSnak("P1231");
		assertEquals(converter.convertSnakToJson(snak).toString(), TestRessources.SOME_VALUE_SNAK_REPRES);
		
	}
	
	@Test
	public void testConvertClaimToJson(){
		ValueSnak snak = objectFactory.createValueSnakTime(42, "P129");
		Claim claim = objectFactory.createClaim("Q31", snak);
		assertEquals(converter.convertClaimToJson(claim).toString(), TestRessources.CLAIM_REPRES);
		
	}
	
	@Test
	public void testConvertPropertyIdValueToJson(){
		PropertyIdValue value = objectFactory.createPropertyIdValue("P200");
		assertEquals(converter.convertPropertyIdValueToJson(value).toString(), TestRessources.PROPERTY_ID_VALUE_REPRES);
		assertEquals(converter.convertEntityIdValueToJson(value).toString(), TestRessources.ENTITY_ID_VALUE_REPRES_2);
		
	}
	
	@Test
	public void testConvertItemIdValueToJson(){
		ItemIdValue value = objectFactory.createItemIdValue("Q200");
		assertEquals(converter.convertItemIdValueToJson(value).toString(), TestRessources.ITEM_ID_VALUE_REPRES);
		
		assertEquals(converter.convertEntityIdValueToJson(value).toString(), TestRessources.ENTITY_ID_VALUE_REPRES);
	}
	
}
