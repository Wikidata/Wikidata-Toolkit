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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.PropertyTargets;
import org.wikidata.wdtk.storage.wdtkbindings.WdtkSorts;

public class StatementsPropertyTargetIterator {

	private static final byte DO_NOVALUE = 2;
	private static final byte DO_SOMEVALUE = 4;

	final WdtkAdaptorHelper helpers;
	final List<StatementGroup> properStatementGroups;
	final List<Statement> noValueStatements;
	final List<Statement> someValueStatements;

	int todos;
	Iterator<StatementGroup> statementGroupIterator;

	public StatementsPropertyTargetIterator(
			Collection<StatementGroup> statementGroups,
			WdtkAdaptorHelper helpers) {
		this.helpers = helpers;

		this.properStatementGroups = new ArrayList<>(statementGroups.size());
		this.noValueStatements = new ArrayList<>();
		this.someValueStatements = new ArrayList<>();
		for (StatementGroup sg : statementGroups) {
			boolean properGroup = false;
			for (Statement s : sg) {
				if (s.getClaim().getMainSnak() instanceof ValueSnak) {
					properGroup = true;
				} else if (s.getClaim().getMainSnak() instanceof NoValueSnak) {
					this.noValueStatements.add(s);
				} else if (s.getClaim().getMainSnak() instanceof SomeValueSnak) {
					this.someValueStatements.add(s);
				}
			}
			if (properGroup) {
				this.properStatementGroups.add(sg);
			}
		}
	}

	public void reset() {
		this.todos = 0;
		if (!this.noValueStatements.isEmpty()) {
			this.todos |= DO_NOVALUE;
		}
		if (!this.someValueStatements.isEmpty()) {
			this.todos |= DO_SOMEVALUE;
		}

		this.statementGroupIterator = this.properStatementGroups.iterator();
	}

	public boolean hasNext() {
		return this.statementGroupIterator.hasNext() || (this.todos != 0);
	}

	public PropertyTargets next() {

		if (this.statementGroupIterator.hasNext()) {
			StatementGroup sg = this.statementGroupIterator.next();
			return new StatementGroupAsPropertyTargets(sg.getProperty()
					.getIri(), sg.getStatements(), true, this.helpers);
		} else if ((this.todos & DO_NOVALUE) != 0) {
			this.todos = this.todos & ~DO_NOVALUE;
			return new StatementGroupAsPropertyTargets(WdtkSorts.PROP_NOVALUE,
					this.noValueStatements, false, this.helpers);
		} else if ((this.todos & DO_SOMEVALUE) != 0) {
			this.todos = this.todos & ~DO_SOMEVALUE;
			return new StatementGroupAsPropertyTargets(
					WdtkSorts.PROP_SOMEVALUE, this.someValueStatements, false,
					this.helpers);
		} else {
			return null;
		}
	}

	public int getEdgeCount() {
		return this.properStatementGroups.size()
				+ (this.noValueStatements.isEmpty() ? 0 : 1)
				+ (this.someValueStatements.isEmpty() ? 0 : 1);
	}
}
