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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
	private static final String KEY_DESCRIPTION = "description";
	private static final String KEY_ALIAS = "aliases";
	private static final String KEY_DATATYPE = "datatype";
	private static final String KEY_CLAIM = "claims";
	private static final String KEY_LINK = "links";

	private final DataObjectFactory factory;
	private String baseIri = "";
	private final MonolingualTextValueHandler mltvHandler;
	private final StatementGroupBuilder statementGroupBuilder;

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
		statementGroupBuilder = new StatementGroupBuilder(this.factory);
	}

	/**
	 * Creates a PropertyDocument from the given JSON object and property id
	 * string. JSON objects may (or may not) contain information about the
	 * property id as well; this information is ignored. Relevant keys in the
	 * given JSON object are "label", "description", "aliases" and "datatype",
	 * where "datatype" must be present and the others are optional.
	 * 
	 * @param jsonObject
	 *            a JSON object representing a PropertyDocument
	 * @param propertyIdString
	 *            the string id of the property that is described in this
	 *            document
	 * @return the corresponding PropertyDocument
	 * @throws JSONException
	 *             if the given JSON did not have the expected form
	 * 
	 */
	public PropertyDocument convertToPropertyDocument(JSONObject jsonObject,
			String propertyIdString) throws JSONException {

		PropertyIdValue propertyIdValue = this
				.getPropertyIdValue(propertyIdString);

		List<MonolingualTextValue> labels = this.getMonolingualTextValues(
				KEY_LABEL, jsonObject);
		List<MonolingualTextValue> descriptions = this
				.getMonolingualTextValues(KEY_DESCRIPTION, jsonObject);
		List<MonolingualTextValue> aliases = this.getMonolingualTextValues(
				KEY_ALIAS, jsonObject);

		String jsonDataTypeId = jsonObject.getString(KEY_DATATYPE);
		DatatypeIdValue datatypeId = this.getDatatypeIdValue(jsonDataTypeId);

		return this.factory.getPropertyDocument(propertyIdValue, labels,
				descriptions, aliases, datatypeId);
	}

	/**
	 * Creates an ItemDocument from the given JSON object and item id string.
	 * JSON objects may (or may not) contain information about the item id as
	 * well; this information is ignored. Relevant keys in the given JSON object
	 * are "label", "description", "aliases", "claims" and "links", all of which
	 * are optional.
	 * 
	 * @param jsonObject
	 *            a JSON object representing an ItemDocument
	 * @param itemIdString
	 *            the string id of the item that is described in this document
	 * @return the corresponding ItemDocument
	 * @throws JSONException
	 *             if the given JSON did not have the expected form
	 * 
	 */
	public ItemDocument convertToItemDocument(JSONObject jsonObject,
			String itemIdString) throws JSONException {

		ItemIdValue itemId = this.getItemIdValue(itemIdString);

		List<MonolingualTextValue> labels = this.getMonolingualTextValues(
				KEY_LABEL, jsonObject);
		List<MonolingualTextValue> descriptions = this
				.getMonolingualTextValues(KEY_DESCRIPTION, jsonObject);
		List<MonolingualTextValue> aliases = this.getMonolingualTextValues(
				KEY_ALIAS, jsonObject);

		List<StatementGroup> statements;
		if (jsonObject.has(KEY_CLAIM)) {
			JSONArray jsonStatements = jsonObject.getJSONArray(KEY_CLAIM);
			statements = this.getStatementGroups(jsonStatements, itemId);
		} else {
			statements = Collections.emptyList();
		}

		Map<String, SiteLink> siteLinks = new HashMap<>();

		if (jsonObject.has(KEY_LINK)) {
			JSONArray linkArray = jsonObject.optJSONArray(KEY_LINK);
			if (linkArray == null) {
				JSONObject jsonLinks = jsonObject.getJSONObject(KEY_LINK);
				siteLinks = this.getSiteLinks(jsonLinks);
			}
		}

		return factory.getItemDocument(itemId, labels, descriptions, aliases,
				statements, siteLinks);
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
	 * Creates a list of MonolingualTextValue objects from the given JSONObject.
	 * The object to be converted is the value associated with the given key in
	 * the top level object. So if there is a JSONObject <i>topLevel</i> and the
	 * key "label" are given, only the JSONObject found in the <i>topLevel</i>
	 * under the key "label" will be converted, not the whole <i>topLevel</i>.
	 * 
	 * @param key
	 *            the key of the object to be converted in the topLevel
	 * @param jsonObject
	 *            the JSONObject that contains the object to be converted under
	 *            the given key
	 * @return the corresponding list of MonolingualTextValue objects, or an
	 *         empty list if the key did not exist
	 */
	private List<MonolingualTextValue> getMonolingualTextValues(String key,
			JSONObject jsonObject) {

		JSONObject toConvert = jsonObject.optJSONObject(key);
		if (toConvert != null) {
			return this.mltvHandler.convertToMltv(toConvert);
		} else {
			return Collections.emptyList();
		}
	}

	/**
	 * Creates a list of Reference objects from the given JSON array. A
	 * reference is an array of reference statements which in turn are value
	 * snaks. Invalid references will be skipped and an error is reported.
	 * 
	 * @param jsonReferences
	 *            a JSON array of JSON arrays of value snaks
	 * @return the corresponding list of References
	 */
	private List<? extends Reference> getReferences(JSONArray jsonReferences) {
		List<Reference> result = new ArrayList<>(jsonReferences.length());

		for (int i = 0; i < jsonReferences.length(); i++) {
			try {
				JSONArray jsonRef = jsonReferences.getJSONArray(i);
				result.add(this.getReference(jsonRef));
			} catch (JSONException e) {
				JsonConverter.logger
						.error("Problem when parsing reference. Error was: "
								+ e.toString());
			}
		}

		return result;
	}

	/**
	 * Creates a Reference from the given JSON array, as it occurs in the JSON
	 * array of references.
	 * 
	 * @param jsonArray
	 *            a JSON array describing a single reference, containing value
	 *            snaks
	 * @return the corresponding Reference
	 * @throws JSONException
	 *             if the given JSON did not have the expected form
	 */
	private Reference getReference(JSONArray jsonArray) throws JSONException {
		List<ValueSnak> valueSnaks = new ArrayList<>(jsonArray.length());

		for (int j = 0; j < jsonArray.length(); j++) {
			JSONArray jsonValueSnak = jsonArray.getJSONArray(j);
			ValueSnak currentValueSnak = this.getValueSnak(jsonValueSnak);
			valueSnaks.add(currentValueSnak);
		}

		return factory.getReference(valueSnaks);
	}

	/**
	 * Creates a map from string keys to SiteLink objects from the given JSON
	 * object. The structure of a sitelink in JSON is as follows:
	 * 
	 * <pre>
	 * {"name":string,"badges":[string] }
	 * </pre>
	 * 
	 * However, there is also an old format that did not have the badges and
	 * only gave the string directly.
	 * 
	 * @param jsonObject
	 *            a JSON object representing a list of site links
	 * @return a mapping from site keys, such as "enwiki", to SiteLink objects
	 * @throws JSONException
	 *             if the given JSON did not have the expected form
	 */
	private Map<String, SiteLink> getSiteLinks(JSONObject jsonObject)
			throws JSONException {

		Map<String, SiteLink> result = new HashMap<String, SiteLink>();

		// FIXME we need to get the proper IRI instead
		String siteIri = "";

		// json.org does not type its Iterator: unchecked cast needed
		@SuppressWarnings("unchecked")
		Iterator<String> linkIterator = jsonObject.keys();

		while (linkIterator.hasNext()) {
			String title;
			List<String> badges;

			String siteKey = linkIterator.next();

			JSONObject currentLink = jsonObject.optJSONObject(siteKey);
			if (currentLink != null) { // modern form wiht badges
				title = currentLink.getString("name");

				JSONArray badgeArray = currentLink.getJSONArray("badges");
				badges = new ArrayList<>(badgeArray.length());
				for (int i = 0; i < badgeArray.length(); i++) {
					badges.add(badgeArray.getString(i));
				}
			} else { // old form without badges
				title = jsonObject.getString(siteKey);
				badges = Collections.emptyList();
			}

			// create the SiteLink instance
			SiteLink siteLink = factory.getSiteLink(title, siteKey, siteIri,
					badges);
			result.put(siteKey, siteLink);
		}

		return result;
	}

	/**
	 * Creates a list of StatementGroup objects from the given JSON array, which
	 * represents a list of statements (without any groups).
	 * 
	 * @param jsonStatements
	 *            a JSON list of JSON representations for Statements
	 * @param entityIdValue
	 *            the subject to which this statement refers
	 * 
	 * @return the corresponding list of StatementGroups
	 * @throws JSONException
	 *             if the given JSON did not have the expected form
	 */
	private List<StatementGroup> getStatementGroups(JSONArray jsonStatements,
			EntityIdValue entityIdValue) throws JSONException {

		List<StatementGroup> result;
		List<Statement> statementsFromJson = new ArrayList<Statement>(
				jsonStatements.length());

		// iterate over all the statements in the item and decompose them
		for (int i = 0; i < jsonStatements.length(); i++) {
			JSONObject statementJson = jsonStatements.getJSONObject(i);
			Statement statement = this.getStatement(statementJson,
					entityIdValue);
			statementsFromJson.add(statement);
		}

		// process the list of statements into a list of statement groups
		result = this.statementGroupBuilder
				.buildFromStatementList(statementsFromJson);

		return result;
	}

	/**
	 * Creates a Statement object for the given entity id from the given JSON
	 * object. The JSON needs to have the following structure:
	 * 
	 * <pre>
	 * {"m":object,
	 *  "q":[],
	 *  "g":string,
	 *  "rank":int,
	 *  "refs":[...]}
	 * </pre>
	 * 
	 * Here, "m" denotes the main snak, "q" the qualifiers, and "g" the
	 * statement id.
	 * 
	 * @param jsonObject
	 *            a JSON object representing a statement
	 * @param entityIdValue
	 *            the subject to which this statement refers
	 * @return the corresponding Statement
	 * @throws JSONException
	 *             if the given JSON did not have the expected form
	 */
	private Statement getStatement(JSONObject jsonObject,
			EntityIdValue entityIdValue) throws JSONException {
		// get the claim
		Claim currentClaim = this.getClaim(jsonObject, entityIdValue);

		// get the references
		JSONArray jsonRefs = jsonObject.getJSONArray("refs");
		List<? extends Reference> references = this.getReferences(jsonRefs);

		// get the statement rank
		int intRank = jsonObject.getInt("rank");
		StatementRank rank = this.getStatementRank(intRank);

		// get the statement id
		String statementId = jsonObject.getString("g");

		return factory
				.getStatement(currentClaim, references, rank, statementId);
	}

	/**
	 * Creates a Claim object for the given entity id from the given JSON object
	 * (this is usually the JSON for encoding a whole Statement). A Claim
	 * consists of the EntityIdValue of the subject (given explicitly), the
	 * claim's main snak (given by a key "m" i JSON) and the claim's qualifiers
	 * (given by a key "q" in JSON).
	 * 
	 * @param jsonObject
	 *            a JSON object representing a whole statement from which the
	 *            claim is to be extracted
	 * @param entityIdValue
	 *            the subject to which this claim refers
	 * @return the corresponding Claim
	 * @throws JSONException
	 *             if the given JSON did not have the expected form
	 */
	private Claim getClaim(JSONObject jsonObject, EntityIdValue entityIdValue)
			throws JSONException {
		// get the main snak
		JSONArray jsonMainSnak = jsonObject.getJSONArray("m");
		Snak mainSnak = getSnak(jsonMainSnak);

		// get the qualifiers
		JSONArray jsonQualifiers = jsonObject.getJSONArray("q");
		List<Snak> qualifiers = this.getQualifiers(jsonQualifiers);

		// build it together
		return this.factory.getClaim(entityIdValue, mainSnak, qualifiers);
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
			JSONArray currentSnak = jsonQualifiers.getJSONArray(i);
			result.add(this.getSnak(currentSnak));
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
