package org.wikidata.wdtk.datamodel.helpers;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;

/*
 * #%L
 * Wikidata Toolkit Data Model
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

import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;

/**
 * A helper to modify (copies of) TermedDocument and StatementDocument instances.
 * 
 * @author antonin
 */
public class EntityDocumentBuilderFactory {
	
	/**
	 * Returns a builder to create a copy of a given EntityDocument
	 * 
	 * @param document
	 * 		the document to copy
	 * @return
	 *       a builder that can be used to modify a copy of the given instance
	 */
	public static
	TermedStatementDocumentBuilder<?,?> builderForDocument(EntityDocument document) {
		if(ItemDocument.class.isInstance(document)) {
			return  ItemDocumentBuilder.fromItemDocument((ItemDocument)document);
		} else if(PropertyDocument.class.isInstance(document)) {
			return PropertyDocumentBuilder.fromPropertyDocument((PropertyDocument)document);
		} else {
			return null;
		}
	}
}
