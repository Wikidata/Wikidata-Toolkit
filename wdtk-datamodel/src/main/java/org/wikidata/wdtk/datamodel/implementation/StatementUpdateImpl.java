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
package org.wikidata.wdtk.datamodel.implementation;

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Jackson implementation of {@link StatementUpdate}.
 * 
 * @see StatementDocumentUpdateImpl
 */
public class StatementUpdateImpl implements StatementUpdate {

	@JsonIgnore
	private final List<Statement> added;
	@JsonIgnore
	private final Map<String, Statement> replaced;
	@JsonIgnore
	private final Set<String> removed;

	/**
	 * Initializes new statement update.
	 * 
	 * @param added
	 *            added statements
	 * @param replaced
	 *            replaced statements
	 * @param removed
	 *            IDs of removed statements
	 * @throws NullPointerException
	 *             if any parameter is {@code null}
	 * @throws IllegalArgumentException
	 *             if any parameters or their combination is invalid
	 */
	public StatementUpdateImpl(Collection<Statement> added,
			Collection<Statement> replaced,
			Collection<String> removed) {
		this.added = Collections.unmodifiableList(new ArrayList<>(added));
		this.replaced = Collections.unmodifiableMap(replaced.stream().collect(toMap(s -> s.getStatementId(), s -> s)));
		this.removed = Collections.unmodifiableSet(new HashSet<>(removed));
	}

	@JsonIgnore
	@Override
	public boolean isEmpty() {
		return added.isEmpty() && replaced.isEmpty() && removed.isEmpty();
	}

	@JsonIgnore
	@Override
	public List<Statement> getAdded() {
		return added;
	}

	@JsonIgnore
	@Override
	public Map<String, Statement> getReplaced() {
		return replaced;
	}

	@JsonIgnore
	@Override
	public Set<String> getRemoved() {
		return removed;
	}

	static class RemovedStatement {

		private final String id;

		RemovedStatement(String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}

		@JsonProperty("remove")
		String getRemoveCommand() {
			return "";
		}

	}

	@JsonValue
	List<Object> toJson() {
		List<Object> list = new ArrayList<>();
		list.addAll(added);
		list.addAll(replaced.values());
		for (String id : removed) {
			list.add(new RemovedStatement(id));
		}
		return list;
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsStatementUpdate(this, obj);
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

}
