package org.wikidata.wdtk.datamodel.json.jackson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.wikidata.wdtk.datamodel.json.jackson.documents.ids.PropertyIdImpl;
import org.wikidata.wdtk.datamodel.json.jackson.snaks.SnakGroupImpl;
import org.wikidata.wdtk.datamodel.json.jackson.snaks.SnakImpl;

/**
 * A class that provides several helper methods.
 * 
 * @author Fredo Erxleben
 *
 */
public class Helper {

	/**
	 * The given parameter holds a mapping from the propertyId as String to the
	 * snak for this propertyId.
	 * 
	 * @param snaks
	 * @return
	 */
	public static List<SnakGroupImpl> buildSnakGroups(
			Map<String, List<SnakImpl>> snaks) {
		// TODO
		return null;
	}

	/**
	 * The given parameter holds a mapping from the propertyId as String to the
	 * statement for this propertyId.
	 * 
	 * @param statements
	 * @return
	 */
	public static List<StatementGroupImpl> buildStatementGroups(
			Map<String, List<StatementImpl>> statements) {
		List<StatementGroupImpl> result = new ArrayList<>();

		for (Entry<String, List<StatementImpl>> entry : statements.entrySet()) {
			result.add(new StatementGroupImpl(
					new PropertyIdImpl(entry.getKey()), entry.getValue()));
		}

		return result;
	}
}
