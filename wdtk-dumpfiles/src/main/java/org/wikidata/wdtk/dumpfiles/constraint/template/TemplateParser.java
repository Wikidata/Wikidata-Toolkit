package org.wikidata.wdtk.dumpfiles.constraint.template;

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
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.Validate;

/**
 * An object of this class parses strings and returns transclusion of Wikibase
 * templates.
 * 
 * @author Julian Mendez
 * 
 */
public class TemplateParser {

	/**
	 * An instance of this class is an auxiliary class of the parser. It is used
	 * to keep a look-ahead during the parsing.
	 * 
	 * @author Julian Mendez
	 * 
	 */
	class LookAhead implements Comparable<LookAhead> {

		LookAheadItem item = LookAheadItem.UNDEFINED;
		int position = -1;

		/**
		 * Creates a new look-ahead.
		 */
		LookAhead() {
		}

		/**
		 * Creates a new look-ahead pointing at a particular position, to a
		 * particular item.
		 * 
		 * @param position
		 *            position
		 * @param item
		 *            item
		 */
		LookAhead(int position, LookAheadItem item) {
			this.position = position;
			this.item = item;
		}

		@Override
		public int compareTo(LookAhead other) {
			if ((this.position == other.position)
					&& this.item.equals(other.item)) {
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

		@Override
		public String toString() {
			return "" + this.item.toString() + "@" + this.position;
		}

	}

	/**
	 * This items are the possible ones that a look-ahead can point to.
	 * 
	 * @author Julian Mendez
	 * 
	 */
	enum LookAheadItem {
		UNDEFINED, OPENING_BRACES, CLOSING_BRACES, OPENING_NOWIKI, CLOSING_NOWIKI, VERTICAL_BAR
	}

	/**
	 * Creates a new template parser.
	 */
	public TemplateParser() {
	}

	private LookAhead findItem(String str, int pos, boolean nowiki) {
		Set<LookAhead> set = new TreeSet<LookAhead>();
		set.add(findItem(str, LookAheadItem.OPENING_NOWIKI, pos));
		set.add(findItem(str, LookAheadItem.CLOSING_NOWIKI, pos));
		if (!nowiki) {
			set.add(findItem(str, LookAheadItem.OPENING_BRACES, pos));
			set.add(findItem(str, LookAheadItem.CLOSING_BRACES, pos));
			set.add(findItem(str, LookAheadItem.VERTICAL_BAR, pos));
		}
		return set.iterator().next();
	}

	private LookAhead findItem(String str, LookAheadItem item, int pos) {
		LookAhead ret = new LookAhead();
		if (!item.equals(LookAheadItem.UNDEFINED)) {
			int nextPos = str.indexOf(getString(item), pos);
			if (nextPos != -1) {
				ret = new LookAhead(nextPos, item);
			}
		}
		return ret;
	}

	private String getKey(String parameter) {
		String ret = parameter;
		int pos = parameter.indexOf(TemplateConstant.EQUALS_SIGN);
		if (pos != -1) {
			ret = parameter.substring(0, pos);
		}
		return ret;
	}

	private List<String> getParameterList(String str) {
		ArrayList<String> ret = new ArrayList<String>();
		LookAhead nextItem = findItem(str, 0, false);
		int level = 0;
		int lastPos = 0;
		boolean noWiki = false;

		while (!nextItem.item.equals(LookAheadItem.UNDEFINED)) {
			if (nextItem.item.equals(LookAheadItem.OPENING_BRACES)) {
				level++;
			} else if (nextItem.item.equals(LookAheadItem.CLOSING_BRACES)) {
				level--;
			} else if (nextItem.item.equals(LookAheadItem.OPENING_NOWIKI)) {
				noWiki = true;
			} else if (nextItem.item.equals(LookAheadItem.CLOSING_NOWIKI)) {
				noWiki = false;
			} else if (nextItem.item.equals(LookAheadItem.VERTICAL_BAR)) {
				if (level == 0) {
					ret.add(str.substring(lastPos, nextItem.position));
					lastPos = nextItem.position
							+ getString(nextItem.item).length();
				}
			}
			nextItem = findItem(str,
					nextItem.position + getString(nextItem.item).length(),
					noWiki);
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

	private String getValue(String parameter) {
		String ret = "";
		int pos = parameter.indexOf(TemplateConstant.EQUALS_SIGN);
		if (pos != -1) {
			ret = parameter.substring(pos
					+ TemplateConstant.EQUALS_SIGN.length());
		}
		return ret;
	}

	private String getString(LookAheadItem item) {
		if (item.equals(LookAheadItem.OPENING_BRACES)) {
			return TemplateConstant.OPENING_BRACES;
		}
		if (item.equals(LookAheadItem.CLOSING_BRACES)) {
			return TemplateConstant.CLOSING_BRACES;
		}
		if (item.equals(LookAheadItem.OPENING_NOWIKI)) {
			return TemplateConstant.OPENING_NOWIKI;
		}
		if (item.equals(LookAheadItem.CLOSING_NOWIKI)) {
			return TemplateConstant.CLOSING_NOWIKI;
		}
		if (item.equals(LookAheadItem.VERTICAL_BAR)) {
			return TemplateConstant.VERTICAL_BAR;
		}
		return "";
	}

	/**
	 * Returns a template obtained by parsing the specified line.
	 * 
	 * @param line
	 *            line containing the template
	 * @return a template obtained by parsing the specified line
	 * @throws IllegalArgumentException
	 *             if the specified line is not a valid template
	 */
	public Template parse(String line) {
		Validate.notNull(line, "Line cannot be null.");
		if (!line.startsWith(TemplateConstant.OPENING_BRACES)
				|| !line.endsWith(TemplateConstant.CLOSING_BRACES)) {
			throw new IllegalArgumentException(
					"This string is not a valid template: '" + line
							+ "'. The line must start with \""
							+ TemplateConstant.OPENING_BRACES
							+ "\" and end with \""
							+ TemplateConstant.CLOSING_BRACES + "\".");
		}

		String id = "";
		String str = line.substring(TemplateConstant.OPENING_BRACES.length(),
				line.length() - TemplateConstant.CLOSING_BRACES.length());

		List<String> list = getParameterList(str);
		if (list.size() > 0) {
			id = list.get(0);
			list.remove(0);
		}
		Map<String, String> parameters = getParameterMap(list);
		return new Template(id, parameters);
	}

}
