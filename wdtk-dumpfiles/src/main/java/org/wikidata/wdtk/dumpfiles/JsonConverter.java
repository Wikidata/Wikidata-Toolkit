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
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
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

// TODO introduce a verbose-flag to enable/disable logging
// TODO make item and property prefixes variable, 
// in case someone does not use "Q" and "P"
// TODO move MonolingualTextValue inlines into a method
// TODO complete assertions
// TODO add @link to documentation where needed
// TODO permanent: check if documentation is up-to-date
/**
 * This class provides methods to convert dump-file JSON objects into
 * representations according to the WDTK data model. Since the converted JSON
 * normally belongs to the same domain, the base IRI is represented as an
 * attribute.
 * 
 * @author Fredo Erxleben
 * 
 */
public class JsonConverter {

	private DataObjectFactory factory = new DataObjectFactoryImpl();
	private String baseIri = "";

	/**
	 * For the <i>baseIri</i> see also
	 * {@link org.wikidata.wdtk.datamodel.interfaces.ItemId}
	 * 
	 * @param baseIri
	 *            the initial IRI to be used for the processed JSON.
	 */
	public JsonConverter(String baseIri) {
		this.setBaseIri(baseIri);
	}

	/**
	 * Attempts to parse a given JSON object into an instance of ItemRecord.
	 * 
	 * @param toConvert
	 *            the JSON object to convert. Must represent an item record.
	 * @param baseIri
	 *            he first part of the IRI of the site this belongs to.
	 * @return the ItemRecord parsed from JSON. Might be <b>null</b>.
	 * @throws NullPointerException
	 *             if toParse was null.
	 * @throws JSONException
	 *             if the JSON object did not contain a key it should have had.
	 */
	public ItemDocument convertToItemRecord(JSONObject toConvert)
			throws JSONException, NullPointerException {

		// initialize variables for the things we need to get
		// TODO check if it would not be better to initialize…
		// …with empty maps/lists
		ItemDocument result = null;
		ItemIdValue itemId = null;
		List<MonolingualTextValue> labels = null;
		List<MonolingualTextValue> descriptions = null;
		List<MonolingualTextValue> aliases = null;
		List<StatementGroup> statements = null;
		Map<String, SiteLink> siteLinks = null;

		// sanity check
		if (toConvert == null) {
			throw new NullPointerException();
		}

		if (toConvert.length() == 0) { // if the JSON object is empty
			throw new JSONException("The JSON to convert was empty");
		}
		// get the item Id
		JSONArray jsonEntity = toConvert.getJSONArray("entity");
		itemId = this.getItemId(jsonEntity);

		// get the labels
		JSONObject jsonLabels = toConvert.getJSONObject("label");
		labels = this.getLabels(jsonLabels);

		// get the description
		JSONObject jsonDescriptions = toConvert.getJSONObject("description");
		descriptions = this.getDescriptions(jsonDescriptions);

		// get the aliases
		JSONObject jsonAliases = toConvert.getJSONObject("aliases");
		aliases = this.getAliases(jsonAliases);

		// get the statements
		JSONArray jsonStatements = toConvert.getJSONArray("claims");
		statements = this.getStatements(jsonStatements, itemId);

		// get the site links
		JSONObject jsonLinks = toConvert.getJSONObject("links");
		siteLinks = this.getSiteLinks(jsonLinks);

		// now put it all together
		result = factory.getItemDocument(itemId, labels, descriptions, aliases,
				statements, siteLinks);
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
	 * 
	 * @param jsonReferences
	 * @return
	 * @throws JSONException
	 */
	private List<? extends Reference> getReferences(JSONArray jsonReferences)
			throws JSONException {
		// References are [singeRef]
		// singleRef are [refStatements]
		// refStatements are value snaks

		List<Reference> result = new LinkedList<>();

		// process the single references
		for (int i = 0; i < jsonReferences.length(); i++) {
			JSONArray jsonSingleRef = jsonReferences.getJSONArray(i);
			List<ValueSnak> valueSnaks = new LinkedList<>();

			// process the reference statements
			for (int j = 0; j < jsonSingleRef.length(); j++) {
				JSONArray jsonValueSnak = jsonSingleRef.getJSONArray(j);
				ValueSnak currentValueSnak = this.getValueSnak(jsonValueSnak);
				valueSnaks.add(currentValueSnak);
			}

			Reference singleReference = factory.getReference(valueSnaks);
			result.add(singleReference);
		}
		return result;
	}

	/**
	 * 
	 * @param jsonValueSnak
	 * @return
	 * @throws JSONException
	 */
	private ValueSnak getValueSnak(JSONArray jsonValueSnak)
			throws JSONException {
		// a value snak is
		// ["value", propertyID, value-type, value]

		assert jsonValueSnak != null : "jsonValueSnak was null";
		assert jsonValueSnak.getString(0).equals("value") : "given JSON was not a value snak";

		ValueSnak result;

		// get the property id
		String id = "P" + jsonValueSnak.getInt(1);
		PropertyIdValue propertyId = factory.getPropertyIdValue(id,
				this.baseIri);

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
	 * 
	 * @param jsonQuantityValue
	 * @return
	 * @throws JSONException
	 */
	private QuantityValue getQuantityValue(JSONObject jsonQuantityValue)
			throws JSONException {
		// example:
		// {"amount":"+34196",
		// "unit":"1",
		// "upperBound":"+34197",
		// "lowerBound":"+34195"}
		// TODO ignore unit?

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
	 * 
	 * @param jsonGlobeCoordinate
	 * @return
	 * @throws JSONException
	 */
	private GlobeCoordinatesValue getGlobeCoordinatesValue(
			JSONObject jsonGlobeCoordinate) throws JSONException {
		// example:
		// {"latitude":51.835,
		// "longitude":10.785277777778,
		// "altitude":null,
		// "precision":0.00027777777777778,
		// "globe":"http:\/\/www.wikidata.org\/entity\/Q2"}
		// NOTE as for now, ignore "altitude".
		// The key will be reviewed in the future.

		long latitude = jsonGlobeCoordinate.getLong("latitude");
		long longitude = jsonGlobeCoordinate.getLong("longitiude");
		long precision = jsonGlobeCoordinate.getLong("precision");
		String globeIri = jsonGlobeCoordinate.getString("globe");

		GlobeCoordinatesValue result = this.factory.getGlobeCoordinatesValue(
				latitude, longitude, precision, globeIri);

		return result;
	}

	/**
	 * 
	 * @param string
	 * @return
	 */
	private StringValue getStringIdValue(String string) {
		// NOTE I decided against inlining, so
		// if the StringValue changes somehow in the future
		// one has only to change this method
		return this.factory.getStringValue(string);
	}

	/**
	 * 
	 * @param jsonObject
	 * @return
	 * @throws JSONException
	 */
	private EntityIdValue getEntityIdValue(JSONObject jsonObject)
			throws JSONException {
		// example:
		// {"entity-type":"item",
		// "numeric-id":842256}
		// TODO will there be any other entity-type then "item"?

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
	 * 
	 * @param intValue
	 * @return
	 */
	private ItemIdValue getItemIdValue(int intValue) {
		String id = "Q" + intValue;
		return this.factory.getItemIdValue(id, this.baseIri);

	}

	/**
	 * 
	 * @param jsonTimeValue
	 * @return
	 * @throws JSONException
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
		// TODO test regex

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
	 * Gets the claim ftom a statement in JSON. A Claim consists out of the
	 * EntityIdValue of the subject, the subjects main snak and its qualifiers.
	 * 
	 * @param currentStatement
	 *            a JSON object representing a whole statement from which the
	 *            claim is to be extracted.
	 * @return
	 * @throws JSONException
	 *             when a required key was not found or the snak type could not
	 *             be identyfied.
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
		Claim result = factory.getClaim(subject, mainSnak, qualifiers);
		return result;
	}

	/**
	 * 
	 * @param jsonMainSnak
	 * @return
	 * @throws JSONException
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
	 * 
	 * @param jsonSomeValueSnak
	 * @return
	 * @throws JSONException
	 */
	private SomeValueSnak getSomeValueSnak(JSONArray jsonSomeValueSnak)
			throws JSONException {
		// example:
		// ["somevalue",22], where P22 is the property "father"

		// TODO documentation

		assert jsonSomeValueSnak != null : "jsonSomeValueSnak was null.";
		assert jsonSomeValueSnak.getString(0).equals("somevalue") : "Argument was not a SomeValueSnak.";

		int intPropertyId = jsonSomeValueSnak.getInt(1);
		String id = "P" + intPropertyId;
		PropertyIdValue propertyId = this.factory.getPropertyIdValue(id,
				this.baseIri);

		SomeValueSnak result = this.factory.getSomeValueSnak(propertyId);

		return result;
	}

	/**
	 * 
	 * @param jsonNoValueSnak
	 * @return
	 * @throws JSONException
	 */
	private NoValueSnak getNoValueSnak(JSONArray jsonNoValueSnak)
			throws JSONException {
		// example:
		// ["novalue",40], where P40 is the property "children"
		// TODO documentation

		assert jsonNoValueSnak != null : "jsonSomeValueSnak was null.";
		assert jsonNoValueSnak.getString(0).equals("novalue") : "Argument was not a SomeValueSnak.";

		int intPropertyId = jsonNoValueSnak.getInt(1);
		String id = "P" + intPropertyId;
		PropertyIdValue propertyId = this.factory.getPropertyIdValue(id,
				this.baseIri);

		NoValueSnak result = this.factory.getNoValueSnak(propertyId);

		return result;
	}

	/**
	 * 
	 * @param jsonQualifiers
	 * @return
	 * @throws JSONException
	 */
	private List<Snak> getQualifiers(JSONArray jsonQualifiers)
			throws JSONException {
		// example:
		// "q":[
		// ["value",585,
		// "time",{
		// "time":"+00000002012-06-30T00:00:00Z",
		// "timezone":0,"before":0,"after":0,
		// "precision":11,
		// "calendarmodel":"http:\/\/www.wikidata.org\/entity\/Q1985727"}
		// ]
		// ]
		// effectively a list of value snaks

		List<Snak> result = new LinkedList<Snak>();
		for (int i = 0; i < jsonQualifiers.length(); i++) {
			JSONArray currentValueSnak = jsonQualifiers.getJSONArray(i);
			// TODO skip conversion attempt on exception
			result.add(this.getSnak(currentValueSnak));
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
				element = factory.getMonolingualTextValue(aliasString, key);
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
			MonolingualTextValue element = factory.getMonolingualTextValue(
					desctiptionString, key);
			result.add(element);
		}

		return result;
	}

	/**
	 * Constructs the item id of a JSON object denoting an item.
	 * 
	 * @param entity
	 *            a JSON array containing information about the entity.
	 * @throws JSONException
	 *             if the entity does not contain an "item"-entry or the entry
	 *             is not followed by an integer denoting the item id.
	 */
	private ItemIdValue getItemId(JSONArray entity) throws JSONException {
		assert entity != null : "Entity JSONArray was null";

		// TODO review this!
		ItemIdValue itemId;
		String id = null;

		for (int i = 0; i < entity.length(); i++) {
			if (entity.getString(i).equals("item")) {
				// the next thing after "item" should be the item id number
				id = "Q" + entity.getInt(i + 1);
			}
		}

		itemId = factory.getItemIdValue(id, baseIri);
		return itemId;
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
			MonolingualTextValue element = factory.getMonolingualTextValue(
					labelString, key);
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

}
