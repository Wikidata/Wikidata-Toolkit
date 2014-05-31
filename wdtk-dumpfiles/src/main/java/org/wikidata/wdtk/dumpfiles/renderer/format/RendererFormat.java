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

import java.util.List;

import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;

public interface RendererFormat {

	URI a_s(PropertyIdValue property);

	URI a_v(PropertyIdValue property);

	URI aItem(ItemIdValue item);

	URI aRp(PropertyIdValue property);

	URI owlThing();

	URI xsdDateTime();

	URI xsdDecimal();

	URI xsdMaxInclusive();

	URI xsdMinInclusive();

	URI xsdPattern();

	URI xsdString();

	BNode getDataIntersectionOf(Resource dataRange0, Resource dataRange1);

	BNode getDataSomeValuesFrom(Resource dataProperty, Resource dataRange);

	BNode getDatatypeRestriction(Resource dataType, URI facet, Resource value);

	BNode getObjectComplementOf(Resource clss);

	BNode getObjectExactCardinality(int cardinality, Resource objectProperty);

	BNode getObjectOneOf(Resource individual);

	BNode getObjectOneOf(List<Resource> listOfIndividuals);

	BNode getObjectSomeValuesFrom(Resource property, Resource clss);

	BNode getObjectUnionOf(Resource class0, Resource class1);

	boolean addAnnotationAssertionComment(Resource subject, String value);

	boolean addDataPropertyRange(Resource dataProperty, Resource dataRange);

	boolean addDatatypeDefinition(Resource datatype, Resource dataRange);

	boolean addDeclarationAnnotationProperty(Resource entity);

	boolean addDeclarationClass(Resource entity);

	boolean addDeclarationDatatype(Resource entity);

	boolean addDeclarationDatatypeProperty(Resource entity);

	boolean addDeclarationNamedIndividual(Resource entity);

	boolean addDeclarationObjectProperty(Resource entity);

	boolean addDisjointClasses(Resource class0, Resource class1);

	boolean addFunctionalObjectProperty(Resource objectProperty);

	boolean addHasKey(Resource clss, Resource objectProperty,
			Resource dataProperty);

	boolean addInverseFunctionalObjectProperty(Resource objectProperty);

	boolean addObjectPropertyDomain(Resource objectProperty, Resource clss);

	boolean addObjectPropertyRange(Resource objectProperty, Resource clss);

	boolean addSubClassOf(Resource subClass, Resource superClass);

	void start();

	void finish();

}
