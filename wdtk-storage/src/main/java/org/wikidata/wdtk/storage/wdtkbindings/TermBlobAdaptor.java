package org.wikidata.wdtk.storage.wdtkbindings;

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
import java.util.List;

import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.TermedDocument;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.PropertyTargets;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.TargetQualifiers;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePair;
import org.wikidata.wdtk.storage.datamodel.StringValueImpl;
import org.wikidata.wdtk.storage.datamodel.Value;

public class TermBlobAdaptor implements PropertyTargets,
		Iterator<TargetQualifiers>, TargetQualifiers {

	final TermedDocument termedDocument;

	boolean iteratorAtStart;

	public TermBlobAdaptor(TermedDocument document) {
		this.termedDocument = document;
	}

	@Override
	public Iterator<TargetQualifiers> iterator() {
		this.iteratorAtStart = true;
		return this;
	}

	@Override
	public String getProperty() {
		return WdtkSorts.PROP_TERMS;
	}

	@Override
	public int getTargetCount() {
		return 1;
	}

	@Override
	public boolean hasNext() {
		return this.iteratorAtStart;
	}

	@Override
	public TargetQualifiers next() {
		this.iteratorAtStart = false;
		return this;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Value getTarget() {
		// FIXME not decodable; just for test:
		StringBuilder sb = new StringBuilder();
		for (MonolingualTextValue mtv : this.termedDocument.getLabels()
				.values()) {
			sb.append(mtv.getLanguageCode()).append("@").append(mtv.getText());
		}
		for (MonolingualTextValue mtv : this.termedDocument.getDescriptions()
				.values()) {
			sb.append(mtv.getLanguageCode()).append("@").append(mtv.getText());
		}
		for (List<MonolingualTextValue> mtvList : this.termedDocument
				.getAliases().values()) {
			for (MonolingualTextValue mtv : mtvList) {
				sb.append(mtv.getLanguageCode()).append("@")
						.append(mtv.getText());
			}
		}
		return new StringValueImpl(sb.toString(), WdtkSorts.SORT_TERMS);
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
