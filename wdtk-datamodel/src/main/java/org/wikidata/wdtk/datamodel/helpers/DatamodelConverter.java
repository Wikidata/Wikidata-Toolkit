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
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;
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

	private final DataObjectFactory dataObjectFactory;

	/**
	 * Constructor.
	 *
	 * @param dataObjectFactory
	 *            the factory to use for creating new objects
	 */
	public DatamodelConverter(DataObjectFactory dataObjectFactory) {
		this.dataObjectFactory = dataObjectFactory;
	}

	public ItemIdValue copy(ItemIdValue object) {
		return this.dataObjectFactory.getItemIdValue(object.getId(),
				object.getSiteIri());
	}

	public PropertyIdValue copy(PropertyIdValue object) {
		return this.dataObjectFactory.getPropertyIdValue(object.getId(),
				object.getSiteIri());
	}

	public DatatypeIdValue copy(DatatypeIdValue object) {
		return this.dataObjectFactory.getDatatypeIdValue(object.getIri());
	}

	public TimeValue copy(TimeValue object) {
		return this.dataObjectFactory.getTimeValue(object.getYear(),
				object.getMonth(), object.getDay(), object.getHour(),
				object.getMinute(), object.getSecond(), object.getPrecision(),
				object.getBeforeTolerance(), object.getAfterTolerance(),
				object.getTimezoneOffset(), object.getPreferredCalendarModel());
	}

	public GlobeCoordinatesValue copy(GlobeCoordinatesValue object) {
		return this.dataObjectFactory.getGlobeCoordinatesValue(
				object.getLatitude(), object.getLongitude(),
				object.getPrecision(), object.getGlobe());
	}

	public StringValue copy(StringValue object) {
		return this.dataObjectFactory.getStringValue(object.getString());
	}

	public MonolingualTextValue copy(MonolingualTextValue object) {
		return this.dataObjectFactory.getMonolingualTextValue(object.getText(),
				object.getLanguageCode());
	}

	public QuantityValue copy(QuantityValue object) {
		return this.dataObjectFactory.getQuantityValue(
				object.getNumericValue(), object.getLowerBound(),
				object.getUpperBound());
	}

	public ValueSnak copy(ValueSnak object) {
		return this.dataObjectFactory.getValueSnak(object.getPropertyId(),
				object.getValue());
	}

	public SomeValueSnak copy(SomeValueSnak object) {
		return this.dataObjectFactory.getSomeValueSnak(object.getPropertyId());
	}

	public NoValueSnak copy(NoValueSnak object) {
		return this.dataObjectFactory.getNoValueSnak(object.getPropertyId());
	}

	public SnakGroup copy(SnakGroup object) {
		return this.dataObjectFactory.getSnakGroup(object.getSnaks());
	}

	public Claim copy(Claim object) {
		return this.dataObjectFactory.getClaim(object.getSubject(),
				object.getMainSnak(), object.getQualifiers());
	}

	public Reference copy(Reference object) {
		return this.dataObjectFactory.getReference(object.getSnakGroups());
	}

	public Statement copy(Statement object) {
		return this.dataObjectFactory.getStatement(object.getClaim(),
				object.getReferences(), object.getRank(),
				object.getStatementId());
	}

	public StatementGroup copy(StatementGroup object) {
		return this.dataObjectFactory.getStatementGroup(object.getStatements());
	}

	public SiteLink copy(SiteLink object) {
		return this.dataObjectFactory.getSiteLink(object.getPageTitle(),
				object.getSiteKey(), object.getBadges());
	}

	public PropertyDocument copy(PropertyDocument object) {
		return this.dataObjectFactory.getPropertyDocument(object
				.getPropertyId(), new ArrayList<>(object.getLabels().values()),
				new ArrayList<>(object.getDescriptions().values()),
				convertAliasList(object.getAliases()), object
						.getStatementGroups(), object.getDatatype());
	}

	public ItemDocument copy(ItemDocument object) {

		return this.dataObjectFactory.getItemDocument(object.getItemId(),
				new ArrayList<>(object.getLabels().values()), new ArrayList<>(
						object.getDescriptions().values()),
				convertAliasList(object.getAliases()), object
						.getStatementGroups(), object.getSiteLinks());
	}

	public ItemDocument deepCopy(ItemDocument object) {

		ItemIdValue convertedItemId = this.copy(object.getItemId());
		ArrayList<MonolingualTextValue> convertedLabels = new ArrayList<>(
				object.getLabels().values());
		ArrayList<MonolingualTextValue> convertedDescriptions = new ArrayList<>(
				object.getDescriptions().values());
		List<MonolingualTextValue> convertedAliases = convertAliasList(object
				.getAliases());
		List<StatementGroup> convertedStatementGroups = new ArrayList<>();
		for (StatementGroup statementGroup : object.getStatementGroups()) {
			convertedStatementGroups.add(this
					.deepCopyStatementGroup(statementGroup));
		}
		Map<String, SiteLink> convertedSiteLinks = object.getSiteLinks();

		return this.dataObjectFactory.getItemDocument(convertedItemId,
				convertedLabels, convertedDescriptions, convertedAliases,
				convertedStatementGroups, convertedSiteLinks);
	}

	private StatementGroup deepCopyStatementGroup(StatementGroup statementGroup) {

		List<Statement> statements = new ArrayList<>();
		for (Statement statement : statementGroup.getStatements()) {
			statements.add(this.deepCopyStatement(statement));
		}
		return this.dataObjectFactory.getStatementGroup(statements);
	}

	private Statement deepCopyStatement(Statement statement) {

		Claim claim = this.deepCopyClaim(statement.getClaim());
		List<? extends Reference> references = this
				.deepCopyReferences(statement.getReferences());
		StatementRank rank = statement.getRank();
		String statementId = statement.getStatementId();

		// FIXME this is a hack
		if (statementId == null) {
			statementId = "NoStatementId";
		}

		return this.dataObjectFactory.getStatement(claim, references, rank,
				statementId);
	}

	private Claim deepCopyClaim(Claim claim) {
		List<SnakGroup> qualifiers = new ArrayList<>();
		for (SnakGroup snakGroup : claim.getQualifiers()) {
			qualifiers.add(this.deepCopySnakGroup(snakGroup));
		}
		EntityIdValue subject = deepCopyEntityIdValue(claim.getSubject());
		return this.dataObjectFactory.getClaim(subject,
				deepCopySnak(claim.getMainSnak()), qualifiers);
	}

	private EntityIdValue deepCopyEntityIdValue(EntityIdValue subject) {
		if (subject instanceof ItemIdValue) {
			return (EntityIdValue) this.deepCopyItemIdValue(subject);
		} else if (subject instanceof PropertyIdValue) {
			// TODO
		}
		return subject;
	}

	private List<Reference> deepCopyReferences(
			List<? extends Reference> references) {

		List<Reference> result = new ArrayList<>();
		for (Reference reference : references) {
			List<SnakGroup> snakGroups = new ArrayList<>();
			for (SnakGroup snakGroup : reference.getSnakGroups()) {
				snakGroups.add(this.deepCopySnakGroup(snakGroup));
			}
			result.add(this.dataObjectFactory.getReference(snakGroups));
		}
		return result;
	}

	private SnakGroup deepCopySnakGroup(SnakGroup snakGroup) {

		List<Snak> snaks = new ArrayList<>();
		for (Snak snak : snakGroup.getSnaks()) {

			Snak intermediate = deepCopySnak(snak);
			if (intermediate != null) {
				snaks.add(intermediate);
			}
		}

		return this.dataObjectFactory.getSnakGroup(snaks);
	}

	private Snak deepCopySnak(Snak snak) {
		if (snak instanceof NoValueSnak) {
			return this.dataObjectFactory.getNoValueSnak(snak.getPropertyId());
		} else if (snak instanceof SomeValueSnak) {
			return this.dataObjectFactory
					.getSomeValueSnak(snak.getPropertyId());
		} else {
			Value convertedValue = this.deepCopyValue(((ValueSnak) snak)
					.getValue());
			if (convertedValue != null) {
				return this.dataObjectFactory.getValueSnak(
						snak.getPropertyId(), convertedValue);
			}
		}
		return null;
	}

	private Value deepCopyValue(Value value) {
		// TODO more?
		if (value instanceof StringValue) {
			return deepCopyStringValue(value);
		} else if (value instanceof GlobeCoordinatesValue) {
			return deepCopyGlobeCoordinatesValue(value);
		} else if (value instanceof QuantityValue) {
			return deepCopyQuantityValue(value);
		} else if (value instanceof TimeValue) {
			return deepCopyTimeValue(value);
		} else if (value instanceof MonolingualTextValue) {
			return deepCopyMonolingualText(value);
		} else if (value instanceof ItemIdValue) {
			return deepCopyItemIdValue(value);
		}
		return null;
	}

	private Value deepCopyItemIdValue(Value value) {
		ItemIdValue iValue = (ItemIdValue) value;
		return this.dataObjectFactory.getItemIdValue(iValue.getId(),
				iValue.getSiteIri());
	}

	private Value deepCopyMonolingualText(Value value) {
		MonolingualTextValue mltValue = (MonolingualTextValue) value;
		return this.dataObjectFactory.getMonolingualTextValue(
				mltValue.getText(), mltValue.getLanguageCode());
	}

	private Value deepCopyTimeValue(Value value) {
		TimeValue tValue = (TimeValue) value;
		return this.dataObjectFactory.getTimeValue(tValue.getYear(),
				tValue.getMonth(), tValue.getDay(), tValue.getHour(),
				tValue.getMinute(), tValue.getSecond(), tValue.getPrecision(),
				tValue.getBeforeTolerance(), tValue.getAfterTolerance(),
				tValue.getTimezoneOffset(), tValue.getPreferredCalendarModel());
	}

	private Value deepCopyQuantityValue(Value value) {
		QuantityValue qValue = (QuantityValue) value;
		return this.dataObjectFactory.getQuantityValue(
				qValue.getNumericValue(), qValue.getLowerBound(),
				qValue.getUpperBound());
	}

	private Value deepCopyGlobeCoordinatesValue(Value value) {
		GlobeCoordinatesValue gValue = (GlobeCoordinatesValue) value;
		return this.dataObjectFactory.getGlobeCoordinatesValue(
				gValue.getLatitude(), gValue.getLongitude(),
				gValue.getPrecision(), gValue.getGlobe());
	}

	private Value deepCopyStringValue(Value value) {
		return this.dataObjectFactory.getStringValue(((StringValue) value)
				.getString());
	}

	private List<MonolingualTextValue> convertAliasList(
			Map<String, List<MonolingualTextValue>> aliasMap) {
		List<MonolingualTextValue> aliases = new ArrayList<>();
		for (List<MonolingualTextValue> langAliases : aliasMap.values()) {
			aliases.addAll(langAliases);
		}
		return aliases;

	}

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

}
