package org.wikidata.wdtk.rdf;

/*
 * #%L
 * Wikidata Toolkit RDF
 * %%
 * Copyright (C) 2014 - 2015 Wikidata Toolkit Developers
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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.rdf4j.model.Resource;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;

/**
 * Holds information about the highest rank in an {@link EntityDocument} and the
 * corresponding statement to generate BestRank triples.
 * Ignores deprecated statements
 *
 * @author Michael Guenther
 *
 */
public class RankBuffer {

	/**
	 * highest Rank of an statment in the current {@link EntityDocument}.
	 */
	private StatementRank bestRank = StatementRank.NORMAL;

	/**
	 * Rdf Resources that refer to statements with the highest rank.
	 */
	private final Set<Resource> subjects = new HashSet<>();

	/**
	 * Clears the buffer. This function should be called after each export of an
	 * entity document.
	 */
	public void clear() {
		bestRank = StatementRank.NORMAL;
		subjects.clear();
	}

	/**
	 * Adds a Statement.
	 *
	 * @param rank
	 *            rank of the statement
	 * @param subject
	 *            rdf resource that refers to the statement
	 */
	public void add(StatementRank rank, Resource subject) {
		if (this.bestRank == rank) {
			subjects.add(subject);
		} else if(bestRank == StatementRank.NORMAL && rank == StatementRank.PREFERRED) {
			//We found a preferred statement
			subjects.clear();
			bestRank = StatementRank.PREFERRED;
			subjects.add(subject);
		}
	}

	/**
	 * Returns the statements with the highest rank.
	 *
	 * @return statement resource with the highest rank.
	 */
	public Set<Resource> getBestRankedStatements() {
		return this.subjects;
	}

}
