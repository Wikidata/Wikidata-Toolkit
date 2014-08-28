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

import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.json.jackson.documents.ids.PropertyIdImpl;

/**
 * This class is merely there to be compatible with the WDTK-data model interface
 * There is no concept of dedicated statement group objects in the JSON rather
 * then a list of statements relating to a property.
 * 
 * @author Fredo Erxleben
 *
 */
public class StatementGroupImpl implements StatementGroup {

	PropertyIdImpl propertyId;
	List<StatementImpl> statements;

	public StatementGroupImpl(PropertyIdImpl propertyId, List<StatementImpl> statements) {
		this.propertyId = propertyId;
		this.statements = statements;
		// TODO sort statements by rank
	}

	@Override
	public List<Statement> getStatements() {
		// TODO rework, once the interface changes
		List<Statement> returnList = new ArrayList<>();
		for(StatementImpl statement : this.statements){
			returnList.add(statement);
		}
		return returnList;
	}

	@Override
	public PropertyIdValue getProperty() {
		return this.propertyId;
	}

	@Override
	public EntityIdValue getSubject() {
		return this.propertyId;
	}

	@Override
	public Iterator<Statement> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

}
