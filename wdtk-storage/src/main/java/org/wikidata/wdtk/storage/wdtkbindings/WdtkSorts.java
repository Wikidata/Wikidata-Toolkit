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
	public static final String SORTNAME_SPECIAL_STRING = "specialStr";
	public static final String SORTNAME_MTV = "monotext";
	public static final String SORTNAME_TIME_VALUE = "time";
	public static final String SORTNAME_GLOBE_COORDINATES_VALUE = "coord";
	public static final String SORTNAME_QUANTITY_VALUE = "quant";
	public static final String SORTNAME_REFERENCE = "reference";
	public static final String SORTNAME_LABEL = "label";
	public static final String SORTNAME_DESCRIPTION = "desc";
	public static final String SORTNAME_ALIAS = "alias";
	public static final String SORTNAME_TERMS = "terms";
	public static final String SORTNAME_SITE_LINK = "site";
	public static final String SORTNAME_LABEL_STRING = "mtvlabel";

	public static final String PROP_DOCTYPE = "wdtk:doctype";
	public static final String PROP_DATATYPE = "wdtk:datatype";
	public static final String PROP_LABEL = "wdtk:label";
	public static final String PROP_DESCRIPTION = "wdtk:desc";
	public static final String PROP_ALIAS = "wdtk:alias";
	public static final String PROP_TERMS = "wdtk:terms";
	public static final String PROP_SITE_LINK = "wdtk:site";

	public static final String PROP_NOVALUE = "novalue";
	public static final String PROP_SOMEVALUE = "somevalue";
	public static final String PROP_RANK = "rank";
	public static final String PROP_REFERENCE = "ref";
	public static final String PROP_MTV_TEXT = "text";
	public static final String PROP_MTV_LANG = "language";
	public static final String PROP_SITE_PAGE = "page";
	public static final String PROP_SITE_KEY = "key";
	public static final String PROP_TIME_YEAR = "year";
	public static final String PROP_TIME_MONTH = "month";
	public static final String PROP_TIME_DAY = "day";
	public static final String PROP_TIME_CALENDAR_MODEL = "cm";
	public static final String PROP_COORDINATES_LATITUDE = "lat";
	public static final String PROP_COORDINATES_LONGITUDE = "lon";
	public static final String PROP_COORDINATES_GLOBE = "globe";
	public static final String PROP_COORDINATES_PRECISION = "prec";
	public static final String PROP_QUANTITY_VALUE = "qval";
	public static final String PROP_QUANTITY_LOWER = "qlow";
	public static final String PROP_QUANTITY_UPPER = "qup";

	public static final String VALUE_DOCTYPE_ITEM = "item";
	public static final String VALUE_DOCTYPE_PROPERTY = "prop";

	public static final List<PropertyRange> PROPLIST_MONOLINGUAL_TEXT_VALUE = new ArrayList<>();
	static {
		PROPLIST_MONOLINGUAL_TEXT_VALUE.add(new PropertyRange(PROP_MTV_TEXT,
				Sort.SORTNAME_STRING));
		PROPLIST_MONOLINGUAL_TEXT_VALUE.add(new PropertyRange(PROP_MTV_LANG,
				Sort.SORTNAME_STRING));
	}

	public static final List<PropertyRange> PROPLIST_LABEL_VALUE = new ArrayList<>();
	static {
		PROPLIST_LABEL_VALUE.add(new PropertyRange(PROP_MTV_TEXT,
				Sort.SORTNAME_STRING));
		PROPLIST_LABEL_VALUE.add(new PropertyRange(PROP_MTV_LANG,
				Sort.SORTNAME_STRING));
	}

	public static final List<PropertyRange> PROPLIST_SITE_LINK = new ArrayList<>();
	static {
		PROPLIST_SITE_LINK.add(new PropertyRange(PROP_SITE_PAGE,
				Sort.SORTNAME_STRING));
		PROPLIST_SITE_LINK.add(new PropertyRange(PROP_SITE_KEY,
				Sort.SORTNAME_STRING));
	}

	public static final List<PropertyRange> PROPLIST_TIME_VALUE = new ArrayList<>();
	static {
		PROPLIST_TIME_VALUE.add(new PropertyRange(PROP_TIME_YEAR,
				Sort.SORTNAME_LONG));
		PROPLIST_TIME_VALUE.add(new PropertyRange(PROP_TIME_MONTH,
				Sort.SORTNAME_LONG));
		PROPLIST_TIME_VALUE.add(new PropertyRange(PROP_TIME_DAY,
				Sort.SORTNAME_LONG));
		PROPLIST_TIME_VALUE.add(new PropertyRange(PROP_TIME_CALENDAR_MODEL,
				SORTNAME_ENTITY));
	}

	public static final List<PropertyRange> PROPLIST_GLOBE_COORDINATES_VALUE = new ArrayList<>();
	static {
		PROPLIST_GLOBE_COORDINATES_VALUE.add(new PropertyRange(
				PROP_COORDINATES_LATITUDE, Sort.SORTNAME_LONG));
		PROPLIST_GLOBE_COORDINATES_VALUE.add(new PropertyRange(
				PROP_COORDINATES_LONGITUDE, Sort.SORTNAME_LONG));
		PROPLIST_GLOBE_COORDINATES_VALUE.add(new PropertyRange(
				PROP_COORDINATES_PRECISION, Sort.SORTNAME_LONG));
		PROPLIST_GLOBE_COORDINATES_VALUE.add(new PropertyRange(
				PROP_COORDINATES_GLOBE, SORTNAME_ENTITY));
	}

	public static final List<PropertyRange> PROPLIST_QUANTITY_VALUE = new ArrayList<>();
	static {
		PROPLIST_QUANTITY_VALUE.add(new PropertyRange(PROP_QUANTITY_VALUE,
				Sort.SORTNAME_DECIMAL));
		PROPLIST_QUANTITY_VALUE.add(new PropertyRange(PROP_QUANTITY_LOWER,
				Sort.SORTNAME_DECIMAL));
		PROPLIST_QUANTITY_VALUE.add(new PropertyRange(PROP_QUANTITY_UPPER,
				Sort.SORTNAME_DECIMAL));
	}

	public static final Sort SORT_ENTITY = new Sort(SORTNAME_ENTITY,
			SortType.STRING, null);
	public static final Sort SORT_SPECIAL_STRING = new Sort(
			SORTNAME_SPECIAL_STRING, SortType.STRING, null);
	public static final Sort SORT_LABEL_STRING = new Sort(
			SORTNAME_LABEL_STRING, SortType.STRING, null);
	public static final Sort SORT_MTV = new Sort(SORTNAME_MTV, SortType.RECORD,
			PROPLIST_MONOLINGUAL_TEXT_VALUE);
	public static final Sort SORT_LABEL = new Sort(SORTNAME_LABEL,
			SortType.RECORD, PROPLIST_LABEL_VALUE);
	public static final Sort SORT_DESCRIPTION = new Sort(SORTNAME_DESCRIPTION,
			SortType.RECORD, PROPLIST_MONOLINGUAL_TEXT_VALUE);
	public static final Sort SORT_ALIAS = new Sort(SORTNAME_ALIAS,
			SortType.RECORD, PROPLIST_MONOLINGUAL_TEXT_VALUE);
	public static final Sort SORT_TERMS = new Sort(SORTNAME_TERMS,
			SortType.STRING, null);
	public static final Sort SORT_REFERENCE = new Sort(SORTNAME_REFERENCE,
			SortType.OBJECT, null);
	public static final Sort SORT_SITE_LINK = new Sort(SORTNAME_SITE_LINK,
			SortType.RECORD, PROPLIST_SITE_LINK);
	public static final Sort SORT_TIME_VALUE = new Sort(SORTNAME_TIME_VALUE,
			SortType.RECORD, PROPLIST_TIME_VALUE);
	public static final Sort SORT_GLOBE_COORDINATES_VALUE = new Sort(
			SORTNAME_GLOBE_COORDINATES_VALUE, SortType.RECORD,
			PROPLIST_GLOBE_COORDINATES_VALUE);
	public static final Sort SORT_QUANTITY_VALUE = new Sort(
			SORTNAME_QUANTITY_VALUE, SortType.RECORD, PROPLIST_QUANTITY_VALUE);
}
