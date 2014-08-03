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

import java.util.List;

import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.TermedDocument;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.PropertyTargets;
import org.wikidata.wdtk.storage.wdtkbindings.WdtkSorts;
import org.wikidata.wdtk.util.NestedIterator;

public class TermsPropertyTargetIterator {

	private static final byte DO_LABELS = 1;
	private static final byte DO_DESCRIPTIONS = 2;
	private static final byte DO_ALIASES = 4;

	final TermedDocument termedDocument;

	int todos;

	public TermsPropertyTargetIterator(TermedDocument termedDocument) {
		this.termedDocument = termedDocument;
	}

	public void reset() {
		this.todos = 0;
		if (!termedDocument.getLabels().isEmpty()) {
			this.todos |= DO_LABELS;
		}
		if (!termedDocument.getDescriptions().isEmpty()) {
			this.todos |= DO_DESCRIPTIONS;
		}
		if (!termedDocument.getAliases().isEmpty()) {
			this.todos |= DO_ALIASES;
		}
	}

	public boolean hasNext() {
		return this.todos != 0;
	}

	public PropertyTargets next() {

		if ((this.todos & DO_LABELS) != 0) {
			this.todos = this.todos & ~DO_LABELS;
			return new TermsAsPropertyTargets(WdtkSorts.PROP_LABEL,
					this.termedDocument.getLabels().values().iterator(),
					this.termedDocument.getLabels().size(),
					WdtkSorts.SORT_LABEL);
		} else if ((this.todos & DO_DESCRIPTIONS) != 0) {
			this.todos = this.todos & ~DO_DESCRIPTIONS;
			return new TermsAsPropertyTargets(WdtkSorts.PROP_DESCRIPTION,
					this.termedDocument.getDescriptions().values().iterator(),
					this.termedDocument.getDescriptions().values().size(),
					WdtkSorts.SORT_DESCRIPTION);
		} else if ((this.todos & DO_ALIASES) != 0) {
			this.todos = this.todos & ~DO_ALIASES;
			int targetCount = 0;
			for (List<MonolingualTextValue> l : this.termedDocument
					.getAliases().values()) {
				targetCount += l.size();
			}
			return new TermsAsPropertyTargets(WdtkSorts.PROP_ALIAS,
					new NestedIterator<MonolingualTextValue>(
							this.termedDocument.getAliases().values()),
							targetCount, WdtkSorts.SORT_ALIAS);
		} else {
			return null;
		}
	}

	public int getEdgeCount() {
		return (this.termedDocument.getLabels().isEmpty() ? 0 : 1)
				+ (this.termedDocument.getDescriptions().isEmpty() ? 0 : 1)
				+ (this.termedDocument.getAliases().isEmpty() ? 0 : 1);
	}

}
