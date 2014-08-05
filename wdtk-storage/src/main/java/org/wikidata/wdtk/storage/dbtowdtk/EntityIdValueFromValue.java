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

import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;
import org.wikidata.wdtk.storage.datamodel.StringValue;

public abstract class EntityIdValueFromValue implements EntityIdValue {

	final StringValue value;

	String id = null;
	String siteIri = null;

	public EntityIdValueFromValue(StringValue value) {
		this.value = value;
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

	private void initStrings() {
		if (this.id == null) {
			int index = this.value.getString().indexOf('>');
			this.siteIri = this.value.getString().substring(0, index);
			this.id = this.value.getString().substring(index + 1);
		}
	}
}
