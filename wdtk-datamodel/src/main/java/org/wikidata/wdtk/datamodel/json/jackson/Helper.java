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
