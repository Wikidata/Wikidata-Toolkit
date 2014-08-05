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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.PropertyTargets;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.TargetQualifiers;

public class StatementGroupFromPropertyTargets implements StatementGroup {

	final PropertyTargets propertyTargets;
	final EntityDocument parentDocument;

	List<Statement> statements = null;

	public StatementGroupFromPropertyTargets(PropertyTargets propertyTargets,
			EntityDocument parentDocument) {
		this.propertyTargets = propertyTargets;
		this.parentDocument = parentDocument;
	}

	@Override
	public Iterator<Statement> iterator() {
		return getStatements().iterator();
	}

	@Override
	public List<Statement> getStatements() {
		if (this.statements == null) {
			this.statements = new ArrayList<>(
					this.propertyTargets.getTargetCount());
			for (TargetQualifiers tqs : this.propertyTargets) {
				this.statements
						.add(new StatementFromTargetQualifiers(tqs,
								this.propertyTargets.getProperty(),
								this.parentDocument));
			}
		}
		return this.statements;
	}

	@Override
	public PropertyIdValue getProperty() {
		return new PropertyIdValueFromPropertyName(
				this.propertyTargets.getProperty());
	}

	@Override
	public EntityIdValue getSubject() {
		return this.parentDocument.getEntityId();
	}

}
