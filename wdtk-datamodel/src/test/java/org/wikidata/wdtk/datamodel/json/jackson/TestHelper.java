package org.wikidata.wdtk.datamodel.json.jackson;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.json.jackson.snaks.NoValueSnakImpl;
import org.wikidata.wdtk.datamodel.json.jackson.snaks.SnakImpl;

public class TestHelper extends JsonConversionTest {

	@Test
	public void testBuildStatementGroups() {

		// set up
		Map<String, List<StatementImpl>> input = new HashMap<>();
		List<StatementGroupImpl> output;

		// build the input map
		for (int snakId = 1; snakId < 6; snakId++) {
			SnakImpl testSnak = new NoValueSnakImpl("P" + snakId);

			List<StatementImpl> statements = new ArrayList<>();
			for (int groupId = 0; groupId < 5; groupId++) {
				StatementImpl statement = new StatementImpl("Id" + snakId
						+ groupId, testSnak);
				statements.add(statement);
			}

			input.put("P" + snakId, statements);
		}

		// run the tested method
		output = Helper.buildStatementGroups(input);

		// check output
		assertNotNull(output);

		for (StatementGroupImpl group : output) {
			assert (input.containsKey(group.getProperty().toString()));
			for (Statement statement : group.getStatements()) {
				assert (input.containsValue(statement));
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
