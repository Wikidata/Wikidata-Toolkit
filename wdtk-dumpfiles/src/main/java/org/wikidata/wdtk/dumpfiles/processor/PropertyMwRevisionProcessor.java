package org.wikidata.wdtk.dumpfiles.processor;

/*
 * #%L
 * Wikidata Toolkit Examples
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
import java.util.Map;
import java.util.TreeMap;

import org.wikidata.wdtk.dumpfiles.MwRevision;
import org.wikidata.wdtk.dumpfiles.MwRevisionProcessor;

/**
 * 
 * @author Julian Mendez
 * 
 */
class PropertyMwRevisionProcessor implements MwRevisionProcessor {

	public static final int PROPERTY_NS = 120;
	public static final String COLON = ":";
	public static final String SLASH = "/";

	final Map<String, String> map = new TreeMap<String, String>();
	String propertyPrefix = "";

	public PropertyMwRevisionProcessor() {
	}

	@Override
	public void startRevisionProcessing(String siteName, String baseUrl,
			Map<Integer, String> namespaces) {
		this.propertyPrefix = namespaces.get(PROPERTY_NS) + COLON;
	}

	@Override
	public void processRevision(MwRevision mwRevision) {
		String title = mwRevision.getPrefixedTitle();
		if (title.startsWith(this.propertyPrefix)
				&& (title.indexOf(SLASH) == -1)) {
			String propertyName = title.substring(this.propertyPrefix.length());
			String text = mwRevision.getText();

			this.map.put(propertyName, text);
		}
	}

	@Override
	public void finishRevisionProcessing() {
	}

	public Map<String, String> getMap() {
		return Collections.unmodifiableMap(map);
	}

}
