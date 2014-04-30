package org.wikidata.wdtk.rdf;

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

import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

/**
 * This class contains static methods and constants that define the various OWL
 * and RDF vocabularies that are used in the export.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class Vocabulary {

	// Prefixes
	public static final String PREFIX_WIKIDATA = "http://www.wikidata.org/entity/";
	public static final String PREFIX_WBONTO = "http://www.wikidata.org/ontology#";
	public static final String PREFIX_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static final String PREFIX_RDFS = "http://www.w3.org/2000/01/rdf-schema#";
	public static final String PREFIX_OWL = "http://www.w3.org/2002/07/owl#";
	public static final String PREFIX_XSD = "http://www.w3.org/2001/XMLSchema#";
	public static final String PREFIX_SCHEMA = "http://schema.org/";
	public static final String PREFIX_SKOS = "http://www.w3.org/2004/02/skos/core#";
	public static final String PREFIX_PROV = "http://www.w3.org/ns/prov#";

	// Specific vocabulary elements
	public static final String RDF_TYPE = PREFIX_RDF + "type";
	public static final String RDFS_LABEL = PREFIX_RDFS + "label";
	public static final String OWL_OBJECT_PROPERTY = PREFIX_OWL
			+ "ObjectProperty";
	public static final String OWL_DATATYPE_PROPERTY = PREFIX_OWL
			+ "DatatypeProperty";
	public static final String SKOS_ALT_LABEL = PREFIX_SKOS + "altLabel";
	public static final String SCHEMA_ABOUT = PREFIX_SCHEMA + "about";
	public static final String SCHEMA_DESCRIPTION = PREFIX_SCHEMA
			+ "description";
	public static final String SCHEMA_IN_LANGUAGE = PREFIX_SCHEMA
			+ "inLanguage";
	public static final String PROV_WAS_DERIVED_FROM = PREFIX_PROV
			+ "wasDerivedFrom";

	// Wikibase ontology
	public static final String WB_ITEM = PREFIX_WBONTO + "Item";
	public static final String WB_PROPERTY = PREFIX_WBONTO + "Property";
	public static final String WB_STATEMENT = PREFIX_WBONTO + "Statement";
	public static final String WB_PROPERTY_TYPE = PREFIX_WBONTO
			+ "propertyType";
	public static final String WB_GLOBE = PREFIX_WBONTO + "globe";
	public static final String WB_LATITUDE = PREFIX_WBONTO + "latitude";
	public static final String WB_LONGITUDE = PREFIX_WBONTO + "longitude";
	public static final String WB_GC_PRECISION = PREFIX_WBONTO + "gcPrecision";
	public static final String WB_TIME = PREFIX_WBONTO + "time";
	public static final String WB_TIME_PRECISION = PREFIX_WBONTO
			+ "timePrecision";
	public static final String WB_PREFERRED_CALENDAR = PREFIX_WBONTO
			+ "preferredCalendar";

	public static String getEntityUri(EntityIdValue entityIdValue) {
		return PREFIX_WIKIDATA + entityIdValue.getId();
	}

	public static String getStatementUri(Statement statement) {
		return PREFIX_WIKIDATA + statement.getStatementId();
	}

	public static String getPropertyUri(PropertyIdValue propertyIdValue,
			PropertyContext propertyContext) {
		switch (propertyContext) {
		case STATEMENT:
			return PREFIX_WIKIDATA + propertyIdValue.getId() + "s";
		case VALUE:
			return PREFIX_WIKIDATA + propertyIdValue.getId() + "v";
		case QUALIFIER:
			return PREFIX_WIKIDATA + propertyIdValue.getId() + "q";
		case REFERENCE:
			return PREFIX_WIKIDATA + propertyIdValue.getId() + "r";
		default:
			return null;
		}

	}
}
