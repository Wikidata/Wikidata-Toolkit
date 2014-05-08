package org.wikidata.wdtk.dumpfiles.renderer.constraint;

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
public interface ConstraintRendererConstant {

	String C_CARET = "^";
	String C_PAR_A = "(";
	String C_PAR_B = ")";
	String C_QUOTATION_MARK = "\"";
	String C_SPACE = " ";
	String DATA_INTERSECTION_OF = "DataIntersectionOf";
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
	String OBJECT_COMPLEMENT_OF = "ObjectComplementOf";
	String OBJECT_EXACT_CARDINALITY = "ObjectExactCardinality";
	String OBJECT_ONE_OF = "ObjectOneOf";
	String OBJECT_PROPERTY_DOMAIN = "ObjectPropertyDomain";
	String OBJECT_PROPERTY_RANGE = "ObjectPropertyRange";
	String OBJECT_SOME_VALUES_FROM = "ObjectSomeValuesFrom";
	String OBJECT_UNION_OF = "ObjectUnionOf";
	String OWL_THING = "owl:Thing";
	String SUB_CLASS_OF = "SubClassOf";
	String XSD_DATE_TIME = "xsd:dateTime";
	String XSD_DECIMAL = "xsd:decimal";
	String XSD_MAX_INCLUSIVE = "xsd:maxInclusive";
	String XSD_MIN_INCLUSIVE = "xsd:minInclusive";
	String XSD_PATTERN = "xsd:pattern";
	String XSD_STRING = "xsd:string";

}
