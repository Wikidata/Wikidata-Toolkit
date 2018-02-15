package org.wikidata.wdtk.datamodel.implementation;

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


import org.wikidata.wdtk.datamodel.helpers.DatamodelConverter;
import org.wikidata.wdtk.datamodel.implementation.json.JacksonInnerEntityId;
import org.wikidata.wdtk.datamodel.implementation.json.JacksonInnerGlobeCoordinates;
import org.wikidata.wdtk.datamodel.implementation.json.JacksonInnerMonolingualText;
import org.wikidata.wdtk.datamodel.implementation.json.JacksonInnerTime;
import org.wikidata.wdtk.datamodel.implementation.json.JacksonPreStatement;
import org.wikidata.wdtk.datamodel.interfaces.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Factory implementation to create Jackson versions of the datamodel objects,
 * where available.
 *
 * @author Markus Kroetzsch
 *
 */
public class DataObjectFactoryImpl implements DataObjectFactory {

	private final DatamodelConverter dataModelConverter = new DatamodelConverter(
			this);

	@Override
	public ItemIdValue getItemIdValue(String id, String siteIri) {
		ItemIdValueImpl result = new ItemIdValueImpl(new JacksonInnerEntityId(id), siteIri);
		return result;
	}

	@Override
	public PropertyIdValue getPropertyIdValue(String id, String siteIri) {
		PropertyIdValueImpl result = new PropertyIdValueImpl(new JacksonInnerEntityId(id), siteIri);
		return result;
	}

	@Override
	public DatatypeIdValue getDatatypeIdValue(String id) {
		return new DatatypeIdImpl(id);
	}

	@Override
	public TimeValue getTimeValue(long year, byte month, byte day, byte hour,
			byte minute, byte second, byte precision, int beforeTolerance,
			int afterTolerance, int timezoneOffset, String calendarModel) {
		JacksonInnerTime innerTime = new JacksonInnerTime(year, month, day,
				hour, minute, second, timezoneOffset, beforeTolerance,
				afterTolerance, precision, calendarModel);
		TimeValueImpl result = new TimeValueImpl(innerTime);
		return result;
	}

	@Override
	public GlobeCoordinatesValue getGlobeCoordinatesValue(double latitude,
			double longitude, double precision, String globeIri) {
		if (precision <= 0) {
			throw new IllegalArgumentException(
					"Coordinates precision must be non-zero positive. Given value: "
							+ precision);
		}
		JacksonInnerGlobeCoordinates innerCoordinates = new JacksonInnerGlobeCoordinates(
				latitude,
				longitude,
				precision,
				globeIri);
		GlobeCoordinatesValueImpl result = new GlobeCoordinatesValueImpl(innerCoordinates);
		return result;
	}

	@Override
	public StringValue getStringValue(String string) {
		StringValueImpl result = new StringValueImpl(string);
		return result;
	}

	@Override
	public MonolingualTextValue getMonolingualTextValue(String text,
			String languageCode) {
		JacksonInnerMonolingualText innerMtlv = new JacksonInnerMonolingualText(
				languageCode, text);
		MonolingualTextValueImpl result = new MonolingualTextValueImpl(innerMtlv);
		return result;
	}


	@Override
	public QuantityValue getQuantityValue(BigDecimal numericValue) {
		return getQuantityValue(numericValue, null, null, "");
	}

	@Override
	public QuantityValue getQuantityValue(BigDecimal numericValue,
			BigDecimal lowerBound, BigDecimal upperBound) {
		return getQuantityValue(numericValue, lowerBound, upperBound, "");
	}

	@Override
	public QuantityValue getQuantityValue(BigDecimal numericValue, String unit) {
		return getQuantityValue(numericValue, null, null, unit);
	}

	@Override
	public QuantityValue getQuantityValue(BigDecimal numericValue,
			BigDecimal lowerBound, BigDecimal upperBound, String unit) {
		QuantityValueImpl result = new QuantityValueImpl(numericValue, lowerBound, upperBound, unit);
		return result;
	}

	/**
	 * Creates a {@link ValueSnakImpl}. Value snaks in JSON need to know the
	 * datatype of their property, which is not given in the parameters of this
	 * method. The snak that will be returned will use a default type based on
	 * the kind of value that is used (usually the "simplest" type for that
	 * value). This may not be desired.
	 *
	 * @see DataObjectFactory#getValueSnak(PropertyIdValue, Value)
	 */
	@Override
	public ValueSnak getValueSnak(PropertyIdValue propertyId, Value value) {
		return new ValueSnakImpl(propertyId, value);
	}

	@Override
	public SomeValueSnak getSomeValueSnak(PropertyIdValue propertyId) {
		SomeValueSnakImpl result = new SomeValueSnakImpl(
				propertyId.getId(),
				propertyId.getSiteIri());
		return result;
	}

	@Override
	public NoValueSnak getNoValueSnak(PropertyIdValue propertyId) {
		NoValueSnakImpl result = new NoValueSnakImpl(
				propertyId.getId(),
				propertyId.getSiteIri());
		return result;
	}

	@Override
	public SnakGroup getSnakGroup(List<? extends Snak> snaks) {
		List<Snak> snakList = new ArrayList<>(snaks.size());
		for(Snak snak : snaks) {
			snakList.add(snak);
		}
		return new SnakGroupImpl(snakList);
	}

	@Override
	public Claim getClaim(EntityIdValue subject, Snak mainSnak,
			List<SnakGroup> qualifiers) {
		// Jackson claims cannot exist without a statement.
		Statement statement = getStatement(
				subject, mainSnak, qualifiers,
				Collections.<Reference> emptyList(), StatementRank.NORMAL,
				"empty id 12345");
		return statement.getClaim();
	}

	@Override
	public Reference getReference(List<SnakGroup> snakGroups) {
		return new ReferenceImpl(snakGroups);
	}

	@Override
	public Statement getStatement(Claim claim,
			List<Reference> references, StatementRank rank,
			String statementId) {
		return getStatement(claim.getSubject(), claim.getMainSnak(), claim.getQualifiers(),
				references, rank, statementId);
	}
	
	private Statement getStatement(EntityIdValue subject, Snak mainSnak, List<SnakGroup> qualifiers,
			List<Reference> references, StatementRank rank, String statementId) {

		return new StatementImpl(statementId,
				rank, mainSnak, qualifiers,
				references, subject);
	}

	@Override
	public StatementGroup getStatementGroup(List<Statement> statements) {
		List<Statement> newStatements = new ArrayList<>(statements.size());
		for (Statement statement : statements) {
			if (statement instanceof JacksonPreStatement) {
				newStatements.add(statement);
			} else {
				newStatements.add(this.dataModelConverter.copy(statement));
			}
		}
		return new StatementGroupImpl(newStatements);
	}

	@Override
	public SiteLink getSiteLink(String title, String siteKey,
			List<String> badges) {
		SiteLinkImpl result = new SiteLinkImpl(
				title, siteKey, badges);
		return result;
	}

	@Override
	public PropertyDocument getPropertyDocument(PropertyIdValue propertyId,
			List<MonolingualTextValue> labels,
			List<MonolingualTextValue> descriptions,
			List<MonolingualTextValue> aliases, DatatypeIdValue datatypeId) {
		return getPropertyDocument(propertyId, labels, descriptions, aliases,
				Collections.<StatementGroup> emptyList(), datatypeId, 0);
	}

	@Override
	public PropertyDocument getPropertyDocument(PropertyIdValue propertyId,
			List<MonolingualTextValue> labels,
			List<MonolingualTextValue> descriptions,
			List<MonolingualTextValue> aliases,
			List<StatementGroup> statementGroups, DatatypeIdValue datatypeId) {
		return getPropertyDocument(propertyId, labels, descriptions, aliases,
				statementGroups, datatypeId, 0);
	}

	@Override
	public PropertyDocument getPropertyDocument(PropertyIdValue propertyId,
			List<MonolingualTextValue> labels,
			List<MonolingualTextValue> descriptions,
			List<MonolingualTextValue> aliases,
			List<StatementGroup> statementGroups, DatatypeIdValue datatypeId,
			long revisionId) {
		PropertyDocumentImpl result = new PropertyDocumentImpl(
				propertyId, labels, descriptions, aliases, statementGroups,
				datatypeId,	revisionId);
		return result;
	}

	@Override
	public ItemDocument getItemDocument(ItemIdValue itemIdValue,
			List<MonolingualTextValue> labels,
			List<MonolingualTextValue> descriptions,
			List<MonolingualTextValue> aliases,
			List<StatementGroup> statementGroups,
			Map<String, SiteLink> siteLinks) {
		return getItemDocument(itemIdValue, labels, descriptions, aliases,
				statementGroups, siteLinks, 0);
	}

	@Override
	public ItemDocument getItemDocument(ItemIdValue itemIdValue,
			List<MonolingualTextValue> labels,
			List<MonolingualTextValue> descriptions,
			List<MonolingualTextValue> aliases,
			List<StatementGroup> statementGroups,
			Map<String, SiteLink> siteLinks, long revisionId) {

		ItemDocumentImpl result = new ItemDocumentImpl(
				itemIdValue, labels, descriptions, aliases, statementGroups,
				siteLinks.values().stream().collect(Collectors.toList()), revisionId);

		return result;
	}
}
