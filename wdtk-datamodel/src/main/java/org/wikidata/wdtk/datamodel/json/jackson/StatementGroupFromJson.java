package org.wikidata.wdtk.datamodel.json.jackson;

/*
 * #%L
 * Wikidata Toolkit Data Model
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

import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;

/**
 * Helper class to represent a {@link StatementGroup} deserialized from JSON.
 * The actual data is part of a map of lists of {@link JacksonStatement} objects
 * in JSON, so there is no corresponding JSON object.
 *
 * @author Markus Kroetzsch
 */
public class StatementGroupFromJson implements StatementGroup {

	final List<Statement> statements;

	public StatementGroupFromJson(List<JacksonStatement> jacksonStatements) {
		this.statements = Collections
				.<Statement> unmodifiableList(jacksonStatements);
	}

	@Override
	public Iterator<Statement> iterator() {
		return this.statements.iterator();
	}

	@Override
	public List<Statement> getStatements() {
		return this.statements;
	}

	@Override
	public PropertyIdValue getProperty() {
		return this.statements.get(0).getClaim().getMainSnak().getPropertyId();
	}

	@Override
	public EntityIdValue getSubject() {
		return this.statements.get(0).getClaim().getSubject();
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsStatementGroup(this, obj);
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}

}
