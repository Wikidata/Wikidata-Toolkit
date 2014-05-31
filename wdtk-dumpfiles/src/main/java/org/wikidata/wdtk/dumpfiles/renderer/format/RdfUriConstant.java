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

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.wikidata.wdtk.rdf.Vocabulary;

public interface RdfUriConstant {

	static final ValueFactory factory = ValueFactoryImpl.getInstance();

	URI OWL_ANNOTATION_COMMENT = factory
			.createURI(RdfStringConstant.OWL_ANNOTATION_COMMENT);
	URI OWL_ANNOTATION_PROPERTY = factory
			.createURI(RdfStringConstant.OWL_ANNOTATION_PROPERTY);
	URI OWL_CLASS = factory.createURI(RdfStringConstant.OWL_CLASS);
	URI OWL_COMPLEMENT_OF = factory
			.createURI(RdfStringConstant.OWL_COMPLEMENT_OF);
	URI OWL_DATA_INTERSECTION_OF = factory
			.createURI(RdfStringConstant.OWL_DATA_INTERSECTION_OF);
	URI OWL_DATA_PROPERTY_RANGE = factory
			.createURI(RdfStringConstant.OWL_DATA_PROPERTY_RANGE);
	URI OWL_DATATYPE_DEFINITION = factory
			.createURI(RdfStringConstant.OWL_DATATYPE_DEFINITION);
	URI OWL_DATATYPE_PROPERTY = factory
			.createURI(RdfStringConstant.OWL_DATATYPE_PROPERTY);
	URI OWL_DATATYPE_RESTRICTION = factory
			.createURI(RdfStringConstant.OWL_DATATYPE_RESTRICTION);
	URI OWL_DECLARATION = factory.createURI(RdfStringConstant.OWL_DECLARATION);
	URI OWL_DESCRIPTION = factory.createURI(RdfStringConstant.OWL_DESCRIPTION);
	URI OWL_DISJOINT_CLASSES = factory
			.createURI(RdfStringConstant.OWL_DISJOINT_CLASSES);
	URI OWL_FUNCTIONAL_PROPERTY = factory
			.createURI(RdfStringConstant.OWL_FUNCTIONAL_PROPERTY);
	URI OWL_HAS_KEY = factory.createURI(RdfStringConstant.OWL_HAS_KEY);
	URI OWL_INVERSE_FUNCTIONAL_PROPERTY = factory
			.createURI(RdfStringConstant.OWL_INVERSE_FUNCTIONAL_PROPERTY);
	URI OWL_NAMED_INDIVIDUAL = factory
			.createURI(RdfStringConstant.OWL_NAMED_INDIVIDUAL);
	URI OWL_OBJECT_COMPLEMENT_OF = factory
			.createURI(RdfStringConstant.OWL_OBJECT_COMPLEMENT_OF);
	URI OWL_OBJECT_PROPERTY = factory
			.createURI(RdfStringConstant.OWL_OBJECT_PROPERTY);
	URI OWL_OBJECT_PROPERTY_DOMAIN = factory
			.createURI(RdfStringConstant.OWL_OBJECT_PROPERTY_DOMAIN);
	URI OWL_OBJECT_PROPERTY_RANGE = factory
			.createURI(RdfStringConstant.OWL_OBJECT_PROPERTY_RANGE);
	URI OWL_ON_CLASS = factory.createURI(RdfStringConstant.OWL_ON_CLASS);
	URI OWL_ON_DATATYPE = factory.createURI(RdfStringConstant.OWL_ON_DATATYPE);
	URI OWL_ON_PROPERTY = factory.createURI(RdfStringConstant.OWL_ON_PROPERTY);
	URI OWL_ONE_OF = factory.createURI(RdfStringConstant.OWL_ONE_OF);
	URI OWL_QUALIFIED_CARDINALITY = factory
			.createURI(RdfStringConstant.OWL_QUALIFIED_CARDINALITY);
	URI OWL_RESTRICTION = factory.createURI(RdfStringConstant.OWL_RESTRICTION);
	URI OWL_SOME_VALUES_FROM = factory
			.createURI(RdfStringConstant.OWL_SOME_VALUES_FROM);
	URI OWL_THING = factory.createURI(RdfStringConstant.OWL_THING);
	URI OWL_UNION_OF = factory.createURI(RdfStringConstant.OWL_UNION_OF);
	URI OWL_WITH_RESTRICTIONS = factory
			.createURI(RdfStringConstant.OWL_WITH_RESTRICTIONS);
	URI PROV_WAS_DERIVED_FROM = factory
			.createURI(Vocabulary.PROV_WAS_DERIVED_FROM);
	URI RDF_FIRST = factory.createURI(RdfStringConstant.RDF_FIRST);
	URI RDF_LIST = factory.createURI(RdfStringConstant.RDF_LIST);
	URI RDF_PARSE_TYPE = factory.createURI(RdfStringConstant.RDF_PARSE_TYPE);
	URI RDF_PROPERTY = factory.createURI(RdfStringConstant.RDF_PROPERTY);
	URI RDF_REST = factory.createURI(RdfStringConstant.RDF_REST);
	URI RDF_TYPE = factory.createURI(RdfStringConstant.RDF_TYPE);
	URI RDFS_CLASS = factory.createURI(RdfStringConstant.RDFS_CLASS);
	URI RDFS_COMMENT = factory.createURI(RdfStringConstant.RDFS_COMMENT);
	URI RDFS_CONTAINER_MEMBERSHIP_PROPERTY = factory
			.createURI(RdfStringConstant.RDFS_CONTAINER_MEMBERSHIP_PROPERTY);
	URI RDFS_DATATYPE = factory.createURI(RdfStringConstant.RDFS_DATATYPE);
	URI RDFS_DOMAIN = factory.createURI(RdfStringConstant.RDFS_DOMAIN);
	URI RDFS_LABEL = factory.createURI(RdfStringConstant.RDFS_LABEL);
	URI RDFS_LITERAL = factory.createURI(RdfStringConstant.RDFS_LITERAL);
	URI RDFS_MEMBER = factory.createURI(RdfStringConstant.RDFS_MEMBER);
	URI RDFS_RANGE = factory.createURI(RdfStringConstant.RDFS_RANGE);
	URI RDFS_RESOURCE = factory.createURI(RdfStringConstant.RDFS_RESOURCE);
	URI RDFS_SUB_CLASS_OF = factory
			.createURI(RdfStringConstant.RDFS_SUB_CLASS_OF);
	URI RDFS_SUB_PROPERTY_OF = factory
			.createURI(RdfStringConstant.RDFS_SUB_PROPERTY_OF);
	URI SCHEMA_ABOUT = factory.createURI(Vocabulary.SCHEMA_ABOUT);
	URI SCHEMA_DESCRIPTION = factory.createURI(Vocabulary.SCHEMA_DESCRIPTION);
	URI SCHEMA_IN_LANGUAGE = factory.createURI(Vocabulary.SCHEMA_IN_LANGUAGE);
	URI SKOS_ALT_LABEL = factory.createURI(Vocabulary.SKOS_ALT_LABEL);
	URI WB_ARTICLE = factory.createURI(Vocabulary.WB_ARTICLE);
	URI WB_GC_PRECISION = factory.createURI(Vocabulary.WB_GC_PRECISION);
	URI WB_GLOBE = factory.createURI(Vocabulary.WB_GLOBE);
	URI WB_GLOBE_COORDINATES_VALUE = factory
			.createURI(Vocabulary.WB_GLOBE_COORDINATES_VALUE);
	URI WB_ITEM = factory.createURI(Vocabulary.WB_ITEM);
	URI WB_LATITUDE = factory.createURI(Vocabulary.WB_LATITUDE);
	URI WB_LONGITUDE = factory.createURI(Vocabulary.WB_LONGITUDE);
	URI WB_LOWER_BOUND = factory.createURI(Vocabulary.WB_LOWER_BOUND);
	URI WB_NUMERIC_VALUE = factory.createURI(Vocabulary.WB_NUMERIC_VALUE);
	URI WB_PREFERRED_CALENDAR = factory
			.createURI(Vocabulary.WB_PREFERRED_CALENDAR);
	URI WB_PROPERTY = factory.createURI(Vocabulary.WB_PROPERTY);
	URI WB_PROPERTY_TYPE = factory.createURI(Vocabulary.WB_PROPERTY_TYPE);
	URI WB_QUANTITY_VALUE = factory.createURI(Vocabulary.WB_QUANTITY_VALUE);
	URI WB_REFERENCE = factory.createURI(Vocabulary.WB_REFERENCE);
	URI WB_STATEMENT = factory.createURI(Vocabulary.WB_STATEMENT);
	URI WB_TIME = factory.createURI(Vocabulary.WB_TIME);
	URI WB_TIME_PRECISION = factory.createURI(Vocabulary.WB_TIME_PRECISION);
	URI WB_TIME_VALUE = factory.createURI(Vocabulary.WB_TIME_VALUE);
	URI WB_UPPER_BOUND = factory.createURI(Vocabulary.WB_UPPER_BOUND);
	// URI XSD_INT = factory.createURI(Vocabulary.XSD_INT);
	// URI XSD_DATE = factory.createURI(Vocabulary.XSD_DATE);
	// URI XSD_G_YEAR = factory.createURI(Vocabulary.XSD_G_YEAR);
	// URI XSD_G_YEAR_MONTH = factory.createURI(Vocabulary.XSD_G_YEAR_MONTH);
	URI XSD_DATE_TIME = factory.createURI(RdfStringConstant.XSD_DATETIME);
	URI XSD_DECIMAL = factory.createURI(RdfStringConstant.XSD_DECIMAL);
	URI XSD_MAX_INCLUSIVE = factory
			.createURI(RdfStringConstant.XSD_MAX_INCLUSIVE);
	URI XSD_MIN_INCLUSIVE = factory
			.createURI(RdfStringConstant.XSD_MIN_INCLUSIVE);
	URI XSD_PATTERN = factory.createURI(RdfStringConstant.XSD_PATTERN);
	URI XSD_STRING = factory.createURI(RdfStringConstant.XSD_STRING);

}
