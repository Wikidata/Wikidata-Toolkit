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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.json.jackson.documents.ids.JacksonPropertyId;

/**
 * This class is merely there to be compatible with the WDTK-data model
 * interface There is no concept of dedicated statement group objects in the
 * JSON rather then a list of statements relating to a property.
 *
 * @author Fredo Erxleben
 *
 */
public class JacksonStatementGroup implements StatementGroup {
	// TODO do not forget to set the claim of the statements

	JacksonPropertyId propertyId;
	List<Statement> statements = new ArrayList<>();

	public JacksonStatementGroup(JacksonPropertyId propertyId,
			List<JacksonStatement> statements) {
		this.propertyId = propertyId;
		for (JacksonStatement statement : statements) {
			JacksonClaim claim = new JacksonClaim(statement, propertyId);
			statement.setClaim(claim);
			this.statements.add(statement);
		}
		// TODO sort statements by rank
	}

	@Override
	public List<Statement> getStatements() {
		return this.statements;
	}

	@Override
	public PropertyIdValue getProperty() {
		return this.propertyId;
	}

	@Override
	public EntityIdValue getSubject() {
		return this.propertyId;
		// TODO check if this really is how it is meant
	}

	@Override
	public Iterator<Statement> iterator() {
		return this.statements.iterator();
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
