package org.wikidata.wdtk.storage.db;

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

public class PropertySignature {

	final int domainId;
	final int rangeId;
	final String propertyName;

	public PropertySignature(String propertyName, int domainId, int rangeId) {
		this.propertyName = propertyName;
		this.domainId = domainId;
		this.rangeId = rangeId;
	}

	public String getPropertyName() {
		return this.propertyName;
	}

	public int getDomainId() {
		return this.domainId;
	}

	public int getRangeId() {
		return this.rangeId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PropertySignature [domainId=" + domainId + ", rangeId="
				+ rangeId + ", propertyName=" + propertyName + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + domainId;
		result = prime * result + propertyName.hashCode();
		result = prime * result + rangeId;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PropertySignature)) {
			return false;
		}
		PropertySignature other = (PropertySignature) obj;
		return (domainId == other.domainId)
				&& propertyName.equals(other.propertyName)
				&& (rangeId == other.rangeId);
	}

}
