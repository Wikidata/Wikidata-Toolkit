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

	String getStart();

	String getEnd();

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

	BNode getDataIntersectionOf(Resource arg0, Resource arg1);

	BNode getDataSomeValuesFrom(Resource arg0, Resource arg1);

	BNode getDatatype(Resource arg);

	BNode getDatatypeRestriction(Resource arg0, Resource arg1, Resource arg2);

	BNode getLiteral(Resource value, Resource type);

	BNode getObjectComplementOf(Resource arg);

	BNode getObjectExactCardinality(int cardinality, Resource property);

	BNode getObjectOneOf(Resource clss);

	BNode getObjectOneOf(List<Resource> list);

	BNode getObjectSomeValuesFrom(Resource arg0, Resource arg1);

	BNode getObjectUnionOf(Resource arg0, Resource arg1);

	boolean addAnnotationAssertion(Resource key, String comment);

	boolean addDataPropertyRange(Resource arg0, Resource arg1);

	boolean addDatatypeDefinition(Resource arg0, Resource arg1);

	boolean addDeclarationAnnotationProperty(Resource arg);

	boolean addDeclarationClass(Resource arg);

	boolean addDeclarationDatatype(Resource arg);

	boolean addDeclarationDatatypeProperty(Resource arg);

	boolean addDeclarationNamedIndividual(Resource arg);

	boolean addDeclarationObjectProperty(Resource arg);

	boolean addDisjointClasses(Resource arg0, Resource arg1);

	boolean addFunctionalObjectProperty(Resource arg);

	boolean addHasKey(Resource arg0, Resource arg1, Resource arg2);

	boolean addInverseFunctionalObjectProperty(Resource arg);

	boolean addObjectPropertyDomain(Resource arg0, Resource arg1);

	boolean addObjectPropertyRange(Resource arg0, Resource arg1);

	boolean addSubClassOf(Resource arg0, Resource arg1);

}
