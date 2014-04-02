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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;

/**
 * A helper class to construct StatementGroups from lists of ungrouped
 * statements.
 * 
 * @author Fredo Erxleben
 * 
 */
public class StatementGroupBuilder {
	 // NOTE: The class is public to allow its use in tests.

	private final DataObjectFactory factory;

	/**
	 * Creates a new StatementGroupBuilder.
	 * 
	 * @param factory
	 *            the DataObjectFactory to be used for generating
	 *            StatementGroups.
	 */
	public StatementGroupBuilder(DataObjectFactory factory) {
		this.factory = factory;
	}

	/**
	 * Creates a list of StatementGroups from a list of ungrouped Statements.
	 * 
	 * @param statements
	 *            a list of Statements concerning the same subject. The list
	 *            will be decomposed in the process.
	 * @return
	 */
	public List<StatementGroup> buildFromStatementList(List<Statement> statements) {
		// NOTE: the list of statements will be modified.
		// Is this acceptable or do we need to work with a copy?

		List<StatementGroup> result = new ArrayList<>();

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
				List<Statement> value = new ArrayList<>();
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
			Collections.sort(value,
					Collections.reverseOrder(new RankComparator()));
			StatementGroup toAdd = this.factory.getStatementGroup(value);
			result.add(toAdd);
		}

		return result;
	}

	/**
	 * A comparator for StatementRank objects. The natural order of ranks
	 * (greatest to least) is PREFERRED, NORMAL, DEPRECATED.
	 * 
	 * @author Fredo Erxleben
	 * 
	 */
	private class RankComparator implements Comparator<Statement> {

		/**
		 * Maps a StatementRank to an integer as follows: PREFERRED to 1, NORMAL
		 * to 0, and DEPRECATED to -1.
		 * 
		 * @param rank
		 *            the rank to convert
		 * @return integer value for the rank
		 */
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
			Integer value0 = rankToInteger(arg0.getRank());
			Integer value1 = rankToInteger(arg1.getRank());
			return value0.compareTo(value1);
		}

	}
}
