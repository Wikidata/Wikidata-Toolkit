package org.wikidata.wdtk.rdf;

/*
 * #%L
 * Wikidata Toolkit RDF
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
 * Enum to define the context in which a propery is used in the RDF export. We
 * use different URIs depending on this context.
 *
 * @author Markus Kroetzsch
 *
 */
public enum PropertyContext {
	STATEMENT, VALUE, QUALIFIER, REFERENCE, REFERENCE_SIMPLE, DIRECT, VALUE_SIMPLE, QUALIFIER_SIMPLE, NO_VALUE, NO_QUALIFIER_VALUE
}
