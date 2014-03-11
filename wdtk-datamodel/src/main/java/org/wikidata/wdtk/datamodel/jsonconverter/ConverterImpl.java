package org.wikidata.wdtk.datamodel.jsonconverter;

/*
 * #%L
 * Wikidata Toolkit Data Model
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
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.*;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
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
import org.wikidata.wdtk.datamodel.interfaces.TermedDocument;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

/**
 * Implementation of {@link Converter} that provides a Conversion from Object
 * constructed by the
 * {@link org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory} into a
 * json-format using JSONObjects from the org.json library.
 * 
 * @author Michael Günther
 * 
 */
public class ConverterImpl implements Converter<JSONObject, JSONArray> {

	final String KEY_ENTITY_TYP_ITEM = "item";
	final String KEY_ENTITY_TYP_PROPERTY = "property";

	final String KEY_ID = "id";
	final String KEY_TITLE = "title";
	final String KEY_CLAIMS = "claims";
	final String KEY_SNAKS = "snaks";
	final String KEY_ALIASES = "aliases";
	final String KEY_DESCRIPTIONS = "descriptions";
	final String KEY_SITE_LINKS = "sitelinks";
	final String KEY_LABELS = "labels";
	final String KEY_DATATYP = "datatype";
	final String KEY_TYP = "type";
	final String KEY_SNAK_TYP = "snaktype";
	final String KEY_PROPERTY = "property";
	final String KEY_VALUE = "value";
	final String KEY_DATAVALUE = "datavalue";
	final String KEY_MAINSNAK = "mainsnak";
	final String KEY_QUALIFIERS = "qualifiers";

	final String STD_UNIT_VALUE = "1";

	final String FORMAT_YEAR = "00000000000";
	final String FORMAT_OTHER = "00";

	/**
	 * converting information of a date to a string value in ISO 8601 format
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param minute
	 * @param second
	 * @return ISO 8601 value (String)
	 * 
	 * @throws IllegalArgumentException
	 */

	String timeToString(long year, byte month, byte day, byte hour,
			byte minute, int second) throws IllegalArgumentException {
		if ((month == 0) || (day == 0)) {
			throw new IllegalArgumentException();
		}

		String result;
		StringBuilder builder = new StringBuilder();
		DecimalFormat yearForm = new DecimalFormat(FORMAT_YEAR);
		DecimalFormat timeForm = new DecimalFormat(FORMAT_OTHER);
		builder.append(yearForm.format(year));
		if (year > 0) {
			builder.insert(0, "+");
		}
		builder.append("-");
		builder.append(timeForm.format(month));
		builder.append("-");
		builder.append(timeForm.format(day));
		builder.append("T");
		builder.append(timeForm.format(hour));
		builder.append(":");
		builder.append(timeForm.format(minute));
		builder.append(":");
		builder.append(timeForm.format(second));
		builder.append("Z");
		result = builder.toString();
		return result;
	}

	/**
	 * format a BigDecimal value into a string value representation
	 * 
	 * @param number
	 * @return String for BigDecimal value
	 */

	String formatBigDecimal(BigDecimal number) {
		StringBuilder builder = new StringBuilder();
		if (number.signum() != -1) {
			builder.append("+");
		}
		builder.append(number.toString());
		return builder.toString();
	}

	/**
	 * merge two JSONObjects. The fist onjFrom will merged into objTo
	 * 
	 * @param objFrom
	 * @param objTo
	 * @return merged JSONObject
	 * @throws JSONException
	 */

	JSONObject mergeJSONObjects(JSONObject objFrom, JSONObject objTo)

	throws JSONException {

		for (Iterator<?> key = objFrom.keys(); key.hasNext();) {
			String nKey = (String) key.next();
			if (!objTo.has(nKey)) {
				objTo.put(nKey, objFrom.get(nKey));
			}

		}
		return objTo;
	}

	/**
	 * create a json representation for the aliases of an document
	 * 
	 * @param document
	 * @return JSONObject representing aliases of a {@link TermedDocument}
	 * @throws JSONException
	 */

	JSONObject convertAliasesToJson(TermedDocument document)
			throws JSONException {
		JSONObject result = new JSONObject();
		for (String key : document.getAliases().keySet()) {
			JSONArray alias = new JSONArray();
			result.put(key, alias);
			for (MonolingualTextValue value : document.getAliases().get(key)) {
				alias.put(convertMonolingualTextValueToJson(value));
			}
		}

		return result;
	}

	/**
	 * create a json representation for the descriptions of an
	 * {@link TermedeDocument}
	 * 
	 * @param document
	 * @return JSONObject representing descriptions of a {@link TermedDocument}
	 * @throws JSONException
	 */

	JSONObject convertDescriptionsToJson(TermedDocument document)
			throws JSONException {
		JSONObject result = new JSONObject();
		for (String key : document.getDescriptions().keySet()) {
			result.put(key, convertMonolingualTextValueToJson(document
					.getDescriptions().get(key)));
		}
		return result;

	}

	/**
	 * create a json representation for the labels of a {@link TermedDocument}
	 * 
	 * @param document
	 * @return JSONObject of labels
	 * @throws JSONException
	 */

	JSONObject convertLabelsToJson(TermedDocument document)
			throws JSONException {
		JSONObject result = new JSONObject();
		for (String key : document.getLabels().keySet()) {
			result.put(key, convertMonolingualTextValueToJson(document
					.getLabels().get(key)));
		}
		return result;
	}

	/**
	 * create a json representation for the SiteLinks of an {@link ItemDocument}
	 * 
	 * @param document
	 * @return JSONObject representation for
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.SiteLink} objects
	 * @throws JSONException
	 */
	JSONObject convertSiteLinksToJson(ItemDocument document)
			throws JSONException {
		JSONObject result = new JSONObject();
		for (String key : document.getSiteLinks().keySet()) {
			result.put(key,
					convertSiteLinkToJson(document.getSiteLinks().get(key)));
		}
		return result;
	}

	/**
	 * create a json representation for qualifiers of {@link Claim}
	 * 
	 * @param qualifiers
	 * @return JSONObject of qualifiers
	 */
	JSONObject convertQualifiersToJson(List<? extends Snak> qualifiers) {
		JSONObject result = new JSONObject();
		Set<String> qualifierGroups = new HashSet<String>();
		for (Snak qualifier : qualifiers) {
			final String pId = qualifier.getPropertyId().getEntityType();
			if (!qualifierGroups.contains(pId)) {
				result.put(pId, new JSONArray());
			}
			final JSONArray group = (JSONArray) result.get(pId);
			group.put(convertSnakToJson(qualifier));
		}
		return result;
	}

	/**
	 * adds the attributes occurring in every {@link TermedDocument} to object
	 * 
	 * @param document
	 * @param to
	 * @return JSONObject with "aliases", "descriptions", "labels" key
	 */
	JSONObject addTermedDocumentAttributes(TermedDocument document,
			JSONObject to) {
		JSONObject result = to;
		result.put(KEY_ID, document.getEntityId().getId());
		result.put(KEY_TITLE, document.getEntityId().getId());
		if (!document.getAliases().isEmpty()) {
			result.put(KEY_ALIASES, convertAliasesToJson(document));
		}
		if (!document.getDescriptions().isEmpty()) {
			result.put(KEY_DESCRIPTIONS, convertDescriptionsToJson(document));
		}
		if (!document.getLabels().isEmpty()) {
			result.put(KEY_LABELS, convertLabelsToJson(document));
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wikidata.wdtk.datamodel.jsonconverter.Converter#convertClaimToJson
	 * (org.wikidata.wdtk.datamodel.interfaces.Claim)
	 */
	@Override
	public JSONObject convertClaimToJson(Claim claim) throws JSONException {
		JSONObject result = new JSONObject();
		result.put(KEY_MAINSNAK, convertSnakToJson(claim.getMainSnak()));
		if (!claim.getQualifiers().isEmpty()) {
			result.put(KEY_QUALIFIERS,
					convertQualifiersToJson(claim.getQualifiers()));
		}
		// what about the subject?
		return result;
	}

	/**
	 * If the parameter is an ItemDocument or a PropertyDocument the function
	 * for that is called, otherwise it will add the attributes of an
	 * EntityDocument to the entity parameter
	 * 
	 * @param entity
	 * @return JSONObject for  {@link org.wikidata.wdtk.datamodel.interfaces.EntityDocument}
	 * @throws JSONException
	 */
	public JSONObject convertEntityDocumentToJson(EntityDocument entity)
			throws JSONException {
		JSONObject result = new JSONObject();
		if (entity instanceof TermedDocument) {
			result = convertTermedDocumentToJson((TermedDocument) entity);
		} else {
			// Should not happen maybe skip that
			JSONObject valueResult = new JSONObject();
			result.put(KEY_VALUE, valueResult);
			result.put(KEY_TYP, entity.getEntityId().getEntityType());

			valueResult
					.put("entity-type", entity.getEntityId().getEntityType());
			valueResult.put("numeric-id", entity.getEntityId().getId());
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wikidata.wdtk.datamodel.jsonconverter.Converter#convertItemDocumentToJson
	 * (org.wikidata.wdtk.datamodel.interfaces.ItemDocument)
	 */
	@Override
	public JSONObject convertItemDocumentToJson(ItemDocument itemDocument)
			throws JSONException {
		JSONObject result = new JSONObject();
		JSONObject statementGroups = new JSONObject();
		result = addTermedDocumentAttributes(itemDocument, result);
		result.put(KEY_TYP, KEY_ENTITY_TYP_ITEM); // result.put("type",
		// itemDocument.getEntityId().getEntityType());
		if (!itemDocument.getStatementGroups().isEmpty()) {
			result.put(KEY_CLAIMS, statementGroups);
		}
		if (!itemDocument.getSiteLinks().isEmpty()) {
			result.put(KEY_SITE_LINKS, convertSiteLinksToJson(itemDocument));
		}

		for (StatementGroup statementGroup : itemDocument.getStatementGroups()) {
			statementGroups.put(
					statementGroup.getProperty().getId().toString(),
					convertStatementGroupToJson(statementGroup));

		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wikidata.wdtk.datamodel.jsonconverter.Converter#
	 * convertPropertyDocumentToJson
	 * (org.wikidata.wdtk.datamodel.interfaces.PropertyDocument)
	 */
	@Override
	public JSONObject convertPropertyDocumentToJson(PropertyDocument document)
			throws JSONException {
		JSONObject result = new JSONObject();
		result.put(KEY_TYP, KEY_ENTITY_TYP_PROPERTY); // result.put("type",
		// document.getEntityId()); giving type
		// with iri
		result = addTermedDocumentAttributes(document, result);

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wikidata.wdtk.datamodel.jsonconverter.Converter#convertReferenceToJson
	 * (org.wikidata.wdtk.datamodel.interfaces.Reference)
	 */
	@Override
	public JSONObject convertReferenceToJson(Reference ref)
			throws JSONException {
		JSONObject result = new JSONObject();
		JSONObject snaks = new JSONObject();
		JSONArray snakOrder = new JSONArray();
		Set<String> snakGroups = new HashSet<String>();

		// no ref.hash value...

		result.put(KEY_SNAKS, snaks);
		result.put("snak-order", snakOrder);

		for (ValueSnak snak : ref.getSnaks()) {
			final String pId = snak.getPropertyId().getId();
			if (!snakGroups.contains(pId)) {
				snaks.put(pId, new JSONArray());
				snakGroups.add(pId);
			}
			final JSONArray group = (JSONArray) snaks.get(pId);
			group.put(convertValueSnakToJson(snak));
		}

		for (String pId : snakGroups) {
			snakOrder.put(pId);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wikidata.wdtk.datamodel.jsonconverter.Converter#convertStatementToJson
	 * (org.wikidata.wdtk.datamodel.interfaces.Statement)
	 */
	@Override
	public JSONObject convertStatementToJson(Statement statement)
			throws JSONException {
		JSONObject result = new JSONObject();

		result.put(KEY_ID, statement.getStatementId());
		result.put(KEY_MAINSNAK, convertSnakToJson(statement.getClaim()
				.getMainSnak()));
		if (statement.getClaim().getQualifiers().isEmpty() == false) {
			result.put(KEY_QUALIFIERS,
					convertQualifiersToJson((List<? extends Snak>) statement
							.getClaim().getQualifiers()));
		}
		// What about the Subject?
		result.put(KEY_TYP, "statement");
		result.put("rank", convertStatementRankToJson(statement.getRank()));
		JSONArray references = new JSONArray();
		if (!statement.getReferences().isEmpty()) {
			result.put("references", references);
		}
		for (Reference ref : statement.getReferences()) {
			references.put(convertReferenceToJson(ref));
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wikidata.wdtk.datamodel.jsonconverter.Converter#
	 * convertStatementGroupToJson
	 * (org.wikidata.wdtk.datamodel.interfaces.StatementGroup)
	 */
	@Override
	public JSONArray convertStatementGroupToJson(StatementGroup statementGroup)
			throws JSONException {
		JSONArray statements = new JSONArray();
		for (Statement statement : statementGroup.getStatements()) {
			statements.put(convertStatementToJson(statement));
		}
		return statements;
	}

	/**
	 * Create a json representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.ValueSnak},
	 * {@link org.wikidata.wdtk.datamodel.interfaces.NoValueSnak} or a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak}.
	 * 
	 * @param snak
	 * @return JSONObject representing a specific
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.Snak}
	 * @throws JSONException
	 */
	public JSONObject convertSnakToJson(Snak snak) throws JSONException {
		JSONObject result = null;
		// TODO better using if snak instanceof Interface
		if (snak instanceof NoValueSnak) {
			result = convertNoValueSnakToJson((NoValueSnak) snak);
		} else if (snak instanceof SomeValueSnak) {
			result = convertSomeValueSnakToJson((SomeValueSnak) snak);
		} else if (snak instanceof ValueSnak) {
			result = convertValueSnakToJson((ValueSnak) snak);
		} else {
			throw new IllegalArgumentException("Snaktype is unknown!");
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wikidata.wdtk.datamodel.jsonconverter.Converter#convertValueSnakToJson
	 * (org.wikidata.wdtk.datamodel.interfaces.ValueSnak)
	 */
	@Override
	public JSONObject convertValueSnakToJson(ValueSnak snak)
			throws JSONException {
		JSONObject result = new JSONObject();
		result.put(KEY_SNAK_TYP, "value");
		result.put(KEY_PROPERTY, snak.getPropertyId().getId());

		// maybe there are more possibilities
		if (snak.getValue() instanceof EntityIdValue) {
			result.put(KEY_DATATYP, "wikibase-item"); // for wikibase-entityid
														// in
														// the dump | are there
														// other EntityTypes?
			result.put(KEY_DATAVALUE,
					convertEntityIdValueToJson((EntityIdValue) snak.getValue()));
		} else if (snak.getValue() instanceof TimeValue) {
			result.put(KEY_DATATYP, "time"); // for time in the dump
			result.put(KEY_DATAVALUE,
					convertTimeValueToJson((TimeValue) snak.getValue()));
		} else if (snak.getValue() instanceof GlobeCoordinatesValue) {
			result.put(KEY_DATATYP, "globe-coordinate"); // for globecoordinate
															// in the dump
			result.put(
					KEY_DATAVALUE,
					convertGlobeCoordinatesValueToJson((GlobeCoordinatesValue) snak
							.getValue()));
		} else if (snak.getValue() instanceof QuantityValue) {
			result.put(KEY_DATATYP, "quantity"); // for quantity in the dump
			result.put(KEY_DATAVALUE,
					convertQuantityValueToJson((QuantityValue) snak.getValue()));
		} else if (snak.getValue() instanceof StringValue) {
			result.put(KEY_DATATYP, "string"); // for string in the dump
			result.put(KEY_DATAVALUE,
					convertStringValueToJson((StringValue) snak.getValue()));
		} else {
			throw new IllegalArgumentException("The class of the value "
					+ snak.getValue().getClass() + " is unknown!");
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wikidata.wdtk.datamodel.jsonconverter.Converter#convertNoValueSnakToJson
	 * (org.wikidata.wdtk.datamodel.interfaces.NoValueSnak)
	 */
	@Override
	public JSONObject convertNoValueSnakToJson(NoValueSnak snak)
			throws JSONException {
		JSONObject result = new JSONObject();
		result.put(KEY_SNAK_TYP, "novalue");
		result.put(KEY_PROPERTY, snak.getPropertyId().getId());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wikidata.wdtk.datamodel.jsonconverter.Converter#
	 * convertSomeValueSnakToJson
	 * (org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak)
	 */
	@Override
	public JSONObject convertSomeValueSnakToJson(SomeValueSnak snak)
			throws JSONException {
		JSONObject result = new JSONObject();
		result.put(KEY_SNAK_TYP, "somevalue");
		result.put(KEY_PROPERTY, snak.getPropertyId().getId());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wikidata.wdtk.datamodel.jsonconverter.Converter#
	 * convertQuantityValueToJson
	 * (org.wikidata.wdtk.datamodel.interfaces.QuantityValue)
	 */
	@Override
	public JSONObject convertQuantityValueToJson(QuantityValue value)
			throws JSONException {
		JSONObject result = new JSONObject();
		JSONObject valueResult = new JSONObject();

		result.put(KEY_VALUE, valueResult);

		valueResult.put("amount", formatBigDecimal(value.getNumericValue()));
		valueResult.put("unit", STD_UNIT_VALUE);
		valueResult.put("upperBound", formatBigDecimal(value.getUpperBound()));
		valueResult.put("lowerBound", formatBigDecimal(value.getLowerBound()));

		result.put(KEY_TYP, "quantity");

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wikidata.wdtk.datamodel.jsonconverter.Converter#convertTimeValueToJson
	 * (org.wikidata.wdtk.datamodel.interfaces.TimeValue)
	 */
	@Override
	public JSONObject convertTimeValueToJson(TimeValue value)
			throws JSONException {
		JSONObject result = new JSONObject();
		JSONObject valueResult = new JSONObject();

		result.put(KEY_VALUE, valueResult);

		valueResult.put(
				"time",
				timeToString(value.getYear(), value.getMonth(), value.getDay(),
						value.getHour(), value.getMinute(), value.getSecond()));
		valueResult.put("timezone", value.getTimezoneOffset());
		valueResult.put("before", value.getBeforeTolerance());
		valueResult.put("after", value.getAfterTolerance());
		valueResult.put("precision", value.getPrecision());
		valueResult.put("calendarmodel", value.getPreferredCalendarModel());

		result.put(KEY_TYP, "time");

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wikidata.wdtk.datamodel.jsonconverter.Converter#
	 * convertGlobeCoordinatesValueToJson
	 * (org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue)
	 */
	@Override
	public JSONObject convertGlobeCoordinatesValueToJson(
			GlobeCoordinatesValue value) throws JSONException {
		JSONObject result = new JSONObject();
		JSONObject valueResult = new JSONObject();
		result.put(KEY_VALUE, valueResult);

		valueResult.put("latitude", value.getLatitude());
		valueResult.put("longitude", value.getLongitude());
		valueResult.put("precision", value.getPrecision()
				/ GlobeCoordinatesValue.PREC_DEGREE);
		valueResult.put("globe", value.getGlobe());

		result.put(KEY_TYP, "globecoordinate");

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wikidata.wdtk.datamodel.jsonconverter.Converter#
	 * convertEntityIdValueToJson
	 * (org.wikidata.wdtk.datamodel.interfaces.EntityIdValue)
	 */
	@Override
	public JSONObject convertEntityIdValueToJson(EntityIdValue value)
			throws JSONException {

		JSONObject result = new JSONObject();
		JSONObject valueResult = new JSONObject();

		result.put(KEY_TYP, "wikibase-entityid");
		switch (value.getEntityType()) {
		case EntityIdValue.ET_ITEM:
			valueResult = convertItemIdValueToJson((ItemIdValue) value);
			break;
		case EntityIdValue.ET_PROPERTY:
			valueResult = convertPropertyIdValueToJson((PropertyIdValue) value);
			break;
		default:
			throw new JSONException("Unknown EntityType: "
					+ value.getEntityType());
		}

		result.put(KEY_VALUE, valueResult);

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wikidata.wdtk.datamodel.jsonconverter.Converter#convertStringValueToJson
	 * (org.wikidata.wdtk.datamodel.interfaces.StringValue)
	 */
	@Override
	public JSONObject convertStringValueToJson(StringValue value)
			throws JSONException {
		JSONObject result = new JSONObject();
		result.put(KEY_VALUE, value.getString());
		result.put(KEY_TYP, "string");
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wikidata.wdtk.datamodel.jsonconverter.Converter#
	 * convertDatatypeIdValueToJson
	 * (org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue)
	 */
	@Override
	public JSONObject convertDatatypeIdValueToJson(DatatypeIdValue value)
			throws JSONException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wikidata.wdtk.datamodel.jsonconverter.Converter#convertItemIdValueToJson
	 * (org.wikidata.wdtk.datamodel.interfaces.ItemIdValue)
	 */
	@Override
	public JSONObject convertItemIdValueToJson(ItemIdValue value)
			throws JSONException {
		JSONObject result = new JSONObject();
		result.put("entity-type", KEY_ENTITY_TYP_ITEM); // or
														// value.getEntityType()
		result.put("numeric-id", value.getId());

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wikidata.wdtk.datamodel.jsonconverter.Converter#
	 * convertMonolingualTextValueToJson
	 * (org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue)
	 */
	@Override
	public JSONObject convertMonolingualTextValueToJson(
			MonolingualTextValue value) throws JSONException {
		JSONObject result = new JSONObject();
		result.put("language", value.getLanguageCode());
		result.put(KEY_VALUE, value.getText());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wikidata.wdtk.datamodel.jsonconverter.Converter#
	 * convertPropertyIdValueToJson
	 * (org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue)
	 */
	@Override
	public JSONObject convertPropertyIdValueToJson(PropertyIdValue value)
			throws JSONException {
		JSONObject result = new JSONObject();
		result.put("entity-type", KEY_ENTITY_TYP_PROPERTY); // or
															// value.getEntityType()
		result.put("numeric-id", value.getId());

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wikidata.wdtk.datamodel.jsonconverter.Converter#convertSiteLinkToJson
	 * (org.wikidata.wdtk.datamodel.interfaces.SiteLink)
	 */
	@Override
	public JSONObject convertSiteLinkToJson(SiteLink link) throws JSONException {
		JSONObject result = new JSONObject();
		result.put("site", link.getSiteKey());
		result.put(KEY_TITLE, link.getArticleTitle());
		result.put("badges", new JSONArray()); // always empty
		return result;
	}

	/**
	 * create a string notation for
	 * {@link org.wikidata.wdtk.datamodel.interfaces.StatementRank}
	 * 
	 * @param rank
	 * 
	 * @return {@link org.wikidata.wdtk.datamodel.interfaces.StatementRank} in
	 *         string notation for the json format
	 * 
	 */
	public String convertStatementRankToJson(StatementRank rank) {
		return rank.toString().toLowerCase();
	}

/**
	 * If the parameter is an ItemDocument or a PropertyDocument the function
	 * for that is called, otherwise it will throw an IllegalArgumentException
	 * 
	 * @param document
	 * @return JSONObject for {@link org.wikidata.wdtk.datamodel.interfaces.TermedDocument}
	 * @throws JSONException
	 */
	public JSONObject convertTermedDocumentToJson(TermedDocument document)
			throws JSONException {

		JSONObject result = new JSONObject();
		if (document instanceof ItemDocument) {
			result = convertItemDocumentToJson((ItemDocument) document);
		} else if (document instanceof PropertyDocument) {
			result = convertPropertyDocumentToJson((PropertyDocument) document);
		} else {
			throw new IllegalArgumentException();
		}

		return result;
	}
}
