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

import java.util.ArrayList;
import java.util.List;

import org.wikidata.wdtk.storage.datamodel.PropertyRange;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.datamodel.SortType;

public class WdtkSorts {
	public static final String SORTNAME_ENTITY = "entity";
	public static final String SORTNAME_MTV = "monotext";
	public static final String SORTNAME_REFERENCE = "reference";

	public static final String PROP_NOVALUE = "novalue";
	public static final String PROP_RANK = "rank";
	public static final String PROP_REFERENCE = "ref";
	public static final String PROP_MTV_TEXT = "text";
	public static final String PROP_MTV_LANG = "language";

	public static final List<PropertyRange> PROPLIST_MONOLINGUAL_TEXT_VALUE = new ArrayList<>();
	static {
		PROPLIST_MONOLINGUAL_TEXT_VALUE.add(new PropertyRange(PROP_MTV_TEXT,
				Sort.SORTNAME_STRING));
		PROPLIST_MONOLINGUAL_TEXT_VALUE.add(new PropertyRange(PROP_MTV_LANG,
				Sort.SORTNAME_STRING));
	}

	public static final Sort SORT_ENTITY = new Sort(SORTNAME_ENTITY,
			SortType.STRING, null);
	public static final Sort SORT_MTV = new Sort(SORTNAME_MTV, SortType.RECORD,
			PROPLIST_MONOLINGUAL_TEXT_VALUE);
	public static final Sort SORT_REFERENCE = new Sort(SORTNAME_REFERENCE,
			SortType.OBJECT, null);
}
