package org.wikidata.wdtk.dumpfiles;

/*
 * #%L
 * Wikidata Toolkit Dump File Handling
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;

/**
 * A helper class to construct StatementGroup-objects according to the WDTK data
 * model.
 * 
 * @author Fredo Erxleben
 * 
 */
class StatementGroupBuilder {

	private DataObjectFactory factory;

	StatementGroupBuilder() {
		this(new DataObjectFactoryImpl());
	}

	/**
	 * Creates a new StatementGroupBuilder - instance.
	 * 
	 * @param factory
	 *            the DataObjectFactory-instance to be used for generating
	 *            StatementGroup-Instances.
	 */
	StatementGroupBuilder(DataObjectFactory factory) {
		this.factory = factory;
	}

	/**
	 * 
	 * @param statements
	 *            a list of Statements concerning the same subject. The list
	 *            will be decomposed in the process.
	 * @return
	 */
	List<StatementGroup> buildFromStatementList(List<Statement> statements) {
		// NOTE: the list of statements will be decomposed.
		// Is this acceptable or do we need to work with a copy?

		List<StatementGroup> result = new LinkedList<>();

		Map<Snak, List<Statement>> groups = new HashMap<>();

		while (!statements.isEmpty()) {
			// pick the first statement and check
			// if an according group already exists
			// Therefore extract the main snak

			Statement currentStatement = statements.get(0);
			Snak currentSnak = currentStatement.getClaim().getMainSnak();

			if (groups.containsKey(currentSnak)) {
				// add currentStatement to the list of statements
				// corresponding to this key
				List<Statement> value = groups.get(currentSnak);
				value.add(currentStatement);
			} else {
				// create a new group
				List<Statement> value = new LinkedList<>();
				value.add(currentStatement);
				groups.put(currentSnak, value);
			}

			// remove processed (first) statements
			statements.remove(0);
		}

		// now, sort each list by rank
		// NOTE that one could extend the code
		// to sort the statements, using other criteria
		// by implementing other comparators.

		for (Snak s : groups.keySet()) {
			List<Statement> value = groups.get(s);
			Collections.sort(value, new RankComparator());
			StatementGroup toAdd = this.factory.getStatementGroup(value);
			result.add(toAdd);
		}

		return result;
	}

	/**
	 * The comparator used for sorting the statement lists by rank. Thereby the
	 * rank PREFERRED is to appear first, then NORMAL, then DEPRECATED.
	 * 
	 * @author Fredo Erxleben
	 * 
	 */
	private class RankComparator implements Comparator<Statement> {

		private Integer rankToInteger(StatementRank rank) {
			switch (rank) {
			case PREFERRED:
				return 1;
			case DEPRECATED:
				return -1;
			default:
				return 0;
			}
		}

		@Override
		public int compare(Statement arg0, Statement arg1) {

			// simple principle: map each rank to a number between 1 and -1
			// and compare these to each other.
			// the result is the result as requested by the Comparator-Interface

			Integer value0 = this.rankToInteger(arg0.getRank());
			Integer value1 = this.rankToInteger(arg1.getRank());

			// return the negative result to achieve a sorting
			// from highest to lowest rank
			return -value0.compareTo(value1);

		}

	}
}
