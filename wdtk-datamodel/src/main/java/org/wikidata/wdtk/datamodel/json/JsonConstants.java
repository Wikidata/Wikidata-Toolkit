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

/**
 * This interface collects constants that are used in the Wikibase JSON format.
 * This refers to the official JSON format the Wikibase uses for exporting data,
 * e.g., through the Web API.
 * 
 * @author Markus Kroetzsch
 * 
 */
public interface JsonConstants {

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
	final String KEY_QUALIFIERS_ORDER = "qualifiers-order";
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
}
