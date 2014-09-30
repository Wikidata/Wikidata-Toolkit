package org.wikidata.wdtk.dumpfiles.oldjson;

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
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
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
class StatementGroupBuilder {

	private final DataObjectFactory factory;

	/**
	 * Creates a new StatementGroupBuilder.
	 *
	 * @param factory
	 *            the DataObjectFactory to be used for generating
	 *            StatementGroups.
	 */
	StatementGroupBuilder(DataObjectFactory factory) {
		this.factory = factory;
	}

	/**
	 * Creates a list of StatementGroups from a list of ungrouped Statements.
	 *
	 * @param statements
	 *            a list of Statements
	 * @return
	 */
	List<StatementGroup> buildFromStatementList(List<Statement> statements) {
		List<StatementGroup> result = new ArrayList<>();

		Map<PropertyIdValue, List<Statement>> groups = new HashMap<>();

		for (Statement currentStatement : statements) {
			PropertyIdValue propertyIdValue = currentStatement.getClaim()
					.getMainSnak().getPropertyId();

			if (groups.containsKey(propertyIdValue)) {
				groups.get(propertyIdValue).add(currentStatement);
			} else {
				List<Statement> groupedStatements = new ArrayList<Statement>();
				groupedStatements.add(currentStatement);
				groups.put(propertyIdValue, groupedStatements);
			}
		}

		// Sort each list of grouped statements by rank:
		for (List<Statement> groupeStatements : groups.values()) {
			Collections.sort(groupeStatements,
					Collections.reverseOrder(new RankComparator()));
			result.add(this.factory.getStatementGroup(groupeStatements));
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
