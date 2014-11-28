package org.wikidata.wdtk.storage.datamodel;

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

/**
 * A value that represents a string.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class StringValueImpl implements StringValue {

	private static final long serialVersionUID = 5647550853966268392L;
	
	final String string;
	final Sort sort;

	public StringValueImpl(String string, Sort sort) {
		this.string = string;
		this.sort = sort;
	}

	@Override
	public String getString() {
		return string;
	}

	@Override
	public Sort getSort() {
		return this.sort;
	}
}
