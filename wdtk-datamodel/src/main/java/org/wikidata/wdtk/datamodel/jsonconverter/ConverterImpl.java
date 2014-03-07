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

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.*;
import org.wikidata.wdtk.datamodel.implementation.NoValueSnakImpl;
import org.wikidata.wdtk.datamodel.implementation.SomeValueSnakImpl;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.IriIdentifiedValue;
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
import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

public class ConverterImpl implements Converter {

	// GLOBAL-TODO replace identifiers in the code with new / old constants

	String timeToString(long year, byte month, byte day, byte hour,
			byte minute, int second) throws IllegalArgumentException {
		if ((month == 0) || (day == 0)) {
			throw new IllegalArgumentException();
		}

		String result;
		StringBuilder builder = new StringBuilder();
		DecimalFormat yearForm = new DecimalFormat("00000000000");
		DecimalFormat timeForm = new DecimalFormat("00");
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

	JSONObject convertDescriptionsToJson(TermedDocument document)
			throws JSONException {
		JSONObject result = new JSONObject();
		for (String key : document.getDescriptions().keySet()) {
			result.put(key, convertMonolingualTextValueToJson(document
					.getDescriptions().get(key)));
		}
		return result;

	}

	JSONObject convertLabelsToJson(TermedDocument document)
			throws JSONException {
		JSONObject result = new JSONObject();
		for (String key : document.getLabels().keySet()) {
			result.put(key, convertMonolingualTextValueToJson(document
					.getLabels().get(key)));
		}
		return result;
	}

	JSONObject convertSiteLinksToJson(ItemDocument document)
			throws JSONException {
		JSONObject result = new JSONObject();
		for (String key : document.getSiteLinks().keySet()) {
			result.put(key,
					convertSiteLinkToJson(document.getSiteLinks().get(key)));
		}
		return result;
	}

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

	JSONObject addTermedDocumentAttributes(TermedDocument document,
			JSONObject to) {
		JSONObject result = to;
		result.put("id", document.getEntityId().getId());
		result.put("title", document.getEntityId().getId());
		if (!document.getAliases().isEmpty()) {
			result.put("aliases", convertAliasesToJson(document));
		}
		if (!document.getDescriptions().isEmpty()) {
			result.put("description", convertDescriptionsToJson(document));
		}
		if (!document.getLabels().isEmpty()) {
			result.put("labels", convertLabelsToJson(document));
		}
		return result;
	}

	@Override
	public JSONObject convertClaimToJson(Claim claim) throws JSONException {
		JSONObject result = new JSONObject();
		result.put("mainsnak", convertSnakToJson(claim.getMainSnak()));
		if (!claim.getQualifiers().isEmpty()) {
			result.put("qualifiers",
					convertQualifiersToJson(claim.getQualifiers()));
		}
		// what about the subject?
		return result;
	}

	@Override
	public JSONObject convertEntityDocumentToJson(EntityDocument entity)
			throws JSONException {
		JSONObject result = new JSONObject();
		if (entity instanceof TermedDocument) {
			result = convertTermedDocumentToJson((TermedDocument) entity);
		} else {
			// Should not happen maybe skip that
			JSONObject valueResult = new JSONObject();
			result.put("value", valueResult);
			result.put("type", entity.getEntityId().getEntityType());

			valueResult
					.put("entity-type", entity.getEntityId().getEntityType()); // using
																				// convertEntityIdValueToJson?
			valueResult.put("numeric-id", entity.getEntityId().getId()); // using
																			// convertEntityIdValueToJson?
		}
		return result;
	}

	@Override
	public JSONObject convertItemDocumentToJson(ItemDocument itemDocument)
			throws JSONException {
		JSONObject result = new JSONObject();
		JSONObject statementGroups = new JSONObject();
		result = addTermedDocumentAttributes(itemDocument, result);
		result.put("type", "item"); // result.put("type",
									// itemDocument.getEntityId().getEntityType());
		if (!itemDocument.getStatementGroups().isEmpty()) {
			result.put("claims", statementGroups);
		}
		if (!itemDocument.getSiteLinks().isEmpty()) {
			result.put("sitelinks", convertSiteLinksToJson(itemDocument));
		}

		for (StatementGroup statementGroup : itemDocument.getStatementGroups()) {
			statementGroups.put(
					statementGroup.getProperty().getId().toString(),
					convertStatementGroupToJson(statementGroup));

		}
		return result;
	}

	@Override
	public JSONObject convertPropertyDocumentToJson(PropertyDocument document)
			throws JSONException {
		JSONObject result = new JSONObject();
		result.put("type", "property"); // result.put("type",
										// document.getEntityId()); giving type
										// with iri
		result = addTermedDocumentAttributes(document, result);

		return result;
	}

	@Override
	public JSONObject convertReferenceToJson(Reference ref)
			throws JSONException {
		JSONObject result = new JSONObject();
		JSONObject snaks = new JSONObject();
		JSONArray snakOrder = new JSONArray();
		Set<String> snakGroups = new HashSet<String>();

		// no ref.hash value...

		result.put("snaks", snaks);
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

	@Override
	public JSONObject convertStatementToJson(Statement statement)
			throws JSONException {
		JSONObject result = new JSONObject();

		result.put("id", statement.getStatementId());
		result.put("mainsnak", convertSnakToJson(statement.getClaim()
				.getMainSnak()));
		if (statement.getClaim().getQualifiers().isEmpty() == false) {
			result.put("qualifiers",
					convertQualifiersToJson((List<? extends Snak>) statement
							.getClaim().getQualifiers()));
		}
		// What about the Subject?
		result.put("type", "statement");
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

	@Override
	public JSONArray convertStatementGroupToJson(StatementGroup statementGroup)
			throws JSONException {
		JSONArray statements = new JSONArray();
		for (Statement statement : statementGroup.getStatements()) {
			statements.put(convertStatementToJson(statement));
		}
		return statements;
	}

	@Override
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

	@Override
	public JSONObject convertValueSnakToJson(ValueSnak snak)
			throws JSONException {
		JSONObject result = new JSONObject();
		result.put("snaktype", "value");
		result.put("property", snak.getPropertyId().getId());

		// maybe there are more possibilities
		if (snak.getValue() instanceof EntityIdValue) {
			result.put("datatype", "wikibase-item"); // for wikibase-entityid in
														// the dump | are there
														// other EntityTypes?
			result.put("datavalue",
					convertEntityIdValueToJson((EntityIdValue) snak.getValue()));
		} else if (snak.getValue() instanceof TimeValue) {
			result.put("datatype", "time"); // for time in the dump
			result.put("datavalue",
					convertTimeValueToJson((TimeValue) snak.getValue()));
		} else if (snak.getValue() instanceof GlobeCoordinatesValue) {
			result.put("datatype", "globe-coordinate"); // for globecoordinate
														// in the dump
			result.put(
					"datavalue",
					convertGlobeCoordinatesValueToJson((GlobeCoordinatesValue) snak
							.getValue()));
		} else if (snak.getValue() instanceof QuantityValue) {
			result.put("datatype", "quantity"); // for quantity in the dump
			result.put("datavalue",
					convertQuantityValueToJson((QuantityValue) snak.getValue()));
		} else if (snak.getValue() instanceof StringValue) {
			result.put("datatype", "string"); // for string in the dump
			result.put("datavalue",
					convertStringValueToJson((StringValue) snak.getValue()));
		} else {
			throw new IllegalArgumentException("The class of the value "
					+ snak.getValue().getClass() + " is unknown!");
		}

		return result;
	}

	@Override
	public JSONObject convertNoValueSnakToJson(NoValueSnak snak)
			throws JSONException {
		JSONObject result = new JSONObject();
		result.put("snaktype", "novalue");
		result.put("property", snak.getPropertyId().getId());
		return result;
	}

	@Override
	public JSONObject convertSomeValueSnakToJson(SomeValueSnak snak)
			throws JSONException {
		JSONObject result = new JSONObject();
		result.put("snaktype", "somevalue");
		result.put("property", snak.getPropertyId().getId());
		return result;
	}

	@Override
	public JSONObject convertQuantityValueToJson(QuantityValue value)
			throws JSONException {
		JSONObject result = new JSONObject();
		JSONObject valueResult = new JSONObject();

		result.put("value", valueResult);

		valueResult.put("amount", value.getNumericValue());
		valueResult.put("unit", 1);
		valueResult.put("upperBound", value.getUpperBound());
		valueResult.put("lowerBound", value.getLowerBound());

		result.put("type", "quantity");

		return result;
	}

	@Override
	public JSONObject convertTimeValueToJson(TimeValue value)
			throws JSONException {
		JSONObject result = new JSONObject();
		JSONObject valueResult = new JSONObject();

		result.put("value", valueResult);

		valueResult.put(
				"time",
				timeToString(value.getYear(), value.getMonth(), value.getDay(),
						value.getHour(), value.getMinute(), value.getSecond()));
		valueResult.put("timezone", value.getTimezoneOffset());
		valueResult.put("before", value.getBeforeTolerance());
		valueResult.put("after", value.getAfterTolerance());
		valueResult.put("precision", value.getPrecision());
		valueResult.put("calendarmodel", value.getPreferredCalendarModel());

		result.put("type", "time");

		return result;
	}

	@Override
	public JSONObject convertGlobeCoordinatesValueToJson(
			GlobeCoordinatesValue value) throws JSONException {
		JSONObject result = new JSONObject();
		JSONObject valueResult = new JSONObject();
		result.put("value", valueResult);

		valueResult.put("latitude", value.getLatitude());
		valueResult.put("longitude", value.getLongitude());
		valueResult.put("precision", value.getPrecision()
				/ GlobeCoordinatesValue.PREC_DEGREE);
		valueResult.put("globe", value.getGlobe());

		result.put("type", "globecoordinate");

		return result;
	}

	@Override
	public JSONObject convertEntityIdValueToJson(EntityIdValue value)
			throws JSONException {

		JSONObject result = new JSONObject();
		JSONObject valueResult = new JSONObject();

		result.put("type", "wikibase-entityid");
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

		result.put("value", valueResult);

		return result;
	}

	@Override
	public JSONObject convertStringValueToJson(StringValue value)
			throws JSONException {
		JSONObject result = new JSONObject();
		result.put("value", value.getString());
		result.put("type", "string");
		return result;
	}

	/*
	 * @Override public JSONObject
	 * convertIriIdentifiedValueToJson(IriIdentifiedValue value) throws
	 * JSONException { JSONObject result = new JSONObject();
	 * 
	 * return result; }
	 */

	@Override
	public JSONObject convertDatatypeIdValueToJson(DatatypeIdValue value)
			throws JSONException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject convertItemIdValueToJson(ItemIdValue value)
			throws JSONException {
		JSONObject result = new JSONObject();
		result.put("entity-type", "item"); // or value.getEntityType()
		result.put("numeric-id", value.getId());

		return result;
	}

	@Override
	public JSONObject convertMonolingualTextValueToJson(
			MonolingualTextValue value) throws JSONException {
		JSONObject result = new JSONObject();
		result.put("language", value.getLanguageCode());
		result.put("value", value.getText());
		return result;
	}

	@Override
	public JSONObject convertPropertyIdValueToJson(PropertyIdValue value)
			throws JSONException {
		JSONObject result = new JSONObject();
		result.put("entity-type", "property"); // or value.getEntityType()
		result.put("numeric-id", value.getId());

		return result;
	}

	@Override
	public JSONObject convertSiteLinkToJson(SiteLink link) throws JSONException {
		JSONObject result = new JSONObject();
		result.put("site", link.getSiteKey());
		result.put("title", link.getArticleTitle());
		result.put("badges", new JSONArray()); // always empty
		return result;
	}

	public String convertStatementRankToJson(StatementRank rank) {
		return rank.toString().toLowerCase();
	}

	@Override
	public JSONObject convertTermedDocumentToJson(TermedDocument document)
			throws JSONException {

		JSONObject result = new JSONObject();
		if (document instanceof ItemDocument) {
			result = convertItemDocumentToJson((ItemDocument) document);
		}
		if (document instanceof PropertyDocument) {
			result = convertPropertyDocumentToJson((PropertyDocument) document);
		}

		return result;
	}
	/*
	 * @Override public JSONObject convertValueToJson(Value value) throws
	 * JSONException { JSONObject result = new JSONObject(); // TODO checking
	 * type of Value and call function for converting value? return result; }
	 */

}
