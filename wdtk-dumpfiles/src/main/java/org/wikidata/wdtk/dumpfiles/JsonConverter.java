package org.wikidata.wdtk.dumpfiles;

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

	/**
	 * Creates a new instance of the JsonConverter. For the <i>baseIri</i> see
	 * also {@link org.wikidata.wdtk.datamodel.interfaces.ItemId} The item
	 * prefix is "Q". The property prefix is "P". If the given factory is
	 * <i>null</i> the
	 * {@link org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl}
	 * will be used.
	 * 
	 * @param baseIri
	 *            the initial IRI to be used for the processed JSON
	 * @param factory
	 *            is the DataObjectFactory to be used upon construction of the
	 *            objects of the data model
	 */
	public JsonConverter(String baseIri, DataObjectFactory factory) {
		this.setBaseIri(baseIri);

		if (factory == null) {
			this.factory = new DataObjectFactoryImpl();
		} else {
			this.factory = factory;
		}
		
		mltvHandler = new MonolingualTextValueHandler(
				this.factory);
	}

	/**
	 * Attempts to parse a given JSON object into an instance of
	 * PropertyDocument.
	 * 
	 * @param jsonObject
	 *            the JSON object to convert. Must represent a property document
	 *            and therefore contain the keys "entity", "label",
	 *            "description", "aliases" and "datatype".
	 * @return the PropertyDocument as described in the JSON.
	 * @throws NullPointerException
	 *             if toConvert was null.
	 * @throws JSONException
	 *             if the JSON object did not contain a key it should have had.
	 */
	public PropertyDocument convertToPropertyDocument(JSONObject jsonObject)
			throws JSONException {

		if (jsonObject.length() == 0) { // if the JSON object is empty
			throw new JSONException("The JSON to convert was empty");
		}

		PropertyDocument result;

		PropertyIdValue propertyId = (PropertyIdValue) this
				.getEntityFromTopLevel(jsonObject, DocumentType.PROPERTY);

		List<MonolingualTextValue> labels = this.getMltv(KEY_LABEL, jsonObject);

		List<MonolingualTextValue> descriptions = this.getMltv(KEY_DESCRIPTION,
				jsonObject);

		List<MonolingualTextValue> aliases = this
				.getMltv(KEY_ALIAS, jsonObject);

		String jsonDataTypeId = jsonObject.getString(KEY_DATATYPE);
		DatatypeIdValue datatypeId = this.getDataTypeId(jsonDataTypeId);

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
	 * @return the ItemDocument as described in the JSON.
	 * @throws NullPointerException
	 *             if toConvert was null.
	 * @throws JSONException
	 *             if the JSON object did not contain a key it should have had.
	 */
	public ItemDocument convertToItemDocument(JSONObject jsonObject)
			throws JSONException, NullPointerException {

		if (jsonObject.length() == 0) { // if the JSON object is empty
			throw new JSONException("The JSON to convert was empty");
		}

		ItemIdValue itemId = (ItemIdValue) this.getEntityFromTopLevel(
				jsonObject, DocumentType.ITEM);

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
	 * Attempts to get the entity id value from the given JSON-object. Note that
	 * in old dumps the entity is not an array but a string with appropriate
	 * prefix in lower case.
	 * 
	 * @param topLevel
	 *            is the JSON object describing the whole entity
	 * @param docType
	 *            describes if the entity is a property or an item
	 * @return the entities id as EntityIdValue; this should be casted into the
	 *         appropriate type by the caller
	 * @throws JSONException
	 *             if the topLevel does not contain the key "entity"
	 */
	private EntityIdValue getEntityFromTopLevel(JSONObject topLevel,
			DocumentType docType) throws JSONException {

		if (!topLevel.has(KEY_ENTITY)) {
			throw new JSONException("No entity entry found.");
		}

		EntityIdValue entityId;
		JSONArray entityJsonArray = topLevel.optJSONArray(KEY_ENTITY);

		if (entityJsonArray != null) { // it is an array
			entityId = this.getEntityId(entityJsonArray, docType);
		} else { // it is a String
			String stringItemId = topLevel.getString(KEY_ENTITY);
			entityId = this.getEntityId(stringItemId, docType);
		}
		return entityId;
	}

	/**
	 * Constructs the item id of a JSON array denoting an entity. The type will
	 * be determined by the document type passed to the method.
	 * 
	 * @param jsonEntity
	 *            a JSON array containing information about the entity or
	 *            property. The array shoud have the structure ["item", itemId]
	 *            or ["property", propertyId].
	 * @param docType
	 *            is the type of the document containing the entity-key.
	 * @return A subclass of EntityIdValue prefixed accordingly. If the document
	 *         type is DocumentType.ITEM the result will be an ItemIdValue. If
	 *         the document type is DocumentType.PROPERTY the result will be an
	 *         PropertyIdValue.
	 * @throws JSONException
	 *             if the entity does not contain an "item"- or "property"-entry
	 *             or the entry is not followed by an integer denoting the
	 *             entity id or the document type is unknown.
	 */
	private EntityIdValue getEntityId(JSONArray jsonEntity, DocumentType docType)
			throws JSONException {

		String expectedTypeIndicator;
		String prefix;

		switch (docType) {
		case ITEM:
			expectedTypeIndicator = "item";
			prefix = PREFIX_ITEM;
			break;
		case PROPERTY:
			expectedTypeIndicator = "property";
			prefix = PREFIX_PROPERTY;
			break;
		default:
			throw new IllegalArgumentException("Unsupported document type");
		}

		String entityTypeIndicator = jsonEntity.getString(0);
		if (!entityTypeIndicator.equalsIgnoreCase(expectedTypeIndicator)) {
			throw new JSONException("Entity type indicator \""
					+ entityTypeIndicator
					+ "\" did not match expected type indicator \""
					+ expectedTypeIndicator + "\".");
		}

		int idValue = jsonEntity.getInt(1);
		String fullId = prefix + idValue;

		switch (docType) {
		case ITEM:
			return this.factory.getItemIdValue(fullId, this.baseIri);
		case PROPERTY:
			return this.factory.getPropertyIdValue(fullId, this.baseIri);
			// Omitted default, this can never happen.
		}

		return null;
	}

	/**
	 * Constructs the entity id of a string denoting an entity. The type will be
	 * determined by the document type passed to the method.
	 * 
	 * @param id
	 *            a string containing information about the entity or property.
	 *            It should have the form prefix + id, where the prefix is
	 *            either "P" for properties or "Q" for items. The prefix is not
	 *            case sensitive. The id should be a numeric string.
	 * @param docType
	 *            is the type of the document containing the entity-key.
	 * @return A subclass of EntityIdValue prefixed accordingly. If the document
	 *         type is DocumentType.ITEM the result will be an ItemIdValue. If
	 *         the document type is DocumentType.PROPERTY the result will be an
	 *         PropertyIdValue.
	 * @throws JSONException
	 *             if the prefix of the id-string did not match the document
	 *             type or the document type is unknown.
	 */
	private EntityIdValue getEntityId(String id, DocumentType docType)
			throws JSONException {

		String expectedPrefix;

		switch (docType) {
		case ITEM:
			expectedPrefix = PREFIX_ITEM;
			break;
		case PROPERTY:
			expectedPrefix = PREFIX_PROPERTY;
			break;
		default:
			throw new JSONException("Unsupported document type");
		}

		String upperCase = id.toUpperCase();
		if (!upperCase.startsWith(expectedPrefix)) {
			throw new JSONException("Entity identifier \"" + id
					+ "\" did not start with expected prefix \""
					+ expectedPrefix + "\".");
		}

		EntityIdValue result = null;
		switch (docType) {
		case ITEM:
			result = this.factory.getItemIdValue(upperCase, this.baseIri);
			break;
		case PROPERTY:
			result = this.factory.getPropertyIdValue(upperCase, this.baseIri);
			break;
		// Omitted default, this can never happen.
		}

		return result;
	}

	/**
	 * Constructs the entity id of an integer denoting an entity. The type will
	 * be determined by the document type passed to the method.
	 * 
	 * @param jsonEntity
	 *            a non-zero integer.
	 * @param docType
	 *            is the type of the document containing the entity-key.
	 * @return A subclass of EntityIdValue prefixed accordingly. If the document
	 *         type is DocumentType.ITEM the result will be an ItemIdValue. If
	 *         the document type is DocumentType.PROPERTY the result will be an
	 *         PropertyIdValue.
	 * @throws JSONException
	 *             if the document type is unknown.
	 */
	private EntityIdValue getEntityId(int entityId, DocumentType docType)
			throws JSONException {

		String prefix;

		switch (docType) {
		case ITEM:
			prefix = PREFIX_ITEM;
			break;
		case PROPERTY:
			prefix = PREFIX_PROPERTY;
			break;
		default:
			throw new JSONException("Unsupported document type");
		}

		EntityIdValue result = null;
		switch (docType) {
		case ITEM:
			result = this.factory.getItemIdValue(prefix + entityId,
					this.baseIri);
			break;
		case PROPERTY:
			result = this.factory.getPropertyIdValue(prefix + entityId,
					this.baseIri);
			break;
		// Omitted default, this can never happen.
		}

		return result;
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
			List<String> badges = new ArrayList<String>();

			String siteKey = linkIterator.next();

			JSONObject currentLink = jsonLinks.optJSONObject(siteKey);

			if (currentLink != null) {

				title = currentLink.getString("name");
				JSONArray badgeArray = currentLink.getJSONArray("badges");

				// convert badges to List<String>
				for (int i = 0; i < badgeArray.length(); i++) {
					badges.add(badgeArray.getString(i));
				}
			} else {
				String stringLink = jsonLinks.optString(siteKey);
				if (stringLink != null) { // its a string
					title = stringLink;
				} else { // none of the above, skip
					// TODO logging
					continue;
				}
			}
			
			// create the SiteLink instance
			SiteLink siteLink = factory.getSiteLink(title, siteKey,
					siteIri, badges);
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
		List<Statement> statementsFromJson = new ArrayList<Statement>(jsonStatements.length());

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
		return this.factory.getClaim(subject, mainSnak, qualifiers);
	}

	/**
	 * Transforms a statement rank from an integer representation to an
	 * enumerated value as requested by the WDTK data model. <br/>
	 * The number 0 maps to DEPRECATED. <br/>
	 * The number 1 maps to NORMAL. <br/>
	 * The number 2 maps to PREFERRED. <br/>
	 * Other ranks are regarded as an error.
	 * 
	 * @param intRank
	 *            the rank as integer.
	 * @return an appropriate StatementRank-value
	 */
	private StatementRank getStatementRank(int intRank) {

		switch(intRank){
		case 0 : return StatementRank.DEPRECATED;
		case 1 : return StatementRank.NORMAL;
		case 2 : return StatementRank.PREFERRED;
		default:
			throw new IllegalArgumentException("Undefined statement rank.");
		}
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

		List<Snak> result = new ArrayList<Snak>(jsonQualifiers.length());
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

		switch (jsonMainSnak.getString(0)) {
		case "value":
			return this.getValueSnak(jsonMainSnak);
		case "somevalue":
			return this.getSomeValueSnak(jsonMainSnak);
		case "novalue":
			return this.getNoValueSnak(jsonMainSnak);
		default: // could not determine snak type...
			throw new JSONException("Unknown snack type: "
					+ jsonMainSnak.getString(0));
		}
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

		// assert jsonNoValueSnak != null : "jsonSomeValueSnak was null.";
		// assert jsonNoValueSnak.getString(0).equals("novalue") :
		// "Argument was not a SomeValueSnak.";

		int intPropertyId = jsonNoValueSnak.getInt(1);
		PropertyIdValue propertyId = (PropertyIdValue) this.getEntityId(
				intPropertyId, DocumentType.PROPERTY);

		NoValueSnak result = this.factory.getNoValueSnak(propertyId);

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

		// assert jsonSomeValueSnak != null : "jsonSomeValueSnak was null.";
		// assert jsonSomeValueSnak.getString(0).equals("somevalue") :
		// "Argument was not a SomeValueSnak.";

		int intPropertyId = jsonSomeValueSnak.getInt(1);
		PropertyIdValue propertyId = (PropertyIdValue) this.getEntityId(
				intPropertyId, DocumentType.PROPERTY);

		SomeValueSnak result = this.factory.getSomeValueSnak(propertyId);

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

		// assert jsonValueSnak != null : "jsonValueSnak was null";
		// assert jsonValueSnak.getString(0).equals("value") :
		// "given JSON was not a value snak";

		ValueSnak result;

		// get the property id
		int intId = jsonValueSnak.getInt(1);
		PropertyIdValue propertyId = (PropertyIdValue) this.getEntityId(intId,
				DocumentType.PROPERTY);

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
					+ " in value snak JSON");
		}

		// put it all together
		result = this.factory.getValueSnak(propertyId, value);
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
		// assert jsonDataTypeId != null : "Given JSON datatype id was null";

		return this.factory.getDatatypeIdValue(jsonDataTypeId);
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
	 *            ignored. Must not be null. Precisions not covered by the
	 *            current data model will be rounded to the next coarse
	 *            precision. Note that an unwanted rounding might occur if the
	 *            chosen representation is a slight bit higher then the limit
	 *            for the precision.
	 * @return an appropriate GlobeCoordinatesValue
	 * @throws JSONException
	 *             if a required label was missing.
	 */
	private GlobeCoordinatesValue getGlobeCoordinatesValue(
			JSONObject jsonGlobeCoordinate) throws JSONException {

		// assert jsonGlobeCoordinate != null :
		// "Globe coordinate JSON was null";
		// example:
		// {"latitude":51.835,
		// "longitude":10.785277777778,
		// "altitude":null,
		// "precision":0.00027777777777778,
		// "globe":"http:\/\/www.wikidata.org\/entity\/Q2"}
		// NOTE as for now, ignore "altitude".
		// The key will be reviewed in the future.
		// NOTE the precision is denoted in float as a part of the degree
		// conversion into long necessary
		// NOTE sometimes the latitude and longitude are provided as int in
		// degree

		// convert latitude and longitude into nanodegrees
		long latitude;
		long longitude;

		// try if the coordinates are already in int (with degree precision?)

		double doubleLatitude = jsonGlobeCoordinate.getDouble("latitude");
		latitude = (long) (doubleLatitude * GlobeCoordinatesValue.PREC_DEGREE);

		double doubleLongitude = jsonGlobeCoordinate.getDouble("longitude");
		longitude = (long) (doubleLongitude * GlobeCoordinatesValue.PREC_DEGREE);

		// getting the precision
		// if the precision is available as double it needs to be converted
		// NOTE this is done by hand, since otherwise one would get rounding
		// errors
		// if the precision is available as int it needs to be multiplied with
		// PREC_DEGREE
		// also in older dumps the precision might be null
		// in this case the precision might default to PREC_DEGREE
		long precision;

		if (jsonGlobeCoordinate.isNull("precision")) {

			precision = GlobeCoordinatesValue.PREC_DEGREE;

		} else {

			Double doublePrecision = jsonGlobeCoordinate.getDouble("precision");

			// Yes you have to check all
			// possible representations since they do not equal
			// in their internal binary representation
			// and Double can not cope with that

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
		// caution: might be null
		String globeIri = null;
		globeIri = jsonGlobeCoordinate.optString("globe", "");

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
		// assert string != null :
		// "String to be converted to a StringValue was null";

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
		int entityId = jsonObject.getInt("numeric-id");

		// check the entity type
		switch (entityType) {
		case "item":
			result = this.getEntityId(entityId, DocumentType.ITEM);
			break;
		case "property":
			// this case is possible in theory,
			// but I never encountered it so far
			result = this.getEntityId(entityId, DocumentType.PROPERTY);
			break;
		default:
			throw new JSONException("Unknown entity type " + entityType
					+ " in entity id value JSON.");
		}
		return result;
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

		// TODO include negative years in test
		// caution: substrings might fail

		String stringTime = jsonTimeValue.getString("time");
		String[] substrings = stringTime.split("(?<!\\A)[\\-\\:TZ]");

		// get the year
		long year = Long.parseLong(substrings[0]);

		// get the month
		byte month = Byte.parseByte(substrings[1]);

		// get the day
		byte day = Byte.parseByte(substrings[2]);

		// get the hour
		byte hour = Byte.parseByte(substrings[3]);

		// get the minute
		byte minute = Byte.parseByte(substrings[4]);

		// get the second
		byte second = Byte.parseByte(substrings[5]);

		// get the precision
		byte precision = (byte) jsonTimeValue.getInt("precision");

		// get the tolerances
		int beforeTolerance = jsonTimeValue.getInt("before");
		int afterTolerance = jsonTimeValue.getInt("after");

		// get the time zone offset
		int timezoneOffset = jsonTimeValue.getInt("timezone");

		// get the calendar model
		String calendarModel = jsonTimeValue.getString("calendarmodel");

		result = this.factory.getTimeValue(year, month, day, hour, minute,
				second, precision, beforeTolerance, afterTolerance,
				timezoneOffset, calendarModel);
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
		Validate.notNull(baseIri);
		this.baseIri = baseIri;
	}

}
