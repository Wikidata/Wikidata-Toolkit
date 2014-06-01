package org.wikidata.wdtk.rdf.extensions;

/*
 * #%L
 * Wikidata Toolkit RDF
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

import org.wikidata.wdtk.datamodel.interfaces.StringValue;

/**
 * Export extension for converting Freebase identifiers into Freebase URIs.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class FreebaseExportExtension extends StringIdExportExtension {

	@Override
	public String getPropertyPostfix() {
		return "freebase";
	}

	@Override
	public String getValueUri(StringValue value) {
		return "http://rdf.freebase.com/ns/"
				+ value.getString().substring(1).replace('/', '.');
	}

}
