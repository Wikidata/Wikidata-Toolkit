package org.wikidata.wdtk.datamodel.externalJsonImplementation;

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

import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

public abstract class EntityIdValueImpl implements EntityIdValue {
	
	String id;
	
	public void setId(String id){
		this.id = id;
	}

	@Override
	public abstract String getIri();

	@Override
	public abstract <T> T accept(ValueVisitor<T> valueVisitor);

	@Override
	public abstract String getEntityType();

	@Override
	public String getId(){
		return this.id;
	}

}
