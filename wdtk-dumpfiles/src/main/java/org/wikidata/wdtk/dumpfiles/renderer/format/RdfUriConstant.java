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

public interface RdfUriConstant {

	static final ValueFactory factory = ValueFactoryImpl.getInstance();

	URI OWL_ANNOTATION_PROPERTY = factory
			.createURI(RdfStringConstant.OWL_ANNOTATION_PROPERTY);
	URI OWL_CLASS = factory.createURI(RdfStringConstant.OWL_CLASS);
	URI OWL_COMPLEMENT_OF = factory
			.createURI(RdfStringConstant.OWL_COMPLEMENT_OF);
	URI OWL_DATATYPE_PROPERTY = factory
			.createURI(RdfStringConstant.OWL_DATATYPE_PROPERTY);
	URI OWL_DISJOINT_WITH = factory
			.createURI(RdfStringConstant.OWL_DISJOINT_WITH);
	URI OWL_EQUIVALENT_CLASS = factory
			.createURI(RdfStringConstant.OWL_EQUIVALENT_CLASS);
	URI OWL_FUNCTIONAL_PROPERTY = factory
			.createURI(RdfStringConstant.OWL_FUNCTIONAL_PROPERTY);
	URI OWL_HAS_KEY = factory.createURI(RdfStringConstant.OWL_HAS_KEY);
	URI OWL_INTERSECTION_OF = factory
			.createURI(RdfStringConstant.OWL_INTERSECTION_OF);
	URI OWL_INVERSE_FUNCTIONAL_PROPERTY = factory
			.createURI(RdfStringConstant.OWL_INVERSE_FUNCTIONAL_PROPERTY);
	URI OWL_NAMED_INDIVIDUAL = factory
			.createURI(RdfStringConstant.OWL_NAMED_INDIVIDUAL);
	URI OWL_OBJECT_PROPERTY = factory
			.createURI(RdfStringConstant.OWL_OBJECT_PROPERTY);
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
	URI RDF_FIRST = factory.createURI(RdfStringConstant.RDF_FIRST);
	URI RDF_NIL = factory.createURI(RdfStringConstant.RDF_NIL);
	URI RDF_REST = factory.createURI(RdfStringConstant.RDF_REST);
	URI RDF_TYPE = factory.createURI(RdfStringConstant.RDF_TYPE);
	URI RDFS_COMMENT = factory.createURI(RdfStringConstant.RDFS_COMMENT);
	URI RDFS_DATATYPE = factory.createURI(RdfStringConstant.RDFS_DATATYPE);
	URI RDFS_DOMAIN = factory.createURI(RdfStringConstant.RDFS_DOMAIN);
	URI RDFS_RANGE = factory.createURI(RdfStringConstant.RDFS_RANGE);
	URI RDFS_SUB_CLASS_OF = factory
			.createURI(RdfStringConstant.RDFS_SUB_CLASS_OF);
	URI WB_QUANTITY_VALUE = factory
			.createURI(RdfStringConstant.WB_QUANTITY_VALUE);
	URI WB_TIME_VALUE = factory.createURI(RdfStringConstant.WB_TIME_VALUE);
	URI XSD_DATE_TIME = factory.createURI(RdfStringConstant.XSD_DATE_TIME);
	URI XSD_DECIMAL = factory.createURI(RdfStringConstant.XSD_DECIMAL);
	URI XSD_MAX_INCLUSIVE = factory
			.createURI(RdfStringConstant.XSD_MAX_INCLUSIVE);
	URI XSD_MIN_INCLUSIVE = factory
			.createURI(RdfStringConstant.XSD_MIN_INCLUSIVE);
	URI XSD_PATTERN = factory.createURI(RdfStringConstant.XSD_PATTERN);
	URI XSD_STRING = factory.createURI(RdfStringConstant.XSD_STRING);

}
