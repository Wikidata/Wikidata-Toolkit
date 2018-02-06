package org.wikidata.wdtk.datamodel.helpers;

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

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Same as Jackson's celebrated ObjectMapper, except
 * that we add injections necessary to fill fields not
 * represented in JSON.
 * 
 * @author antonin
 *
 */
public class DatamodelMapper extends ObjectMapper {

	private static final long serialVersionUID = -236841297410109272L;
	
	/**
	 * Constructs a mapper with the given siteIri. This IRI
	 * will be used to fill all the siteIris of the entity ids
	 * contained in the payloads.
	 * 
	 * @param siteIri
	 * 		the ambient IRI of the Wikibase site
	 */
	public DatamodelMapper(String siteIri) {
		super();
		InjectableValues injection = new InjectableValues.Std()
				.addValue("siteIri", siteIri);
		this.setInjectableValues(injection);
	}
}
