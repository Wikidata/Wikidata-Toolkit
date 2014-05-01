package org.wikidata.wdtk.rdf;

/*
 * #%L
 * Wikidata Toolkit RDF
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

import java.util.HashMap;
import java.util.Map;

import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

/**
 * This class helps to manage the exact datatype of properties used in an RDF
 * dump. It caches known types and fetches type information from the Web if
 * needed.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class PropertyTypes implements ValueVisitor<String> {

	final Map<String, String> propertyTypes;
	final String projectName;

	public PropertyTypes(String projectName) {
		this.projectName = projectName;
		this.propertyTypes = new HashMap<String, String>();
	}

	public String getPropertyType(PropertyIdValue propertyIdValue) {
		if (!propertyTypes.containsKey(propertyIdValue.getId())) {
			propertyTypes.put(propertyIdValue.getId(),
					fetchPropertyType(propertyIdValue));
			// TODO some sane error handling if fetch failed?
		}
		return propertyTypes.get(propertyIdValue.getId());
	}

	public void setPropertyType(PropertyIdValue propertyIdValue,
			String datatypeIri) {
		propertyTypes.put(propertyIdValue.getId(), datatypeIri);
	}

	public String setPropertyTypeFromValue(PropertyIdValue propertyIdValue,
			Value value) {
		if (!propertyTypes.containsKey(propertyIdValue.getId())) {
			String datatypeIri = value.accept(this);
			if (datatypeIri == null) {
				datatypeIri = fetchPropertyType(propertyIdValue);
			}
			// TODO some sane error handling if fetch failed?
			propertyTypes.put(propertyIdValue.getId(), datatypeIri);
		}
		return propertyTypes.get(propertyIdValue);
	}

	/**
	 * Find the datatype of a property online.
	 * 
	 * @param propertyIdValue
	 * @return
	 */
	String fetchPropertyType(PropertyIdValue propertyIdValue) {
		// TODO implement
		return null;
	}

	@Override
	public String visit(DatatypeIdValue value) {
		// No property datatype currently uses this
		return null;
	}

	@Override
	public String visit(EntityIdValue value) {
		// Only Items can be used as entity values so far
		return DatatypeIdValue.DT_ITEM;
	}

	@Override
	public String visit(GlobeCoordinatesValue value) {
		return DatatypeIdValue.DT_GLOBE_COORDINATES;
	}

	@Override
	public String visit(MonolingualTextValue value) {
		// No property datatype currently uses this
		return null;
	}

	@Override
	public String visit(QuantityValue value) {
		return DatatypeIdValue.DT_QUANTITY;
	}

	@Override
	public String visit(StringValue value) {
		// Cannot determine type from this kind of values
		return null;
	}

	@Override
	public String visit(TimeValue value) {
		return DatatypeIdValue.DT_TIME;
	}

}
