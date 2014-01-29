package org.wikidata.wdtk.datamodel.interfaces;

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

/**
 * A URL Value represents a single web address, which is a IRI.
 * 
 * It was called IRI value in the original Wikidata datamodel specification, but
 * we now use {@link IriValue} as a general interface for all kinds of
 * {@link Value} that have an IRI associated with them. The new label also
 * matches the name URL datatype. However, the URL Value may still store
 * basically any IRI, without additional formal conditions (such as being
 * resolvable).
 * 
 * @author Markus Kroetzsch
 * 
 */
public interface UrlValue extends IriValue {

	/**
	 * Get the URL stored by this value. Should always return the same result as
	 * getIri().
	 * 
	 * @return URL string
	 */
	public String getUrl();
}
