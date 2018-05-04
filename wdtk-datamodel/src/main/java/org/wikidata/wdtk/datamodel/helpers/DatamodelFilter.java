package org.wikidata.wdtk.datamodel.helpers;

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
