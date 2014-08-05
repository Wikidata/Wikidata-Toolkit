package org.wikidata.wdtk.storage.wdtkbindings;

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

import java.util.Collections;
import java.util.Iterator;

import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer;
import org.wikidata.wdtk.storage.datamodel.StringValueImpl;
import org.wikidata.wdtk.storage.datamodel.Value;
import org.wikidata.wdtk.storage.db.DatabaseManager;
import org.wikidata.wdtk.storage.db.PropertySignature;
import org.wikidata.wdtk.storage.dbquery.ScanEdgeContainerIterator;
import org.wikidata.wdtk.storage.dbtowdtk.EntityDocumentFromEdgeContainerIterator;
import org.wikidata.wdtk.storage.dbtowdtk.WdtkFromDb;
import org.wikidata.wdtk.storage.wdtktodb.ValueToValueVisitor;
import org.wikidata.wdtk.storage.wdtktodb.WdtkAdaptorHelper;

public class WdtkDatabaseManager extends DatabaseManager {

	public WdtkDatabaseManager(String dbName) {
		super(dbName);

		this.sortSchema.declareSort(WdtkSorts.SORT_ENTITY, true);
		this.sortSchema.declareSort(WdtkSorts.SORT_SPECIAL_STRING, true);
		this.sortSchema.declareSort(WdtkSorts.SORT_MTV, false);
		this.sortSchema.declareSort(WdtkSorts.SORT_LABEL, false);
		this.sortSchema.declareSort(WdtkSorts.SORT_LABEL_STRING, false);
		this.sortSchema.declareSort(WdtkSorts.SORT_DESCRIPTION, false);
		this.sortSchema.declareSort(WdtkSorts.SORT_ALIAS, false);
		// this.sortSchema.declareSort(WdtkSorts.SORT_TERMS, true);
		this.sortSchema.declareSort(WdtkSorts.SORT_REFERENCE, true);
		this.sortSchema.declareSort(WdtkSorts.SORT_TIME_VALUE, true);
		this.sortSchema.declareSort(WdtkSorts.SORT_GLOBE_COORDINATES_VALUE,
				true);
		this.sortSchema.declareSort(WdtkSorts.SORT_QUANTITY_VALUE, true);
		this.sortSchema.declareSort(WdtkSorts.SORT_SITE_LINK, false);
	}

	public EntityDocument getEntityDocument(EntityIdValue entityIdValue) {
		return getEntityDocument(entityIdValue.getId(),
				entityIdValue.getSiteIri(), entityIdValue.getEntityType());
	}

	public EntityDocument getEntityDocument(String entityId, String siteIri,
			String entityType) {
		String entityName = WdtkAdaptorHelper.getStringForEntityIdValue(
				entityId, siteIri, entityType);
		EdgeContainer edgeContainer = fetchEdgeContainer(new StringValueImpl(
				entityName, WdtkSorts.SORT_ENTITY));
		if (edgeContainer == null) {
			return null;
		} else {
			return WdtkFromDb.EntityDocumentFromEdgeContainer(edgeContainer,
					new DataObjectFactoryImpl());
		}
	}

	public Iterator<EntityDocument> entityDocuments() {
		return new EntityDocumentFromEdgeContainerIterator(edgeContainers(
				WdtkSorts.SORTNAME_ENTITY).iterator());
	}

	public Iterator<EntityDocument> findEntityDocuments(
			PropertyIdValue propertyIdValue,
			org.wikidata.wdtk.datamodel.interfaces.Value value) {
		return new EntityDocumentFromEdgeContainerIterator(findEdgeContainers(
				propertyIdValue, value));
	}

	public Iterator<? extends EdgeContainer> findEdgeContainers(
			PropertyIdValue propertyIdValue,
			org.wikidata.wdtk.datamodel.interfaces.Value value) {

		if (propertyIdValue == null) {
			return edgeContainers(WdtkSorts.SORTNAME_ENTITY).iterator();
		}

		String propertyName = WdtkAdaptorHelper
				.getStringForEntityIdValue(propertyIdValue);

		int valueId;
		String rangeSort;
		if (value != null) {
			ValueToValueVisitor vtvv = new ValueToValueVisitor();
			Value v = value.accept(vtvv);
			valueId = getValueId(v);
			rangeSort = v.getSort().getName();
		} else {
			valueId = -1;
			int entitySortId = getSortSchema().getSortId(
					WdtkSorts.SORTNAME_ENTITY);
			rangeSort = null;
			for (PropertySignature ps : properties()) {
				if (entitySortId == ps.getDomainId()
						&& propertyName.equals(ps.getPropertyName())) {
					rangeSort = getSortSchema().getSort(ps.getRangeId())
							.getName();
					break;
				}
			}

		}

		if (rangeSort != null) {
			int propId = getOrCreatePropertyId(propertyName,
					WdtkSorts.SORTNAME_ENTITY, rangeSort);

			return new ScanEdgeContainerIterator(WdtkSorts.SORTNAME_ENTITY,
					propId, valueId, this);
		} else {
			return Collections.<EdgeContainer> emptyList().iterator();
		}
	}
}
