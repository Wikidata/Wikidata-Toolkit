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

import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.PropertyTargets;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.TargetQualifiers;
import org.wikidata.wdtk.storage.datamodel.ObjectValue;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePair;
import org.wikidata.wdtk.storage.datamodel.StringValue;
import org.wikidata.wdtk.storage.wdtkbindings.WdtkSorts;

public class WdtkFromDb {

	public static Map<String, MonolingualTextValue> getMtvMapFromPropertyTargets(
			PropertyTargets propertyTargets, DataObjectFactory dataObjectFactory) {
		Map<String, MonolingualTextValue> result = new HashMap<>();
		for (TargetQualifiers tqs : propertyTargets) {
			ObjectValue value = (ObjectValue) tqs.getTarget();
			String language = null;
			String text = null;
			for (PropertyValuePair pvp : value) {
				if (WdtkSorts.PROP_MTV_LANG.equals(pvp.getProperty())) {
					language = ((StringValue) pvp.getValue()).getString();
				} else if (WdtkSorts.PROP_MTV_TEXT.equals(pvp.getProperty())) {
					text = ((StringValue) pvp.getValue()).getString();
				}
			}
			result.put(language,
					dataObjectFactory.getMonolingualTextValue(text, language));
		}
		return result;
	}

	public static Map<String, List<MonolingualTextValue>> getMtvListMapFromPropertyTargets(
			PropertyTargets propertyTargets, DataObjectFactory dataObjectFactory) {
		Map<String, List<MonolingualTextValue>> result = new HashMap<>();
		for (TargetQualifiers tqs : propertyTargets) {
			ObjectValue value = (ObjectValue) tqs.getTarget();
			String language = null;
			String text = null;
			for (PropertyValuePair pvp : value) {
				if (WdtkSorts.PROP_MTV_LANG.equals(pvp.getProperty())) {
					language = ((StringValue) pvp.getValue()).getString();
				} else if (WdtkSorts.PROP_MTV_TEXT.equals(pvp.getProperty())) {
					text = ((StringValue) pvp.getValue()).getString();
				}
			}
			List<MonolingualTextValue> languageList = result.get(language);
			if (languageList == null) {
				languageList = new ArrayList<>();
				result.put(language, languageList);
			}
			languageList.add(dataObjectFactory.getMonolingualTextValue(text,
					language));
		}
		return result;
	}

	public static Map<String, SiteLink> getSiteLinkMapFromPropertyTargets(
			PropertyTargets propertyTargets, DataObjectFactory dataObjectFactory) {
		Map<String, SiteLink> result = new HashMap<>();
		for (TargetQualifiers tqs : propertyTargets) {
			ObjectValue value = (ObjectValue) tqs.getTarget();
			String key = null;
			String page = null;
			for (PropertyValuePair pvp : value) {
				if (WdtkSorts.PROP_SITE_KEY.equals(pvp.getProperty())) {
					key = ((StringValue) pvp.getValue()).getString();
				} else if (WdtkSorts.PROP_SITE_PAGE.equals(pvp.getProperty())) {
					page = ((StringValue) pvp.getValue()).getString();
				}
			}
			result.put(
					key,
					dataObjectFactory.getSiteLink(page, key,
							Collections.<String> emptyList()));
		}
		return result;
	}
}
