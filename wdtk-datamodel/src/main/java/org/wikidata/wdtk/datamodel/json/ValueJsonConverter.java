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

import org.json.JSONException;
import org.json.JSONObject;
import org.wikidata.wdtk.datamodel.helpers.DataFormatter;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

/**
 * Helper class that converts instances of {@link Value} to JSON for later
 * serialization.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class ValueJsonConverter implements ValueVisitor<JSONObject> {

	/**
	 * The value that should be used for the (not yet supported) unit field when
	 * converting quantity values.
	 */
	final String STD_UNIT_VALUE = "1";

	@Override
	public JSONObject visit(DatatypeIdValue value) {
		throw new UnsupportedOperationException(
				"DatatypeIdValues cannot be converted to JSON. They are not handled as snak values in the conversion.");
	}

	@Override
	public JSONObject visit(EntityIdValue value) {
		JSONObject valueResult = new JSONObject();
		valueResult.put("numeric-id", value.getId());

		String jsonEntityType;
		switch (value.getEntityType()) {
		case EntityIdValue.ET_ITEM:
			jsonEntityType = JsonConstants.NAME_ENTITY_TYPE_ITEM;
			break;
		case EntityIdValue.ET_PROPERTY:
			jsonEntityType = JsonConstants.NAME_ENTITY_TYPE_PROPERTY;
			break;
		default:
			throw new JSONException("Unsupported entity type: "
					+ value.getEntityType());
		}

		valueResult.put("entity-type", jsonEntityType);

		JSONObject result = new JSONObject();
		result.put(JsonConstants.KEY_TYPE, "wikibase-entityid");
		result.put(JsonConstants.KEY_VALUE, valueResult);
		return result;
	}

	@Override
	public JSONObject visit(GlobeCoordinatesValue value) {
		JSONObject result = new JSONObject();
		JSONObject valueResult = new JSONObject();
		result.put(JsonConstants.KEY_VALUE, valueResult);

		valueResult
				.put("latitude",
						((double) value.getLatitude() / GlobeCoordinatesValue.PREC_DEGREE));
		valueResult.put("longitude", (double) value.getLongitude()
				/ GlobeCoordinatesValue.PREC_DEGREE);
		valueResult.put("precision", (double) value.getPrecision()
				/ GlobeCoordinatesValue.PREC_DEGREE);
		valueResult.put("globe", value.getGlobe());

		result.put(JsonConstants.KEY_TYPE, "globecoordinate");

		return result;
	}

	@Override
	public JSONObject visit(MonolingualTextValue value) {
		JSONObject result = new JSONObject();
		result.put("language", value.getLanguageCode());
		result.put(JsonConstants.KEY_VALUE, value.getText());
		return result;
	}

	@Override
	public JSONObject visit(QuantityValue value) {
		JSONObject result = new JSONObject();
		JSONObject valueResult = new JSONObject();

		result.put(JsonConstants.KEY_VALUE, valueResult);

		valueResult.put("amount",
				DataFormatter.formatBigDecimal(value.getNumericValue()));
		valueResult.put("unit", STD_UNIT_VALUE);
		valueResult.put("upperBound",
				DataFormatter.formatBigDecimal(value.getUpperBound()));
		valueResult.put("lowerBound",
				DataFormatter.formatBigDecimal(value.getLowerBound()));

		result.put(JsonConstants.KEY_TYPE, "quantity");

		return result;
	}

	@Override
	public JSONObject visit(StringValue value) {
		JSONObject result = new JSONObject();
		result.put(JsonConstants.KEY_VALUE, value.getString());
		result.put(JsonConstants.KEY_TYPE, "string");
		return result;
	}

	@Override
	public JSONObject visit(TimeValue value) {
		JSONObject result = new JSONObject();
		JSONObject valueResult = new JSONObject();

		result.put(JsonConstants.KEY_VALUE, valueResult);

		valueResult.put("time", DataFormatter.formatTimeISO8601(value));
		valueResult.put("timezone", value.getTimezoneOffset());
		valueResult.put("before", value.getBeforeTolerance());
		valueResult.put("after", value.getAfterTolerance());
		valueResult.put("precision", value.getPrecision());
		valueResult.put("calendarmodel", value.getPreferredCalendarModel());

		result.put(JsonConstants.KEY_TYPE, "time");

		return result;
	}

}
