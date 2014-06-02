package org.wikidata.wdtk.datamodel.json;

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
import org.json.JSONObject;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;
import org.wikidata.wdtk.datamodel.interfaces.TermedDocument;

/**
 * Class for converting instances of {@link EntityDocument} to JSON for later
 * serialization. The JSON matches the official JSON format the Wikibase uses
 * for exporting data, e.g., through the Web API.
 * 
 * @author Michael Günther
 * 
 */
public class JsonConverter {

	final ValueJsonConverter valueJsonConverter = new ValueJsonConverter();
	final SnakJsonConverter snakJsonConverter = new SnakJsonConverter(
			this.valueJsonConverter);

	/**
	 * Returns a JSONObject that encodes the given {@link ItemDocument}.
	 * 
	 * @param itemDocument
	 *            the document to be converted
	 * @return corresponding JSON representation
	 */
	public JSONObject getJsonForItemDocument(ItemDocument document) {
		JSONObject result = new JSONObject();
		JSONObject statementGroups = new JSONObject();
		result = addTermedDocumentAttributes(document, result);
		result.put(JsonConstants.KEY_TYPE, JsonConstants.NAME_ENTITY_TYPE_ITEM);
		if (!document.getStatementGroups().isEmpty()) {
			result.put(JsonConstants.KEY_CLAIMS, statementGroups);
		}
		if (!document.getSiteLinks().isEmpty()) {
			result.put(JsonConstants.KEY_SITE_LINKS,
					convertSiteLinksToJson(document));
		}

		for (StatementGroup statementGroup : document.getStatementGroups()) {
			statementGroups.put(
					statementGroup.getProperty().getId().toString(),
					convertStatementGroupToJson(statementGroup));

		}
		return result;
	}

	/**
	 * Returns a JSONObject that encodes the given {@link PropertyDocument}.
	 * 
	 * @param propertyDocument
	 *            the document to be converted
	 * @return corresponding JSON representation
	 */
	public JSONObject getJsonForPropertyDocument(PropertyDocument document) {
		JSONObject result = new JSONObject();
		result.put(JsonConstants.KEY_TYPE,
				JsonConstants.NAME_ENTITY_TYPE_PROPERTY);
		result = addTermedDocumentAttributes(document, result);

		return result;
	}

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
				alias.put(value.accept(this.valueJsonConverter));
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
			result.put(
					key,
					document.getDescriptions().get(key)
							.accept(this.valueJsonConverter));
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
			result.put(
					key,
					document.getLabels().get(key)
							.accept(this.valueJsonConverter));
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
			result.put(key,
					getJsonForSiteLink(document.getSiteLinks().get(key)));
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
			String pId = snakGroup.getProperty().getId();
			JSONArray jsonArray = new JSONArray();
			result.put(pId, jsonArray);
			for (Snak snak : snakGroup.getSnaks()) {
				jsonArray.put(snak.accept(this.snakJsonConverter));
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
		result.put(JsonConstants.KEY_ID, document.getEntityId().getId());
		result.put(JsonConstants.KEY_TITLE, document.getEntityId().getId());
		if (!document.getAliases().isEmpty()) {
			result.put(JsonConstants.KEY_ALIASES,
					convertAliasesToJson(document));
		}
		if (!document.getDescriptions().isEmpty()) {
			result.put(JsonConstants.KEY_DESCRIPTIONS,
					convertDescriptionsToJson(document));
		}
		if (!document.getLabels().isEmpty()) {
			result.put(JsonConstants.KEY_LABELS, convertLabelsToJson(document));
		}
		return result;
	}

	/**
	 * Converts the attributes of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.Claim}
	 * 
	 * @param claim
	 * @return representation of an
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.Claim}
	 */
	JSONObject getJsonForClaim(Claim claim) {
		JSONObject result = new JSONObject();
		JSONArray order = new JSONArray();
		result.put(JsonConstants.KEY_MAINSNAK,
				claim.getMainSnak().accept(this.snakJsonConverter));
		if (!claim.getQualifiers().isEmpty()) {
			result.put(JsonConstants.KEY_QUALIFIERS,
					convertQualifiersToJson(claim.getQualifiers()));
			result.put(JsonConstants.KEY_QUALIFIERS_ORDER, order);
			for (SnakGroup snakGroup : claim.getQualifiers()) {
				order.put(snakGroup.getProperty().getId());
			}
		}
		return result;
	}

	/**
	 * Create a representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.Reference}
	 * 
	 * @param reference
	 * @return representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.Reference}
	 */
	JSONObject getJsonForReference(Reference reference) {

		JSONObject snaks = new JSONObject();
		JSONArray snakOrder = new JSONArray();

		for (SnakGroup snakGroup : reference.getSnakGroups()) {
			final String pId = snakGroup.getProperty().getId();
			final JSONArray group = new JSONArray();
			snaks.put(pId, group);
			snakOrder.put(pId);
			for (Snak snak : snakGroup.getSnaks()) {
				group.put(snak.accept(this.snakJsonConverter));
			}
		}

		JSONObject result = new JSONObject();
		result.put(JsonConstants.KEY_SNAKS, snaks);
		result.put(JsonConstants.KEY_SNAK_ORDER, snakOrder);
		return result;
	}

	/**
	 * Create a representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.Statement}
	 * 
	 * @param statement
	 * @return representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.Statement}
	 */
	JSONObject getJsonForStatement(Statement statement) {
		JSONObject result = new JSONObject();
		result = getJsonForClaim(statement.getClaim());
		result.put(JsonConstants.KEY_ID, statement.getStatementId());
		result.put(JsonConstants.KEY_TYPE, "statement");
		result.put("rank", convertStatementRankToJson(statement.getRank()));
		JSONArray references = new JSONArray();
		if (!statement.getReferences().isEmpty()) {
			result.put("references", references);
		}
		for (Reference ref : statement.getReferences()) {
			references.put(getJsonForReference(ref));
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
			statements.put(getJsonForStatement(statement));
		}
		return statements;
	}

	/**
	 * Create a representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.SiteLink}
	 * 
	 * @param link
	 * @return representation of
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.SiteLink}
	 */
	JSONObject getJsonForSiteLink(SiteLink link) {
		JSONObject result = new JSONObject();
		result.put("site", link.getSiteKey());
		result.put(JsonConstants.KEY_TITLE, link.getPageTitle());
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
			return JsonConstants.NAME_RANK_PREFERRED;
		case NORMAL:
			return JsonConstants.NAME_RANK_NORMAL;
		case DEPRECATED:
			return JsonConstants.NAME_RANK_DEPRECATED;
		default:
			throw new IllegalArgumentException("Unknown rank "
					+ rank.toString());
		}
	}
}
