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


import org.wikidata.wdtk.datamodel.interfaces.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Factory implementation to create Jackson versions of the datamodel objects,
 * where available.
 *
 * @author Markus Kroetzsch
 *
 */
public class DataObjectFactoryImpl implements DataObjectFactory {

	@Override
	public ItemIdValue getItemIdValue(String id, String siteIri) {
		return new ItemIdValueImpl(id, siteIri);
	}

	@Override
	public PropertyIdValue getPropertyIdValue(String id, String siteIri) {
		return new PropertyIdValueImpl(id, siteIri);
	}

	@Override
	public LexemeIdValue getLexemeIdValue(String id, String siteIri) {
		return new LexemeIdValueImpl(id, siteIri);
	}

	@Override
	public FormIdValue getFormIdValue(String id, String siteIri) {
		return new FormIdValueImpl(id, siteIri);
	}

	@Override
	public SenseIdValue getSenseIdValue(String id, String siteIri) {
		return new SenseIdValueImpl(id, siteIri);
	}

	@Override
	public MediaInfoIdValue getMediaInfoIdValue(String id, String siteIri) {
		return new MediaInfoIdValueImpl(id, siteIri);
	}

	@Override
	public DatatypeIdValue getDatatypeIdValue(String id) {
		return new DatatypeIdImpl(id);
	}

	@Override
	public TimeValue getTimeValue(long year, byte month, byte day, byte hour,
			byte minute, byte second, byte precision, int beforeTolerance,
			int afterTolerance, int timezoneOffset, String calendarModel) {
		return new TimeValueImpl(year, month, day,
				hour, minute, second, precision, beforeTolerance,
				afterTolerance, timezoneOffset, calendarModel);
	}

	@Override
	public GlobeCoordinatesValue getGlobeCoordinatesValue(double latitude,
			double longitude, double precision, String globeIri) {
		if (precision <= 0) {
			throw new IllegalArgumentException(
					"Coordinates precision must be non-zero positive. Given value: "
							+ precision);
		}
		return new GlobeCoordinatesValueImpl(latitude,
				longitude,
				precision,
				globeIri);
	}

	@Override
	public StringValue getStringValue(String string) {
		return new StringValueImpl(string);
	}

	@Override
	public MonolingualTextValue getMonolingualTextValue(String text,
			String languageCode) {
		return new MonolingualTextValueImpl(text, languageCode);
	}


	@Override
	public QuantityValue getQuantityValue(BigDecimal numericValue) {
		return getQuantityValue(numericValue, null, null, "1");
	}

	@Override
	public QuantityValue getQuantityValue(BigDecimal numericValue,
			BigDecimal lowerBound, BigDecimal upperBound) {
		return getQuantityValue(numericValue, lowerBound, upperBound, "1");
	}

	@Override
	public QuantityValue getQuantityValue(BigDecimal numericValue, String unit) {
		return getQuantityValue(numericValue, null, null, unit);
	}

	@Override
	public QuantityValue getQuantityValue(BigDecimal numericValue,
			BigDecimal lowerBound, BigDecimal upperBound, String unit) {
		return new QuantityValueImpl(numericValue, lowerBound, upperBound, unit);
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
		return new SomeValueSnakImpl(
				propertyId.getId(),
				propertyId.getSiteIri());
	}

	@Override
	public NoValueSnak getNoValueSnak(PropertyIdValue propertyId) {
		return new NoValueSnakImpl(
				propertyId.getId(),
				propertyId.getSiteIri());
	}

	@Override
	public SnakGroup getSnakGroup(List<? extends Snak> snaks) {
		return new SnakGroupImpl(new ArrayList<>(snaks));
	}

	@Override
	public Claim getClaim(EntityIdValue subject, Snak mainSnak,
			List<SnakGroup> qualifiers) {
		// Jackson claims cannot exist without a statement.
		return getStatement(
				subject, mainSnak, qualifiers,
				Collections. emptyList(), StatementRank.NORMAL,
				"empty id 12345").getClaim();
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

	@Override
	public Statement getStatement(EntityIdValue subject, Snak mainSnak, List<SnakGroup> qualifiers,
			List<Reference> references, StatementRank rank, String statementId) {
		return new StatementImpl(statementId,
				rank, mainSnak, qualifiers,
				references, subject);
	}

	@Override
	public StatementGroup getStatementGroup(List<Statement> statements) {
		return new StatementGroupImpl(statements);
	}

	@Override
	public SiteLink getSiteLink(String title, String siteKey,
			List<ItemIdValue> badges) {
		return new SiteLinkImpl(title, siteKey, badges);
	}

	@Override
	public PropertyDocument getPropertyDocument(PropertyIdValue propertyId,
			List<MonolingualTextValue> labels,
			List<MonolingualTextValue> descriptions,
			List<MonolingualTextValue> aliases,
			List<StatementGroup> statementGroups, DatatypeIdValue datatypeId,
			long revisionId) {
		return new PropertyDocumentImpl(
				propertyId, labels, descriptions, aliases, statementGroups,
				datatypeId,	revisionId);
	}

	@Override
	public ItemDocument getItemDocument(ItemIdValue itemIdValue,
			List<MonolingualTextValue> labels,
			List<MonolingualTextValue> descriptions,
			List<MonolingualTextValue> aliases,
			List<StatementGroup> statementGroups,
			Map<String, SiteLink> siteLinks, long revisionId) {

		return new ItemDocumentImpl(
				itemIdValue, labels, descriptions, aliases, statementGroups,
				new ArrayList<>(siteLinks.values()), revisionId);
	}

	@Override
	public LexemeDocument getLexemeDocument(LexemeIdValue lexemeIdValue,
			ItemIdValue lexicalCategory,
			ItemIdValue language,
			List<MonolingualTextValue> lemmas,
			List<StatementGroup> statementGroups,
			List<FormDocument> forms,
			List<SenseDocument> senses,
			long revisionId) {
		return new LexemeDocumentImpl(lexemeIdValue, lexicalCategory, language, lemmas, statementGroups, forms, senses, revisionId);
	}

	@Override
	public FormDocument getFormDocument(FormIdValue formIdValue,
			List<MonolingualTextValue> representations,
			List<ItemIdValue> grammaticalFeatures,
			List<StatementGroup> statementGroups,
			long revisionId) {
		return new FormDocumentImpl(formIdValue, representations, grammaticalFeatures, statementGroups, revisionId);
	}

	@Override
	public SenseDocument getSenseDocument(SenseIdValue senseIdValue,
										List<MonolingualTextValue> glosses,
										List<StatementGroup> statementGroups,
										long revisionId) {
		return new SenseDocumentImpl(senseIdValue, glosses, statementGroups, revisionId);
	}


	@Override
	public MediaInfoDocument getMediaInfoDocument(MediaInfoIdValue mediaInfoIdValue,
										List<MonolingualTextValue> labels,
										List<StatementGroup> statementGroups,
										long revisionId) {

		return new MediaInfoDocumentImpl(
				mediaInfoIdValue, labels, statementGroups, revisionId);
	}
}
