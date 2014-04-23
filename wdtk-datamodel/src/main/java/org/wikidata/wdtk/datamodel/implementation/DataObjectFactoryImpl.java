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

import java.math.BigDecimal;
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
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

/**
 * Implementation of {@link DataObjectFactory} that uses the data object
 * implementations from this package.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class DataObjectFactoryImpl implements DataObjectFactory {

	@Override
	public ItemIdValue getItemIdValue(String id, String baseIri) {
		return new ItemIdValueImpl(id, baseIri);
	}

	@Override
	public PropertyIdValue getPropertyIdValue(String id, String baseIri) {
		return new PropertyIdValueImpl(id, baseIri);
	}

	@Override
	public DatatypeIdValue getDatatypeIdValue(String id) {
		return new DatatypeIdImpl(id);
	}

	@Override
	public TimeValue getTimeValue(long year, byte month, byte day, byte hour,
			byte minute, byte second, byte precision, int beforeTolerance,
			int afterTolerance, int timezoneOffset, String calendarModel) {
		return new TimeValueImpl(year, month, day, hour, minute, second,
				precision, beforeTolerance, afterTolerance, timezoneOffset,
				calendarModel);
	}

	@Override
	public GlobeCoordinatesValue getGlobeCoordinatesValue(long latitude,
			long longitude, long precision, String globeIri) {
		return new GlobeCoordinatesValueImpl(latitude, longitude, precision,
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
	public QuantityValue getQuantityValue(BigDecimal numericValue,
			BigDecimal lowerBound, BigDecimal upperBound) {
		return new QuantityValueImpl(numericValue, lowerBound, upperBound);
	}

	@Override
	public ValueSnak getValueSnak(PropertyIdValue propertyId, Value value) {
		return new ValueSnakImpl(propertyId, value);
	}

	@Override
	public SomeValueSnak getSomeValueSnak(PropertyIdValue propertyId) {
		return new SomeValueSnakImpl(propertyId);
	}

	@Override
	public NoValueSnak getNoValueSnak(PropertyIdValue propertyId) {
		return new NoValueSnakImpl(propertyId);
	}

	@Override
	public SnakGroup getSnakGroup(List<? extends Snak> snaks) {
		return new SnakGroupImpl(snaks);
	}

	@Override
	public Claim getClaim(EntityIdValue subject, Snak mainSnak,
			List<SnakGroup> qualifiers) {
		return new ClaimImpl(subject, mainSnak, qualifiers);
	}

	@Override
	public Reference getReference(List<SnakGroup> snakGroups) {
		return new ReferenceImpl(snakGroups);
	}

	@Override
	public Statement getStatement(Claim claim,
			List<? extends Reference> references, StatementRank rank,
			String statementId) {
		return new StatementImpl(claim, references, rank, statementId);
	}

	@Override
	public StatementGroup getStatementGroup(List<Statement> statements) {
		return new StatementGroupImpl(statements);
	}

	@Override
	public SiteLink getSiteLink(String title, String siteKey,
			List<String> badges) {
		return new SiteLinkImpl(title, siteKey, badges);
	}

	@Override
	public PropertyDocument getPropertyDocument(PropertyIdValue propertyId,
			List<MonolingualTextValue> labels,
			List<MonolingualTextValue> descriptions,
			List<MonolingualTextValue> aliases, DatatypeIdValue datatypeId) {
		return new PropertyDocumentImpl(propertyId, labels, descriptions,
				aliases, datatypeId);
	}

	@Override
	public ItemDocument getItemDocument(ItemIdValue itemIdValue,
			List<MonolingualTextValue> labels,
			List<MonolingualTextValue> descriptions,
			List<MonolingualTextValue> aliases,
			List<StatementGroup> statementGroups,
			Map<String, SiteLink> siteLinks) {
		return new ItemDocumentImpl(itemIdValue, labels, descriptions, aliases,
				statementGroups, siteLinks);
	}

}
