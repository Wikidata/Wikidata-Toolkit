package org.wikidata.wdtk.datamodel.helpers;

/*
 * #%L
 * Wikidata Toolkit Data Model
 * %%
 * Copyright (C) 2014 - 2018 Wikidata Toolkit Developers
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

import org.wikidata.wdtk.datamodel.interfaces.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This is a utility class that allows to filter {@link ItemDocument} and {@link PropertyDocument}
 * using the data declared in a {@link DocumentDataFilter}.
 */
public class DatamodelFilter {

	private final DataObjectFactory dataObjectFactory;

	private final DocumentDataFilter filter;

	public DatamodelFilter(DataObjectFactory dataObjectFactory, DocumentDataFilter filter) {
		this.dataObjectFactory = dataObjectFactory;
		this.filter = filter;
	}

	public ItemDocument filter(ItemDocument item) {
		return dataObjectFactory.getItemDocument(
				item.getEntityId(),
				filterMonoLingualTextValues(item.getLabels().values().stream()),
				filterMonoLingualTextValues(item.getDescriptions().values().stream()),
				filterMonoLingualTextValues(item.getAliases().values().stream().flatMap(List::stream)),
				filterStatementGroups(item.getStatementGroups()),
				filterSiteLinks(item.getSiteLinks()),
				item.getRevisionId()
		);
	}

	public PropertyDocument filter(PropertyDocument property) {
		return dataObjectFactory.getPropertyDocument(
				property.getEntityId(),
				filterMonoLingualTextValues(property.getLabels().values().stream()),
				filterMonoLingualTextValues(property.getDescriptions().values().stream()),
				filterMonoLingualTextValues(property.getAliases().values().stream().flatMap(List::stream)),
				filterStatementGroups(property.getStatementGroups()),
				property.getDatatype(),
				property.getRevisionId()
		);
	}

	public LexemeDocument filter(LexemeDocument lexeme) {
		return dataObjectFactory.getLexemeDocument(
				lexeme.getEntityId(),
				lexeme.getLexicalCategory(),
				lexeme.getLanguage(),
				filterMonoLingualTextValues(lexeme.getLemmas().values().stream()),
				filterStatementGroups(lexeme.getStatementGroups()),
				lexeme.getForms().stream().map(this::filter).collect(Collectors.toList()),
				lexeme.getRevisionId()
		);
	}

	public FormDocument filter(FormDocument form) {
		return dataObjectFactory.getFormDocument(
				form.getEntityId(),
				filterMonoLingualTextValues(form.getRepresentations().values().stream()),
				form.getGrammaticalFeatures(),
				filterStatementGroups(form.getStatementGroups()),
				form.getRevisionId()
		);
	}

	private List<MonolingualTextValue> filterMonoLingualTextValues(Stream<MonolingualTextValue> values) {
		if (filter.getLanguageFilter() == null) {
			return values.collect(Collectors.toList());
		}
		if (filter.getLanguageFilter().isEmpty()) {
			return Collections.emptyList();
		}

		return values
				.filter(value -> filter.getLanguageFilter().contains(value.getLanguageCode()))
				.collect(Collectors.toList());
	}

	private List<StatementGroup> filterStatementGroups(List<StatementGroup> statementGroups) {
		if (filter.getPropertyFilter() == null) {
			return statementGroups;
		}
		if (filter.getPropertyFilter().isEmpty()) {
			return Collections.emptyList();
		}

		return statementGroups.stream()
				.filter(statementGroup-> filter.getPropertyFilter().contains(statementGroup.getProperty()))
				.collect(Collectors.toList());
	}

	private Map<String, SiteLink> filterSiteLinks(Map<String, SiteLink> siteLinks) {
		if (filter.getSiteLinkFilter() == null) {
			return siteLinks;
		}
		if (filter.getSiteLinkFilter().isEmpty()) {
			return Collections.emptyMap();
		}

		Map<String, SiteLink> result = new HashMap<>(siteLinks.size());
		for (Map.Entry<String, SiteLink> entry : siteLinks.entrySet()) {
			if (filter.getSiteLinkFilter().contains(entry.getKey())) {
				result.put(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}
}
