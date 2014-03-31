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

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
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
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TermedDocument;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

/**
 * Implementation of {@link Converter} that provides a Conversions from Objects
 * constructed by the
 * {@link org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory} into a
 * json-format using JSONObjects from the org.json library.
 * 
 * @author Michael Günther
 * 
 */
public class ConverterImpl implements Converter<JSONObject> {

	final String NAME_ENTITY_TYPE_ITEM = "item";
	final String NAME_ENTITY_TYPE_PROPERTY = "property";

	final String NAME_RANK_NORMAL = "normal";
	final String NAME_RANK_DEPRECATED = "deprecated";
	final String NAME_RANK_PREFERRED = "preferred";

	final String KEY_ID = "id";
	final String KEY_TITLE = "title";
	final String KEY_CLAIMS = "claims";
	final String KEY_SNAKS = "snaks";
	final String KEY_SNAK_ORDER = "snak-order";
	final String KEY_ALIASES = "aliases";
	final String KEY_DESCRIPTIONS = "descriptions";
	final String KEY_SITE_LINKS = "sitelinks";
	final String KEY_LABELS = "labels";
	final String KEY_DATATYPE = "datatype";
	final String KEY_TYPE = "type";
	final String KEY_SNAK_TYPE = "snaktype";
	final String KEY_PROPERTY = "property";
	final String KEY_VALUE = "value";
	final String KEY_DATAVALUE = "datavalue";
	final String KEY_MAINSNAK = "mainsnak";
	final String KEY_QUALIFIERS = "qualifiers";

	final String STD_UNIT_VALUE = "1";

	/**
	 * Creates a json-representation for the aliases of an document.
	 * 
	 * @param document
	 * @return JSONObject representing aliases of a {@link TermedDocument}
	 */
	JSONObject convertAliasesToJson(TermedDocument document) {
		JSONObject result = new JSONObject();
		for (String key : document.getAliases().keySet()) {
			JSONArray alias = new JSONArray();
			result.put(key, alias);
			for (MonolingualTextValue value : document.getAliases().get(key)) {
				alias.put(visit(value));
			}
		}

		return result;
	}

	/**
	 * Creates a json-representation for the descriptions of an
	 * {@link TermedeDocument}.
	 * 
	 * @param document
	 * @return JSONObject representing descriptions of a {@link TermedDocument}
	 */
	JSONObject convertDescriptionsToJson(TermedDocument document) {
		JSONObject result = new JSONObject();
		for (String key : document.getDescriptions().keySet()) {
			result.put(key, visit(document.getDescriptions().get(key)));
		}
		return result;

	}

	/**
	 * Creates a json-representation for the labels of a {@link TermedDocument}.
	 * 
	 * @param document
	 * @return JSONObject of labels
	 */
	JSONObject convertLabelsToJson(TermedDocument document) {
		JSONObject result = new JSONObject();
		for (String key : document.getLabels().keySet()) {
			result.put(key, visit(document.getLabels().get(key)));
		}
		return result;
	}

	/**
	 * Creates a json-representation for the SiteLinks of an
	 * {@link ItemDocument}.
	 * 
	 * @param document
	 * @return JSONObject representation for
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.SiteLink} objects
	 */
	JSONObject convertSiteLinksToJson(ItemDocument document) {
		JSONObject result = new JSONObject();
		for (String key : document.getSiteLinks().keySet()) {
			result.put(key, visit(document.getSiteLinks().get(key)));
		}
		return result;
	}

	/**
	 * Creates a json-representation for qualifiers of a {@link Claim}.
	 * 
	 * @param qualifiers
	 * @return JSONObject of qualifiers
	 */
	JSONObject convertQualifiersToJson(List<SnakGroup> qualifiers) {
		JSONObject result = new JSONObject();

		for (SnakGroup snakGroup : qualifiers) {
			String pId = snakGroup.getProperty().getEntityType();
			JSONArray jsonArray = new JSONArray();
			result.put(pId, jsonArray);
			for (Snak snak : snakGroup.getSnaks()) {
				jsonArray.put(convertSnakToJson(snak));
			}
		}
		return result;
	}

	/**
	 * Adds the attributes occurring in every {@link TermedDocument} to elem.
	 * 
	 * @param document
	 * @param elem
	 * @return JSONObject with "aliases", "descriptions", "labels" key
	 */
	JSONObject addTermedDocumentAttributes(TermedDocument document,
			JSONObject elem) {
		JSONObject result = elem;
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

	@Override
	public JSONObject visit(Claim claim) {
		JSONObject result = new JSONObject();
		result.put(KEY_MAINSNAK, convertSnakToJson(claim.getMainSnak()));
		if (!claim.getQualifiers().isEmpty()) {
			result.put(KEY_QUALIFIERS,
					convertQualifiersToJson(claim.getQualifiers()));
		}
		return result;
	}

	@Override
	public JSONObject visit(ItemDocument document) {
		JSONObject result = new JSONObject();
		JSONObject statementGroups = new JSONObject();
		result = addTermedDocumentAttributes(document, result);
		result.put(KEY_TYPE, NAME_ENTITY_TYPE_ITEM);
		if (!document.getStatementGroups().isEmpty()) {
			result.put(KEY_CLAIMS, statementGroups);
		}
		if (!document.getSiteLinks().isEmpty()) {
			result.put(KEY_SITE_LINKS, convertSiteLinksToJson(document));
		}

		for (StatementGroup statementGroup : document.getStatementGroups()) {
			statementGroups.put(
					statementGroup.getProperty().getId().toString(),
					convertStatementGroupToJson(statementGroup));

		}
		return result;
	}

	@Override
	public JSONObject visit(PropertyDocument document) {
		JSONObject result = new JSONObject();
		result.put(KEY_TYPE, NAME_ENTITY_TYPE_PROPERTY);
		result = addTermedDocumentAttributes(document, result);

		return result;
	}

	@Override
	public JSONObject visit(Reference ref) {

		JSONObject snaks = new JSONObject();
		JSONArray snakOrder = new JSONArray();

		for (SnakGroup snakGroup : ref.getSnakGroups()) {
			final String pId = snakGroup.getProperty().getId();
			final JSONArray group = new JSONArray();
			snaks.put(pId, group);
			snakOrder.put(pId);
			for (Snak snak : snakGroup.getSnaks()) {
				group.put(convertSnakToJson(snak));
			}
		}

		JSONObject result = new JSONObject();
		result.put(KEY_SNAKS, snaks);
		result.put(KEY_SNAK_ORDER, snakOrder);
		return result;
	}

	@Override
	public JSONObject visit(Statement statement) {
		JSONObject result = new JSONObject();

		result.put(KEY_ID, statement.getStatementId());
		result.put(KEY_MAINSNAK, convertSnakToJson(statement.getClaim()
				.getMainSnak()));
		if (statement.getClaim().getQualifiers().isEmpty() == false) {
			result.put(KEY_QUALIFIERS, convertQualifiersToJson(statement
					.getClaim().getQualifiers()));
		}
		result.put(KEY_TYPE, "statement");
		result.put("rank", convertStatementRankToJson(statement.getRank()));
		JSONArray references = new JSONArray();
		if (!statement.getReferences().isEmpty()) {
			result.put("references", references);
		}
		for (Reference ref : statement.getReferences()) {
			references.put(visit(ref));
		}
		return result;
	}

	/**
	 * Creates a json-representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.StatementGroup}.
	 * 
	 * @param statementGroup
	 * @return Json representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.StatementGroup}
	 */
	JSONArray convertStatementGroupToJson(StatementGroup statementGroup) {
		JSONArray statements = new JSONArray();
		for (Statement statement : statementGroup.getStatements()) {
			statements.put(visit(statement));
		}
		return statements;
	}

	/**
	 * Creates a json-representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.ValueSnak},
	 * {@link org.wikidata.wdtk.datamodel.interfaces.NoValueSnak} or a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak}.
	 * 
	 * @param snak
	 * @return JSONObject representing for a specific
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.Snak}
	 */
	JSONObject convertSnakToJson(Snak snak) {
		JSONObject result;
		if (snak instanceof NoValueSnak) {
			result = visit((NoValueSnak) snak);
		} else if (snak instanceof SomeValueSnak) {
			result = visit((SomeValueSnak) snak);
		} else if (snak instanceof ValueSnak) {
			result = visit((ValueSnak) snak);
		} else {
			throw new IllegalArgumentException("Snaktype is unknown!");
		}
		return result;
	}

	@Override
	public JSONObject visit(ValueSnak snak) {
		JSONObject result = new JSONObject();
		result.put(KEY_SNAK_TYPE, "value");
		result.put(KEY_PROPERTY, snak.getPropertyId().getId());

		if (snak.getValue() instanceof EntityIdValue) {
			result.put(KEY_DATAVALUE,
					convertEntityIdValueToJson((EntityIdValue) snak.getValue()));
		} else if (snak.getValue() instanceof TimeValue) {
			result.put(KEY_DATAVALUE, visit((TimeValue) snak.getValue()));
		} else if (snak.getValue() instanceof GlobeCoordinatesValue) {
			result.put(KEY_DATAVALUE,
					visit((GlobeCoordinatesValue) snak.getValue()));
		} else if (snak.getValue() instanceof QuantityValue) {
			result.put(KEY_DATAVALUE, visit((QuantityValue) snak.getValue()));
		} else if (snak.getValue() instanceof StringValue) {
			result.put(KEY_DATAVALUE, visit((StringValue) snak.getValue()));
		} else {
			throw new IllegalArgumentException("class of the value "
					+ snak.getValue().getClass() + " is unknown");
		}

		// TODO put datatype in result (datatype of the property)

		return result;
	}

	@Override
	public JSONObject visit(NoValueSnak snak) {
		JSONObject result = new JSONObject();
		result.put(KEY_SNAK_TYPE, "novalue");
		result.put(KEY_PROPERTY, snak.getPropertyId().getId());
		return result;
	}

	@Override
	public JSONObject visit(SomeValueSnak snak) {
		JSONObject result = new JSONObject();
		result.put(KEY_SNAK_TYPE, "somevalue");
		result.put(KEY_PROPERTY, snak.getPropertyId().getId());
		return result;
	}

	@Override
	public JSONObject visit(QuantityValue value) {
		JSONObject result = new JSONObject();
		JSONObject valueResult = new JSONObject();

		result.put(KEY_VALUE, valueResult);

		valueResult.put("amount",
				DatatypeConverters.formatBigDecimal(value.getNumericValue()));
		valueResult.put("unit", STD_UNIT_VALUE);
		valueResult.put("upperBound",
				DatatypeConverters.formatBigDecimal(value.getUpperBound()));
		valueResult.put("lowerBound",
				DatatypeConverters.formatBigDecimal(value.getLowerBound()));

		result.put(KEY_TYPE, "quantity");

		return result;
	}

	@Override
	public JSONObject visit(TimeValue value) {
		JSONObject result = new JSONObject();
		JSONObject valueResult = new JSONObject();

		result.put(KEY_VALUE, valueResult);

		valueResult.put("time", DatatypeConverters.formatTimeISO8601(value));
		valueResult.put("timezone", value.getTimezoneOffset());
		valueResult.put("before", value.getBeforeTolerance());
		valueResult.put("after", value.getAfterTolerance());
		valueResult.put("precision", value.getPrecision());
		valueResult.put("calendarmodel", value.getPreferredCalendarModel());

		result.put(KEY_TYPE, "time");

		return result;
	}

	@Override
	public JSONObject visit(GlobeCoordinatesValue value) {
		JSONObject result = new JSONObject();
		JSONObject valueResult = new JSONObject();
		result.put(KEY_VALUE, valueResult);

		valueResult
				.put("latitude",
						((double) value.getLatitude() / GlobeCoordinatesValue.PREC_DEGREE));
		valueResult.put("longitude", (double) value.getLongitude()
				/ GlobeCoordinatesValue.PREC_DEGREE);
		valueResult.put("precision", (double) value.getPrecision()
				/ GlobeCoordinatesValue.PREC_DEGREE);
		valueResult.put("globe", value.getGlobe());

		result.put(KEY_TYPE, "globecoordinate");

		return result;
	}

	/**
	 * Creates a json-representation of an
	 * {@link org.wikidata.wdtk.datamodel.interfaces.EntityIdValue}.
	 * 
	 * @param value
	 * @return json-representation of an
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.EntityIdValue}
	 */
	JSONObject convertEntityIdValueToJson(EntityIdValue value) {

		JSONObject result = new JSONObject();
		JSONObject valueResult = new JSONObject();

		result.put(KEY_TYPE, "wikibase-entityid");
		switch (value.getEntityType()) {
		case EntityIdValue.ET_ITEM:
			valueResult = visit((ItemIdValue) value);
			break;
		case EntityIdValue.ET_PROPERTY:
			valueResult = visit((PropertyIdValue) value);
			break;
		default:
			throw new JSONException("Unknown EntityType: "
					+ value.getEntityType());
		}

		result.put(KEY_VALUE, valueResult);

		return result;
	}

	@Override
	public JSONObject visit(StringValue value) {
		JSONObject result = new JSONObject();
		result.put(KEY_VALUE, value.getString());
		result.put(KEY_TYPE, "string");
		return result;
	}

	@Override
	public JSONObject visit(DatatypeIdValue value) {
		// TODO implement
		return new JSONObject(); // empty
	}

	@Override
	public JSONObject visit(ItemIdValue value) {
		JSONObject result = new JSONObject();
		result.put("entity-type", NAME_ENTITY_TYPE_ITEM);
		result.put("numeric-id", value.getId());

		return result;
	}

	@Override
	public JSONObject visit(MonolingualTextValue value) {
		JSONObject result = new JSONObject();
		result.put("language", value.getLanguageCode());
		result.put(KEY_VALUE, value.getText());
		return result;
	}

	@Override
	public JSONObject visit(PropertyIdValue value) {
		JSONObject result = new JSONObject();
		result.put("entity-type", NAME_ENTITY_TYPE_PROPERTY);
		result.put("numeric-id", value.getId());

		return result;
	}

	@Override
	public JSONObject visit(SiteLink link) {
		JSONObject result = new JSONObject();
		result.put("site", link.getSiteKey());
		result.put(KEY_TITLE, link.getArticleTitle());
		result.put("badges", new JSONArray()); // always empty at the moment
		return result;
	}

	/**
	 * Creates a string notation for
	 * {@link org.wikidata.wdtk.datamodel.interfaces.StatementRank}.
	 * 
	 * @param rank
	 * 
	 * @return {@link org.wikidata.wdtk.datamodel.interfaces.StatementRank} in
	 *         string notation for the json-format
	 * 
	 */
	String convertStatementRankToJson(StatementRank rank) {
		switch (rank) {
		case PREFERRED:
			return NAME_RANK_PREFERRED;
		case NORMAL:
			return NAME_RANK_NORMAL;
		case DEPRECATED:
			return NAME_RANK_DEPRECATED;
		default:
			throw new IllegalArgumentException("Unknown rank "
					+ rank.toString());
		}
	}
}
