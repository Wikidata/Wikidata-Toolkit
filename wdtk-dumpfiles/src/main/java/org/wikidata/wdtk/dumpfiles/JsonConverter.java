package org.wikidata.wdtk.dumpfiles;

/*
 * #%L
 * Wikidata Toolkit Dump File Handling
 * %%
 * Copyright (C) 2014 Wikidata Toolkit Developers
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
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

// TODO logging support
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

	public static final String PREFIX_ITEM = "Q";
	public static final String PREFIX_PROPERTY = "P";

	// Keys that might occur in the documents
	private static final String KEY_LABEL = "label";
	private static final String KEY_ENTITY = "entity";
	private static final String KEY_DESCRIPTION = "description";
	private static final String KEY_ALIAS = "aliases";
	private static final String KEY_DATATYPE = "datatype";
	private static final String KEY_CLAIM = "claims";
	private static final String KEY_LINK = "links";

	private final DataObjectFactory factory;
	private String baseIri = "";
	private final MonolingualTextValueHandler mltvHandler;

	static final Logger logger = LoggerFactory.getLogger(JsonConverter.class);

	/**
	 * Creates a new instance of the JsonConverter. For the <i>baseIri</i> see
	 * also {@link org.wikidata.wdtk.datamodel.interfaces.ItemIdValue}.
	 * 
	 * @param baseIri
	 *            the base IRI to be used for entities
	 * @param factory
	 *            the DataObjectFactory to be used to construct objects of the
	 *            data model
	 */
	public JsonConverter(String baseIri, DataObjectFactory factory) {
		this.setBaseIri(baseIri);
		this.factory = factory;

		mltvHandler = new MonolingualTextValueHandler(this.factory);
	}

	/**
	 * Attempts to parse a given JSON object into an instance of
	 * PropertyDocument.
	 * 
	 * @param jsonObject
	 *            the JSON object to convert. Must represent a property document
	 *            and therefore contain the keys "entity", "label",
	 *            "description", "aliases" and "datatype".
	 * @param propertyIdString
	 *            is a string containing the id of the property provided by
	 *            external sources; if null is given, the information will be
	 *            extracted from the JSON
	 * @return the PropertyDocument as described in the JSON.
	 * @throws NullPointerException
	 *             if toConvert was null.
	 * @throws JSONException
	 *             if the JSON object did not contain a key it should have had.
	 */
	public PropertyDocument convertToPropertyDocument(JSONObject jsonObject,
			String propertyIdString) throws JSONException {

		if (jsonObject.length() == 0) { // if the JSON object is empty
			throw new JSONException("The JSON to convert was empty");
		}

		PropertyDocument result;

		PropertyIdValue propertyId;

		if (propertyIdString != null) {
			propertyId = this.getPropertyIdValue(propertyIdString);
		} else {
			propertyId = this.getPropertyIdFromTopLevel(jsonObject);
		}

		List<MonolingualTextValue> labels = this.getMltv(KEY_LABEL, jsonObject);

		List<MonolingualTextValue> descriptions = this.getMltv(KEY_DESCRIPTION,
				jsonObject);

		List<MonolingualTextValue> aliases = this
				.getMltv(KEY_ALIAS, jsonObject);

		String jsonDataTypeId = jsonObject.getString(KEY_DATATYPE);
		DatatypeIdValue datatypeId = this.getDatatypeIdValue(jsonDataTypeId);

		result = this.factory.getPropertyDocument(propertyId, labels,
				descriptions, aliases, datatypeId);
		return result;
	}

	/**
	 * Attempts to parse a given JSON object into an instance of ItemDocument.
	 * 
	 * @param jsonObject
	 *            the JSON object to convert. Must represent an item document
	 *            and therefore might contain the keys "entity", "label",
	 *            "description", "aliases", "claims" and "links".
	 * @param propertyIdString
	 *            is a string containing the id of the item provided by external
	 *            sources; if null is given, the information will be extracted
	 *            from the JSON
	 * @return the ItemDocument as described in the JSON.
	 * @throws NullPointerException
	 *             if toConvert was null.
	 * @throws JSONException
	 *             if the JSON object did not contain a key it should have had.
	 */
	public ItemDocument convertToItemDocument(JSONObject jsonObject,
			String itemIdString) throws JSONException, NullPointerException {

		if (jsonObject.length() == 0) { // if the JSON object is empty
			throw new JSONException("The JSON to convert was empty");
		}

		ItemIdValue itemId;
		if (itemIdString != null) {
			itemId = this.getItemIdValue(itemIdString);
		} else {
			itemId = this.getItemIdFromTopLevel(jsonObject);
		}
		List<MonolingualTextValue> labels = this.getMltv(KEY_LABEL, jsonObject);

		List<MonolingualTextValue> descriptions = this.getMltv(KEY_DESCRIPTION,
				jsonObject);

		List<MonolingualTextValue> aliases = this
				.getMltv(KEY_ALIAS, jsonObject);

		List<StatementGroup> statements = new LinkedList<>();
		if (jsonObject.has(KEY_CLAIM)) {
			JSONArray jsonStatements = jsonObject.getJSONArray(KEY_CLAIM);
			statements = this.getStatements(jsonStatements, itemId);
		}

		Map<String, SiteLink> siteLinks = new HashMap<>();

		if (jsonObject.has(KEY_LINK)) {
			JSONArray linkArray = jsonObject.optJSONArray(KEY_LINK);
			if (linkArray == null) {
				JSONObject jsonLinks = jsonObject.getJSONObject(KEY_LINK);
				siteLinks = this.getSiteLinks(jsonLinks);
			}
		}

		ItemDocument result = factory.getItemDocument(itemId, labels,
				descriptions, aliases, statements, siteLinks);
		return result;
	}

	/**
	 * Attempts to get the item id value from the given JSON-object. Note that
	 * in old dumps the entity is not an array but a string with appropriate
	 * prefix in lower case.
	 * 
	 * @param topLevel
	 *            is the JSON object describing the whole entity
	 * @return the items id as ItemIdValue
	 * @throws JSONException
	 *             if the topLevel does not contain the key "entity"
	 */
	private ItemIdValue getItemIdFromTopLevel(JSONObject topLevel)
			throws JSONException {

		if (!topLevel.has(KEY_ENTITY)) {
			throw new JSONException("No entity entry found.");
		}

		ItemIdValue itemId;
		JSONArray entityJsonArray = topLevel.optJSONArray(KEY_ENTITY);

		if (entityJsonArray != null) { // it is an array
			itemId = this.getItemIdValue(entityJsonArray);
		} else { // it is a String
			String stringItemId = topLevel.getString(KEY_ENTITY);
			itemId = this.getItemIdValue(stringItemId);
		}
		return itemId;
	}

	/**
	 * Attempts to get the property id value from the given JSON-object. Note
	 * that in old dumps the entity is not an array but a string with
	 * appropriate prefix in lower case.
	 * 
	 * @param topLevel
	 *            is the JSON object describing the whole entity
	 * @return the properties id as PropertyIdValue
	 * @throws JSONException
	 *             if the topLevel does not contain the key "entity"
	 */
	private PropertyIdValue getPropertyIdFromTopLevel(JSONObject topLevel)
			throws JSONException {

		if (!topLevel.has(KEY_ENTITY)) {
			throw new JSONException("No entity entry found.");
		}

		PropertyIdValue propertyId;
		JSONArray entityJsonArray = topLevel.optJSONArray(KEY_ENTITY);

		if (entityJsonArray != null) { // it is an array
			propertyId = this.getPropertyIdValue(entityJsonArray);
		} else { // it is a String
			String stringItemId = topLevel.getString(KEY_ENTITY);
			propertyId = this.getPropertyIdValue(stringItemId);
		}
		return propertyId;
	}

	/**
	 * Creates an ItemIdValue from a JSON array that represents an item in JSON.
	 * 
	 * @param jsonEntity
	 *            a JSON array containing information about the item; the array
	 *            should have the structure ["item", itemId] where itemId is an
	 *            integer
	 * @return the corresponding ItemIdValue
	 * @throws JSONException
	 *             if the entity does not contain an "item" entry or the id does
	 *             not have the correct format
	 */
	private ItemIdValue getItemIdValue(JSONArray jsonEntity)
			throws JSONException {

		String entityTypeIndicator = jsonEntity.getString(0);
		if (!entityTypeIndicator.equalsIgnoreCase("item")) {
			throw new JSONException("Entity type indicator \""
					+ entityTypeIndicator
					+ "\" did not match expected type indicator \"item\".");
		}

		int idValue = jsonEntity.getInt(1);
		return this.factory.getItemIdValue(PREFIX_ITEM + idValue, this.baseIri);
	}

	/**
	 * Creates a PropertyIdValue from a JSON array that represents a property in
	 * JSON.
	 * 
	 * @param jsonEntity
	 *            a JSON array containing information about the property; the
	 *            array should have the structure ["property", propertyId] where
	 *            propertyId is an integer
	 * @return the corresponding PropertyIdValue
	 * @throws JSONException
	 *             if the entity does not contain an "property" entry or the id
	 *             does not have the correct format
	 */
	private PropertyIdValue getPropertyIdValue(JSONArray jsonEntity)
			throws JSONException {

		String entityTypeIndicator = jsonEntity.getString(0);
		if (!entityTypeIndicator.equalsIgnoreCase("property")) {
			throw new JSONException("Entity type indicator \""
					+ entityTypeIndicator
					+ "\" did not match expected type indicator \"property\".");
		}

		int idValue = jsonEntity.getInt(1);
		return this.factory.getPropertyIdValue(PREFIX_PROPERTY + idValue,
				this.baseIri);
	}

	/**
	 * Creates an ItemIdValue from a string id given in JSON, which may have the
	 * form Q12345 or q12345. The lower case version is not a valid id but may
	 * occur in the JSON dump for historic reasons.
	 * 
	 * @param id
	 *            JSON string id of the item
	 * @return the corresponding ItemIdValue
	 * @throws JSONException
	 */
	private ItemIdValue getItemIdValue(String id) throws JSONException {
		try {
			return this.factory.getItemIdValue(id.toUpperCase(), this.baseIri);
		} catch (IllegalArgumentException e) { // invalid id format
			throw new JSONException(e);
		}
	}

	/**
	 * Creates a PropertyIdValue from a string id given in JSON, which may have
	 * the form P12345 or p12345. The lower case version is not a valid id but
	 * may occur in the JSON dump for historic reasons.
	 * 
	 * @param id
	 *            JSON string id of the property
	 * @return the corresponding PropertyIdValue
	 * @throws JSONException
	 */
	private PropertyIdValue getPropertyIdValue(String id) throws JSONException {
		try {
			return this.factory.getPropertyIdValue(id.toUpperCase(),
					this.baseIri);
		} catch (IllegalArgumentException e) { // invalid id format
			throw new JSONException(e);
		}
	}

	/**
	 * Converts a JSONObject into a list of mono-lingual text values. The object
	 * to be converted is the value associated with the given key in the top
	 * level object. So if there is a JSONObject <i>topLevel</i> and the key
	 * "label" are given, only the JSONObject found in the <i>topLevel</i> under
	 * the key "label" will be converted, not the whole <i>topLevel</i>.
	 * 
	 * MLTV is the abbreviation for MonoLingualTextValue.
	 * 
	 * @param key
	 *            is the key of the object to be converted in the topLevel. If
	 *            the key is not present or not an object an empty list will be
	 *            returned.
	 * @param topLevel
	 *            is the JSONObject that contains the object to be converted
	 *            under a given key.
	 * @return a list of extracted mono-lingual text values. Might be empty, but
	 *         not null.
	 */
	private List<MonolingualTextValue> getMltv(String key, JSONObject topLevel) {

		JSONObject toConvert = topLevel.optJSONObject(key);
		if (toConvert != null) {
			return this.mltvHandler.convertToMltv(toConvert);
		} // else…

		return new LinkedList<>();
	}

	/**
	 * Converts the given JSON array into a list of Reference-objects. A
	 * reference is an array of reference statements which in turn are value
	 * snaks. Invalid references will be skipped.
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
				Reference singleReference = this
						.getSingleReference(jsonSingleRef);
				result.add(singleReference);

			} catch (JSONException e) {
				// skip over invalid references
				continue;
			}

		}
		return result;
	}

	/**
	 * Converts a given JSON-array into a single Reference. The array must be
	 * taken from the JSON-array of references.
	 * 
	 * @param jsonSingleRef
	 *            is a JSON-array describing a single reference, containing
	 *            value snaks
	 * @return the appropriate reference
	 * @throws JSONException
	 *             in case the conversion of the value snaks fails
	 */
	private Reference getSingleReference(JSONArray jsonSingleRef)
			throws JSONException {
		List<ValueSnak> valueSnaks = new LinkedList<>();

		// process the reference statements
		// do not recover from broken snaks, skip the reference
		for (int j = 0; j < jsonSingleRef.length(); j++) {

			JSONArray jsonValueSnak = jsonSingleRef.getJSONArray(j);
			ValueSnak currentValueSnak = this.getValueSnak(jsonValueSnak);
			valueSnaks.add(currentValueSnak);
		}

		Reference singleReference = factory.getReference(valueSnaks);
		return singleReference;
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
		// assert jsonLinks != null : "Link JSON object was null";

		// links are siteKey:{"name":string,"badges":[string] }
		// the siteKey is the key for the returned map
		// or they are siteKey:string

		Map<String, SiteLink> result = new HashMap<String, SiteLink>();

		// TODO where to get the site IRI from
		String siteIri = "";

		@SuppressWarnings("unchecked")
		Iterator<String> linkIterator = jsonLinks.keys();

		while (linkIterator.hasNext()) {

			String title;
			List<String> badges;

			String siteKey = linkIterator.next();

			JSONObject currentLink = jsonLinks.optJSONObject(siteKey);

			if (currentLink != null) {

				title = currentLink.getString("name");
				JSONArray badgeArray = currentLink.getJSONArray("badges");
				badges = new ArrayList<>(badgeArray.length());

				// convert badges to List<String>
				for (int i = 0; i < badgeArray.length(); i++) {
					badges.add(badgeArray.getString(i));
				}
			} else {
				String stringLink = jsonLinks.optString(siteKey);
				if (stringLink != null) { // its a string
					title = stringLink;
					// initialize the badges as empty list
					// since they are needed for the factory
					badges = new ArrayList<>();
				} else { // none of the above, skip
					logger.info("Site link is neither a JSON object nor a String. Skipped.");
					continue;
				}
			}

			// create the SiteLink instance
			SiteLink siteLink = factory.getSiteLink(title, siteKey, siteIri,
					badges);
			result.put(siteKey, siteLink);
		}

		return result;
	}

	/**
	 * Converts a JSON array containing statements into a list of statement
	 * groups as represented by the WDTK data model.
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

		// assert jsonStatements != null : "statements JSON array was null";
		// structure is [{"m":object, "q":[], "g":string, "rank":int,
		// "refs":[…]},…]
		// "q" => qualifiers
		// "m" => main snak
		// "g" => statement id

		List<StatementGroup> result;
		List<Statement> statementsFromJson = new ArrayList<Statement>(
				jsonStatements.length());

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
	 * Creates a Claim object for the given entity id from the given JSON object
	 * (this is usually the JSON for encoding a whole Statement). A Claim
	 * consists of the EntityIdValue of the subject (given explicitly), the
	 * claim's main snak (given by a key "m" i JSON) and the claim's qualifiers
	 * (given by a key "q" in JSON).
	 * 
	 * @param currentStatement
	 *            a JSON object representing a whole statement from which the
	 *            claim is to be extracted
	 * @param subject
	 *            the subject to which this claim refers
	 * @return the corresponding Claim
	 * @throws JSONException
	 *             if the given JSON did not have the expected form
	 */
	private Claim getClaim(JSONObject currentStatement, EntityIdValue subject)
			throws JSONException {
		// get the main snak
		JSONArray jsonMainSnak = currentStatement.getJSONArray("m");
		Snak mainSnak = getSnak(jsonMainSnak);

		// get the qualifiers
		JSONArray jsonQualifiers = currentStatement.getJSONArray("q");
		List<Snak> qualifiers = this.getQualifiers(jsonQualifiers);

		// build it together
		return this.factory.getClaim(subject, mainSnak, qualifiers);
	}

	/**
	 * Transforms a statement rank from an integer representation to an
	 * enumerated value as used in the WDTK data model. <br/>
	 * The number 0 maps to DEPRECATED. <br/>
	 * The number 1 maps to NORMAL. <br/>
	 * The number 2 maps to PREFERRED. <br/>
	 * Other ranks are regarded as an error.
	 * 
	 * @param intRank
	 *            the rank as integer
	 * @return the corresponding StatementRank
	 */
	private StatementRank getStatementRank(int intRank) {

		switch (intRank) {
		case 0:
			return StatementRank.DEPRECATED;
		case 1:
			return StatementRank.NORMAL;
		case 2:
			return StatementRank.PREFERRED;
		default:
			throw new IllegalArgumentException("Unknown statement rank "
					+ intRank);
		}
	}

	/**
	 * Creates a list of ValueSnak objects from a JSON array as it is used to
	 * encode qualifiers.
	 * 
	 * @param jsonQualifiers
	 *            a JSON array containing several Snaks
	 * @return the corresponding list of Snaks
	 * @throws JSONException
	 *             if the given JSON did not have the expected form
	 */
	private List<Snak> getQualifiers(JSONArray jsonQualifiers)
			throws JSONException {
		List<Snak> result = new ArrayList<Snak>(jsonQualifiers.length());
		for (int i = 0; i < jsonQualifiers.length(); i++) {
			JSONArray currentValueSnak = jsonQualifiers.getJSONArray(i);
			result.add(this.getSnak(currentValueSnak));
		}

		return result;
	}

	/**
	 * Creates a Snak from the given JSON array. This might either be a
	 * ValueSnak, NoValueSnak or SomeValueSnak.
	 * 
	 * @param jsonSnak
	 *            is a JSON array representing a Snak
	 * @return the corresponding Snak
	 * @throws JSONException
	 *             if the given JSON did not have the expected form
	 */
	private Snak getSnak(JSONArray jsonSnak) throws JSONException {
		switch (jsonSnak.getString(0)) {
		case "value":
			return this.getValueSnak(jsonSnak);
		case "somevalue":
			return this.getSomeValueSnak(jsonSnak);
		case "novalue":
			return this.getNoValueSnak(jsonSnak);
		default:
			throw new JSONException("Unknown snack type: "
					+ jsonSnak.getString(0));
		}
	}

	/**
	 * Creates a NoValueSnak from the given JSON array. The JSON should have the
	 * form as in the following example:
	 * 
	 * <pre>
	 * ["novalue",40]
	 * </pre>
	 * 
	 * In this example, 40 is the id of the property the snak refers to.
	 * 
	 * @param jsonNoValueSnak
	 *            is a JSON array representing a NoValueSnak
	 * @return the corresponding NoValueSnak
	 * @throws JSONException
	 *             if the given JSON did not have the expected form
	 */
	private NoValueSnak getNoValueSnak(JSONArray jsonNoValueSnak)
			throws JSONException {

		int intPropertyId = jsonNoValueSnak.getInt(1);
		PropertyIdValue propertyId = this.getPropertyIdValue(PREFIX_PROPERTY
				+ intPropertyId);

		return this.factory.getNoValueSnak(propertyId);
	}

	/**
	 * Creates a SomeValueSnak from the given JSON array. The JSON should have
	 * the form as in the following example:
	 * 
	 * <pre>
	 * ["somevalue",22]
	 * </pre>
	 * 
	 * In this example, 22 is the id of the property the snak refers to.
	 * 
	 * @param jsonSomeValueSnak
	 *            is a JSON array representing a SomeValueSnak
	 * @return the corresponding SomeValueSnak
	 * @throws JSONException
	 *             if the given JSON did not have the expected form
	 */
	private SomeValueSnak getSomeValueSnak(JSONArray jsonSomeValueSnak)
			throws JSONException {

		int intPropertyId = jsonSomeValueSnak.getInt(1);
		PropertyIdValue propertyId = this.getPropertyIdValue(PREFIX_PROPERTY
				+ intPropertyId);

		return this.factory.getSomeValueSnak(propertyId);
	}

	/**
	 * Creates a ValueSnak from the given JSON array. The JSON should have the
	 * form as in the following example:
	 * 
	 * <pre>
	 * ["value", 22, "wikibase-entityid", jsonForValue]
	 * </pre>
	 * 
	 * In this example, 22 is the id of the property the snak refers to, and
	 * jsonForValue should be replaced by the JSON encoding of a value of the
	 * given type "wikibase-entityid".
	 * 
	 * @param jsonValueSnak
	 *            is a JSON array representing a ValueSnak
	 * @return the corresponding ValueSnak
	 * @throws JSONException
	 *             if the given JSON did not have the expected form
	 */
	private ValueSnak getValueSnak(JSONArray jsonValueSnak)
			throws JSONException {

		// get the property id value
		int intPropertyId = jsonValueSnak.getInt(1);
		PropertyIdValue propertyIdValue = this
				.getPropertyIdValue(PREFIX_PROPERTY + intPropertyId);

		// get the value
		String valueType = jsonValueSnak.getString(2);
		Value value;
		switch (valueType) {
		case "time":
			value = this.getTimeValue(jsonValueSnak.getJSONObject(3));
			break;
		case "wikibase-entityid":
			value = this.getEntityIdValue(jsonValueSnak.getJSONObject(3));
			break;
		case "string":
			value = this.getStringValue(jsonValueSnak.getString(3));
			break;
		case "globecoordinate":
			value = this.getGlobeCoordinatesValue(jsonValueSnak
					.getJSONObject(3));
			break;
		case "quantity":
			value = this.getQuantityValue(jsonValueSnak.getJSONObject(3));
			break;
		default:
			throw new JSONException("Unknown value type " + valueType
					+ " in value snak JSON");
		}

		// put it all together
		return this.factory.getValueSnak(propertyIdValue, value);
	}

	/**
	 * Creates a DatatypeIdValue for the given datatype id.
	 * 
	 * @param jsonDataTypeId
	 *            the id of the datatype
	 * @return the corresponding DatatypeIdValue
	 */
	private DatatypeIdValue getDatatypeIdValue(String jsonDataTypeId) {
		// FIXME This is not correct. The datatype id in JSON is not the
		// datatype that we need to use in the datamodel.
		return this.factory.getDatatypeIdValue(jsonDataTypeId);
	}

	/**
	 * Create a QuantityValue from a given JSON object. The JSON should have the
	 * form as in the following example:
	 * 
	 * <pre>
	 * {"amount":"+34196",
	 *  "unit":"1",
	 *  "upperBound":"+34197",
	 *  "lowerBound":"+34195"
	 * }
	 * </pre>
	 * 
	 * The unit is currently ignored since it is not clear yet how exactly it
	 * will work when supported by Wikibase.
	 * 
	 * @param jsonQuantityValue
	 *            a JSON object representing a QuantityValue
	 * @return the corresponding QuantityValue
	 * @throws JSONException
	 *             if the given JSON did not have the expected form
	 */
	private QuantityValue getQuantityValue(JSONObject jsonQuantityValue)
			throws JSONException {
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
	 * Creates a GlobeCordinatesValue from a given JSON object. The JSON should
	 * have the form as in the following example:
	 * 
	 * <pre>
	 * {"latitude":51.835,
	 *  "longitude":10.785277777778,
	 *  "altitude":null,
	 *  "precision":0.00027777777777778,
	 *  "globe":"http:\/\/www.wikidata.org\/entity\/Q2"
	 * }
	 * </pre>
	 * 
	 * Altitude is present for historical reasons. It has never been supported
	 * by Wikibase and will vanish altogether in the future. Only specific
	 * numbers are allowed as precisions; all others will be rounded to the next
	 * greater precision, which might lead to conversion errors. If the
	 * precision is not given, this implementation defaults to the maximal
	 * precision (milli-arcsecond). If the globe is not given, this
	 * implementation defaults to Earth.
	 * 
	 * @param jsonGlobeCoordinate
	 *            a JSON object representing a GlobeCoordinatesValue
	 * @return an appropriate GlobeCoordinatesValue
	 * @throws JSONException
	 *             if the given JSON did not have the expected form
	 */
	private GlobeCoordinatesValue getGlobeCoordinatesValue(
			JSONObject jsonGlobeCoordinate) throws JSONException {

		// convert latitude and longitude from double (degrees) to long
		// (nanodegrees)
		double doubleLatitude = jsonGlobeCoordinate.getDouble("latitude");
		long latitude = (long) (doubleLatitude * GlobeCoordinatesValue.PREC_DEGREE);

		double doubleLongitude = jsonGlobeCoordinate.getDouble("longitude");
		long longitude = (long) (doubleLongitude * GlobeCoordinatesValue.PREC_DEGREE);

		// get the precision
		long precision;

		if (jsonGlobeCoordinate.isNull("precision")) {
			precision = GlobeCoordinatesValue.PREC_MILLI_ARCSECOND;
		} else {
			Double doublePrecision = jsonGlobeCoordinate.getDouble("precision");

			// determine precision by comparing intervals, since exact
			// comparisons of long and double do not work reliably
			if (doublePrecision > 1.0) {
				precision = GlobeCoordinatesValue.PREC_TEN_DEGREE;
			} else if (doublePrecision > 0.1) {
				precision = GlobeCoordinatesValue.PREC_DEGREE;
			} else if (doublePrecision > 0.016666666666667) {
				precision = GlobeCoordinatesValue.PREC_DECI_DEGREE;
			} else if (doublePrecision > 0.01) {
				precision = GlobeCoordinatesValue.PREC_ARCMINUTE;
			} else if (doublePrecision > 0.001) {
				precision = GlobeCoordinatesValue.PREC_CENTI_DEGREE;
			} else if (doublePrecision > 0.00027777777777778) {
				precision = GlobeCoordinatesValue.PREC_MILLI_DEGREE;
			} else if (doublePrecision > 0.0001) {
				precision = GlobeCoordinatesValue.PREC_MILLI_DEGREE;
			} else if (doublePrecision > 0.00002777777777778) {
				precision = GlobeCoordinatesValue.PREC_HUNDRED_MICRO_DEGREE;
			} else if (doublePrecision > 0.00001) {
				precision = GlobeCoordinatesValue.PREC_ARCSECOND;
			} else if (doublePrecision > 0.00000277777777778) {
				precision = GlobeCoordinatesValue.PREC_TEN_MICRO_DEGREE;
			} else if (doublePrecision > 0.000001) {
				precision = GlobeCoordinatesValue.PREC_CENTI_ARCSECOND;
			} else if (doublePrecision > 0.00000027777777778) {
				precision = GlobeCoordinatesValue.PREC_MILLI_DEGREE;
			} else {
				precision = GlobeCoordinatesValue.PREC_MILLI_ARCSECOND;
			}
		}

		// get the globeIri
		String globeIri = jsonGlobeCoordinate.optString("globe",
				GlobeCoordinatesValue.GLOBE_EARTH);

		GlobeCoordinatesValue result = this.factory.getGlobeCoordinatesValue(
				latitude, longitude, precision, globeIri);

		return result;
	}

	/**
	 * Creates a StringValue for a given string.
	 * <p>
	 * Currently this just calls the corresponding factory method, but it could
	 * change in the future if the JSON encoding diverges from the data model in
	 * any way.
	 * 
	 * @param string
	 * @return corresponding StringValue
	 */
	private StringValue getStringValue(String string) {
		return this.factory.getStringValue(string);
	}

	/**
	 * Creates an EntityIdValue from a given JSON object. The JSON should have
	 * the form as in the following example:
	 * 
	 * <pre>
	 * {"entity-type":"item",
	 *  "numeric-id":842256}
	 * </pre>
	 * 
	 * @param jsonObject
	 *            an JSON object denoting an entity id
	 * @return the corresponding EntityIdValue
	 * @throws JSONException
	 *             if the given JSON did not have the expected form
	 */
	private EntityIdValue getEntityIdValue(JSONObject jsonObject)
			throws JSONException {

		String entityType = jsonObject.getString("entity-type");
		int entityId = jsonObject.getInt("numeric-id");

		// check the entity type
		switch (entityType) {
		case "item":
			return this.getItemIdValue(PREFIX_ITEM + entityId);
		case "property":
			// using properties as values is planned for the future,
			// but is not supported by Wikibase as of March 2014
			return this.getPropertyIdValue(PREFIX_PROPERTY + entityId);
		default:
			throw new JSONException("Unknown entity type " + entityType
					+ " in entity id value JSON.");
		}
	}

	/**
	 * Creates a TimeValue from a given JSON object. The JSON should have the
	 * form as in the following example:
	 * 
	 * <pre>
	 * {"time":"+00000002012-06-30T00:00:00Z",
	 * "timezone":0,
	 * "before":0,
	 * "after":0,
	 * "precision":11,
	 * "calendarmodel":"http:\/\/www.wikidata.org\/entity\/Q1985727"}
	 * </pre>
	 * 
	 * @param jsonTimeValue
	 *            an JSON object denoting a time value
	 * @return the corresponding TimeValue
	 * @throws JSONException
	 *             if the given JSON did not have the expected form
	 */
	private TimeValue getTimeValue(JSONObject jsonTimeValue)
			throws JSONException {

		String stringTime = jsonTimeValue.getString("time");
		String[] substrings = stringTime.split("(?<!\\A)[\\-\\:TZ]");

		// get the components of the date
		long year = Long.parseLong(substrings[0]);
		byte month = Byte.parseByte(substrings[1]);
		byte day = Byte.parseByte(substrings[2]);
		byte hour = Byte.parseByte(substrings[3]);
		byte minute = Byte.parseByte(substrings[4]);
		byte second = Byte.parseByte(substrings[5]);

		// get the precision and tolerances
		byte precision = (byte) jsonTimeValue.getInt("precision");
		int beforeTolerance = jsonTimeValue.getInt("before");
		int afterTolerance = jsonTimeValue.getInt("after");

		// get the time zone offset
		int timezoneOffset = jsonTimeValue.getInt("timezone");

		// get the calendar model
		String calendarModel = jsonTimeValue.getString("calendarmodel");

		return this.factory.getTimeValue(year, month, day, hour, minute,
				second, precision, beforeTolerance, afterTolerance,
				timezoneOffset, calendarModel);
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
		Validate.notNull(baseIri);
		this.baseIri = baseIri;
	}

}
