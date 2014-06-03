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

	URI getItem(ItemIdValue item);

	URI getProperty(PropertyIdValue property);

	URI getDaux(PropertyIdValue property);

	URI getPs(PropertyIdValue property);

	URI getPv(PropertyIdValue property);

	URI rdfsComment();

	URI owlThing();

	URI wbTimeValue();

	URI wbQuantityValue();

	URI xsdDateTime();

	URI xsdDecimal();

	URI xsdMaxInclusive();

	URI xsdMinInclusive();

	URI xsdPattern();

	URI xsdString();

	BNode getDataIntersectionOf(Resource dataRange0, Resource dataRange1);

	BNode getDataSomeValuesFrom(URI dataPropertyExpression, Resource dataRange);

	BNode getDatatypeRestriction(URI datatype, URI constrainingFacet,
			Resource restrictionValue);

	BNode getObjectComplementOf(Resource classExpression);

	BNode getObjectExactCardinality(int nonNegativeInteger,
			Resource objectPropertyExpression);

	BNode getObjectOneOf(Resource individual);

	BNode getObjectOneOf(List<Resource> listOfIndividuals);

	BNode getObjectSomeValuesFrom(Resource objectPropertyExpression,
			Resource classExpression);

	BNode getObjectUnionOf(Resource classExpression0, Resource classExpression1);

	boolean addAnnotationAssertion(URI annotationProperty,
			URI annotationSubject, String annotationValue);

	boolean addDataPropertyRange(URI dataPropertyExpression, Resource dataRange);

	boolean addDatatypeDefinition(URI datatype, Resource dataRange);

	boolean addDeclarationAnnotationProperty(URI annotationProperty);

	boolean addDeclarationClass(URI clss);

	boolean addDeclarationDatatype(URI datatype);

	boolean addDeclarationDatatypeProperty(URI datatypeProperty);

	boolean addDeclarationNamedIndividual(URI namedIndividual);

	boolean addDeclarationObjectProperty(URI objectProperty);

	boolean addDisjointClasses(Resource classExpression0,
			Resource classExpression1);

	boolean addFunctionalObjectProperty(Resource objectPropertyExpression);

	boolean addHasKey(Resource classExpression, Resource dataPropertyExpression);

	boolean addInverseFunctionalObjectProperty(Resource objectPropertyExpression);

	boolean addObjectPropertyDomain(Resource objectPropertyExpression,
			Resource classExpression);

	boolean addObjectPropertyRange(Resource objectPropertyExpression,
			Resource classExpression);

	boolean addSubClassOf(Resource subClassExpression,
			Resource superClassExpression);

	void start();

	void finish();

}
