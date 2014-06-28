package org.wikidata.wdtk.dumpfiles.constraint.format;

import org.wikidata.wdtk.dumpfiles.constraint.builder.ConstraintMainBuilder;
import org.wikidata.wdtk.rdf.Vocabulary;

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

/**
 * 
 * @author Julian Mendez
 * 
 */
public interface Owl2FunctionalConstant {

	String C_CARET = "^";
	String C_LT = "<";
	String C_GT = ">";
	String C_COLON_EQUALS = ":=";
	String C_PAR_A = "(";
	String C_PAR_B = ")";
	String C_QUOTATION_MARK = "\"";
	String C_SPACE = " ";
	String C_BACKSLASH = "\\";
	String AUX = "aux";

	String PREFIX_WIKIDATA = ConstraintMainBuilder.PREFIX_WIKIDATA; // "http://www.wikidata.org/entity/";
	String PREFIX_CONSTRAINTS = PREFIX_WIKIDATA + "constraints/"; // "http://www.wikidata.org/entity/constraints/";
	String PREFIX_ONTOLOGY = PREFIX_CONSTRAINTS + "ont"; // "http://www.wikidata.org/entity/constraints/ont";
	String PREFIX_XML = "http://www.w3.org/XML/1998/namespace";

	String PREFIX_OWL = Vocabulary.PREFIX_OWL; // "http://www.w3.org/2002/07/owl#";
	String PREFIX_RDF = Vocabulary.PREFIX_RDF; // "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	String PREFIX_RDFS = Vocabulary.PREFIX_RDFS; // "http://www.w3.org/2000/01/rdf-schema#";
	String PREFIX_XSD = Vocabulary.PREFIX_XSD; // "http://www.w3.org/2001/XMLSchema#";
	String PREFIX_WBONTO = Vocabulary.PREFIX_WBONTO; // "http://www.wikidata.org/ontology#";

	String ANNOTATION_ASSERTION = "AnnotationAssertion";
	String ANNOTATION_PROPERTY = "AnnotationProperty";
	String CLASS = "Class";
	String DATA_INTERSECTION_OF = "DataIntersectionOf";
	String DATA_ONE_OF = "DataOneOf";
	String DATA_PROPERTY = "DataProperty";
	String DATA_PROPERTY_RANGE = "DataPropertyRange";
	String DATA_SOME_VALUES_FROM = "DataSomeValuesFrom";
	String DATATYPE = "Datatype";
	String DATATYPE_DEFINITION = "DatatypeDefinition";
	String DATATYPE_RESTRICTION = "DatatypeRestriction";
	String DECLARATION = "Declaration";
	String DISJOINT_CLASSES = "DisjointClasses";
	String FUNCTIONAL_OBJECT_PROPERTY = "FunctionalObjectProperty";
	String HAS_KEY = "HasKey";
	String INVERSE_FUNCTIONAL_OBJECT_PROPERTY = "InverseFunctionalObjectProperty";
	String NAMED_INDIVIDUAL = "NamedIndividual";
	String OBJECT_COMPLEMENT_OF = "ObjectComplementOf";
	String OBJECT_EXACT_CARDINALITY = "ObjectExactCardinality";
	String OBJECT_ONE_OF = "ObjectOneOf";
	String OBJECT_PROPERTY = "ObjectProperty";
	String OBJECT_PROPERTY_DOMAIN = "ObjectPropertyDomain";
	String OBJECT_PROPERTY_RANGE = "ObjectPropertyRange";
	String OBJECT_SOME_VALUES_FROM = "ObjectSomeValuesFrom";
	String OBJECT_UNION_OF = "ObjectUnionOf";
	String OWL_THING = PREFIX_OWL + "Thing";
	String SUB_CLASS_OF = "SubClassOf";
	String RDFS_COMMENT = PREFIX_RDFS + "comment";
	String WB_TIME_VALUE = Vocabulary.WB_TIME_VALUE;
	String WB_QUANTITY_VALUE = Vocabulary.WB_QUANTITY_VALUE;
	String XSD_DATE_TIME = PREFIX_XSD + "dateTime";
	String XSD_DECIMAL = PREFIX_XSD + "decimal";
	String XSD_MAX_INCLUSIVE = PREFIX_XSD + "maxInclusive";
	String XSD_MIN_INCLUSIVE = PREFIX_XSD + "minInclusive";
	String XSD_PATTERN = PREFIX_XSD + "pattern";
	String XSD_STRING = PREFIX_XSD + "string";

	String PREFIX = "Prefix";
	String ONTOLOGY = "Ontology";

	String OWL = "owl";
	String RDF = "rdf";
	String XML = "xml";
	String XSD = "xsd";
	String RDFS = "rdfs";
	String WO = "wo";
	String ID = "id";

}
