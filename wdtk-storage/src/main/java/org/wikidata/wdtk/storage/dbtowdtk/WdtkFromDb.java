package org.wikidata.wdtk.storage.dbtowdtk;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.PropertyTargets;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.TargetQualifiers;
import org.wikidata.wdtk.storage.datamodel.ObjectValue;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePair;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.datamodel.SortType;
import org.wikidata.wdtk.storage.datamodel.StringValue;
import org.wikidata.wdtk.storage.wdtkbindings.WdtkSorts;

public class WdtkFromDb {

	public static EntityDocument EntityDocumentFromEdgeContainer(
			EdgeContainer edgeContainer) {

		PropertyTargets labels = null;
		PropertyTargets descriptions = null;
		PropertyTargets aliases = null;
		PropertyTargets siteLinks = null;
		List<PropertyTargets> statements = new ArrayList<>(
				edgeContainer.getEdgeCount());

		String docType = null;
		StringValue datatype = null;

		for (PropertyTargets pts : edgeContainer) {
			switch (pts.getProperty()) {
			case WdtkSorts.PROP_DOCTYPE:
				docType = ((StringValue) pts.iterator().next().getTarget())
						.getString();
				break;
			case WdtkSorts.PROP_LABEL:
				labels = pts;
				break;
			case WdtkSorts.PROP_DESCRIPTION:
				descriptions = pts;
				break;
			case WdtkSorts.PROP_ALIAS:
				aliases = pts;
				break;
			case WdtkSorts.PROP_SITE_LINK:
				siteLinks = pts;
				break;
			case WdtkSorts.PROP_NOVALUE:
				// TODO
				break;
			case WdtkSorts.PROP_SOMEVALUE:
				// TODO
				break;
			case WdtkSorts.PROP_DATATYPE:
				datatype = (StringValue) pts.iterator().next().getTarget();
				break;
			default:
				statements.add(pts);
				break;
			}
		}

		if (WdtkSorts.VALUE_DOCTYPE_ITEM.equals(docType)) {
			return new ItemDocumentFromEdgeContainer(edgeContainer, labels,
					descriptions, aliases, siteLinks, statements);
		} else {
			return new PropertyDocumentFromEdgeContainer(edgeContainer, labels,
					descriptions, aliases, datatype);
		}

	}

	public static Map<String, MonolingualTextValue> getMtvMapFromPropertyTargets(
			PropertyTargets propertyTargets) {
		Map<String, MonolingualTextValue> result = new HashMap<>();

		if (propertyTargets != null) {
			for (TargetQualifiers tqs : propertyTargets) {
				ObjectValue value = (ObjectValue) tqs.getTarget();
				String language = null;
				String text = null;
				for (PropertyValuePair pvp : value) {
					if (WdtkSorts.PROP_MTV_LANG.equals(pvp.getProperty())) {
						language = ((StringValue) pvp.getValue()).getString();
					} else if (WdtkSorts.PROP_MTV_TEXT
							.equals(pvp.getProperty())) {
						text = ((StringValue) pvp.getValue()).getString();
					}
				}
				result.put(language,
						Datamodel.makeMonolingualTextValue(text, language));
			}
		}

		return result;
	}

	public static Map<String, List<MonolingualTextValue>> getMtvListMapFromPropertyTargets(
			PropertyTargets propertyTargets) {
		Map<String, List<MonolingualTextValue>> result = new HashMap<>();

		if (propertyTargets != null) {
			for (TargetQualifiers tqs : propertyTargets) {
				ObjectValue value = (ObjectValue) tqs.getTarget();
				String language = null;
				String text = null;
				for (PropertyValuePair pvp : value) {
					if (WdtkSorts.PROP_MTV_LANG.equals(pvp.getProperty())) {
						language = ((StringValue) pvp.getValue()).getString();
					} else if (WdtkSorts.PROP_MTV_TEXT
							.equals(pvp.getProperty())) {
						text = ((StringValue) pvp.getValue()).getString();
					}
				}
				List<MonolingualTextValue> languageList = result.get(language);
				if (languageList == null) {
					languageList = new ArrayList<>();
					result.put(language, languageList);
				}
				languageList.add(Datamodel.makeMonolingualTextValue(text,
						language));
			}
		}

		return result;
	}

	public static Map<String, SiteLink> getSiteLinkMapFromPropertyTargets(
			PropertyTargets propertyTargets) {
		Map<String, SiteLink> result = new HashMap<>();

		if (propertyTargets != null) {
			for (TargetQualifiers tqs : propertyTargets) {
				ObjectValue value = (ObjectValue) tqs.getTarget();
				String key = null;
				String page = null;
				for (PropertyValuePair pvp : value) {
					if (WdtkSorts.PROP_SITE_KEY.equals(pvp.getProperty())) {
						key = ((StringValue) pvp.getValue()).getString();
					} else if (WdtkSorts.PROP_SITE_PAGE.equals(pvp
							.getProperty())) {
						page = ((StringValue) pvp.getValue()).getString();
					}
				}
				result.put(
						key,
						Datamodel.makeSiteLink(page, key,
								Collections.<String> emptyList()));
			}
		}

		return result;
	}

	public static Value getValueFromValue(
			org.wikidata.wdtk.storage.datamodel.Value value) {
		if (SortType.STRING.equals(value.getSort().getType())) {
			switch (value.getSort().getName()) {
			case WdtkSorts.SORTNAME_ENTITY:
				return getEntityIdValueFromStringValue((StringValue) value);
			case Sort.SORTNAME_STRING:
				return new StringValueFromStringValue((StringValue) value);
			default:
				throw new RuntimeException("Unsupported string sort "
						+ value.getSort().getName());
			}
		} else if (SortType.RECORD.equals(value.getSort().getType())) {
			switch (value.getSort().getName()) {
			case WdtkSorts.SORTNAME_QUANTITY_VALUE:
				return new QuantityValueFromObjectValue((ObjectValue) value);
			case WdtkSorts.SORTNAME_TIME_VALUE:
				return new TimeValueFromObjectValue((ObjectValue) value);
			case WdtkSorts.SORTNAME_GLOBE_COORDINATES_VALUE:
				return new GlobeCoordinatesValueFromObjectValue(
						(ObjectValue) value);
			default:
				throw new RuntimeException("Unsupported record sort "
						+ value.getSort().getName());
			}
		} else {
			throw new RuntimeException("Unsupported sort type "
					+ value.getSort().getType().toString());
		}
	}

	public static EntityIdValue getEntityIdValueFromStringValue(
			StringValue value) {
		// FIXME change entity string encoding to include
		// type of entity on first char; this will not be reliable:
		int index = value.getString().indexOf('>');
		if (value.getString().charAt(index + 1) == 'Q') {
			return new ItemIdValueFromValue(value);
		} else {
			return new PropertyIdValueFromPropertyName(value.getString());
		}
	}

}
