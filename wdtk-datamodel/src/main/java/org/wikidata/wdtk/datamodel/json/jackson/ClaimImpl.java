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

import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.json.jackson.snaks.SnakGroupImpl;

/**
 * This class only exists to satisfy the interface of the data model.
 * @author Fredo Erxleben
 *
 */
public class ClaimImpl implements Claim {

	private StatementImpl statement;
	private EntityIdValue subject;
	
	ClaimImpl(StatementImpl statement, EntityIdValue subject){
		this.statement = statement;
		this.subject = subject;
	}
	
	@Override
	public EntityIdValue getSubject() {
		return this.subject;
	}

	@Override
	public Snak getMainSnak() {
		return this.statement.getMainsnak();
	}

	@Override
	public List<SnakGroup> getQualifiers() {
		List<SnakGroup> resultList = new ArrayList<>();
		for(SnakGroupImpl snaks : Helper.buildSnakGroups(this.statement.getQualifiers())){
			resultList.add(snaks);
		}
		return resultList;
	}

	@Override
	public Iterator<Snak> getAllQualifiers() {
		// TODO Auto-generated method stub
		return null;
	}

}
