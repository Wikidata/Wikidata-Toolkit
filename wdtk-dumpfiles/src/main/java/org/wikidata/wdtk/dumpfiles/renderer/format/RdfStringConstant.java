package org.wikidata.wdtk.dumpfiles.renderer.format;

/*
 * #%L
 * Wikidata Toolkit Dump File Handling
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

import org.wikidata.wdtk.rdf.Vocabulary;

public interface RdfStringConstant {

	// FIXME some of these constants may need to be removed

	static final String PREFIX_WIKIDATA = "http://www.wikidata.org/entity/";

	String PREFIX_OWL = Vocabulary.PREFIX_OWL; // "http://www.w3.org/2002/07/owl#";
	String PREFIX_PROV = Vocabulary.PREFIX_PROV; // "http://www.w3.org/ns/prov#";
	String PREFIX_RDF = Vocabulary.PREFIX_RDF; // "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	String PREFIX_RDFS = Vocabulary.PREFIX_RDFS; // "http://www.w3.org/2000/01/rdf-schema#";
	String PREFIX_SCHEMA = Vocabulary.PREFIX_SCHEMA; // "http://schema.org/";
	String PREFIX_SKOS = Vocabulary.PREFIX_SKOS; // "http://www.w3.org/2004/02/skos/core#";
	String PREFIX_WBONTO = Vocabulary.PREFIX_WBONTO; // "http://www.wikidata.org/ontology#";
	String PREFIX_XSD = Vocabulary.PREFIX_XSD; // "http://www.w3.org/2001/XMLSchema#";
	String OWL_ANNOTATION_COMMENT = PREFIX_OWL + "annotationComment";
	String OWL_ANNOTATION_PROPERTY = PREFIX_OWL + "AnnotationProperty";
	String OWL_CLASS = PREFIX_OWL + "Class";
	String OWL_COMPLEMENT_OF = PREFIX_OWL + "complementOf";
	String OWL_DATA_PROPERTY_RANGE = PREFIX_OWL + "dataPropertyRange";
	String OWL_DATATYPE_PROPERTY = PREFIX_OWL + "DatatypeProperty";
	String OWL_DECLARATION = PREFIX_OWL + "declaration";
	String OWL_DESCRIPTION = PREFIX_OWL + "Description";
	String OWL_DISJOINT_WITH = PREFIX_OWL + "disjointWith";
	String OWL_EQUIVALENT_CLASS = PREFIX_OWL + "equivalentClass";
	String OWL_FUNCTIONAL_PROPERTY = PREFIX_OWL + "FunctionalProperty";
	String OWL_HAS_KEY = PREFIX_OWL + "hasKey";
	String OWL_INTERSECTION_OF = PREFIX_OWL + "intersectionOf";
	String OWL_INVERSE_FUNCTIONAL_PROPERTY = PREFIX_OWL
			+ "InverseFunctionalProperty";
	String OWL_NAMED_INDIVIDUAL = PREFIX_OWL + "NamedIndividual";
	String OWL_OBJECT_COMPLEMENT_OF = PREFIX_OWL + "objectComplementOf";
	String OWL_OBJECT_PROPERTY = PREFIX_OWL + "ObjectProperty";
	String OWL_OBJECT_PROPERTY_DOMAIN = PREFIX_OWL + "objectPropertyDomain";
	String OWL_OBJECT_PROPERTY_RANGE = PREFIX_OWL + "objectPropertyRange";
	String OWL_ON_CLASS = PREFIX_OWL + "onClass";
	String OWL_ON_DATATYPE = PREFIX_OWL + "onDatatype";
	String OWL_ON_PROPERTY = PREFIX_OWL + "onProperty";
	String OWL_ONE_OF = PREFIX_OWL + "oneOf";
	String OWL_QUALIFIED_CARDINALITY = PREFIX_OWL + "qualifiedCardinality";
	String OWL_RESTRICTION = PREFIX_OWL + "Restriction";
	String OWL_SOME_VALUES_FROM = PREFIX_OWL + "someValuesFrom";
	String OWL_THING = PREFIX_OWL + "Thing";
	String OWL_UNION_OF = PREFIX_OWL + "unionOf";
	String OWL_WITH_RESTRICTIONS = PREFIX_OWL + "withRestrictions";
	String RDF_FIRST = PREFIX_RDF + "first";
	String RDF_LIST = PREFIX_RDF + "List";
	String RDF_PARSE_TYPE = PREFIX_RDF + "parseType";
	String RDF_PROPERTY = PREFIX_RDF + "Property";
	String RDF_REST = PREFIX_RDF + "rest";
	String RDF_TYPE = PREFIX_RDF + "type";
	String RDFS_CLASS = PREFIX_RDFS + "Class";
	String RDFS_COMMENT = PREFIX_RDFS + "comment";
	String RDFS_CONTAINER_MEMBERSHIP_PROPERTY = PREFIX_RDFS
			+ "ContainerMembershipProperty";
	String RDFS_DATATYPE = PREFIX_RDFS + "Datatype";
	String RDFS_DOMAIN = PREFIX_RDFS + "domain";
	String RDFS_LABEL = PREFIX_RDFS + "label";
	String RDFS_LITERAL = PREFIX_RDFS + "Literal";
	String RDFS_MEMBER = PREFIX_RDFS + "member";
	String RDFS_RANGE = PREFIX_RDFS + "range";
	String RDFS_RESOURCE = PREFIX_RDFS + "Resource";
	String RDFS_SUB_CLASS_OF = PREFIX_RDFS + "subClassOf";
	String RDFS_SUB_PROPERTY_OF = PREFIX_RDFS + "subPropertyOf";
	String VALUE_PREFIX_GLOBECOORDS = "VC";
	String VALUE_PREFIX_QUANTITY = "VQ";
	String VALUE_PREFIX_REFERENCE = "R";
	String VALUE_PREFIX_TIME = "VT";
	String XSD_DATE = PREFIX_XSD + "date";
	String XSD_DATE_TIME = PREFIX_XSD + "dateTime";
	String XSD_DATETIME = PREFIX_XSD + "dateTime";
	String XSD_DECIMAL = PREFIX_XSD + "decimal";
	String XSD_G_YEAR = PREFIX_XSD + "gYear";
	String XSD_G_YEAR_MONTH = PREFIX_XSD + "gYearMonth";
	String XSD_INT = PREFIX_XSD + "int";
	String XSD_MAX_INCLUSIVE = PREFIX_XSD + "maxInclusive";
	String XSD_MIN_INCLUSIVE = PREFIX_XSD + "minInclusive";
	String XSD_PATTERN = PREFIX_XSD + "pattern";
	String XSD_STRING = PREFIX_XSD + "string";

}
