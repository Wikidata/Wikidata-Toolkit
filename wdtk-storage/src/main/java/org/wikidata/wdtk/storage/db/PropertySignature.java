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

}
