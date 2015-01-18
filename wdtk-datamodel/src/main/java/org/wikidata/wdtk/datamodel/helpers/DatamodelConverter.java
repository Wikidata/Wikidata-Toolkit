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

import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.SnakVisitor;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

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

	/**
	 * The factory to use for copying.
	 */
	private final DataObjectFactory dataObjectFactory;

	/**
	 * If set to true, subobjects will always be copied recursively. This is the
	 * default.
	 */
	private boolean deepCopy = true;

	/**
	 * If set to true, references will be copied when making deep copyies of
	 * statements. This is the default. The setting does not have any effect
	 * when making shallow copies.
	 */
	private boolean deepCopyReferences = true;

	/**
	 * Set of language codes to restrict the copying of terms (labels,
	 * descriptions, aliases). If set to null, copying of terms will not be
	 * restricted.
	 */
	private Set<String> languageFilter = null;

	/**
	 * Set of property id values to restrict the copying of statements. If set
	 * to null, copying of statements will not be restricted.
	 */
	private Set<PropertyIdValue> propertyFilter = null;

	/**
	 * Set of site keys to restrict the copying of site keys. If set to null,
	 * copying of site links will not be restricted.
	 */
	private Set<String> siteLinkFilter = null;

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
	 * Returns true if objects will be copied by deep copy, replacing all
	 * subobjects recursively. This is the default.
	 *
	 * @return true if deep copy is to be used
	 */
	public boolean hasOptionDeepCopy() {
		return this.deepCopy;
	}

	/**
	 * Sets the value of the deep copy option. If true, subobjects of objects
	 * will be copied recursively.
	 *
	 * @param value
	 *            the value of the deep copy option
	 */
	public void setOptionDeepCopy(boolean value) {
		this.deepCopy = value;
	}

	/**
	 * Returns true if references will be copied when creating a deep copy of
	 * statements. This is the default. The setting ahs no effect when shallow
	 * copies are used.
	 *
	 * @return true if references are included in deep copy
	 */
	public boolean hasOptionDeepCopyReferences() {
		return this.deepCopyReferences;
	}

	/**
	 * Sets the value of the deep copy references option. If true, references
	 * will be copied when creating a deep copy of statements.
	 *
	 * @param value
	 *            the value of the deep copy references option
	 */
	public void setOptionDeepCopyReferences(boolean value) {
		this.deepCopyReferences = value;
	}

	/**
	 * Returns the (possibly empty) set of language codes that are used to
	 * filter data while copying it, or null if no such filter is configured
	 * (default). If not equal to null, only terms in the given language will be
	 * copied.
	 *
	 * @return set of language codes to use for filtering
	 */
	public Set<String> getOptionLanguageFilter() {
		return this.languageFilter;
	}

	/**
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
	public void setOptionLanguageFilter(Set<String> languageFilter) {
		this.languageFilter = languageFilter;
	}

	/**
	 * Returns the (possibly empty) set of {@link PropertyIdValue} objects that
	 * are used to filter statements while copying data, or null if no such
	 * filter is configured (default). If not equal to null, only statements
	 * using the given properties will be copied.
	 *
	 * @return set of properties to use for filtering
	 */
	public Set<PropertyIdValue> getOptionPropertyFilter() {
		return this.propertyFilter;
	}

	/**
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
	public void setOptionPropertyFilter(Set<PropertyIdValue> propertyFilter) {
		this.propertyFilter = propertyFilter;
	}

	/**
	 * Returns the (possibly empty) set of site keys that are used to filter
	 * {@link SiteLink} objects while copying data, or null if no such filter is
	 * configured (default). If not equal to null, only site links for the given
	 * sites will be copied.
	 *
	 * @return set of site keys to use for filtering
	 */
	public Set<String> getOptionSiteLinkFilter() {
		return this.siteLinkFilter;
	}

	/**
	 * Sets the (possibly empty) set of site keys that are used to filter
	 * {@link SiteLink} objects while copying data. Setting this to null
	 * disables this filter (this is the default). If not equal to null, only
	 * site links for the given sites will be copied.
	 *
	 * @param siteLinkFilter
	 *            set of site keys to use for filtering
	 */
	public void setOptionSiteLinkFilter(Set<String> siteLinkFilter) {
		this.siteLinkFilter = siteLinkFilter;
	}

	/**
	 * Copies an {@link ItemIdValue}.
	 *
	 * @param object
	 *            object to copy
	 * @return copied object
	 */
	public ItemIdValue copy(ItemIdValue object) {
		return this.dataObjectFactory.getItemIdValue(object.getId(),
				object.getSiteIri());
	}

	/**
	 * Copies a {@link PropertyIdValue}.
	 *
	 * @param object
	 *            object to copy
	 * @return copied object
	 */
	public PropertyIdValue copy(PropertyIdValue object) {
		return this.dataObjectFactory.getPropertyIdValue(object.getId(),
				object.getSiteIri());
	}

	/**
	 * Copies a {@link DatatypeIdValue}.
	 *
	 * @param object
	 *            object to copy
	 * @return copied object
	 */
	public DatatypeIdValue copy(DatatypeIdValue object) {
		return this.dataObjectFactory.getDatatypeIdValue(object.getIri());
	}

	/**
	 * Copies a {@link TimeValue}.
	 *
	 * @param object
	 *            object to copy
	 * @return copied object
	 */
	public TimeValue copy(TimeValue object) {
		return this.dataObjectFactory.getTimeValue(object.getYear(),
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
		return this.dataObjectFactory.getGlobeCoordinatesValue(
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
		return this.dataObjectFactory.getStringValue(object.getString());
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
		return this.dataObjectFactory.getMonolingualTextValue(object.getText(),
				object.getLanguageCode());
	}

	/**
	 * Copies a {@link QuantityValue}.
	 *
	 * @param object
	 *            object to copy
	 * @return copied object
	 */
	public QuantityValue copy(QuantityValue object) {
		return this.dataObjectFactory.getQuantityValue(
				object.getNumericValue(), object.getLowerBound(),
				object.getUpperBound());
	}

	/**
	 * Copies a {@link ValueSnak}.
	 *
	 * @param object
	 *            object to copy
	 * @return copied object
	 */
	public ValueSnak copy(ValueSnak object) {
		if (this.deepCopy) {
			return this.dataObjectFactory.getValueSnak(
					copy(object.getPropertyId()), copyValue(object.getValue()));
		} else {
			return this.dataObjectFactory.getValueSnak(object.getPropertyId(),
					object.getValue());
		}
	}

	/**
	 * Copies a {@link SomeValueSnak}.
	 *
	 * @param object
	 *            object to copy
	 * @return copied object
	 */
	public SomeValueSnak copy(SomeValueSnak object) {
		return this.dataObjectFactory
				.getSomeValueSnak(innerCopyPropertyIdValue(object
						.getPropertyId()));
	}

	/**
	 * Copies a {@link NoValueSnak}.
	 *
	 * @param object
	 *            object to copy
	 * @return copied object
	 */
	public NoValueSnak copy(NoValueSnak object) {
		return this.dataObjectFactory
				.getNoValueSnak(innerCopyPropertyIdValue(object.getPropertyId()));
	}

	/**
	 * Copies a {@link SnakGroup}.
	 *
	 * @param object
	 *            object to copy
	 * @return copied object
	 */
	public SnakGroup copy(SnakGroup object) {
		if (this.deepCopy) {
			return deepCopySnakGroup(object);
		} else {
			return this.dataObjectFactory.getSnakGroup(object.getSnaks());
		}
	}

	/**
	 * Copies a {@link Claim}.
	 *
	 * @param object
	 *            object to copy
	 * @return copied object
	 */
	public Claim copy(Claim object) {
		if (this.deepCopy) {
			return deepCopyClaim(object);
		} else {
			return this.dataObjectFactory.getClaim(object.getSubject(),
					object.getMainSnak(), object.getQualifiers());
		}
	}

	/**
	 * Copies a {@link Reference}.
	 *
	 * @param object
	 *            object to copy
	 * @return copied object
	 */
	public Reference copy(Reference object) {
		if (this.deepCopy) {
			return this.dataObjectFactory
					.getReference(deepCopySnakGroups(object.getSnakGroups()));
		} else {
			return this.dataObjectFactory.getReference(object.getSnakGroups());
		}
	}

	/**
	 * Copies a {@link Statement}.
	 *
	 * @param object
	 *            object to copy
	 * @return copied object
	 */
	public Statement copy(Statement object) {
		if (this.deepCopy) {
			return deepCopyStatement(object);
		} else {
			return this.dataObjectFactory.getStatement(object.getClaim(),
					object.getReferences(), object.getRank(),
					object.getStatementId());
		}
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
		if (this.deepCopy) {
			return deepCopyStatementGroup(object);
		} else {
			return this.dataObjectFactory.getStatementGroup(object
					.getStatements());
		}
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
		return this.dataObjectFactory.getSiteLink(object.getPageTitle(),
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
		if (this.deepCopy) {
			return this.dataObjectFactory.getPropertyDocument(copy(object
					.getPropertyId()), deepCopyMonoLingualTextValues(object
					.getLabels().values()),
					deepCopyMonoLingualTextValues(object.getDescriptions()
							.values()), flattenDeepCopyAliasMap(object
							.getAliases()), deepCopyStatementGroups(object
							.getStatementGroups()), copy(object.getDatatype()));
		} else {
			return this.dataObjectFactory.getPropertyDocument(object
					.getPropertyId(), copyMonoLingualTextValues(object
					.getLabels().values()), copyMonoLingualTextValues(object
					.getDescriptions().values()), flattenAliasMap(object
					.getAliases()), copyStatementGroups(object
					.getStatementGroups()), object.getDatatype());
		}
	}

	/**
	 * Copies an {@link ItemDocument}.
	 *
	 * @param object
	 *            object to copy
	 * @return copied object
	 */
	public ItemDocument copy(ItemDocument object) {
		if (this.deepCopy) {
			return this.dataObjectFactory.getItemDocument(copy(object
					.getItemId()), deepCopyMonoLingualTextValues(object
					.getLabels().values()),
					deepCopyMonoLingualTextValues(object.getDescriptions()
							.values()), flattenDeepCopyAliasMap(object
							.getAliases()), deepCopyStatementGroups(object
							.getStatementGroups()), deepCopySiteLinks(object
							.getSiteLinks()));
		} else {
			return this.dataObjectFactory
					.getItemDocument(object.getItemId(),
							copyMonoLingualTextValues(object.getLabels()
									.values()),
							copyMonoLingualTextValues(object.getDescriptions()
									.values()), flattenAliasMap(object
									.getAliases()), copyStatementGroups(object
									.getStatementGroups()),
							copySiteLinks(object.getSiteLinks()));
		}
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
	public Value visit(DatatypeIdValue value) {
		return copy(value);
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

	/**
	 * Converts a map of language keys to lists of {@link MonolingualTextValue}
	 * objects to a flat list of such objects, as required for the factory
	 * methods.
	 *
	 * @param aliasMap
	 *            the map to convert
	 * @return the flattened list
	 */
	private List<MonolingualTextValue> flattenAliasMap(
			Map<String, List<MonolingualTextValue>> aliasMap) {
		List<MonolingualTextValue> aliases = new ArrayList<>();
		for (Entry<String, List<MonolingualTextValue>> langAliases : aliasMap
				.entrySet()) {
			if (this.languageFilter == null
					|| this.languageFilter.contains(langAliases.getKey())) {
				aliases.addAll(langAliases.getValue());
			}
		}
		return aliases;
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
	private List<MonolingualTextValue> flattenDeepCopyAliasMap(
			Map<String, List<MonolingualTextValue>> aliasMap) {
		List<MonolingualTextValue> aliases = new ArrayList<>();
		for (Entry<String, List<MonolingualTextValue>> langAliases : aliasMap
				.entrySet()) {
			if (this.languageFilter == null
					|| this.languageFilter.contains(langAliases.getKey())) {
				for (MonolingualTextValue mtv : langAliases.getValue()) {
					aliases.add(copy(mtv));
				}
			}
		}
		return aliases;

	}

	/**
	 * Copies the given {@link PropertyIdValue} appearing as a subobject of some
	 * copied structure. Depending on the setting for the deep copy option, this
	 * will either return the value unchanged or make a copy.
	 *
	 * @param propertyIdValue
	 *            the value to copy
	 * @return the copied value
	 */
	private PropertyIdValue innerCopyPropertyIdValue(
			PropertyIdValue propertyIdValue) {
		return this.deepCopy ? copy(propertyIdValue) : propertyIdValue;
	}

	/**
	 * Copies a {@link Snak}, creating deep copies of all subobjects.
	 *
	 * @param snak
	 *            object to copy
	 * @return the copied object
	 */
	private Snak deepCopySnak(Snak snak) {
		if (snak instanceof ValueSnak) {
			return this.dataObjectFactory.getValueSnak(
					copy(snak.getPropertyId()),
					copyValue(((ValueSnak) snak).getValue()));
		} else if (snak instanceof NoValueSnak) {
			return this.dataObjectFactory.getNoValueSnak(copy(snak
					.getPropertyId()));
		} else if (snak instanceof SomeValueSnak) {
			return this.dataObjectFactory.getSomeValueSnak(copy(snak
					.getPropertyId()));
		} else {
			throw new IllegalArgumentException(
					"I don't know how to copy snaks of type " + snak.getClass());
		}
	}

	/**
	 * Copies a {@link SnakGroup}, creating deep copies of all subobjects.
	 *
	 * @param snakGroup
	 *            object to copy
	 * @return the copied object
	 */
	private SnakGroup deepCopySnakGroup(SnakGroup snakGroup) {
		List<Snak> snaks = new ArrayList<>(snakGroup.getSnaks().size());
		for (Snak snak : snakGroup.getSnaks()) {
			snaks.add(deepCopySnak(snak));
		}
		return this.dataObjectFactory.getSnakGroup(snaks);
	}

	/**
	 * Copies a list of {@link SnakGroup} objects, creating deep copies of all
	 * subobjects.
	 *
	 * @param snakGroups
	 *            object to copy
	 * @return the copied object
	 */
	private List<SnakGroup> deepCopySnakGroups(List<SnakGroup> snakGroups) {
		List<SnakGroup> result = new ArrayList<>(snakGroups.size());
		for (SnakGroup snakGroup : snakGroups) {
			result.add(deepCopySnakGroup(snakGroup));
		}
		return result;
	}

	/**
	 * Copies a {@link Claim}, creating deep copies of all subobjects.
	 *
	 * @param claim
	 *            object to copy
	 * @return the copied object
	 */
	private Claim deepCopyClaim(Claim claim) {
		return this.dataObjectFactory.getClaim(
				(EntityIdValue) visit(claim.getSubject()),
				deepCopySnak(claim.getMainSnak()),
				deepCopySnakGroups(claim.getQualifiers()));
	}

	/**
	 * Copies a list of {@link Reference} objects, creating deep copies of all
	 * subobjects.
	 *
	 * @param references
	 *            object to copy
	 * @return the copied object
	 */
	private List<Reference> deepCopyReferences(
			List<? extends Reference> references) {
		if (!this.deepCopyReferences) {
			return Collections.<Reference> emptyList();
		}

		List<Reference> result = new ArrayList<>(references.size());
		for (Reference reference : references) {
			result.add(this.dataObjectFactory
					.getReference(deepCopySnakGroups(reference.getSnakGroups())));
		}
		return result;
	}

	/**
	 * Copies a {@link Statement}, creating deep copies of all subobjects.
	 *
	 * @param statement
	 *            object to copy
	 * @return the copied object
	 */
	private Statement deepCopyStatement(Statement statement) {
		return this.dataObjectFactory.getStatement(
				deepCopyClaim(statement.getClaim()),
				deepCopyReferences(statement.getReferences()),
				statement.getRank(), statement.getStatementId());
	}

	/**
	 * Copies a {@link StatementGroup}, creating deep copies of all subobjects.
	 *
	 * @param statementGroup
	 *            object to copy
	 * @return the copied object
	 */
	private StatementGroup deepCopyStatementGroup(StatementGroup statementGroup) {
		List<Statement> statements = new ArrayList<>(statementGroup
				.getStatements().size());
		for (Statement statement : statementGroup.getStatements()) {
			statements.add(this.deepCopyStatement(statement));
		}
		return this.dataObjectFactory.getStatementGroup(statements);
	}

	/**
	 * Copies a list of {@link StatementGroup} objects, creating deep copies of
	 * all subobjects.
	 *
	 * @param statementGroups
	 *            object to copy
	 * @return the copied object
	 */
	private List<StatementGroup> deepCopyStatementGroups(
			List<StatementGroup> statementGroups) {
		if (this.propertyFilter != null && this.propertyFilter.isEmpty()) {
			return Collections.<StatementGroup> emptyList();
		}

		List<StatementGroup> result = new ArrayList<>(statementGroups.size());
		for (StatementGroup statementGroup : statementGroups) {
			if (this.propertyFilter == null
					|| this.propertyFilter.contains(statementGroup
							.getProperty())) {
				result.add(this.deepCopyStatementGroup(statementGroup));
			}
		}
		return result;
	}

	/**
	 * Copies a list of {@link StatementGroup} objects as part of a shallow
	 * copy.
	 *
	 * @param statementGroups
	 *            object to copy
	 * @return the copied object
	 */
	private List<StatementGroup> copyStatementGroups(
			List<StatementGroup> statementGroups) {
		if (this.propertyFilter == null) {
			return statementGroups;
		} else if (this.propertyFilter.isEmpty()) {
			return Collections.<StatementGroup> emptyList();
		}

		List<StatementGroup> result = new ArrayList<>(statementGroups.size());
		for (StatementGroup statementGroup : statementGroups) {
			if (this.propertyFilter.contains(statementGroup.getProperty())) {
				result.add(statementGroup);
			}
		}
		return result;
	}

	/**
	 * Copies a collection of {@link MonolingualTextValue} objects, creating
	 * deep copies of all values.
	 *
	 * @param monoLingualTextValues
	 *            object to copy
	 * @return the copied object
	 */
	private List<MonolingualTextValue> deepCopyMonoLingualTextValues(
			Collection<MonolingualTextValue> monoLingualTextValues) {
		if (this.languageFilter != null && this.languageFilter.isEmpty()) {
			return Collections.<MonolingualTextValue> emptyList();
		}

		List<MonolingualTextValue> result = new ArrayList<>(
				monoLingualTextValues.size());
		for (MonolingualTextValue mtv : monoLingualTextValues) {
			if (this.languageFilter == null
					|| this.languageFilter.contains(mtv.getLanguageCode())) {
				result.add(copy(mtv));
			}
		}
		return result;
	}

	/**
	 * Copies a collection of {@link MonolingualTextValue} objects.
	 *
	 * @param monoLingualTextValues
	 *            object to copy
	 * @return the copied object
	 */
	private List<MonolingualTextValue> copyMonoLingualTextValues(
			Collection<MonolingualTextValue> monoLingualTextValues) {
		if (this.languageFilter == null) {
			return new ArrayList<>(monoLingualTextValues);
		} else if (this.languageFilter.isEmpty()) {
			return Collections.<MonolingualTextValue> emptyList();
		}

		List<MonolingualTextValue> result = new ArrayList<>(
				monoLingualTextValues.size());
		for (MonolingualTextValue mtv : monoLingualTextValues) {
			if (this.languageFilter.contains(mtv.getLanguageCode())) {
				result.add(mtv);
			}
		}
		return result;
	}

	/**
	 * Copies a map of {@link SiteLink} objects, creating deep copies of all
	 * sitelinks.
	 *
	 * @param siteLinks
	 *            object to copy
	 * @return the copied object
	 */
	private Map<String, SiteLink> deepCopySiteLinks(
			Map<String, SiteLink> siteLinks) {
		if (this.siteLinkFilter != null && this.siteLinkFilter.isEmpty()) {
			return Collections.<String, SiteLink> emptyMap();
		}

		Map<String, SiteLink> result = new HashMap<>(siteLinks.size());
		for (Entry<String, SiteLink> entry : siteLinks.entrySet()) {
			if (this.siteLinkFilter == null
					|| this.siteLinkFilter.contains(entry.getKey())) {
				result.put(entry.getKey(), copy(entry.getValue()));
			}
		}
		return result;
	}

	/**
	 * Copies a map of {@link SiteLink} objects as part of a shallow copy.
	 *
	 * @param siteLinks
	 *            object to copy
	 * @return the copied object
	 */
	private Map<String, SiteLink> copySiteLinks(Map<String, SiteLink> siteLinks) {
		if (this.siteLinkFilter == null) {
			return siteLinks;
		} else if (this.siteLinkFilter.isEmpty()) {
			return Collections.<String, SiteLink> emptyMap();
		}

		Map<String, SiteLink> result = new HashMap<>(siteLinks.size());
		for (Entry<String, SiteLink> entry : siteLinks.entrySet()) {
			if (this.siteLinkFilter.contains(entry.getKey())) {
				result.put(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}

}
