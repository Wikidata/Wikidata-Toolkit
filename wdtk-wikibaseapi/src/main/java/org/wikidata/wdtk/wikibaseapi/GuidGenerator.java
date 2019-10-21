package org.wikidata.wdtk.wikibaseapi;

/*
 * #%L
 * Wikidata Toolkit Wikibase API
 * %%
 * Copyright (C) 2014 - 2018 Wikidata Toolkit Developers
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

public interface GuidGenerator {
	
	String STATEMENT_GUID_SEPARATOR = "$";
	
	/**
	 * Generates a fresh statement id. This consists of a first part
	 * with the entity id of the item the statement belongs to, the separator $, plus
	 * a random hash of the form
	 * /^\{?[A-Z\d]{8}-[A-Z\d]{4}-[A-Z\d]{4}-[A-Z\d]{4}-[A-Z\d]{12}\}?\z/
	 * @param entityId
	 *          the entity the statement belongs to
	 * @return a fresh UUID in the required format.
	 */
	String freshStatementId(String entityId);
}
