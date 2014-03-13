package org.wikidata.wdtk.dumpfiles;

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
 * Exception class to report errors in the format of a MediaWiki dump file.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class MwDumpFormatException extends Exception {

	private static final long serialVersionUID = 8281842207514453147L;

	/**
	 * Constructs a new exception with the given message.
	 * 
	 * @param message
	 *            the message string
	 */
	public MwDumpFormatException(String message) {
		super(message);
	}

}
