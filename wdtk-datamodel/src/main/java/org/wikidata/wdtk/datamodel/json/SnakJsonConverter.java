package org.wikidata.wdtk.datamodel.json;

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

import org.json.JSONObject;
import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakVisitor;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

/**
 * Class that converts instances of {@link Snak} to JSON for later
 * serialization.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class SnakJsonConverter implements SnakVisitor<JSONObject> {

	final ValueJsonConverter valueJsonConverter;

	public SnakJsonConverter(ValueJsonConverter valueJsonConverter) {
		this.valueJsonConverter = valueJsonConverter;
	}

	@Override
	public JSONObject visit(ValueSnak snak) {
		JSONObject result = new JSONObject();
		result.put(JsonConstants.KEY_SNAK_TYPE, "value");
		result.put(JsonConstants.KEY_PROPERTY, snak.getPropertyId().getId());
		result.put(JsonConstants.KEY_DATAVALUE,
				snak.getValue().accept(this.valueJsonConverter));
		// TODO put datatype in result (datatype of the property)
		return result;
	}

	@Override
	public JSONObject visit(SomeValueSnak snak) {
		JSONObject result = new JSONObject();
		result.put(JsonConstants.KEY_SNAK_TYPE, "somevalue");
		result.put(JsonConstants.KEY_PROPERTY, snak.getPropertyId().getId());
		return result;
	}

	@Override
	public JSONObject visit(NoValueSnak snak) {
		JSONObject result = new JSONObject();
		result.put(JsonConstants.KEY_SNAK_TYPE, "novalue");
		result.put(JsonConstants.KEY_PROPERTY, snak.getPropertyId().getId());
		return result;
	}

}
