package org.wikidata.wdtk.datamodel.implementation;

/*
 * #%L
 * Wikidata Toolkit Data Model
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

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.UrlValue;

/**
 * Implementation of {@link UrlValue}.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class UrlValueImpl implements UrlValue {

	final String url;

	/**
	 * Constructor.
	 * 
	 * There is currently no validation of the URL string.
	 * 
	 * @param url
	 */
	public UrlValueImpl(String url) {
		Validate.notNull(url, "URL cannot be null");
		this.url = url;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public String getIri() {
		return url;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return url.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof UrlValueImpl)) {
			return false;
		}
		return url.equals(((UrlValueImpl) obj).url);
	}

}
