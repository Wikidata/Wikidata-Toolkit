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

import java.util.Iterator;

import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.PropertyTargets;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.TargetQualifiers;

public class StatementGroupAdaptor implements PropertyTargets,
		Iterator<TargetQualifiers> {

	final WdtkAdaptorHelper helpers;
	final StatementGroup statementGroup;
	final Iterator<Statement> statements;

	public StatementGroupAdaptor(StatementGroup statementGroup,
			WdtkAdaptorHelper helpers) {
		this.statementGroup = statementGroup;
		this.statements = statementGroup.getStatements().iterator();
		this.helpers = helpers;
	}

	@Override
	public Iterator<TargetQualifiers> iterator() {
		return this;
	}

	@Override
	public String getProperty() {
		return this.statementGroup.getProperty().getIri();
	}

	@Override
	public boolean hasNext() {
		return this.statements.hasNext();
	}

	@Override
	public TargetQualifiers next() {
		return new StatementTargetQualifiers(this.statements.next(),
				this.helpers);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
