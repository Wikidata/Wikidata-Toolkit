package org.wikidata.wdtk.storage.serialization;

/*
 * #%L
 * Wikidata Toolkit Storage
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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;

import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.wikidata.wdtk.storage.datamodel.DecimalValue;
import org.wikidata.wdtk.storage.datamodel.DecimalValueImpl;
import org.wikidata.wdtk.storage.datamodel.LongValue;
import org.wikidata.wdtk.storage.datamodel.LongValueImpl;
import org.wikidata.wdtk.storage.datamodel.ObjectValue;
import org.wikidata.wdtk.storage.datamodel.PropertyRange;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePair;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.datamodel.StringValue;
import org.wikidata.wdtk.storage.datamodel.StringValueImpl;
import org.wikidata.wdtk.storage.datamodel.Value;
import org.wikidata.wdtk.storage.db.DatabaseManager;
import org.wikidata.wdtk.storage.db.PropertySignature;

public class Serialization {

	public static void serializeValue(DataOutput out, Value value,
			DatabaseManager databaseManager) throws IOException {

		if (databaseManager.getSortSchema().useDictionary(
				value.getSort().getName())) {
			out.writeInt(databaseManager.getOrCreateValueId(value));
		} else {
			serializeInlineValue(out, value, databaseManager);
		}
	}

	public static void serializeInlineValue(DataOutput out, Value value,
			DatabaseManager databaseManager) throws IOException {
		switch (value.getSort().getType()) {
		case OBJECT:
			serializeObjectValue(out, ((ObjectValue) value), databaseManager);
			break;
		case RECORD:
			serializeRecordValue(out, ((ObjectValue) value), databaseManager);
			break;
		case STRING:
			serializeStringValue(out, (StringValue) value);
			break;
		case LONG:
			DataOutput2.packLong(out, ((LongValue) value).getLong());
			break;
		case DECIMAL:
			serializeDecimalValue(out, (DecimalValue) value);
			break;
		default:
			throw new RuntimeException("Unsupported sort type");
		}
	}

	public static void serializeStringValue(DataOutput out,
			StringValue stringValue) throws IOException {
		out.writeUTF(stringValue.getString());
	}

	public static void serializeDecimalValue(DataOutput out,
			DecimalValue decimalValue) throws IOException {
		try {
			long longValue = decimalValue.getDecimal().longValueExact();
			if (longValue == Long.MIN_VALUE) {
				throw new ArithmeticException(
						"Cannot decrement this long. Store as string.");
			}
			if (longValue <= 0) { // never store 0
				longValue--;
			}
			DataOutput2.packLong(out, longValue);
		} catch (ArithmeticException e) {
			DataOutput2.packLong(out, 0L); // marker for string encoding
			out.writeUTF(decimalValue.toString());
		}
	}

	public static void serializeRecordValue(DataOutput out,
			ObjectValue recordValue, DatabaseManager databaseManager)
					throws IOException {

		Iterator<PropertyValuePair> propertyValuePairs = recordValue.iterator();
		int i = 0;
		for (PropertyRange pr : recordValue.getSort().getPropertyRanges()) {
			PropertyValuePair pvp = propertyValuePairs.next();

			// TODO evaluate the performance hit of this check; maybe use
			// assertions instead
			if (!pr.getProperty().equals(pvp.getProperty())
					|| !pr.getRange()
					.equals(pvp.getValue().getSort().getName())) {
				String message = "The given object value of type "
						+ recordValue.getClass()
						+ "\ndoes not match the sort specification of \""
						+ recordValue.getSort().getName() + "\":\n"
						+ "Position " + i + " should be " + pr.getProperty()
						+ ":" + pr.getRange() + " but was " + pvp.getProperty()
						+ ":" + pvp.getValue().getSort().getName();
				throw new IllegalArgumentException(message);
			}

			serializeValue(out, pvp.getValue(), databaseManager);

			i++;
		}
	}

	public static void serializeObjectValue(DataOutput out,
			ObjectValue objectValue, DatabaseManager databaseManager)
					throws IOException {

		out.writeInt(objectValue.size());

		for (PropertyValuePair pvp : objectValue) {
			String valueSortName = pvp.getValue().getSort().getName();
			int property = databaseManager.getOrCreatePropertyId(
					pvp.getProperty(), objectValue.getSort().getName(),
					valueSortName);
			out.writeInt(property);
		}

		for (PropertyValuePair pvp : objectValue) {
			serializeValue(out, pvp.getValue(), databaseManager);
		}
	}

	public static StringValue deserializeStringValue(DataInput in, Sort sort)
			throws IOException {
		return new StringValueImpl(in.readUTF(), sort);
	}

	public static DecimalValue deserializeDecimalValue(DataInput in, Sort sort)
			throws IOException {
		long longValue = DataInput2.unpackLong(in);
		if (longValue == 0) {
			return new DecimalValueImpl(new BigDecimal(in.readUTF()), sort);
		} else {
			if (longValue < 0) {
				longValue++;
			}
			return new DecimalValueImpl(new BigDecimal(longValue), sort);
		}
	}

	public static ObjectValue deserializeRecordValue(DataInput in, Sort sort,
			DatabaseManager databaseManager) throws IOException {

		int propertyCount = sort.getPropertyRanges().size();
		Sort[] sorts = new Sort[propertyCount];

		// TODO the sorts array is always the same for records; could be
		// pre-computed
		int i = 0;
		int refCount = 0;
		for (PropertyRange pr : sort.getPropertyRanges()) {
			Sort valueSort = databaseManager.getSortSchema().getSort(
					pr.getRange());
			if (databaseManager.getSortSchema().useDictionary(
					valueSort.getName())) {
				refCount++;
				sorts[i] = null;
			} else {
				sorts[i] = valueSort;
			}
			i++;
		}

		int[] refs = new int[refCount];
		Value[] values = new Value[propertyCount - refCount];
		deserializeSortedValues(in, sorts, databaseManager, refs, values);

		return new RecordValueFromSerialization(refs, values, sort,
				databaseManager);
	}

	public static ObjectValue deserializeObjectValue(DataInput in, Sort sort,
			DatabaseManager databaseManager) throws IOException {
		int propertyCount = in.readInt();
		int[] properties = new int[propertyCount];
		Sort[] sorts = new Sort[propertyCount];

		int refCount = 0;
		for (int i = 0; i < propertyCount; i++) {
			properties[i] = in.readInt();
			PropertySignature ps = databaseManager
					.fetchPropertySignature(properties[i]);
			Sort valueSort = databaseManager.getSortSchema().getSort(
					ps.getRangeId());
			if (databaseManager.getSortSchema().useDictionary(
					valueSort.getName())) {
				refCount++;
				sorts[i] = null;
			} else {
				sorts[i] = valueSort;
			}
		}

		int[] refs = new int[refCount];
		Value[] values = new Value[propertyCount - refCount];
		deserializeSortedValues(in, sorts, databaseManager, refs, values);

		return new ObjectValueFromSerialization(properties, refs, values, sort,
				databaseManager);
	}

	protected static void deserializeSortedValues(DataInput in, Sort[] sorts,
			DatabaseManager databaseManager, int[] refs, Value[] values)
					throws IOException {
		int iRef = 0;
		int iObject = 0;
		for (Sort valueSort : sorts) {
			if (valueSort == null) {
				refs[iRef] = in.readInt();
				iRef++;
			} else {
				values[iObject] = deserializeInlineValue(in, valueSort,
						databaseManager);
				iObject++;
			}
		}
	}

	protected static Value deserializeInlineValue(DataInput in, Sort sort,
			DatabaseManager databaseManager) throws IOException {
		switch (sort.getType()) {
		case OBJECT:
			return deserializeObjectValue(in, sort, databaseManager);
		case RECORD:
			return deserializeRecordValue(in, sort, databaseManager);
		case STRING:
			return new StringValueImpl(in.readUTF(), sort);
		case LONG:
			return new LongValueImpl(DataInput2.unpackLong(in), sort);
		default:
			throw new RuntimeException("Unsupported sort type");
		}
	}
}
