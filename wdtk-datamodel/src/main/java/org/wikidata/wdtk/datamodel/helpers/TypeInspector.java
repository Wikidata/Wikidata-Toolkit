package org.wikidata.wdtk.datamodel.helpers;

/*
 * #%L
 * Wikidata Toolkit Data Model
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

import java.util.List;

import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.SnakVisitor;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

// TODO this is still completely rudimentary
public class TypeInspector implements SnakVisitor<String>, ValueVisitor<String> {

	public String inspect(ItemDocument document) {
		String result = document.getClass().getName();
		result += "\n--- Id ---";
		result += document.getItemId().getClass().getName();
		result += "\n--- Labels ---";
		for (MonolingualTextValue label : document.getLabels().values()) {
			result += "\n\t" + label.getClass().getName();
		}
		result += "\n--- Aliases ---";
		for (List<MonolingualTextValue> aliasList : document.getAliases()
				.values()) {
			result += "\n\t ---";
			for (MonolingualTextValue alias : aliasList) {
				result += "\n\t\t" + alias.getClass().getName();
			}
		}
		result += "\n--- StatementGroups ---";
		for (StatementGroup statementGroup : document.getStatementGroups()) {
			result += "\n\t ---";
			for (Statement statement : statementGroup.getStatements()) {
				result += "\n\t\tStatement: " + statement.getClass().getName();
				result += "\n\t\tMainSnak: " + statement.getClaim().getMainSnak().accept(this);
				for(SnakGroup snakGroup : statement.getClaim().getQualifiers()){
					result += "\n\t\tSnakGroup: " + snakGroup.getClass().getName();
					for(Snak snak : snakGroup.getSnaks()){
						result += snak.accept(this);
					}
				}
			}
		}

		return result;
	}

	@Override
	public String visit(DatatypeIdValue value) {
		return "\t" + value.getClass().getName() + "\n";
	}

	@Override
	public String visit(EntityIdValue value) {
		return "\t" + value.getClass().getName() + "\n";
	}

	@Override
	public String visit(GlobeCoordinatesValue value) {
		return "\t" + value.getClass().getName() + "\n";
	}

	@Override
	public String visit(MonolingualTextValue value) {
		return "\t" + value.getClass().getName() + "\n";
	}

	@Override
	public String visit(QuantityValue value) {
		return "\t" + value.getClass().getName() + "\n";
	}

	@Override
	public String visit(StringValue value) {
		return "\t" + value.getClass().getName() + "\n";
	}

	@Override
	public String visit(TimeValue value) {
		return "\t" + value.getClass().getName() + "\n";
	}

	@Override
	public String visit(ValueSnak snak) {
		return snak.getClass().getName() + "\n" + "\n\t" + snak.getValue().accept(this);
	}

	@Override
	public String visit(SomeValueSnak snak) {
		return snak.getClass().getName() + "\n";
	}

	@Override
	public String visit(NoValueSnak snak) {
		return snak.getClass().getName() + "\n";
	}

}
