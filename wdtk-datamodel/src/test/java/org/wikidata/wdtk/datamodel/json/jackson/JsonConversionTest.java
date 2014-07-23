package org.wikidata.wdtk.datamodel.json.jackson;

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
	
	protected ObjectMapper mapper = new ObjectMapper();
	
	// the id's used in the tests
	protected static final String propertyId = "P1";
	protected static final String itemId = "Q1";
	
	// stand-alone descriptions of parts
	protected static final String itemTypeJson = "\"type\":\"item\"";
	protected static final String mltvJson = "{\"language\": \"en\", \"value\": \"foobar\"}";
	protected static final String siteLinkJson = "{\"site\":\"enwiki\", \"title\":\"foobar\", \"badges\":[]}";
	protected static final String noValueSnakJson = "{\"snaktype\":\"novalue\",\"property\":\"" + propertyId + "\"}";
	protected static final String someValueSnakJson = "{\"snaktype\":\"somevalue\",\"property\":\"" + propertyId + "\"}";
	
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
	

	
}
