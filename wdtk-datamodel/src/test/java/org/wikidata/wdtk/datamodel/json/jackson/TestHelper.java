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

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.json.jackson.snaks.JacksonNoValueSnak;
import org.wikidata.wdtk.datamodel.json.jackson.snaks.JacksonSnak;

public class TestHelper extends JsonConversionTest {

	@Test
	public void testBuildStatementGroups() {

		// set up
		Map<String, List<JacksonStatement>> input = new HashMap<>();
		List<JacksonStatementGroup> output;

		// build the input map
		for (int snakId = 1; snakId < 6; snakId++) {
			JacksonSnak testSnak = new JacksonNoValueSnak("P" + snakId);

			List<JacksonStatement> statements = new ArrayList<>();
			for (int groupId = 0; groupId < 5; groupId++) {
				JacksonStatement statement = new JacksonStatement("Id" + snakId
						+ groupId, testSnak);
				statements.add(statement);
			}

			input.put("P" + snakId, statements);
		}

		// run the tested method
		output = Helper.buildStatementGroups(input);

		// check output
		assertNotNull(output);

		for (JacksonStatementGroup group : output) {
			assert (input.containsKey(group.getProperty().getId().toString()));
			List<JacksonStatement> valueList = input.get(group.getProperty().getId());
			for (Statement statement : group.getStatements()) {
				// one of the lists in input contains the statement
				assert(valueList.contains(statement));
				assertNotNull(statement.getClaim());
			}
		}

		// TODO further checks would be nice

	}

	@Test
	public void testBuildSnakGroups() {

	}

	@Test
	public void testConstructEntityId() {

	}
}
