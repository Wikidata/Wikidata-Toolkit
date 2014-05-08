package org.wikidata.wdtk.dumpfiles.parser.template;

/*
 * #%L
 * Wikidata Toolkit Dump File Handling
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
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.Validate;

/**
 * 
 * @author Julian Mendez
 * 
 */
public class TemplateParser {

	class LookAhead implements Comparable<LookAhead> {

		LookAheadItem item = LookAheadItem.UNDEFINED;
		int position = -1;

		LookAhead() {
		}

		LookAhead(int position, boolean found, LookAheadItem item) {
			this.position = position;
			this.item = item;
		}

		public int compareTo(LookAhead other) {
			if (this.position == other.position && this.item.equals(other.item)) {
				return 0;
			}
			if (this.position == -1) {
				if (other.position == -1) {
					return 0;
				} else {
					return 1;
				}
			} else {
				if (other.position == -1) {
					return -1;
				} else {
					return this.position - other.position;
				}
			}
		}

		public String toString() {
			return "" + item.toString() + "@" + position;
		}
	}

	enum LookAheadItem {
		CLOSING_BRACES, OPENING_BRACES, UNDEFINED, VERTICAL_BAR
	}

	public TemplateParser() {
	}

	private LookAhead findItem(String str, int pos) {
		LookAhead nextOpeningBraces = findItem(str,
				LookAheadItem.OPENING_BRACES, pos);
		LookAhead nextClosingBraces = findItem(str,
				LookAheadItem.CLOSING_BRACES, pos);
		LookAhead nextVerticalBar = findItem(str, LookAheadItem.VERTICAL_BAR,
				pos);
		return getMin(getMin(nextOpeningBraces, nextClosingBraces),
				nextVerticalBar);
	}

	private LookAhead findItem(String str, LookAheadItem item, int pos) {
		LookAhead ret = new LookAhead();
		if (item != LookAheadItem.UNDEFINED) {
			int nextPos = str.indexOf(getString(item), pos);
			if (nextPos != -1) {
				ret = new LookAhead(nextPos, true, item);
			}
		}
		return ret;
	}

	private String getKey(String parameter) {
		String ret = parameter;
		int pos = parameter.indexOf(ParserConstant.EQUALS_SIGN);
		if (pos != -1) {
			ret = parameter.substring(0, pos);
		}
		return ret;
	}

	private LookAhead getMin(LookAhead a, LookAhead b) {
		if (a.compareTo(b) <= 0) {
			return a;
		} else {
			return b;
		}
	}

	private List<String> getParameterList(String str) {
		ArrayList<String> ret = new ArrayList<String>();
		LookAhead nextItem = findItem(str, 0);
		int level = 0;
		int lastPos = 0;
		while (!nextItem.item.equals(LookAheadItem.UNDEFINED)) {
			if (nextItem.item.equals(LookAheadItem.OPENING_BRACES)) {
				level++;
			} else if (nextItem.item.equals(LookAheadItem.CLOSING_BRACES)) {
				level--;
			} else if (nextItem.item.equals(LookAheadItem.VERTICAL_BAR)) {
				if (level == 0) {
					ret.add(str.substring(lastPos, nextItem.position));
					lastPos = nextItem.position
							+ getString(nextItem.item).length();
				}
			}
			nextItem = findItem(str,
					nextItem.position + getString(nextItem.item).length());
		}
		ret.add(str.substring(lastPos));
		return ret;
	}

	private Map<String, String> getParameterMap(List<String> list) {
		TreeMap<String, String> ret = new TreeMap<String, String>();
		for (String line : list) {
			String key = getKey(line);
			String value = getValue(line);
			if (!key.trim().isEmpty()) {
				ret.put(key, value);
			}
		}
		return ret;
	}

	private String getString(LookAheadItem item) {
		if (item.equals(LookAheadItem.OPENING_BRACES)) {
			return ParserConstant.OPENING_BRACES;
		}
		if (item.equals(LookAheadItem.CLOSING_BRACES)) {
			return ParserConstant.CLOSING_BRACES;
		}
		if (item.equals(LookAheadItem.VERTICAL_BAR)) {
			return ParserConstant.VERTICAL_BAR;
		}
		return "";
	}

	private String getValue(String parameter) {
		String ret = "";
		int pos = parameter.indexOf(ParserConstant.EQUALS_SIGN);
		if (pos != -1) {
			ret = parameter
					.substring(pos + ParserConstant.EQUALS_SIGN.length());
		}
		return ret;
	}

	public Template parse(String page, String line) {
		Validate.notNull(page, "Page cannot be null.");
		Validate.notNull(line, "Line cannot be null.");
		if (!line.startsWith(ParserConstant.OPENING_BRACES)
				|| !line.endsWith(ParserConstant.CLOSING_BRACES)) {
			throw new IllegalArgumentException(
					"This string is not a valid template: '" + line
							+ "'. The line must start with \""
							+ ParserConstant.OPENING_BRACES
							+ "\" and end with \""
							+ ParserConstant.CLOSING_BRACES + "\".");
		}

		String id = "";
		String str = line.substring(ParserConstant.OPENING_BRACES.length(),
				line.length() - ParserConstant.CLOSING_BRACES.length());

		List<String> list = getParameterList(str);
		if (list.size() > 0) {
			id = list.get(0);
			list.remove(0);
		}
		Map<String, String> parameters = getParameterMap(list);
		return new Template(page, id, parameters);
	}

}
