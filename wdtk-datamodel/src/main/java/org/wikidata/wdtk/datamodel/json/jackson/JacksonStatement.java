package org.wikidata.wdtk.datamodel.json.jackson;

/*
 * #%L
 * Wikidata Toolkit Data Model
 * %%
 * Copyright (C) 2014 - 2018 Wikidata Toolkit Developers
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

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Jackson implementation of {@link Statement}. In JSON, the corresponding
 * structures are referred to as "claim".
 * <p>
 * Like all Jackson objects, it is not technically immutable, but it is strongly
 * recommended to treat it as such in all contexts: the setters are for Jackson;
 * never call them in your code.
 *
 * @author Fredo Erxleben
 *
 */
public class JacksonStatement extends JacksonPreStatement implements Statement {
	
	/**
	 * The subject entity that this statement refers to. This is needed since it
	 * is not part of the JSON serialization of statements, but is needed in
	 * WDTK as part of {@link Claim}. Thus, it is necessary to set this
	 * information after each deserialization using
	 * {@link JacksonPreStatement#setSubject(EntityIdValue)}
	 * .
	 */
	@JsonIgnore
	private EntityIdValue subject = null;
	
	/**
	 * Constructor. Creates an empty object that can be populated during JSON
	 * deserialization. Should only be used by Jackson for this very purpose.
	 */
	public JacksonStatement(
			String id,
			StatementRank rank,
			JacksonSnak mainsnak,
			Map<String, List<JacksonSnak>> qualifiers,
			List<String> propertyOrder,
			List<JacksonReference> references,
			EntityIdValue subjectId) {
		super(id, rank, mainsnak, qualifiers, propertyOrder, references);
		Validate.notNull(subjectId);
		this.subject = subjectId;
	}
	

	/**
	 * TODO review the utility of this constructor.
	 *
	 * @param id
	 * @param mainsnak
	 */
	public JacksonStatement(String id, JacksonSnak mainsnak, EntityIdValue subject) {
		super(id, StatementRank.NORMAL, mainsnak, null, null, null);
		this.subject = subject;
	}

	/**
	 * Returns the subject that this statement refers to. This method is only
	 * used by {@link ClaimFromJson} to retrieve data about the subject of this
	 * statement. To access this data from elsewhere, use {@link #getClaim()}.
	 *
	 * @see Claim#getSubject()
	 * @return the subject of this statement
	 */
	@JsonIgnore 
	public EntityIdValue getSubject() {
		return this.subject;
	}
	
	@JsonIgnore
	public Claim getClaim() {
		return new ClaimFromJson(this);
	}
	
	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsStatement(this, obj);
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}
}
