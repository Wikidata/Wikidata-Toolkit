package org.wikidata.wdtk.datamodel.json.jackson;

import org.wikidata.wdtk.datamodel.json.jackson.datavalues.EntityId;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.GlobeCoordinate;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.Time;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This is a superclass for all tests regarding the conversion of Wikidata
 * Web-API JSON into the WDTK data model and the other way around.
 * It provides mainly constants and the needed mapper objects.
 * 
 * @author Fredo Erxleben
 *
 */
public abstract class JsonConversionTest {
	
	// TODO maybe decompose the time a bit to have less magic strings in it
	
	protected ObjectMapper mapper = new ObjectMapper();
	
	protected static final String entityTypeItem = "item";
	
	// the id's used in the tests
	protected static final String propertyId = "P1";
	protected static final String itemId = "Q1";
	protected static final int numericId = 1;
	
	// stand-alone descriptions of ItemDocument-parts
	protected static final String itemTypeJson = "\"type\":\"item\"";
	protected static final String mltvJson = "{\"language\": \"en\", \"value\": \"foobar\"}";
	protected static final String siteLinkJson = "{\"site\":\"enwiki\", \"title\":\"foobar\", \"badges\":[]}";
	protected static final String noValueSnakJson = "{\"snaktype\":\"novalue\",\"property\":\"" + propertyId + "\"}";
	protected static final String someValueSnakJson = "{\"snaktype\":\"somevalue\",\"property\":\"" + propertyId + "\"}";
	
	// stand-alone descriptions of Value-parts
	protected static final String stringValueJson = "{\"type\":\"" + ValueImpl.typeString + "\",\"value\":\"foobar\"}";
	protected static final String entityIdValueJson = "{\"type\":\"" + ValueImpl.typeEntity + "\",\"value\":{\"entity-type\":\"" + entityTypeItem + "\",\"numeric-id\":" + numericId + "}}";
	protected static final String timeValueJson = "{\"type\":\"" + ValueImpl.typeTime + "\", \"value\":{\"time\":\"+00000002013-10-28T00:00:00Z\",\"timezone\":0,\"before\":0,\"after\":0,\"precision\":11,\"calendarmodel\":\"http://www.wikidata.org/entity/Q1985727\"}}";
	protected static final String globeCoordinateValueJson = "{\"type\":\"" + ValueImpl.typeCoordinate + "\", \"value\":{\"latitude\":-90,\"longitude\":0,\"precision\":10,\"globe\":\"http://www.wikidata.org/entity/Q2\"}}";
	
	// wrapping into item document structure for dedicated tests
	protected static final String wrappedLabelJson = "{\"labels\":{\"en\":" + mltvJson + "}," + itemTypeJson + "}";
	protected static final String wrappedDescriptionJson = "{\"descriptions\":{\"en\":" + mltvJson + "}," + itemTypeJson + "}";
	protected static final String wrappedAliasJson = "{ \"aliases\":{\"en\":[" + mltvJson + "]}," + itemTypeJson + "}";
	protected static final String wrappedItemIdJson = "{\"id\":\"" + itemId + "\"," + itemTypeJson + "}";
	protected static final String wrappedSiteLinkJson = "{\"sitelinks\":{\"enwiki\":" + siteLinkJson + "}," + itemTypeJson + "}";

	// objects to test against
	// should (of course) correspond to the JSON strings counterpart
	protected static final MonolingualTextValueImpl testMltv = new MonolingualTextValueImpl("en", "foobar");
	protected static final SiteLinkImpl testSiteLink = new SiteLinkImpl("enwiki", "foobar");
	protected static final NoValueSnakImpl testNoValueSnak = new NoValueSnakImpl(propertyId);
	protected static final SomeValueSnakImpl testSomeValueSnak = new SomeValueSnakImpl(propertyId);
	protected static final StringValueImpl testStringValue = new StringValueImpl("foobar");
	protected static final EntityIdValueImpl testEntityIdValue = new EntityIdValueImpl(new EntityId(entityTypeItem, numericId));
	protected static final TimeValueImpl testTimeValue = new TimeValueImpl(new Time("+00000002013-10-28T00:00:00Z",0,0,0,11, "http://www.wikidata.org/entity/Q1985727"));
	protected static final GlobeCoordinateValueImpl testGlobeCoordinateValue = new GlobeCoordinateValueImpl(new GlobeCoordinate(-90, 0, 10, "http://www.wikidata.org/entity/Q2"));
	

	
}
