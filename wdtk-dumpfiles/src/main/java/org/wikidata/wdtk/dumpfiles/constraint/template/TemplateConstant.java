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

/**
 * This interface contains constants used to have a string representation of
 * Wikibase templates.
 * 
 * @author Julian Mendez
 * 
 */
public interface TemplateConstant {

	String OPENING_BRACES = "{{";
	String CLOSING_BRACES = "}}";
	String OPENING_BRACKETS = "[[";
	String CLOSING_BRACKETS = "]]";
	String OPENING_NOWIKI = "<nowiki>";
	String CLOSING_NOWIKI = "</nowiki>";
	String VERTICAL_BAR = "|";
	String EQUALS_SIGN = "=";
	String UNDERSCORE = "_";
	String SPACE = " ";
	String COMMA = ",";
	String COLON = ":";
	String SEMICOLON = ";";
	String NEWLINE = "\n";

	/**
	 * Regular expression to identify all HTML comments in a string.
	 * <p>
	 * (?s) makes dot '.' also match new lines '\n'
	 * <p>
	 * .*? is a reluctant quantifier
	 */
	String REG_EXP_HTML_COMMENT = "(?s)<!--.*?-->";

}
