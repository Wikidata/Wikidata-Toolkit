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

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

public class PropertyIdValueFromPropertyName implements PropertyIdValue {

	final String propertyName;

	String id = null;
	String siteIri = null;

	public PropertyIdValueFromPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	@Override
	public String getEntityType() {
		return EntityIdValue.ET_PROPERTY;
	}

	@Override
	public String getId() {
		initStrings();
		return this.id;
	}

	@Override
	public String getSiteIri() {
		initStrings();
		return this.siteIri;
	}

	@Override
	public String getIri() {
		initStrings();
		return this.siteIri.concat(this.id);
	}

	@Override
	public <T> T accept(ValueVisitor<T> valueVisitor) {
		return valueVisitor.visit(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder(773, 241).append(getSiteIri())
				.append(getId()).append(getEntityType()).toHashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof EntityIdValue)) {
			return false;
		}

		EntityIdValue other = (EntityIdValue) obj;
		return getId().equals(other.getId())
				&& getSiteIri().equals(other.getSiteIri())
				&& getEntityType().equals(other.getEntityType());
	}

	private void initStrings() {
		if (this.id == null) {
			int index = this.propertyName.indexOf('>');
			this.siteIri = this.propertyName.substring(0, index);
			this.id = this.propertyName.substring(index + 1);
		}
	}
}
