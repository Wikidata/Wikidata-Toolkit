package org.wikidata.wdtk.datamodel.helpers;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.interfaces.*;

/**
 * Class to re-create data model objects using a specified factory. This is
 * provided in place of having copy constructors in each and every
 * implementation of the data model. Note that data model objects are usually
 * immutable and do not need to be copied. The use of this class is to convert
 * data objects to a specific implementation, as might be needed for some
 * specialized purpose (e.g., for JSON export).
 *
 * @author Markus Kroetzsch
 *
 */
public class DatamodelConverter implements SnakVisitor<Snak>,
		ValueVisitor<Value> {

	static final Logger logger = LoggerFactory
			.getLogger(DatamodelConverter.class);

	/**
	 * The factory to use for copying.
	 */
	private final DataObjectFactory dataObjectFactory;

	private DocumentDataFilter filter = new DocumentDataFilter();

	/**
	 * If set to true, references will be copied when making deep copies of
	 * statements. This is the default. The setting does not have any effect
	 * when making shallow copies.
	 */
	private boolean deepCopyReferences = true;

	/**
	 * Constructor.
	 *
	 * @param dataObjectFactory
	 *            the factory to use for creating new objects
	 */
	public DatamodelConverter(DataObjectFactory dataObjectFactory) {
		this.dataObjectFactory = dataObjectFactory;
	}

	/**
	 * @deprecated Shallow copy is not supported anymore
	 *
	 * Returns true if objects will be copied by deep copy, replacing all
	 * subobjects recursively. This is the default.
	 *
	 * @return true if deep copy is to be used
	 */
	@Deprecated
	public boolean hasOptionDeepCopy() {
		return true;
	}

	/**
	 * @deprecated Shallow copy is not supported anymore
	 *
	 * Sets the value of the deep copy option. If true, subobjects of objects
	 * will be copied recursively.
	 *
	 * @param value
	 *            the value of the deep copy option
	 */
	@Deprecated
	public void setOptionDeepCopy(boolean value) {
	}

	/**
	 * @deprecated Support for filtering references is going to be removed from DatamodelConverter
	 *
	 * Returns true if references will be copied when creating a deep copy of
	 * statements. This is the default. The setting has no effect when shallow
	 * copies are used.
	 *
	 * @return true if references are included in deep copy
	 */
	@Deprecated
	public boolean hasOptionDeepCopyReferences() {
		return deepCopyReferences;
	}

	/**
	 * @deprecated Support for filtering references is going to be removed from DatamodelConverter
	 *
	 * Sets the value of the deep copy references option. If true, references
	 * will be copied when creating a deep copy of statements.
	 *
	 * @param value
	 *            the value of the deep copy references option
	 */
	@Deprecated
	public void setOptionDeepCopyReferences(boolean value) {
		deepCopyReferences = value;
	}

	/**
	 * @deprecated Use {@link DatamodelFilter}
	 *
	 * Replaces the current filter settings with the given ones. Future changes
	 * to the settings will take effect (a reference is used, not a full copy).
	 *
	 * @param filter
	 *            the filter to use
	 */
	@Deprecated
	public void setOptionFilter(DocumentDataFilter filter) {
		this.filter = filter;
	}

	/**
	 * @deprecated Use {@link DatamodelFilter}
	 *
	 * Returns the (possibly empty) set of language codes that are used to
	 * filter data while copying it, or null if no such filter is configured
	 * (default). If not equal to null, only terms in the given language will be
	 * copied.
	 *
	 * @return set of language codes to use for filtering
	 */
	@Deprecated
	public Set<String> getOptionLanguageFilter() {
		return filter.getLanguageFilter();
	}

	/**
	 * @deprecated Use {@link DatamodelFilter}
	 *
	 * Sets the (possibly empty) set of language codes that are used to filter
	 * data while copying it. Setting this to null disables this filter (this is
	 * the default). If not equal to null, only terms in the given language will
	 * be copied.
	 * <p>
	 * The language filter is not applied to monolingual text values in
	 * statements. Only labels, descriptions, and aliases are filtered.
	 *
	 * @param languageFilter
	 *            set of language codes to restrict copying to
	 */
	@Deprecated
	public void setOptionLanguageFilter(Set<String> languageFilter) {
		filter.setLanguageFilter(languageFilter);
	}

	/**
	 * @deprecated Use {@link DatamodelFilter}
	 *
	 * Returns the (possibly empty) set of {@link PropertyIdValue} objects that
	 * are used to filter statements while copying data, or null if no such
	 * filter is configured (default). If not equal to null, only statements
	 * using the given properties will be copied.
	 *
	 * @return set of properties to use for filtering
	 */
	@Deprecated
	public Set<PropertyIdValue> getOptionPropertyFilter() {
		return filter.getPropertyFilter();
	}

	/**
	 * @deprecated Use {@link DatamodelFilter}
	 *
	 * Sets the (possibly empty) set of {@link PropertyIdValue} objects that are
	 * used to filter statements while copying data. Setting this to null
	 * disables this filter (this is the default). If not equal to null, only
	 * statements using the given properties will be copied.
	 * <p>
	 * The property filter is not applied to qualifiers and references in
	 * statements. Only the main property of statements is filtered.
	 *
	 * @param propertyFilter
	 *            set of properties to use for filtering
	 */
	@Deprecated
	public void setOptionPropertyFilter(Set<PropertyIdValue> propertyFilter) {
		filter.setPropertyFilter(propertyFilter);
	}

	/**
	 * @deprecated Use {@link DatamodelFilter}
	 *
	 * Returns the (possibly empty) set of site keys that are used to filter
	 * {@link SiteLink} objects while copying data, or null if no such filter is
	 * configured (default). If not equal to null, only site links for the given
	 * sites will be copied.
	 *
	 * @return set of site keys to use for filtering
	 */
	@Deprecated
	public Set<String> getOptionSiteLinkFilter() {
		return filter.getSiteLinkFilter();
	}

	/**
	 * @deprecated Use {@link DatamodelFilter}
	 *
	 * Sets the (possibly empty) set of site keys that are used to filter
	 * {@link SiteLink} objects while copying data. Setting this to null
	 * disables this filter (this is the default). If not equal to null, only
	 * site links for the given sites will be copied.
	 *
	 * @param siteLinkFilter
	 *            set of site keys to use for filtering
	 */
	@Deprecated
	public void setOptionSiteLinkFilter(Set<String> siteLinkFilter) {
		filter.setSiteLinkFilter(siteLinkFilter);
	}

	/**
	 * Copies an {@link ItemIdValue}.
	 *
	 * @param object
	 *            object to copy
	 * @return copied object
	 */
	public ItemIdValue copy(ItemIdValue object) {
		return dataObjectFactory.getItemIdValue(object.getId(), object.getSiteIri());
	}

	/**
	 * Copies a {@link PropertyIdValue}.
	 *
	 * @param object
	 *            object to copy
	 * @return copied object
	 */
	public PropertyIdValue copy(PropertyIdValue object) {
		return dataObjectFactory.getPropertyIdValue(object.getId(), object.getSiteIri());
	}

	/**
	 * Copies a {@link LexemeIdValue}.
	 *
	 * @param object
	 *            object to copy
	 * @return copied object
	 */
	public LexemeIdValue copy(LexemeIdValue object) {
		return dataObjectFactory.getLexemeIdValue(object.getId(), object.getSiteIri());
	}

	/**
	 * Copies a {@link DatatypeIdValue}.
	 *
	 * @param object
	 *            object to copy
	 * @return copied object
	 */
	public DatatypeIdValue copy(DatatypeIdValue object) {
		return dataObjectFactory.getDatatypeIdValue(object.getIri());
	}

	/**
	 * Copies a {@link TimeValue}.
	 *
	 * @param object
	 *            object to copy
	 * @return copied object
	 */
	public TimeValue copy(TimeValue object) {
		return dataObjectFactory.getTimeValue(object.getYear(),
				object.getMonth(), object.getDay(), object.getHour(),
				object.getMinute(), object.getSecond(), object.getPrecision(),
				object.getBeforeTolerance(), object.getAfterTolerance(),
				object.getTimezoneOffset(), object.getPreferredCalendarModel());
	}

	/**
	 * Copies a {@link GlobeCoordinatesValue}.
	 *
	 * @param object
	 *            object to copy
	 * @return copied object
	 */
	public GlobeCoordinatesValue copy(GlobeCoordinatesValue object) {
		return dataObjectFactory.getGlobeCoordinatesValue(
				object.getLatitude(), object.getLongitude(),
				object.getPrecision(), object.getGlobe());
	}

	/**
	 * Copies a {@link StringValue}.
	 *
	 * @param object
	 *            object to copy
	 * @return copied object
	 */
	public StringValue copy(StringValue object) {
		return dataObjectFactory.getStringValue(object.getString());
	}

	/**
	 * Copies a {@link MonolingualTextValue}. This method is not affected by the
	 * language filter option (see
	 * {@link DatamodelConverter#setOptionLanguageFilter(Set)}).
	 *
	 * @param object
	 *            object to copy
	 * @return copied object
	 */
	public MonolingualTextValue copy(MonolingualTextValue object) {
		return dataObjectFactory.getMonolingualTextValue(object.getText(), object.getLanguageCode());
	}

	/**
	 * Copies a {@link QuantityValue}.
	 *
	 * @param object
	 *            object to copy
	 * @return copied object
	 */
	public QuantityValue copy(QuantityValue object) {
		return dataObjectFactory.getQuantityValue(
				object.getNumericValue(), object.getLowerBound(),
				object.getUpperBound(), object.getUnit());
	}
	
	/**
	 * Copies an {@link UnsupportedValue}.
	 *
	 * @param object
	 *            object to copy
	 * @return copied object
	 */
    public UnsupportedValue copy(UnsupportedValue object) {
    	// unsupported values cannot be copied!
    	return object;
    }

	/**
	 * Copies a {@link Snak}.
	 *
	 * @param snak
	 *            object to copy
	 * @return the copied object
	 */
	private Snak copy(Snak snak) {
		if (snak instanceof ValueSnak) {
			return copy((ValueSnak) snak);
		} else if (snak instanceof NoValueSnak) {
			return copy((NoValueSnak) snak);
		} else if (snak instanceof SomeValueSnak) {
			return copy((SomeValueSnak) snak);
		} else {
			throw new IllegalArgumentException(
					"I don't know how to copy snaks of type " + snak.getClass());
		}
	}

	/**
	 * Copies a {@link ValueSnak}.
	 *
	 * @param object
	 *            object to copy
	 * @return copied object
	 */
	public ValueSnak copy(ValueSnak object) {
		return dataObjectFactory.getValueSnak(copy(object.getPropertyId()), copyValue(object.getValue()));
	}

	/**
	 * Copies a {@link SomeValueSnak}.
	 *
	 * @param object
	 *            object to copy
	 * @return copied object
	 */
	public SomeValueSnak copy(SomeValueSnak object) {
		return dataObjectFactory.getSomeValueSnak(copy(object.getPropertyId()));
	}

	/**
	 * Copies a {@link NoValueSnak}.
	 *
	 * @param object
	 *            object to copy
	 * @return copied object
	 */
	public NoValueSnak copy(NoValueSnak object) {
		return dataObjectFactory.getNoValueSnak(copy(object.getPropertyId()));
	}

	/**
	 * Copies a {@link SnakGroup}.
	 *
	 * @param object
	 *            object to copy
	 * @return copied object
	 */
	public SnakGroup copy(SnakGroup object) {
		List<Snak> snaks = new ArrayList<>(object.size());
		for (Snak snak : object) {
			snaks.add(copy(snak));
		}
		return dataObjectFactory.getSnakGroup(snaks);
	}

	/**
	 * Copies a {@link Claim}.
	 *
	 * @param object
	 *            object to copy
	 * @return copied object
	 */
	public Claim copy(Claim object) {
		return dataObjectFactory.getClaim(
				(EntityIdValue) visit(object.getSubject()),
				copy(object.getMainSnak()),
				copy(object.getQualifiers()));
	}

	/**
	 * Copies a {@link Reference}.
	 *
	 * @param object
	 *            object to copy
	 * @return copied object
	 */
	public Reference copy(Reference object) {
		return dataObjectFactory.getReference(copy(object.getSnakGroups()));
	}

	/**
	 * Copies a {@link Statement}.
	 *
	 * @param object
	 *            object to copy
	 * @return copied object
	 */
	public Statement copy(Statement object) {
		return dataObjectFactory.getStatement(
				(EntityIdValue) visit(object.getSubject()),
				copy(object.getMainSnak()),
				copy(object.getQualifiers()),
				copyReferences(object.getReferences()),
				object.getRank(), object.getStatementId());
	}

	/**
	 * Copies a {@link StatementGroup}.
	 * <p>
	 * This method ignores filters set via {@link #setOptionPropertyFilter(Set)}.
	 *
	 * @param object
	 *            object to copy
	 * @return copied object
	 */
	public StatementGroup copy(StatementGroup object) {
		List<Statement> statements = new ArrayList<>(object.getStatements().size());
		for (Statement statement : object.getStatements()) {
			statements.add(copy(statement));
		}
		return dataObjectFactory.getStatementGroup(statements);
	}

	/**
	 * Copies a {@link SiteLink}.
	 * <p>
	 * This method is not affected by the setting made via
	 * {@link #setOptionSiteLinkFilter(Set)}.
	 *
	 * @param object
	 *            object to copy
	 * @return copied object
	 */
	public SiteLink copy(SiteLink object) {
		return dataObjectFactory.getSiteLink(object.getPageTitle(),
				object.getSiteKey(), object.getBadges());
	}

	/**
	 * Copies a {@link PropertyDocument}.
	 *
	 * @param object
	 *            object to copy
	 * @return copied object
	 */
	public PropertyDocument copy(PropertyDocument object) {
		return dataObjectFactory.getPropertyDocument(
				copy(object.getEntityId()),
				copyMonoLingualTextValues(object.getLabels().values()),
				copyMonoLingualTextValues(object.getDescriptions().values()),
				copyAliasMap(object.getAliases()),
				copyStatementGroups(object.getStatementGroups()),
				copy(object.getDatatype()),
				object.getRevisionId());
	}

	/**
	 * Copies an {@link ItemDocument}.
	 *
	 * @param object
	 *            object to copy
	 * @return copied object
	 */
	public ItemDocument copy(ItemDocument object) {
		return dataObjectFactory.getItemDocument(
				copy(object.getEntityId()),
				copyMonoLingualTextValues(object.getLabels().values()),
				copyMonoLingualTextValues(object.getDescriptions().values()),
				copyAliasMap(object.getAliases()),
				copyStatementGroups(object.getStatementGroups()),
				copySiteLinks(object.getSiteLinks()),
				object.getRevisionId());
	}

	/**
	 * Copies a {@link Snak}.
	 *
	 * @param snak
	 *            object to copy
	 * @return copied object
	 */
	public Snak copySnak(Snak snak) {
		return snak.accept(this);
	}

	@Override
	public Snak visit(ValueSnak snak) {
		return copy(snak);
	}

	@Override
	public Snak visit(SomeValueSnak snak) {
		return copy(snak);
	}

	@Override
	public Snak visit(NoValueSnak snak) {
		return copy(snak);
	}

	/**
	 * Copies a {@link Value}.
	 *
	 * @param value
	 *            object to copy
	 * @return copied object
	 */
	public Value copyValue(Value value) {
		return value.accept(this);
	}

	@Override
	public Value visit(EntityIdValue value) {
		if (value instanceof ItemIdValue) {
			return copy((ItemIdValue) value);
		} else if (value instanceof PropertyIdValue) {
			return copy((PropertyIdValue) value);
		} else {
			throw new UnsupportedOperationException(
					"Cannot convert entity id value: " + value.getClass());
		}
	}

	@Override
	public Value visit(GlobeCoordinatesValue value) {
		return copy(value);
	}

	@Override
	public Value visit(MonolingualTextValue value) {
		return copy(value);
	}

	@Override
	public Value visit(QuantityValue value) {
		return copy(value);
	}

	@Override
	public Value visit(StringValue value) {
		return copy(value);
	}

	@Override
	public Value visit(TimeValue value) {
		return copy(value);
	}
	
	@Override
	public Value visit(UnsupportedValue value) {
		return copy(value);
	}

	/**
	 * Converts a map of language keys to lists of {@link MonolingualTextValue}
	 * objects to a flat list of such objects, as required for the factory
	 * methods, where the values in the flat lists are new copies of the
	 * original values.
	 *
	 * @param aliasMap
	 *            the map to convert
	 * @return the flattened list with copied values
	 */
	private List<MonolingualTextValue> copyAliasMap(
			Map<String, List<MonolingualTextValue>> aliasMap) {
		List<MonolingualTextValue> aliases = new ArrayList<>();
		for (Entry<String, List<MonolingualTextValue>> langAliases : aliasMap.entrySet()) {
			if (filter.includeLanguage(langAliases.getKey())) {
				for (MonolingualTextValue mtv : langAliases.getValue()) {
					aliases.add(copy(mtv));
				}
			}
		}
		return aliases;

	}

	/**
	 * Copies a list of {@link SnakGroup} objects.
	 *
	 * @param snakGroups
	 *            object to copy
	 * @return the copied object
	 */
	private List<SnakGroup> copy(List<SnakGroup> snakGroups) {
		List<SnakGroup> result = new ArrayList<>(snakGroups.size());
		for (SnakGroup snakGroup : snakGroups) {
			result.add(copy(snakGroup));
		}
		return result;
	}

	/**
	 * Copies a list of {@link Reference} objects.
	 *
	 * @param references
	 *            object to copy
	 * @return the copied object
	 */
	private List<Reference> copyReferences(List<Reference> references) {
		if (!deepCopyReferences) {
			return Collections.emptyList();
		}

		List<Reference> result = new ArrayList<>(references.size());
		for (Reference reference : references) {
			result.add(dataObjectFactory
					.getReference(copy(reference.getSnakGroups())));
		}
		return result;
	}

	/**
	 * Copies a list of {@link StatementGroup} objects.
	 *
	 * @param statementGroups
	 *            object to copy
	 * @return the copied object
	 */
	private List<StatementGroup> copyStatementGroups(List<StatementGroup> statementGroups) {
		if (filter.excludeAllProperties()) {
			return Collections.emptyList();
		}

		List<StatementGroup> result = new ArrayList<>(statementGroups.size());
		for (StatementGroup statementGroup : statementGroups) {
			if (filter.includePropertyId(statementGroup.getProperty())) {
				result.add(copy(statementGroup));
			}
		}
		return result;
	}

	/**
	 * Copies a collection of {@link MonolingualTextValue} objects
	 *
	 * @param monoLingualTextValues
	 *            object to copy
	 * @return the copied object
	 */
	private List<MonolingualTextValue> copyMonoLingualTextValues(Collection<MonolingualTextValue> monoLingualTextValues) {
		if (filter.excludeAllLanguages()) {
			return Collections.emptyList();
		}

		List<MonolingualTextValue> result = new ArrayList<>(monoLingualTextValues.size());
		for (MonolingualTextValue mtv : monoLingualTextValues) {
			if (filter.includeLanguage(mtv.getLanguageCode())) {
				result.add(copy(mtv));
			}
		}
		return result;
	}

	/**
	 * Copies a map of {@link SiteLink} objects.
	 *
	 * @param siteLinks
	 *            object to copy
	 * @return the copied object
	 */
	private Map<String, SiteLink> copySiteLinks(Map<String, SiteLink> siteLinks) {
		if (filter.excludeAllSiteLinks()) {
			return Collections.emptyMap();
		}

		Map<String, SiteLink> result = new HashMap<>(siteLinks.size());
		for (Entry<String, SiteLink> entry : siteLinks.entrySet()) {
			if (filter.includeSiteLink(entry.getKey())) {
				result.put(entry.getKey(), copy(entry.getValue()));
			}
		}
		return result;
	}

}
