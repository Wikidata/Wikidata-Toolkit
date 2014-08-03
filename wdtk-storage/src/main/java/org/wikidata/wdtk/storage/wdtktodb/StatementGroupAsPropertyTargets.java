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

import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.PropertyTargets;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.TargetQualifiers;

public class StatementGroupAsPropertyTargets implements PropertyTargets,
		Iterator<TargetQualifiers> {

	final WdtkAdaptorHelper helpers;
	final String property;
	final List<Statement> valueStatements;

	Statement nextProperStatement;
	Iterator<Statement> statementIterator;

	public StatementGroupAsPropertyTargets(String property,
			Collection<Statement> statements, WdtkAdaptorHelper helpers) {
		this.property = property;
		this.helpers = helpers;

		this.valueStatements = new ArrayList<>(statements.size());
		for (Statement s : statements) {
			if (s.getClaim().getMainSnak() instanceof ValueSnak) {
				this.valueStatements.add(s);
			}
		}
	}

	@Override
	public Iterator<TargetQualifiers> iterator() {
		this.statementIterator = this.valueStatements.iterator();
		return this;
	}

	@Override
	public String getProperty() {
		return this.property;
	}

	@Override
	public boolean hasNext() {
		return this.statementIterator.hasNext();
	}

	@Override
	public TargetQualifiers next() {
		return new StatementAsTargetQualifiers(this.statementIterator.next(),
				this.helpers);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getTargetCount() {
		return this.valueStatements.size();
	}

}
