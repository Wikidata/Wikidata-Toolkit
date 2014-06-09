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
 * 
 * @author Julian Mendez
 * 
 */
public class TemplateScanner {

	public TemplateScanner() {
	}

	public List<String> getTemplates(String str) {
		return Collections.unmodifiableList(parse(removeHTMLComments(str)));
	}

	List<String> parse(String str) {
		List<String> ret = new ArrayList<String>();
		int pos = 0;
		int level = 0;
		int lastBegin = 0;
		while (pos != -1) {
			int nextBegin = str.indexOf(ParserConstant.OPENING_BRACES, pos);
			int nextEnd = str.indexOf(ParserConstant.CLOSING_BRACES, pos);
			if (nextEnd == -1) {
				pos = -1;
			} else {
				if ((nextBegin != -1) && (nextBegin < nextEnd)) {
					pos = nextBegin + ParserConstant.OPENING_BRACES.length();
					if (level == 0) {
						lastBegin = nextBegin;
					}
					level++;
				} else {
					level--;
					if (level == 0) {
						ret.add(str.substring(lastBegin, nextEnd
								+ ParserConstant.CLOSING_BRACES.length()));
					}
					pos = nextEnd + ParserConstant.CLOSING_BRACES.length();
				}
			}
		}
		return ret;
	}

	String removeHTMLComments(String str) {
		return str.replaceAll(ParserConstant.REG_EXP_HTML_COMMENT, "");
	}

}
