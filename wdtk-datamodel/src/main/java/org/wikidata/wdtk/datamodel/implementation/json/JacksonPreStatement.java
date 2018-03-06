package org.wikidata.wdtk.datamodel.implementation.json;

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

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.wikidata.wdtk.datamodel.implementation.ReferenceImpl;
import org.wikidata.wdtk.datamodel.implementation.SnakImpl;
import org.wikidata.wdtk.datamodel.implementation.StatementImpl;
import org.wikidata.wdtk.datamodel.interfaces.*;

import java.util.*;

/**
 * Helper class for deserializing statements from JSON.
 * 
 * @author Antonin Delpeuch
 * @author Thomas Pellissier Tanon
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class JacksonPreStatement {

	private final String statementId;

	private final StatementRank rank;

	private final List<Reference> references;

	private final Snak mainSnak;

	private final Map<String, List<Snak>> qualifiers;
	
	private final List<String> qualifiersOrder;

	private JacksonPreStatement(
			String statementId,
			StatementRank rank,
			Snak mainsnak,
			Map<String, List<Snak>> qualifiers,
			List<String> qualifiersOrder,
			List<Reference> references) {
		this.statementId = statementId;
		this.rank = rank;
		this.mainSnak = mainsnak;
		this.qualifiers = qualifiers;
		this.qualifiersOrder = qualifiersOrder;
		this.references = references;
	}
	
	/**
	 * JSON deserialization creator.
	 */
	@JsonCreator
	static JacksonPreStatement fromJson(
			@JsonProperty("id") String id,
			@JsonProperty("rank") 	@JsonDeserialize(using = StatementRankDeserializer.class) StatementRank rank,
			@JsonProperty("mainsnak") SnakImpl mainsnak,
			@JsonProperty("qualifiers") Map<String, List<SnakImpl>> qualifiers,
			@JsonProperty("qualifiers-order") List<String> qualifiersOrder,
			@JsonProperty("references") @JsonDeserialize(contentAs=ReferenceImpl.class) List<Reference> references) {
		// Forget the concrete type of Jackson snaks for the qualifiers
		if(qualifiers == null) {
			qualifiers = Collections.emptyMap();
		}
		Map<String, List<Snak>> newQualifiers = new HashMap<>(qualifiers.size());
		for(Map.Entry<String,List<SnakImpl>> entry : qualifiers.entrySet()) {
			List<Snak> snaks = new ArrayList<>(entry.getValue());
			newQualifiers.put(entry.getKey(), snaks);
		}
		return new JacksonPreStatement(id, rank, mainsnak, newQualifiers, qualifiersOrder, references);
	}
	
	
	public StatementImpl withSubject(EntityIdValue subjectId) {
		return new StatementImpl(statementId, rank, mainSnak, qualifiers, qualifiersOrder, references, subjectId);
	}
}
