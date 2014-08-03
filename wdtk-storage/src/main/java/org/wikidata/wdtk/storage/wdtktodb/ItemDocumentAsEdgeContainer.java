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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.PropertyTargets;
import org.wikidata.wdtk.storage.datamodel.StringValueImpl;
import org.wikidata.wdtk.storage.datamodel.Value;
import org.wikidata.wdtk.storage.wdtkbindings.WdtkSorts;
import org.wikidata.wdtk.util.NestedIterator;

public class ItemDocumentAsEdgeContainer implements EdgeContainer,
		Iterator<PropertyTargets> {

	final ItemDocument itemDocument;
	final WdtkAdaptorHelper helpers;
	final List<StatementGroup> properStatementGroups;
	final List<Statement> noValueStatements;
	final List<Statement> someValueStatements;

	static final byte MODE_DOCTYPE = 0;
	static final byte MODE_STATEMENTS = 1;
	static final byte MODE_NOVALUE = 2;
	static final byte MODE_SOMEVALUE = 3;
	static final byte MODE_LABELS = 4;
	static final byte MODE_DESCRIPTIONS = 5;
	static final byte MODE_ALIASES = 6;
	static final byte MODE_SITELINKS = 7;
	static final byte MODE_DONE = 100;
	byte currentMode;
	Iterator<StatementGroup> statementGroupIterator;

	byte maxMode;
	int edgeCount;

	public ItemDocumentAsEdgeContainer(ItemDocument itemDocument,
			WdtkAdaptorHelper helpers) {
		this.itemDocument = itemDocument;
		this.helpers = helpers;

		this.properStatementGroups = new ArrayList<>(itemDocument
				.getStatementGroups().size());
		this.noValueStatements = new ArrayList<>();
		this.someValueStatements = new ArrayList<>();
		for (StatementGroup sg : itemDocument.getStatementGroups()) {
			boolean properGroup = false;
			for (Statement s : sg) {
				if (s.getClaim().getMainSnak() instanceof ValueSnak) {
					properGroup = true;
				} else if (s.getClaim().getMainSnak() instanceof NoValueSnak) {
					// this.noValueStatements.add(s);
				} else if (s.getClaim().getMainSnak() instanceof SomeValueSnak) {
					// this.someValueStatements.add(s);
				}
			}
			if (properGroup) {
				this.properStatementGroups.add(sg);
			}
		}

		this.edgeCount = 1;
		this.maxMode = MODE_DOCTYPE;
		if (!this.properStatementGroups.isEmpty()) {
			this.maxMode = MODE_STATEMENTS;
			this.edgeCount += this.properStatementGroups.size();
		}
		if (!this.noValueStatements.isEmpty()) {
			this.maxMode = MODE_NOVALUE;
			this.edgeCount++;
		}
		if (!this.someValueStatements.isEmpty()) {
			this.maxMode = MODE_SOMEVALUE;
			this.edgeCount++;
		}
		if (!itemDocument.getLabels().isEmpty()) {
			this.maxMode = MODE_LABELS;
			this.edgeCount++;
		}
		if (!itemDocument.getDescriptions().isEmpty()) {
			this.maxMode = MODE_DESCRIPTIONS;
			this.edgeCount++;
		}
		if (!itemDocument.getAliases().isEmpty()) {
			this.maxMode = MODE_ALIASES;
			this.edgeCount++;
		}
		if (!itemDocument.getSiteLinks().isEmpty()) {
			this.maxMode = MODE_SITELINKS;
			this.edgeCount++;
		}
	}

	@Override
	public Iterator<PropertyTargets> iterator() {
		this.statementGroupIterator = this.properStatementGroups.iterator();
		this.currentMode = MODE_STATEMENTS;
		return this;
	}

	@Override
	public Value getSource() {
		return new EntityValueAsValue(this.itemDocument.getEntityId());
	}

	@Override
	public boolean hasNext() {
		return this.currentMode <= this.maxMode;
	}

	@Override
	public PropertyTargets next() {

		if (this.currentMode == MODE_DOCTYPE) {
			this.currentMode = MODE_STATEMENTS;
			return new SimplePropertyTargets(WdtkSorts.PROP_DOCTYPE,
					new StringValueImpl(WdtkSorts.VALUE_DOCTYPE_ITEM,
							WdtkSorts.SORT_SPECIAL_STRING));
		}

		if (this.currentMode == MODE_STATEMENTS) {
			if (this.statementGroupIterator.hasNext()) {
				StatementGroup sg = this.statementGroupIterator.next();
				if (!this.statementGroupIterator.hasNext()) {
					this.currentMode = MODE_NOVALUE;
				}
				return new StatementGroupAsPropertyTargets(sg.getProperty()
						.getIri(), sg.getStatements(), this.helpers);
			} else {
				this.currentMode = MODE_NOVALUE;
			}
		}

		if (this.currentMode == MODE_NOVALUE) {
			this.currentMode = MODE_SOMEVALUE;
			if (!this.noValueStatements.isEmpty()) {
				return new StatementGroupAsPropertyTargets(
						WdtkSorts.PROP_NOVALUE, this.noValueStatements,
						this.helpers);
			}
		}

		if (this.currentMode == MODE_SOMEVALUE) {
			this.currentMode = MODE_LABELS;
			if (!this.someValueStatements.isEmpty()) {
				return new StatementGroupAsPropertyTargets(
						WdtkSorts.PROP_SOMEVALUE, this.someValueStatements,
						this.helpers);
			}
		}

		if (this.currentMode == MODE_LABELS) {
			this.currentMode = MODE_DESCRIPTIONS;
			if (!itemDocument.getLabels().isEmpty()) {
				return new TermsAsPropertyTargets(WdtkSorts.PROP_LABEL,
						this.itemDocument.getLabels().values().iterator(),
						this.itemDocument.getLabels().size(),
						WdtkSorts.SORT_LABEL);
			}
		}

		if (this.currentMode == MODE_DESCRIPTIONS) {
			this.currentMode = MODE_ALIASES;
			if (!itemDocument.getDescriptions().isEmpty()) {
				return new TermsAsPropertyTargets(
						WdtkSorts.PROP_DESCRIPTION,
						this.itemDocument.getDescriptions().values().iterator(),
						this.itemDocument.getDescriptions().values().size(),
						WdtkSorts.SORT_DESCRIPTION);
			}
		}

		if (this.currentMode == MODE_ALIASES) {
			this.currentMode = MODE_SITELINKS;
			if (!itemDocument.getAliases().isEmpty()) {
				int targetCount = 0;
				for (List<MonolingualTextValue> l : this.itemDocument
						.getAliases().values()) {
					targetCount += l.size();
				}
				return new TermsAsPropertyTargets(WdtkSorts.PROP_ALIAS,
						new NestedIterator<MonolingualTextValue>(
								this.itemDocument.getAliases().values()),
								targetCount, WdtkSorts.SORT_ALIAS);
			}
		}

		if (this.currentMode == MODE_SITELINKS) {
			this.currentMode = MODE_DONE;
			if (!itemDocument.getSiteLinks().isEmpty()) {
				return new SiteLinksAsPropertyTargets(this.itemDocument
						.getSiteLinks().values());
			}
		}

		return null;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getEdgeCount() {
		return this.edgeCount;
	}

}
