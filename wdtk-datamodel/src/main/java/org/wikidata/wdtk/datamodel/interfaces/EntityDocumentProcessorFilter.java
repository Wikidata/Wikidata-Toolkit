package org.wikidata.wdtk.datamodel.interfaces;

/*
 * #%L
 * Wikidata Toolkit Data Model
 * %%
 * Copyright (C) 2014 - 2015 Wikidata Toolkit Developers
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

import java.util.Set;

import org.wikidata.wdtk.datamodel.helpers.DatamodelConverter;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;

/**
 * Implementation of {@link EntityDocumentProcessor} that acts as a filter,
 * removing some of the data from {@link EntityDocument} objects before passing
 * them on to another processor. There is an overhead involved in using this,
 * even if no filters are set, since a deep copy of the data is created to
 * filter it.
 *
 *
 * @author Markus Kroetzsch
 *
 */
public class EntityDocumentProcessorFilter implements EntityDocumentProcessor {

	final DatamodelConverter converter;
	final EntityDocumentProcessor entityDocumentProcessor;

	/**
	 * Constructor.
	 *
	 * @param entityDocumentProcessor
	 *            the processor to use on the filtered data
	 */
	public EntityDocumentProcessorFilter(
			EntityDocumentProcessor entityDocumentProcessor) {
		this.converter = new DatamodelConverter(new DataObjectFactoryImpl());
		this.entityDocumentProcessor = entityDocumentProcessor;
	}

	/**
	 * Sets a property filter. If given, all data will be preprocessed to
	 * contain only statements for the given (main) properties.
	 *
	 * @see DatamodelConverter#setOptionPropertyFilter(Set)
	 * @param propertyFilter
	 *            set of language codes that should be retained (can be empty)
	 */
	public void setPropertyFilter(Set<PropertyIdValue> propertyFilter) {
		this.converter.setOptionPropertyFilter(propertyFilter);
	}

	/**
	 * Sets a site link filter. If given, all data will be preprocessed to
	 * contain only data for the given site keys.
	 *
	 * @see DatamodelConverter#setOptionSiteLinkFilter(Set)
	 * @param siteLinkFilter
	 *            set of language codes that should be retained (can be empty)
	 */
	public void setSiteLinkFilter(Set<String> siteLinkFilter) {
		this.converter.setOptionSiteLinkFilter(siteLinkFilter);
	}

	/**
	 * Sets a language filter. If given, all data will be preprocessed to
	 * contain only data for the given languages.
	 *
	 * @see DatamodelConverter#setOptionLanguageFilter(Set)
	 * @param languageFilter
	 *            set of language codes that should be retained (can be empty)
	 */
	public void setLanguageFilter(Set<String> languageFilter) {
		this.converter.setOptionLanguageFilter(languageFilter);
	}

	@Override
	public void processItemDocument(ItemDocument itemDocument) {
		itemDocument = this.converter.copy(itemDocument);
		this.entityDocumentProcessor.processItemDocument(itemDocument);
	}

	@Override
	public void processPropertyDocument(PropertyDocument propertyDocument) {
		propertyDocument = this.converter.copy(propertyDocument);
		this.entityDocumentProcessor.processPropertyDocument(propertyDocument);
	}

}
