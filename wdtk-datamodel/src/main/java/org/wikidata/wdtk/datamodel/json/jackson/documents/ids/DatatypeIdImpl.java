package org.wikidata.wdtk.datamodel.json.jackson.documents.ids;

/*
 * #%L
 * Wikidata Toolkit Data Model
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

import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

/**
 * This is not actually in the JSON but needed to satisfy the interface.
 * 
 * @author Fredo Erxleben
 *
 */
public class DatatypeIdImpl implements DatatypeIdValue {

	public static final String jsonTypeItem = "wikibase-item";
	public static final String jsonTypeGlobe = "globe-coordinate";
	public static final String jsonTypeUrl = "url";
	public static final String jsonTypeCommonsMedia = "commonsMedia";
	public static final String jsonTypeTime = "time";
	public static final String jsonTypeQuantity = "quantity";
	public static final String jsonTypeString = "string";
	public static final String jsonTypeMonolingualText = "monolingualtext ";

	private String iri;

	/**
	 * This constructs an object representing the datatype id from a string
	 * denoting the datatype. It also sets the correct IRI for the datatype.
	 * 
	 * @param datatype
	 *            denotes the datatype which to represent; case-sensitive
	 * @throws IllegalArgumentException
	 *             if the given datatype string could not be matched to a known
	 *             datatype or was null
	 */
	public DatatypeIdImpl(String datatype) throws IllegalArgumentException {
		switch (datatype) {
		case jsonTypeItem:
			this.iri = DT_ITEM;
			break;
		case jsonTypeGlobe:
			this.iri = DT_GLOBE_COORDINATES;
			break;
		case jsonTypeUrl:
			this.iri = DT_URL;
			break;
		case jsonTypeCommonsMedia:
			this.iri = DT_COMMONS_MEDIA;
			break;
		case jsonTypeTime:
			this.iri = DT_TIME;
			break;
		case jsonTypeQuantity:
			this.iri = DT_QUANTITY;
			break;
		case jsonTypeString:
			this.iri = DT_STRING;
			break;
		case jsonTypeMonolingualText:
			this.iri = DT_MONOLINGUAL_TEXT;
			break;
		default:
			throw new IllegalArgumentException("Unknown datatype \"" + datatype
					+ "\" when constructing DatatypeId - instance.");
		}
	}

	@Override
	public String getIri() {
		return this.iri;
	}

	@Override
	public <T> T accept(ValueVisitor<T> valueVisitor) {
		return valueVisitor.visit(this);
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}

	@Override
	public boolean equals(Object o) {
		return Equality.equalsDatatypeIdValue(this, o);
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}
}
