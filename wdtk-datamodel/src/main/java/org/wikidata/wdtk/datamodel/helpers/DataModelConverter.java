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
import java.util.List;
import java.util.Map;

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
public class DataModelConverter implements SnakVisitor<Snak>,
		ValueVisitor<Value> {

	private final DataObjectFactory dataObjectFactory;

	/**
	 * Constructor.
	 *
	 * @param dataObjectFactory
	 *            the factory to use for creating new objects
	 */
	public DataModelConverter(DataObjectFactory dataObjectFactory) {
		this.dataObjectFactory = dataObjectFactory;
	}

	public ItemIdValue convert(ItemIdValue object) {
		return this.dataObjectFactory.getItemIdValue(object.getId(),
				object.getIri());
	}

	public PropertyIdValue convert(PropertyIdValue object) {
		return this.dataObjectFactory.getPropertyIdValue(object.getId(),
				object.getIri());
	}

	public DatatypeIdValue convert(DatatypeIdValue object) {
		return this.dataObjectFactory.getDatatypeIdValue(object.getIri());
	}

	public TimeValue convert(TimeValue object) {
		return this.dataObjectFactory.getTimeValue(object.getYear(),
				object.getMonth(), object.getDay(), object.getHour(),
				object.getMinute(), object.getSecond(), object.getPrecision(),
				object.getBeforeTolerance(), object.getAfterTolerance(),
				object.getTimezoneOffset(), object.getPreferredCalendarModel());
	}

	public GlobeCoordinatesValue convert(GlobeCoordinatesValue object) {
		return this.dataObjectFactory.getGlobeCoordinatesValue(
				object.getLatitude(), object.getLongitude(),
				object.getPrecision(), object.getGlobe());
	}

	public StringValue convert(StringValue object) {
		return this.dataObjectFactory.getStringValue(object.getString());
	}

	public MonolingualTextValue convert(MonolingualTextValue object) {
		return this.dataObjectFactory.getMonolingualTextValue(object.getText(),
				object.getLanguageCode());
	}

	public QuantityValue convert(QuantityValue object) {
		return this.dataObjectFactory.getQuantityValue(
				object.getNumericValue(), object.getLowerBound(),
				object.getUpperBound());
	}

	public ValueSnak convert(ValueSnak object) {
		return this.dataObjectFactory.getValueSnak(object.getPropertyId(),
				object.getValue());
	}

	public SomeValueSnak convert(SomeValueSnak object) {
		return this.dataObjectFactory.getSomeValueSnak(object.getPropertyId());
	}

	public NoValueSnak convert(NoValueSnak object) {
		return this.dataObjectFactory.getNoValueSnak(object.getPropertyId());
	}

	public SnakGroup convert(SnakGroup object) {
		return this.dataObjectFactory.getSnakGroup(object.getSnaks());
	}

	public Claim convert(Claim object) {
		return this.dataObjectFactory.getClaim(object.getSubject(),
				object.getMainSnak(), object.getQualifiers());
	}

	public Reference convert(Reference object) {
		return this.dataObjectFactory.getReference(object.getSnakGroups());
	}

	public Statement convert(Statement object) {
		return this.dataObjectFactory.getStatement(object.getClaim(),
				object.getReferences(), object.getRank(),
				object.getStatementId());
	}

	public StatementGroup convert(StatementGroup object) {
		return this.dataObjectFactory.getStatementGroup(object.getStatements());
	}

	public SiteLink convert(SiteLink object) {
		return this.dataObjectFactory.getSiteLink(object.getPageTitle(),
				object.getSiteKey(), object.getBadges());
	}

	public PropertyDocument convert(PropertyDocument object) {
		return this.dataObjectFactory.getPropertyDocument(object
				.getPropertyId(), new ArrayList<>(object.getLabels().values()),
				new ArrayList<>(object.getDescriptions().values()),
				convertAliasList(object.getAliases()), object.getDatatype());
	}

	public ItemDocument convert(ItemDocument object) {
		return this.dataObjectFactory.getItemDocument(object.getItemId(),
				new ArrayList<>(object.getLabels().values()), new ArrayList<>(
						object.getDescriptions().values()),
				convertAliasList(object.getAliases()), object
						.getStatementGroups(), object.getSiteLinks());
	}

	private List<MonolingualTextValue> convertAliasList(
			Map<String, List<MonolingualTextValue>> aliasMap) {
		List<MonolingualTextValue> aliases = new ArrayList<>();
		for (List<MonolingualTextValue> langAliases : aliasMap.values()) {
			aliases.addAll(langAliases);
		}
		return aliases;

	}

	public Snak convertSnak(Snak snak) {
		return snak.accept(this);
	}

	@Override
	public Snak visit(ValueSnak snak) {
		return convert(snak);
	}

	@Override
	public Snak visit(SomeValueSnak snak) {
		return convert(snak);
	}

	@Override
	public Snak visit(NoValueSnak snak) {
		return convert(snak);
	}

	public Value convertValue(Value value) {
		return value.accept(this);
	}

	@Override
	public Value visit(DatatypeIdValue value) {
		return convert(value);
	}

	@Override
	public Value visit(EntityIdValue value) {
		if (value instanceof ItemIdValue) {
			return convert((ItemIdValue) value);
		} else if (value instanceof PropertyIdValue) {
			return convert((ItemIdValue) value);
		} else {
			throw new UnsupportedOperationException(
					"Cannot convert entity id value: " + value.getClass());
		}
	}

	@Override
	public Value visit(GlobeCoordinatesValue value) {
		return convert(value);
	}

	@Override
	public Value visit(MonolingualTextValue value) {
		return convert(value);
	}

	@Override
	public Value visit(QuantityValue value) {
		return convert(value);
	}

	@Override
	public Value visit(StringValue value) {
		return convert(value);
	}

	@Override
	public Value visit(TimeValue value) {
		return convert(value);
	}

}
