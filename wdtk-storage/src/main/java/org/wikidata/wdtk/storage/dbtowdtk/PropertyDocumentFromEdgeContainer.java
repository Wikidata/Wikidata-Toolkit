package org.wikidata.wdtk.storage.dbtowdtk;

/*
 * #%L
 * Wikidata Toolkit Storage
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

import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.PropertyTargets;
import org.wikidata.wdtk.storage.datamodel.StringValue;

public class PropertyDocumentFromEdgeContainer extends
		TermedDocumentFromEdgeContainer implements PropertyDocument {

	final EdgeContainer edgeContainer;
	final StringValue datatype;

	public PropertyDocumentFromEdgeContainer(EdgeContainer edgeContainer,
			PropertyTargets labels, PropertyTargets descriptions,
			PropertyTargets aliases, StringValue datatype) {
		super(labels, descriptions, aliases);

		this.edgeContainer = edgeContainer;
		this.datatype = datatype;
	}

	@Override
	public EntityIdValue getEntityId() {
		return getPropertyId();
	}

	@Override
	public PropertyIdValue getPropertyId() {
		return new PropertyIdValueFromValue(
				(StringValue) this.edgeContainer.getSource());
	}

	@Override
	public DatatypeIdValue getDatatype() {
		return Datamodel.makeDatatypeIdValue(this.datatype.getString());
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsPropertyDocument(this, obj);
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}

}
