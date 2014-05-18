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

import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;

public interface RendererFormat {

	String a_s(PropertyIdValue property);

	String a_v(PropertyIdValue property);

	String aItem(ItemIdValue item);

	String aRp(PropertyIdValue property);

	String makePair(String arg0, String arg1);

	String makeList(List<ItemIdValue> list);

	String aDataIntersectionOf(String arg0, String arg1);

	String aDataPropertyRange(String arg0, String arg1);

	String aDataSomeValuesFrom(String arg0, String arg1);

	String aDatatype(String arg);

	String aDatatypeDefinition(String arg0, String arg1);

	String aDatatypeRestriction(String arg0, String arg1, String arg2);

	String aDeclaration(String arg);

	String aDisjointClasses(String arg0, String arg1);

	String aFunctionalObjectProperty(String arg);

	String aHasKey(String arg0, String arg1, String arg2);

	String aInverseFunctionalObjectProperty(String arg);

	String aLiteral(String value, String type);

	String aObjectComplementOf(String arg);

	String aObjectExactCardinality(String arg0, String arg1);

	String aObjectOneOf(ItemIdValue q);

	String aObjectOneOf(List<ItemIdValue> list);

	String aObjectPropertyDomain(String arg0, String arg1);

	String aObjectPropertyRange(String arg0, String arg1);

	String aObjectSomeValuesFrom(String arg0, String arg1);

	String aObjectUnionOf(String arg0, String arg1);

	String aSubClassOf(String arg0, String arg1);

	String owlThing();

	String xsdDateTime();

	String xsdDecimal();

	String xsdMaxInclusive();

	String xsdMinInclusive();

	String xsdPattern();

	String xsdString();

}
