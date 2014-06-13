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
import java.util.Collections;
import java.util.List;

/**
 * An object of this class scans a text to get pieces of text with template
 * transclusions. HTML comments are ignored.
 * 
 * @author Julian Mendez
 * 
 */
public class TemplateScanner {

	/**
	 * Creates a new template scanner.
	 */
	public TemplateScanner() {
	}

	/**
	 * Scans a test and returns a list with all the pieces of text that can be
	 * template transclusions. HTML comments are ignored.
	 * 
	 * @param text
	 *            text
	 * @return a list with all the pieces of text that can be template
	 *         transclusions
	 */
	public List<String> getTemplates(String text) {
		return Collections.unmodifiableList(parse(removeHTMLComments(text)));
	}

	/**
	 * Scans a test and returns a list with all the pieces of text that can be
	 * template transclusions.
	 * 
	 * @param text
	 *            text
	 * @return a list with all the pieces of text that can be template
	 *         transclusions
	 */
	List<String> parse(String text) {
		List<String> ret = new ArrayList<String>();
		int pos = 0;
		int level = 0;
		int lastBegin = 0;
		while (pos != -1) {
			int nextBegin = text.indexOf(TemplateConstant.OPENING_BRACES, pos);
			int nextEnd = text.indexOf(TemplateConstant.CLOSING_BRACES, pos);
			if (nextEnd == -1) {
				pos = -1;
			} else {
				if ((nextBegin != -1) && (nextBegin < nextEnd)) {
					pos = nextBegin + TemplateConstant.OPENING_BRACES.length();
					if (level == 0) {
						lastBegin = nextBegin;
					}
					level++;
				} else {
					level--;
					if (level == 0) {
						ret.add(text.substring(lastBegin, nextEnd
								+ TemplateConstant.CLOSING_BRACES.length()));
					}
					pos = nextEnd + TemplateConstant.CLOSING_BRACES.length();
				}
			}
		}
		return ret;
	}

	/**
	 * Returns a text without its HTML comments.
	 * 
	 * @param text
	 *            text
	 * @return text without its HTML comments
	 */
	String removeHTMLComments(String text) {
		return text.replaceAll(TemplateConstant.REG_EXP_HTML_COMMENT, "");
	}

}
