package org.wikidata.wdtk.storage.dboldcode;

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

import java.util.Iterator;

import org.wikidata.wdtk.storage.datamodel.EdgeContainer;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.PropertyTargets;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.TargetQualifiers;
import org.wikidata.wdtk.storage.datamodel.ObjectValue;
import org.wikidata.wdtk.storage.datamodel.PropertyRange;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePair;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.datamodel.SortType;
import org.wikidata.wdtk.storage.datamodel.StringValue;
import org.wikidata.wdtk.storage.db.DatabaseManager;

/**
 * Class for converting between db data objects and internal records that are
 * easy to serialize.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class SerializationConverter {

	public static final byte TYPE_REF = 1;
	public static final byte TYPE_STRING = 2;
	public static final byte TYPE_RECORD = 3;
	public static final byte TYPE_OBJECT = 4;

	public static byte getFieldType(String sortName,
			DatabaseManager databaseManager) {
		if (databaseManager.getSortSchema().useDictionary(sortName)) {
			return SerializationConverter.TYPE_REF;
		} else {
			Sort sort = databaseManager.getSortSchema().getSort(sortName);
			switch (sort.getType()) {
			case OBJECT:
				return SerializationConverter.TYPE_OBJECT;
			case RECORD:
				return SerializationConverter.TYPE_RECORD;
			case STRING:
				return SerializationConverter.TYPE_STRING;
			default:
				throw new RuntimeException("Unsupported sort type");
			}
		}
	}

	public static ObjectValueForSerialization makeObjectValueForSerialization(
			ObjectValue outer, DatabaseManager databaseManager) {
		int length = outer.size();
		byte[] types = new byte[length];
		int[] properties = new int[length];
		Object[] values = new Object[length];

		int refCount = 0;
		int stringCount = 0;

		int i = 0;
		for (PropertyValuePair pvp : outer) {
			properties[i] = databaseManager.getOrCreatePropertyId(
					pvp.getProperty(), outer.getSort().getName(), pvp
							.getValue().getSort().getName());

			if (pvp.getValue().getSort().getType() == SortType.STRING) {
				types[i] = SerializationConverter.TYPE_STRING;
				values[i] = ((StringValue) pvp.getValue()).getString();
				stringCount++;
			} else if (pvp.getValue().getSort().getType() == SortType.OBJECT
					|| pvp.getValue().getSort().getType() == SortType.RECORD) {
				types[i] = SerializationConverter.TYPE_REF;
				values[i] = Integer.valueOf(databaseManager
						.getOrCreateValueId(pvp.getValue()));
				refCount++;
			} else {
				throw new IllegalArgumentException(
						"Value sort not supported yet");
			}
			i++;
		}

		int[] refs = new int[refCount];
		int iRef = 0;
		String[] strings = new String[stringCount];
		int iString = 0;
		for (int j = 0; j < length; j++) {
			if (types[j] == SerializationConverter.TYPE_REF) {
				refs[iRef] = ((Integer) values[j]).intValue();
				iRef++;
			} else if (types[j] == SerializationConverter.TYPE_STRING) {
				strings[iString] = ((String) values[j]);
				iString++;
			}
		}

		return new ObjectValueForSerialization(properties, types, refs, strings);
	}

	public static RecordValueForSerialization makeRecordValueForSerialization(
			ObjectValue outer, Sort sort, byte[] types, int refCount,
			DatabaseManager databaseManager) {

		int[] refs = new int[refCount];
		int iRef = 0;
		Object[] objects = new Object[types.length - refCount];
		int iObject = 0;

		Iterator<PropertyValuePair> propertyValuePairs = outer.iterator();
		Iterator<PropertyRange> propertyRanges = sort.getPropertyRanges()
				.iterator();

		for (int i = 0; i < types.length; i++) {
			PropertyValuePair pvp = propertyValuePairs.next();
			PropertyRange pr = propertyRanges.next();

			// TODO evaluate the performance hit of this check; maybe use
			// assertions instead
			if (!pr.getProperty().equals(pvp.getProperty())
					|| !pr.getRange()
							.equals(pvp.getValue().getSort().getName())) {
				String message = "The given object value of type "
						+ outer.getClass()
						+ "\ndoes not match the sort specification of \""
						+ sort.getName() + "\":\n" + "Position " + i
						+ " should be " + pr.getProperty() + ":"
						+ pr.getRange() + " but was " + pvp.getProperty() + ":"
						+ pvp.getValue().getSort().getName();
				throw new IllegalArgumentException(message);
			}

			if (types[i] == SerializationConverter.TYPE_STRING) {
				// Inline objects:
				objects[iObject] = ((StringValue) pvp.getValue()).getString();
				iObject++;
			} else {
				refs[iRef] = databaseManager.getOrCreateValueId(pvp.getValue());
				iRef++;
			}
		}

		return new RecordValueForSerialization(refs, objects);
	}

	public static EdgeContainerForSerialization getEdgeContainerForSerialization(
			EdgeContainer edgeContainer, int sourceId, Sort sourceSort,
			DatabaseManager databaseManager) {

		int propertyCount = edgeContainer.getEdgeCount();

		int[] properties = new int[propertyCount];
		int[][][] targetQualifiers = new int[propertyCount][][];
		int i = 0;
		for (PropertyTargets pts : edgeContainer) {
			targetQualifiers[i] = new int[pts.getTargetCount()][];

			int j = 0;
			String rangeSort = null;
			for (TargetQualifiers tqs : pts) {
				if (j == 0) {
					rangeSort = tqs.getTarget().getSort().getName();
				}
				targetQualifiers[i][j] = new int[2 * tqs.getQualifierCount() + 1];
				targetQualifiers[i][j][0] = databaseManager
						.getOrCreateValueId(tqs.getTarget());
				int k = 0;
				for (PropertyValuePair pvp : tqs.getQualifiers()) {
					targetQualifiers[i][j][2 * k + 1] = databaseManager
							.getOrCreatePropertyId(pvp.getProperty(),
									rangeSort, pvp.getValue().getSort()
											.getName());
					targetQualifiers[i][j][2 * k + 2] = databaseManager
							.getOrCreateValueId(pvp.getValue());
					k++;
				}
				j++;
			}

			properties[i] = databaseManager.getOrCreatePropertyId(
					pts.getProperty(), sourceSort.getName(), rangeSort);
			i++;
		}

		return new EdgeContainerForSerialization(sourceId, properties,
				targetQualifiers);
	}

}
