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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

public interface Converter {
	
	public JSONObject convertClaimToJson(Claim claim)  throws JSONException;
	public JSONObject convertEntityDocumentToJson(EntityDocument entity)  throws JSONException;
	public JSONObject convertItemDocumentToJson(ItemDocument itemDocument)  throws JSONException;
	public JSONObject convertPropertyDocumentToJson(PropertyDocument propertyDocument)  throws JSONException;
	public JSONObject convertReferenceToJson(Reference ref)  throws JSONException;
	public JSONObject convertStatementToJson(Statement statement)  throws JSONException;
	public JSONArray convertStatementGroupToJson(StatementGroup statementGroup)  throws JSONException;
	public JSONObject convertSnakToJson(Snak snak) throws JSONException;
	public JSONObject convertValueSnakToJson(ValueSnak snak) throws JSONException;
	public JSONObject convertNoValueSnakToJson(NoValueSnak snak) throws JSONException;
	public JSONObject convertSomeValueSnakToJson(SomeValueSnak snak) throws JSONException;
	public JSONObject convertQuantityValueToJson(QuantityValue value) throws JSONException;
	public JSONObject convertTimeValueToJson(TimeValue value) throws JSONException;
	public JSONObject convertGlobeCoordinatesValueToJson(GlobeCoordinatesValue value) throws JSONException;
	public JSONObject convertEntityIdValueToJson(EntityIdValue value) throws JSONException;
	public JSONObject convertStringValueToJson(StringValue value) throws JSONException;
	public JSONObject convertIriIdentifiedValueToJson(IriIdentifiedValue value) throws JSONException;
	public JSONObject convertDatatypeIdValueToJson(DatatypeIdValue value) throws JSONException;
	public JSONObject convertItemIdValueToJson(ItemIdValue value) throws JSONException;
	public JSONObject convertMonolingualTextValueToJson(MonolingualTextValue value) throws JSONException;
	public JSONObject convertPropertyIdValueToJson(PropertyIdValue value) throws JSONException;
	public JSONObject convertSiteLinkToJson(SiteLink link) throws JSONException;
	public JSONObject convertTermedDocumentToJson(TermedDocument document) throws JSONException;
	public JSONObject convertValueToJson(Value value) throws JSONException;
	public String convertStatementRankToJson(StatementRank rank);
	
}
