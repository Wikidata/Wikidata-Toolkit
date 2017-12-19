package org.wikidata.wdtk.dumpfiles.constraint.format;

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

import org.wikidata.wdtk.dumpfiles.constraint.builder.ConstraintMainBuilder;
import org.wikidata.wdtk.rdf.Vocabulary;

public interface RdfStringConstant {

	String AUX = "aux";

	String PREFIX_WIKIDATA = ConstraintMainBuilder.PREFIX_WIKIDATA; // "http://www.wikidata.org/entity/";
	String PREFIX_XML = "http://www.w3.org/XML/1998/namespace";

	String PREFIX_OWL = Vocabulary.PREFIX_OWL; // "http://www.w3.org/2002/07/owl#";
	String PREFIX_RDF = Vocabulary.PREFIX_RDF; // "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	String PREFIX_RDFS = Vocabulary.PREFIX_RDFS; // "http://www.w3.org/2000/01/rdf-schema#";
	String PREFIX_XSD = Vocabulary.PREFIX_XSD; // "http://www.w3.org/2001/XMLSchema#";
	String PREFIX_WBONTO = Vocabulary.PREFIX_WBONTO; // "http://www.wikidata.org/ontology#";

	String OWL_ANNOTATION_PROPERTY = PREFIX_OWL + "AnnotationProperty";
	String OWL_CLASS = PREFIX_OWL + "Class";
	String OWL_COMPLEMENT_OF = PREFIX_OWL + "complementOf";
	String OWL_DATATYPE_PROPERTY = PREFIX_OWL + "DatatypeProperty";
	String OWL_DISJOINT_WITH = PREFIX_OWL + "disjointWith";
	String OWL_EQUIVALENT_CLASS = PREFIX_OWL + "equivalentClass";
	String OWL_FUNCTIONAL_PROPERTY = PREFIX_OWL + "FunctionalProperty";
	String OWL_HAS_KEY = PREFIX_OWL + "hasKey";
	String OWL_INTERSECTION_OF = PREFIX_OWL + "intersectionOf";
	String OWL_INVERSE_FUNCTIONAL_PROPERTY = PREFIX_OWL
			+ "InverseFunctionalProperty";
	String OWL_NAMED_INDIVIDUAL = PREFIX_OWL + "NamedIndividual";
	String OWL_OBJECT_PROPERTY = PREFIX_OWL + "ObjectProperty";
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
	String RDF_NIL = PREFIX_RDF + "nil";
	String RDF_REST = PREFIX_RDF + "rest";
	String RDF_TYPE = PREFIX_RDF + "type";
	String RDFS_COMMENT = PREFIX_RDFS + "comment";
	String RDFS_DATATYPE = PREFIX_RDFS + "Datatype";
	String RDFS_DOMAIN = PREFIX_RDFS + "domain";
	String RDFS_RANGE = PREFIX_RDFS + "range";
	String RDFS_SUB_CLASS_OF = PREFIX_RDFS + "subClassOf";
	String WB_TIME_VALUE = Vocabulary.WB_TIME_VALUE;
	String WB_QUANTITY_VALUE = Vocabulary.WB_QUANTITY_VALUE;
	String XSD_DATE_TIME = PREFIX_XSD + "dateTime";
	String XSD_DECIMAL = PREFIX_XSD + "decimal";
	String XSD_INTEGER = PREFIX_XSD + "integer";
	String XSD_MAX_INCLUSIVE = PREFIX_XSD + "maxInclusive";
	String XSD_MIN_INCLUSIVE = PREFIX_XSD + "minInclusive";
	String XSD_PATTERN = PREFIX_XSD + "pattern";
	String XSD_STRING = PREFIX_XSD + "string";

	String OWL = "owl";
	String RDF = "rdf";
	String XML = "xml";
	String XSD = "xsd";
	String RDFS = "rdfs";
	String WO = "wo";
	String ID = "id";

}
