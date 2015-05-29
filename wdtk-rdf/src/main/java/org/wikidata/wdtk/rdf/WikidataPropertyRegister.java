package org.wikidata.wdtk.rdf;

/*
 * #%L
 * Wikidata Toolkit RDF
 * %%
 * Copyright (C) 2014 - 2015 Wikidata Toolkit Developers
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;

/**
 * This class helps to manage information about Properties that has to obtained
 * by a webservice.
 * 
 * @author michael
 * 
 */
public class WikidataPropertyRegister implements PropertyRegister {

	static final Logger logger = LoggerFactory
			.getLogger(WikidataPropertyRegister.class);

	public static WikibaseDataFetcher dataFetcher = new WikibaseDataFetcher();

	final Map<String, String> datatypes = new HashMap<String, String>();

	final Map<String, String> uriPatterns = new HashMap<String, String>();

	final static int API_MAX_ENTITY_DOCUMENT_NUMBER = 50;

	int lowestPropertyIdNumber;

	WikidataPropertyRegister() {
		lowestPropertyIdNumber = 1;
	}

	void fetchPropertyInformation(PropertyIdValue startProperty) {
		List<String> propertyIds = new ArrayList<String>();

		propertyIds.add(startProperty.getId());
		for (int i = 1; i < API_MAX_ENTITY_DOCUMENT_NUMBER; i++) {
			propertyIds.add("P" + this.lowestPropertyIdNumber);
			this.lowestPropertyIdNumber++;
		}

		Map<String, EntityDocument> properties = dataFetcher
				.getEntityDocuments(propertyIds);

		// add some handling for the case that the proposed property was not
		// found

		for (String key : properties.keySet()) {
			EntityDocument property = properties.get(key);
			if (property instanceof PropertyDocument) {
				String datatype = ((PropertyDocument) property).getDatatype()
						.getIri();
				datatypes.put(key, datatype);
				if (datatype == DatatypeIdValue.DT_STRING) {
					Iterator<Statement> itr = ((PropertyDocument) property)
							.getAllStatements();
					while (itr.hasNext()) {
						Statement statement = itr.next();
						if (statement.getClaim().getMainSnak().getPropertyId()
								.getId() == "P_UNDEFINED") { // TODO replace
																// with actual
																// P_ID
							String uriPattern = ((StringValue) ((ValueSnak) statement
									.getClaim().getMainSnak()).getValue())
									.getString(); // should I insert some
													// instanceofs?
							uriPatterns.put(key, uriPattern);
						}
					}
				}
			}
		}
	}

	@Override
	public String getPropertyType(PropertyIdValue propertyIdValue) {
		if (!datatypes.containsKey(propertyIdValue.getId())) {
			fetchPropertyInformation(propertyIdValue);
		}
		return datatypes.get(propertyIdValue.getId());
	}

	@Override
	public void setPropertyType(PropertyIdValue propertyIdValue,
			String datatypeIri) {
		datatypes.put(propertyIdValue.getId(), datatypeIri);

	}

	@Override
	public String getPropertyUriPattern(PropertyIdValue propertyIdValue) {
		if (!uriPatterns.containsKey(propertyIdValue.getId())) {
			fetchPropertyInformation(propertyIdValue);
		}
		return uriPatterns.get(propertyIdValue.getId());

	}

	@Override
	public String setPropertyTypeFromEntityIdValue(
			PropertyIdValue propertyIdValue, EntityIdValue value) {
		switch (propertyIdValue.getId().charAt(0)) {
		case 'Q':
			return DatatypeIdValue.DT_ITEM;
		case 'P':
			return DatatypeIdValue.DT_PROPERTY;
		default:
			logger.warn("Could not determine Type of "
					+ propertyIdValue.getId()
					+ ". It is not a valid EntityDocument Id");
			return null;
		}
	}

	@Override
	public String setPropertyTypeFromGlobeCoordinatesValue(
			PropertyIdValue propertyIdValue, GlobeCoordinatesValue value) {
		return DatatypeIdValue.DT_GLOBE_COORDINATES;
	}

	@Override
	public String setPropertyTypeFromQuantityValue(
			PropertyIdValue propertyIdValue, QuantityValue value) {
		return DatatypeIdValue.DT_QUANTITY;
	}

	@Override
	public String setPropertyTypeFromStringValue(
			PropertyIdValue propertyIdValue, StringValue value) {
		String datatype = getPropertyType(propertyIdValue);
		if (datatype == null) {
			logger.warn("Could not fetch datatype of "
					+ propertyIdValue.getIri() + ". Assume type "
					+ DatatypeIdValue.DT_STRING);
			return DatatypeIdValue.DT_STRING; // default type for StringValue
		} else {
			return datatype;
		}
	}

	@Override
	public String setPropertyTypeFromTimeValue(PropertyIdValue propertyIdValue,
			TimeValue value) {
		return DatatypeIdValue.DT_TIME;
	}

	@Override
	public String setPropertyTypeFromMonolingualTextValue(
			PropertyIdValue propertyIdValue, MonolingualTextValue value) {
		return DatatypeIdValue.DT_MONOLINGUAL_TEXT;
	}

}
