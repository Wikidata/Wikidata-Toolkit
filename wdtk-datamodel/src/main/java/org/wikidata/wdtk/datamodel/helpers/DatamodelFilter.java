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

/**
 * This is a utility class that allows to filter {@link EntityDocument}
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
				filterMonoLingualTextValues(item.getLabels().values()),
				filterMonoLingualTextValues(item.getDescriptions().values()),
				filterMonoLingualTextValues(flatten(item.getAliases().values())),
				filterStatementGroups(item.getStatementGroups()),
				filterSiteLinks(item.getSiteLinks()),
				item.getRevisionId()
		);
	}

	public PropertyDocument filter(PropertyDocument property) {
		return dataObjectFactory.getPropertyDocument(
				property.getEntityId(),
				filterMonoLingualTextValues(property.getLabels().values()),
				filterMonoLingualTextValues(property.getDescriptions().values()),
				filterMonoLingualTextValues(flatten(property.getAliases().values())),
				filterStatementGroups(property.getStatementGroups()),
				property.getDatatype(),
				property.getRevisionId()
		);
	}

	public MediaInfoDocument filter(MediaInfoDocument mediaInfo) {
		return dataObjectFactory.getMediaInfoDocument(
				mediaInfo.getEntityId(),
				filterMonoLingualTextValues(mediaInfo.getLabels().values()),
				filterStatementGroups(mediaInfo.getStatementGroups()),
				mediaInfo.getRevisionId()
		);
	}

	public LexemeDocument filter(LexemeDocument lexeme) {
		return dataObjectFactory.getLexemeDocument(
				lexeme.getEntityId(),
				lexeme.getLexicalCategory(),
				lexeme.getLanguage(),
				filterMonoLingualTextValues(lexeme.getLemmas().values()),
				filterStatementGroups(lexeme.getStatementGroups()),
				filterForms(lexeme.getForms()),
				filterSenses(lexeme.getSenses()),
				lexeme.getRevisionId()
		);
	}

	public FormDocument filter(FormDocument form) {
		return dataObjectFactory.getFormDocument(
				form.getEntityId(),
				filterMonoLingualTextValues(form.getRepresentations().values()),
				form.getGrammaticalFeatures(),
				filterStatementGroups(form.getStatementGroups()),
				form.getRevisionId()
		);
	}

	public SenseDocument filter(SenseDocument sense) {
		return dataObjectFactory.getSenseDocument(
				sense.getEntityId(),
				filterMonoLingualTextValues(sense.getGlosses().values()),
				filterStatementGroups(sense.getStatementGroups()),
				sense.getRevisionId()
		);
	}

	private List<FormDocument> filterForms(List<FormDocument> forms) {
		List<FormDocument> filtered = new ArrayList<>(forms.size());
		for(FormDocument form : forms) {
			filtered.add(filter(form));
		}
		return filtered;
	}

	private List<SenseDocument> filterSenses(List<SenseDocument> senses) {
		List<SenseDocument> filtered = new ArrayList<>(senses.size());
		for(SenseDocument sense : senses) {
			filtered.add(filter(sense));
		}
		return filtered;
	}

	private <T> List<T> flatten(Collection<List<T>> values) {
		List<T> flattened = new ArrayList<>();
		for(Collection<T> part : values) {
			flattened.addAll(part);
		}
		return flattened;
	}

	private List<MonolingualTextValue> filterMonoLingualTextValues(Collection<MonolingualTextValue> values) {
		if (filter.getLanguageFilter() == null) {
			return new ArrayList<>(values);
		}
		if (filter.getLanguageFilter().isEmpty()) {
			return Collections.emptyList();
		}

		List<MonolingualTextValue> output = new ArrayList<>();
		for(MonolingualTextValue value : values) {
			if (filter.getLanguageFilter().contains(value.getLanguageCode())) {
				output.add(value);
			}
		}
		return output;
	}

	private List<StatementGroup> filterStatementGroups(List<StatementGroup> statementGroups) {
		if (filter.getPropertyFilter() == null) {
			return statementGroups;
		}
		if (filter.getPropertyFilter().isEmpty()) {
			return Collections.emptyList();
		}

		List<StatementGroup> output = new ArrayList<>(statementGroups.size());
		for(StatementGroup statementGroup : statementGroups) {
			if(filter.getPropertyFilter().contains(statementGroup.getProperty())) {
				output.add(statementGroup);
			}
		}
		return output;
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
