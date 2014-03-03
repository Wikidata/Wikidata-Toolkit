package org.wikidata.wdtk.dumpfiles;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

// IDEA introduce a verbose-flag to enable/disable logging
// IDEA move MonolingualTextValue inlines into a method
// TODO permanent: check if documentation is up-to-date
/**
 * This class provides methods to convert dump-file JSON objects into
 * representations according to the WDTK data model. Since the converted JSON
 * normally belongs to the same domain, the base IRI is represented as an
 * attribute. The prefixes used for items and properties may be set
 * individually. The default is "Q" for items and "P" for properties.
 * 
 * @author Fredo Erxleben
 * 
 */
public class JsonConverter {

	private String itemPrefix;
	private String propertyPrefix;
	private DataObjectFactory factory = new DataObjectFactoryImpl();
	private String baseIri = "";

	/**
	 * Creates a new instance of the JsonConverter. For the <i>baseIri</i> see
	 * also {@link org.wikidata.wdtk.datamodel.interfaces.ItemId} The item
	 * prefix defaults to "Q". The property prefix defaults to "P".
	 * 
	 * @param baseIri
	 *            the initial IRI to be used for the processed JSON.
	 */
	public JsonConverter(String baseIri) {
		this(baseIri, "Q", "P");
	}

	/**
	 * Creates a new instance of the JsonConverter. For the <i>baseIri</i> see
	 * also {@link org.wikidata.wdtk.datamodel.interfaces.ItemId}
	 * 
	 * @param baseIri
	 *            the initial IRI to be used for the processed JSON.
	 * @param itemPrefix
	 *            the prefix used for any item id.
	 * @param propertyPrefix
	 *            the prefix used for any property id.
	 */
	public JsonConverter(String baseIri, String itemPrefix,
			String PropertyPrefix) {
		this.setBaseIri(baseIri);
		this.setItemPrefix(itemPrefix);
		this.setPropertyPrefix(PropertyPrefix);
	}

	/**
	 * Attempts to parse a given JSON object into an instance of
	 * PropertyDocument.
	 * 
	 * @param toConvert
	 *            the JSON object to convert. Must represent a property document
	 *            and therefore contain the keys "entity", "label",
	 *            "description", "aliases" and "datatype".
	 * @return the PropertyDocument as described in the JSON.
	 * @throws NullPointerException
	 *             if toConvert was null.
	 * @throws JSONException
	 *             if the JSON object did not contain a key it should have had.
	 */
	PropertyDocument convertToPropertyDocument(JSONObject toConvert)
			throws JSONException {

		// sanity check
		if (toConvert == null) {
			throw new NullPointerException();
		}

		if (toConvert.length() == 0) { // if the JSON object is empty
			throw new JSONException("The JSON to convert was empty");
		}

		PropertyDocument result;

		// get the property id
		JSONArray jsonEntity = toConvert.getJSONArray("entity");
		PropertyIdValue propertyId = this.getPropertyId(jsonEntity);

		// get the labels
		JSONObject jsonLabels = toConvert.getJSONObject("label");
		List<MonolingualTextValue> labels = this.getLabels(jsonLabels);

		// get the descriptions
		JSONObject jsonDescriptions = toConvert.getJSONObject("description");
		List<MonolingualTextValue> descriptions = this
				.getDescriptions(jsonDescriptions);

		// get the aliases
		JSONObject jsonAliases = toConvert.getJSONObject("aliases");
		List<MonolingualTextValue> aliases = this.getAliases(jsonAliases);

		// get the datatype id
		String jsonDataTypeId = toConvert.getString("datatype");
		DatatypeIdValue datatypeId = this.getDataTypeId(jsonDataTypeId);

		result = this.factory.getPropertyDocument(propertyId, labels,
				descriptions, aliases, datatypeId);
		return result;
	}

	/**
	 * Transforms a given string into a DatatypeIdValue.
	 * 
	 * @param jsonDataTypeId
	 *            is the string to be converted. Must not be null.
	 * @return the appropriate DatatypeIdValue-instance.
	 */
	private DatatypeIdValue getDataTypeId(String jsonDataTypeId) {
		assert jsonDataTypeId != null : "Given JSON datatype id was null";

		return this.factory.getDatatypeIdValue(jsonDataTypeId);
	}

	/**
	 * Converts a given JSON array into a PropertyIdValue. The appropriate
	 * prefix for properties will be used.
	 * 
	 * @param jsonEntity
	 *            is a JSON array denoting the property id. Must be of the form <br/>
	 *            ["property", <i>propertyID</i>]<br/>
	 *            Must not be null.
	 * @return the appropriate PropertyIdValue-instance.
	 * @throws JSONException
	 *             if the format requirements are not met.
	 */
	private PropertyIdValue getPropertyId(JSONArray jsonEntity)
			throws JSONException {
		assert jsonEntity != null : "Entity JSON was null.";
		assert jsonEntity.getString(0).equals("property") : "Entity JSON did not denote a property";

		return this.getPropertyIdValue(jsonEntity.getInt(1));
	}

	/**
	 * Creates a PropertyIdValue from a given integer and the set property
	 * prefix.
	 * 
	 * @param intValue
	 *            is the integer id of the property.
	 * @return a PropertyIdValue-instance.
	 */
	private PropertyIdValue getPropertyIdValue(int intValue) {

		String id = this.propertyPrefix + intValue;
		return this.factory.getPropertyIdValue(id, this.baseIri);
	}

	/**
	 * Attempts to parse a given JSON object into an instance of ItemDocument.
	 * 
	 * @param toConvert
	 *            the JSON object to convert. Must represent an item document
	 *            and therefore contain the keys "entity", "label",
	 *            "description", "aliases", "claims" and "links".
	 * @return the ItemDocument as described in the JSON.
	 * @throws NullPointerException
	 *             if toConvert was null.
	 * @throws JSONException
	 *             if the JSON object did not contain a key it should have had.
	 */
	public ItemDocument convertToItemRecord(JSONObject toConvert)
			throws JSONException, NullPointerException {

		// sanity check
		if (toConvert == null) {
			throw new NullPointerException();
		}

		if (toConvert.length() == 0) { // if the JSON object is empty
			throw new JSONException("The JSON to convert was empty");
		}

		// get the item Id
		JSONArray jsonEntity = toConvert.getJSONArray("entity");
		ItemIdValue itemId = this.getItemId(jsonEntity);

		// get the labels
		JSONObject jsonLabels = toConvert.getJSONObject("label");
		List<MonolingualTextValue> labels = this.getLabels(jsonLabels);

		// get the description
		JSONObject jsonDescriptions = toConvert.getJSONObject("description");
		List<MonolingualTextValue> descriptions = this
				.getDescriptions(jsonDescriptions);

		// get the aliases
		// NOTE empty aliases are an JSON array
		// non-empty aliases are JSON objects
		List<MonolingualTextValue> aliases;
		JSONArray jsonEmptyAliases = toConvert.optJSONArray("aliases");
		if (jsonEmptyAliases == null) {
			JSONObject jsonAliases = toConvert.getJSONObject("aliases");
			aliases = this.getAliases(jsonAliases);
		} else {
			aliases = new LinkedList<>();
		}

		// get the statements
		JSONArray jsonStatements = toConvert.getJSONArray("claims");
		List<StatementGroup> statements = this.getStatements(jsonStatements,
				itemId);

		// get the site links
		JSONObject jsonLinks = toConvert.getJSONObject("links");
		Map<String, SiteLink> siteLinks = this.getSiteLinks(jsonLinks);

		// now put it all together
		ItemDocument result = factory.getItemDocument(itemId, labels,
				descriptions, aliases, statements, siteLinks);
		return result;
	}

	/**
	 * Converts a JSON array containing statements into a list of statement
	 * groups a represented by the WDTK data model.
	 * 
	 * @param jsonStatements
	 *            contains all the statements about an item. must consist of
	 *            JSON objects containing the keys "m", "q", "g", "refs" and
	 *            "rank" each.
	 * 
	 * @return a list of statement groups as specified by the WDTK data model.
	 * @throws JSONException
	 *             if one of the JSON objects in the array did not contain all
	 *             required keys.
	 */
	private List<StatementGroup> getStatements(JSONArray jsonStatements,
			EntityIdValue subject) throws JSONException {

		assert jsonStatements != null : "statements JSON array was null";
		// structure is [{"m":object, "q":[], "g":string, "rank":int,
		// "refs":[…]},…]
		// "q" => qualifiers
		// "m" => main snak
		// "g" => statement id

		List<StatementGroup> result = new LinkedList<StatementGroup>();
		List<Statement> statementsFromJson = new LinkedList<Statement>();

		// iterate over all the statements in the item and decompose them
		for (int i = 0; i < jsonStatements.length(); i++) {
			JSONObject currentStatement = jsonStatements.getJSONObject(i);

			// get a list of statements in the order they are in the JSON
			// get the claim
			Claim currentClaim = this.getClaim(currentStatement, subject);

			// get the references
			JSONArray jsonRefs = currentStatement.getJSONArray("refs");
			List<? extends Reference> references = this.getReferences(jsonRefs);

			// get the statement rank
			int rankAsInt = currentStatement.getInt("rank");
			StatementRank rank = this.getStatementRank(rankAsInt);

			// get the statement id
			String statementId = currentStatement.getString("g");

			// combine into statement
			Statement statement = factory.getStatement(currentClaim,
					references, rank, statementId);

			statementsFromJson.add(statement);

		}

		// process the list of statements into a list of statement groups
		StatementGroupBuilder builder = new StatementGroupBuilder(this.factory);
		result = builder.buildFromStatementList(statementsFromJson);

		return result;
	}

	/**
	 * Converts the given JSON array into a list of Reference-objects. A
	 * reference is an array of reference statements which in turn are value
	 * snaks.
	 * 
	 * @param jsonReferences
	 *            is an JSON array of JSON arrays of value snaks
	 * @return the appropriate List of references.
	 */
	private List<? extends Reference> getReferences(JSONArray jsonReferences) {
		// References are [singeRef]
		// singleRef are [refStatements]
		// refStatements are value snaks

		List<Reference> result = new LinkedList<>();

		// process the single references
		for (int i = 0; i < jsonReferences.length(); i++) {
			try {
				JSONArray jsonSingleRef = jsonReferences.getJSONArray(i);
				List<ValueSnak> valueSnaks = new LinkedList<>();

				// process the reference statements
				for (int j = 0; j < jsonSingleRef.length(); j++) {
					try {
						JSONArray jsonValueSnak = jsonSingleRef.getJSONArray(j);
						ValueSnak currentValueSnak = this
								.getValueSnak(jsonValueSnak);
						valueSnaks.add(currentValueSnak);
					} catch (JSONException e) {
						// skip over invalid references
						continue;
					}
				}

				Reference singleReference = factory.getReference(valueSnaks);
				result.add(singleReference);

			} catch (JSONException e) {
				// skip over invalid references
				continue;
			}

		}
		return result;
	}

	/**
	 * Converts the given JSON array into a ValueSnak.
	 * 
	 * @param jsonValueSnak
	 *            is a JSON array of the form <br/>
	 *            ["value", <i>propertyID</i>, <i>value type</i>, <i>value</i>]
	 *            where the structure of the value depends on the value type.
	 *            Must not be null.
	 * @return a ValueSnak-instance according to the given JSON representation.
	 * @throws JSONException
	 *             if the required format was not matched.
	 */
	private ValueSnak getValueSnak(JSONArray jsonValueSnak)
			throws JSONException {
		// a value snak is
		// ["value", propertyID, value-type, value]

		assert jsonValueSnak != null : "jsonValueSnak was null";
		assert jsonValueSnak.getString(0).equals("value") : "given JSON was not a value snak";

		ValueSnak result;

		// get the property id
		int intId = jsonValueSnak.getInt(1);
		PropertyIdValue propertyId = this.getPropertyIdValue(intId);

		// get the value
		String valueString = jsonValueSnak.getString(2);
		Value value;
		switch (valueString) {
		case "time":
			value = this.getTimeValue(jsonValueSnak.getJSONObject(3));
			break;
		case "wikibase-entityid":
			value = this.getEntityIdValue(jsonValueSnak.getJSONObject(3));
			break;
		case "string":
			value = this.getStringIdValue(jsonValueSnak.getString(3));
			break;
		case "globecoordinate":
			value = this.getGlobeCoordinatesValue(jsonValueSnak
					.getJSONObject(3));
			break;
		case "quantity":
			value = this.getQuantityValue(jsonValueSnak.getJSONObject(3));
			break;
		default:
			throw new JSONException("Unknown value type " + valueString
					+ "in value snak JSON");
		}

		// put it all together
		result = this.factory.getValueSnak(propertyId, value);
		return result;
	}

	/**
	 * Converts a JSON-objects into QuantityValues.
	 * 
	 * @param jsonQuantityValue
	 *            is a JSON-object containing the labels "amount", "upperBound"
	 *            and "lowerBound". All other labels will be ignored. Must not
	 *            be null.
	 * @return an appropriate QuantityValue-instance.
	 * @throws JSONException
	 */
	private QuantityValue getQuantityValue(JSONObject jsonQuantityValue)
			throws JSONException {
		// example:
		// {"amount":"+34196",
		// "unit":"1",
		// "upperBound":"+34197",
		// "lowerBound":"+34195"}
		// NOTE ignore unit for now
		// it will be reviewed later

		BigDecimal numericValue = new BigDecimal(
				jsonQuantityValue.getString("amount"));

		BigDecimal lowerBound = new BigDecimal(
				jsonQuantityValue.getString("lowerBound"));

		BigDecimal upperBound = new BigDecimal(
				jsonQuantityValue.getString("upperBound"));

		QuantityValue result = this.factory.getQuantityValue(numericValue,
				lowerBound, upperBound);

		return result;
	}

	/**
	 * Converts a JSON-object into a GlobeCordinatesValue.
	 * 
	 * @param jsonGlobeCoordinate
	 *            is a JSON-object containing the labels "latitude",
	 *            "longitude", "precision" and "globe". All other labels will be
	 *            ignored. Must not be null.
	 * @return an appropriate GlobeCoordinatesValue
	 * @throws JSONException
	 *             if a required label was missing.
	 */
	private GlobeCoordinatesValue getGlobeCoordinatesValue(
			JSONObject jsonGlobeCoordinate) throws JSONException {

		assert jsonGlobeCoordinate != null : "Globe coordinate JSON was null";
		// example:
		// {"latitude":51.835,
		// "longitude":10.785277777778,
		// "altitude":null,
		// "precision":0.00027777777777778,
		// "globe":"http:\/\/www.wikidata.org\/entity\/Q2"}
		// NOTE as for now, ignore "altitude".
		// The key will be reviewed in the future.

		long latitude = jsonGlobeCoordinate.getLong("latitude");
		long longitude = jsonGlobeCoordinate.getLong("longitude");
		long precision = jsonGlobeCoordinate.getLong("precision");
		String globeIri = jsonGlobeCoordinate.getString("globe");

		GlobeCoordinatesValue result = this.factory.getGlobeCoordinatesValue(
				latitude, longitude, precision, globeIri);

		return result;
	}

	/**
	 * Acquires the StringValue for a given String.
	 * 
	 * @param string
	 * @return
	 */
	private StringValue getStringIdValue(String string) {
		assert string != null : "String to be converted to a StringValue was null";

		// NOTE I decided against inlining, so
		// if the StringValue changes somehow in the future
		// one has only to change this method
		return this.factory.getStringValue(string);
	}

	/**
	 * Converts a given JSON-object to an EntityIdValue.
	 * 
	 * @param jsonObject
	 *            an JSON object denoting an entity id. It must contain the
	 *            labels "entity-type" and "numeric-id".
	 * @return
	 * @throws JSONException
	 *             if a required key was not available.
	 */
	private EntityIdValue getEntityIdValue(JSONObject jsonObject)
			throws JSONException {
		// example:
		// {"entity-type":"item",
		// "numeric-id":842256}
		// NOTE there be any other entity-type then "item" in later releases

		EntityIdValue result;
		String entityType = jsonObject.getString("entity-type");

		// check the entity type
		switch (entityType) {
		case "item":
			result = this.getItemIdValue(jsonObject.getInt("numeric-id"));
			break;
		default:
			throw new JSONException("Unknown entity type " + entityType
					+ " in entity id value JSON.");
		}
		return result;
	}

	/**
	 * Creates a ItemIdValue from a given integer and the set item prefix.
	 * 
	 * @param intValue
	 *            is the integer id of the item.
	 * @return a ItemIdValue-instance.
	 */
	private ItemIdValue getItemIdValue(int intValue) {
		String id = this.itemPrefix + intValue;
		return this.factory.getItemIdValue(id, this.baseIri);

	}

	/**
	 * Converts a JSON-object into a TimeValue.
	 * 
	 * @param jsonTimeValue
	 *            is a JSON-object with the keys "time", "timezone", "before",
	 *            "after", "precision" and "calendarmodel".
	 * @return the TimeValue as described by the JSON-object.
	 * @throws JSONException
	 *             if a required label was missing.
	 */
	private TimeValue getTimeValue(JSONObject jsonTimeValue)
			throws JSONException {
		// example:
		// {"time":"+00000002012-06-30T00:00:00Z",
		// "timezone":0,
		// "before":0,
		// "after":0,
		// "precision":11,
		// "calendarmodel":"http:\/\/www.wikidata.org\/entity\/Q1985727"}
		TimeValue result;

		String stringTime = jsonTimeValue.getString("time");
		String[] stringValues = stringTime.split("[\\-\\:TZ]");

		// get the year
		int year = Integer.parseInt(stringValues[0]);

		// get the month
		byte month = Byte.parseByte(stringValues[1]);

		// get the day
		byte day = Byte.parseByte(stringValues[2]);

		// get the hour
		byte hour = Byte.parseByte(stringValues[3]);

		// get the minute
		byte minute = Byte.parseByte(stringValues[4]);
		;

		// get the second
		byte second = Byte.parseByte(stringValues[5]);
		;

		// get the precision
		byte precision = (byte) jsonTimeValue.getInt("precision");

		// get the tolerances
		int beforeTolerance = jsonTimeValue.getInt("before");
		int afterTolerance = jsonTimeValue.getInt("after");

		// get the timezone offset
		int timezoneOffset = jsonTimeValue.getInt("timezone");

		// get the calendar model
		String calendarModel = jsonTimeValue.getString("calendarmodel");

		result = this.factory.getTimeValue(year, month, day, hour, minute,
				second, precision, beforeTolerance, afterTolerance,
				timezoneOffset, calendarModel);
		return result;
	}

	/**
	 * Transforms a statement rank from an integer representation to an
	 * enumerated value as requested by the WDTK data model. <br/>
	 * The number 0 maps to DEPRECATED. <br/>
	 * The number 1 maps to NORMAL. <br/>
	 * The number 2 maps to PREFERRED. <br/>
	 * 
	 * To accommodate for possible other values that may occur any number below
	 * 0 also maps to DEPRECATED and any number above 2 also maps to PREFERRED.
	 * 
	 * @param intRank
	 *            the rank as integer.
	 * @return an appropriate StatementRank-value
	 */
	private StatementRank getStatementRank(int intRank) {

		// this is the default case
		StatementRank result = StatementRank.NORMAL;

		if (intRank < 1) {
			result = StatementRank.DEPRECATED;
		} else if (intRank > 1) {
			result = StatementRank.PREFERRED;
		}
		return result;
	}

	/**
	 * Gets the claim from a statement in JSON. A Claim consists out of the
	 * EntityIdValue of the subject, the subjects main snak and its qualifiers.
	 * 
	 * @param currentStatement
	 *            a JSON object representing a whole statement from which the
	 *            claim is to be extracted.
	 * @return
	 * @throws JSONException
	 *             when a required key was not found or the snak type could not
	 *             be identified.
	 */
	private Claim getClaim(JSONObject currentStatement, EntityIdValue subject)
			throws JSONException {

		// m: main snak
		// q: qualifiers

		// get the main snak
		JSONArray jsonMainSnak = currentStatement.getJSONArray("m");
		Snak mainSnak = getSnak(jsonMainSnak);

		// get the qualifiers
		JSONArray jsonQualifiers = currentStatement.getJSONArray("q");
		List<Snak> qualifiers = this.getQualifiers(jsonQualifiers);

		// build it together
		Claim result = this.factory.getClaim(subject, mainSnak, qualifiers);
		return result;
	}

	/**
	 * Converts the given JSON array into a Snak. This might either be a
	 * ValueSnak, NoValueSnak or SomeValueSnak.
	 * 
	 * @param jsonMainSnak
	 *            is the JSON array to be converted. Must not be null.
	 * @return A Snak corresponding to the given JSON array.
	 * @throws JSONException
	 *             if the snack type could not determined.
	 */
	private Snak getSnak(JSONArray jsonMainSnak) throws JSONException {

		Snak result;
		switch (jsonMainSnak.getString(0)) {
		case "value":
			result = this.getValueSnak(jsonMainSnak);
			break;
		case "somevalue":
			result = this.getSomeValueSnak(jsonMainSnak);
			break;
		case "novalue":
			result = this.getNoValueSnak(jsonMainSnak);
			break;
		default: // could not determine snak type...
			throw new JSONException("Unknown snack type: "
					+ jsonMainSnak.getString(0));
		}
		return result;
	}

	/**
	 * Converts a JSON array into a SomeValueSnak.
	 * 
	 * @param jsonSomeValueSnak
	 *            is an JSON array that denotes a some-value snak. It has the
	 *            form<br/>
	 *            ["somevalue", <i>propertyID</i>]
	 * 
	 * @return an appropriate SomeValueSnak-instance
	 * @throws JSONException
	 *             if the format does not match the required one.
	 */
	private SomeValueSnak getSomeValueSnak(JSONArray jsonSomeValueSnak)
			throws JSONException {
		// example:
		// ["somevalue",22], where P22 is the property "father"

		assert jsonSomeValueSnak != null : "jsonSomeValueSnak was null.";
		assert jsonSomeValueSnak.getString(0).equals("somevalue") : "Argument was not a SomeValueSnak.";

		int intPropertyId = jsonSomeValueSnak.getInt(1);
		PropertyIdValue propertyId = this.getPropertyIdValue(intPropertyId);

		SomeValueSnak result = this.factory.getSomeValueSnak(propertyId);

		return result;
	}

	/**
	 * Converts a JSON array into a NoValueSnak.
	 * 
	 * @param jsonNoValueSnak
	 *            is an JSON array that denotes a no-value snak. It has the form<br/>
	 *            ["novalue", <i>propertyID</i>]
	 * 
	 * @return an appropriate NoValueSnak-instance
	 * @throws JSONException
	 *             if the format does not match the required one.
	 */
	private NoValueSnak getNoValueSnak(JSONArray jsonNoValueSnak)
			throws JSONException {
		// example:
		// ["novalue",40], where P40 is the property "children"

		assert jsonNoValueSnak != null : "jsonSomeValueSnak was null.";
		assert jsonNoValueSnak.getString(0).equals("novalue") : "Argument was not a SomeValueSnak.";

		int intPropertyId = jsonNoValueSnak.getInt(1);
		PropertyIdValue propertyId = this.getPropertyIdValue(intPropertyId);

		NoValueSnak result = this.factory.getNoValueSnak(propertyId);

		return result;
	}

	/**
	 * Converts the qualifiers from a JSON array to a list of value snaks.
	 * 
	 * @param jsonQualifiers
	 *            is a JSON array containing several snaks.
	 * @return a list of snaks corresponding to the given JSON array.
	 */
	private List<Snak> getQualifiers(JSONArray jsonQualifiers) {
		// effectively a list of value snaks

		List<Snak> result = new LinkedList<Snak>();
		for (int i = 0; i < jsonQualifiers.length(); i++) {
			try {
				JSONArray currentValueSnak = jsonQualifiers.getJSONArray(i);
				result.add(this.getSnak(currentValueSnak));
			} catch (JSONException e) {
				// skip the snak on error
				continue;
			}
		}

		return result;
	}

	/**
	 * Converts a JSON object into a mapping from site keys to
	 * SiteLink-instances.
	 * 
	 * @param jsonLinks
	 *            a JSON object representing the site links.
	 * @return A mapping with a String representing a site key e.g. "enwiki" as
	 *         key and a SiteLink-object as value.
	 * @throws JSONException
	 */
	private Map<String, SiteLink> getSiteLinks(JSONObject jsonLinks)
			throws JSONException {
		assert jsonLinks != null : "Link JSON object was null";

		// links are siteKey:{"name":string,"badges":[string] }
		// the siteKey is the key for the returned map

		Map<String, SiteLink> result = new HashMap<String, SiteLink>();

		@SuppressWarnings("unchecked")
		Iterator<String> linkIterator = jsonLinks.keys();

		while (linkIterator.hasNext()) {

			String siteKey = linkIterator.next();
			JSONObject currentLink = jsonLinks.getJSONObject(siteKey);
			String title = currentLink.getString("name");
			JSONArray badgeArray = currentLink.getJSONArray("badges");

			// convert badges to List<String>
			List<String> badges = new LinkedList<String>();
			for (int i = 0; i < badgeArray.length(); i++) {
				badges.add(badgeArray.getString(i));
			}

			// create the SiteLink instance
			SiteLink siteLink = factory.getSiteLink(title, siteKey,
					this.baseIri, badges);
			result.put(siteKey, siteLink);
		}

		return result;
	}

	/**
	 * Convert a JSON object representing the aliases of an item into a list of
	 * MonolingualTextValues.
	 * 
	 * @param aliases
	 *            a JSON object representing the aliases.
	 * @return a list of MonolingualTextValues. Might be empty but not null.
	 * @throws JSONException
	 */
	private List<MonolingualTextValue> getAliases(JSONObject aliases)
			throws JSONException {
		assert aliases != null : "Aliases JSON object was null";

		List<MonolingualTextValue> result = new LinkedList<MonolingualTextValue>();

		// aliases are of the form string:[string]

		@SuppressWarnings("unchecked")
		Iterator<String> keyIterator = aliases.keys();

		while (keyIterator.hasNext()) {
			String key = keyIterator.next();
			JSONArray aliasEntries = aliases.getJSONArray(key);

			// get all aliases for a certain language
			for (int i = 0; i < aliasEntries.length(); i++) {
				String aliasString = aliasEntries.getString(i);
				MonolingualTextValue element;
				element = this.factory
						.getMonolingualTextValue(aliasString, key);
				result.add(element);
			}
		}

		return result;
	}

	/**
	 * Converts a JSON object into the description format used by the WDTK data
	 * model.
	 * 
	 * @param descriptions
	 *            a JSON object representing the descriptions of an entity.
	 * @return a map representing descriptions. The key is the language
	 *         abbreviation, the value is the description in the language
	 *         represented by the key.
	 * @throws JSONException
	 */
	private List<MonolingualTextValue> getDescriptions(JSONObject descriptions)
			throws JSONException {
		assert descriptions != null : "Description JSON object was null";

		List<MonolingualTextValue> result = new LinkedList<MonolingualTextValue>();

		@SuppressWarnings("unchecked")
		Iterator<String> keyIterator = descriptions.keys();

		while (keyIterator.hasNext()) {
			String key = keyIterator.next();
			String desctiptionString = descriptions.getString(key);
			MonolingualTextValue element = this.factory
					.getMonolingualTextValue(desctiptionString, key);
			result.add(element);
		}

		return result;
	}

	/**
	 * Constructs the item id of a JSON object denoting an item.
	 * 
	 * @param jsonEntity
	 *            a JSON array containing information about the entity or
	 *            property. The array shoud have the structure ["item", itemId]
	 *            or ["property", propertyId].
	 * @return An item id value prefixed accordingly.
	 * @throws JSONException
	 *             if the entity does not contain an "item"-entry or the entry
	 *             is not followed by an integer denoting the item id.
	 */
	private ItemIdValue getItemId(JSONArray jsonEntity) throws JSONException {
		assert jsonEntity != null : "Entity JSONArray was null";
		assert jsonEntity.getString(0).equals("item") : "JSONArray did not denote an item id.";

		return this.getItemIdValue(jsonEntity.getInt(1));
	}

	/**
	 * Converts a JSON description of the labels into a mapping from language
	 * abbreviations to the labels in these languages.
	 * 
	 * @param labels
	 *            a JSON object containing the labels
	 * @return a mapping between language abbreviations to the label in the
	 *         referring language.
	 * @throws JSONException
	 *             if the iterator returned a non existing key. This could mean
	 *             the JSON object is broken (i.e. has no Strings as keys) or
	 *             something is wrong with the <i>org.json</i> JSON parser.
	 */
	private List<MonolingualTextValue> getLabels(JSONObject labels)
			throws JSONException {
		assert labels != null : "Label JSON was null";

		List<MonolingualTextValue> result = new LinkedList<MonolingualTextValue>();

		@SuppressWarnings("unchecked")
		Iterator<String> keyIterator = labels.keys();

		while (keyIterator.hasNext()) {
			String key = keyIterator.next();
			String labelString = labels.getString(key);
			MonolingualTextValue element = this.factory
					.getMonolingualTextValue(labelString, key);
			result.add(element);
		}

		return result;
	}

	public String getBaseIri() {
		return baseIri;
	}

	/**
	 * For the <i>baseIri</i> see also
	 * {@link org.wikidata.wdtk.datamodel.interfaces.ItemId}
	 * 
	 * @param baseIri
	 *            the new baseIRI to be set. If the given string is null,
	 *            nothing will be done.
	 */
	public void setBaseIri(String baseIri) {
		if (baseIri == null)
			return;
		this.baseIri = baseIri;
	}

	public String getItemPrefix() {
		return itemPrefix;
	}

	/**
	 * Sets the prefix to be prepended before an item id. For example if your
	 * items id number is "123" and you set the prefix to "X", the full item id
	 * would be "X123".
	 * 
	 * @param itemPrefix
	 *            is the prefix to be used. If the given value is null, the
	 *            prefix will default to "Q".
	 */
	public void setItemPrefix(String itemPrefix) {
		if (itemPrefix == null) {
			this.itemPrefix = "Q";
		} else {
			this.itemPrefix = itemPrefix;
		}
	}

	public String getPropertyPrefix() {
		return propertyPrefix;
	}

	/**
	 * Sets the prefix to be prepended before an property id. For example if
	 * your properties id number is "123" and you set the prefix to "X", the
	 * full property id would be "X123".
	 * 
	 * @param propertyPrefix
	 *            is the prefix to be used. If the given value is null, the
	 *            prefix will default to "P".
	 */
	public void setPropertyPrefix(String propertyPrefix) {
		if (propertyPrefix == null) {
			this.propertyPrefix = "P";
		} else {
			this.propertyPrefix = propertyPrefix;
		}
	}

}
