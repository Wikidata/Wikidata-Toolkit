package org.wikidata.wdtk.storage.wdtktodb;

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

import java.util.Collections;
import java.util.Iterator;

import org.wikidata.wdtk.storage.datamodel.EdgeContainer.PropertyTargets;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.TargetQualifiers;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePair;
import org.wikidata.wdtk.storage.datamodel.Value;

public class SimplePropertyTargets implements PropertyTargets,
		Iterator<TargetQualifiers>, TargetQualifiers {

	final String property;
	final Value value;

	boolean hasNext;

	public SimplePropertyTargets(String property, Value value) {
		this.property = property;
		this.value = value;
	}

	@Override
	public Iterator<TargetQualifiers> iterator() {
		this.hasNext = true;
		return this;
	}

	@Override
	public String getProperty() {
		return property;
	}

	@Override
	public int getTargetCount() {
		return 1;
	}

	@Override
	public boolean hasNext() {
		return this.hasNext;
	}

	@Override
	public TargetQualifiers next() {
		return this;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Value getTarget() {
		return this.value;
	}

	@Override
	public Iterable<PropertyValuePair> getQualifiers() {
		return Collections.<PropertyValuePair> emptyList();
	}

	@Override
	public int getQualifierCount() {
		return 0;
	}

}
