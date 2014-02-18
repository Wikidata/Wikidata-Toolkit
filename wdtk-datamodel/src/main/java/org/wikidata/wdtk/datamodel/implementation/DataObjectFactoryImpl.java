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

import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeId;
import org.wikidata.wdtk.datamodel.interfaces.EntityId;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemId;
import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyId;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
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
	public ItemId getItemId(String id, String baseIri) {
		return new ItemIdImpl(id, baseIri);
	}

	@Override
	public PropertyId getPropertyId(String id, String baseIri) {
		return new PropertyIdImpl(id, baseIri);
	}

	@Override
	public DatatypeId getDatatypeId(String id) {
		return new DatatypeIdImpl(id);
	}

	@Override
	public UrlValue getUrlValue(String url) {
		return new UrlValueImpl(url);
	}

	@Override
	public TimeValue getTimeValue(int year, byte month, byte day, byte hour,
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
	public QuantityValue getQuantityValue(BigDecimal numericValue,
			BigDecimal lowerBound, BigDecimal upperBound) {
		return new QuantityValueImpl(numericValue, lowerBound, upperBound);
	}

	@Override
	public ValueSnak getValueSnak(PropertyId propertyId, Value value) {
		return new ValueSnakImpl(propertyId, value);
	}

	@Override
	public SomeValueSnak getSomeValueSnak(PropertyId propertyId) {
		return new SomeValueSnakImpl(propertyId);
	}

	@Override
	public NoValueSnak getNoValueSnak(PropertyId propertyId) {
		return new NoValueSnakImpl(propertyId);
	}

	@Override
	public Statement getStatement(EntityId subject, Snak mainSnak,
			List<? extends Snak> qualifiers,
			List<List<? extends Snak>> references, StatementRank rank) {
		return new StatementImpl(subject, mainSnak, qualifiers, references,
				rank);
	}

	@Override
	public SiteLink getSiteLink(String title, String siteKey, String baseIri,
			List<String> badges) {
		return new SiteLinkImpl(title, siteKey, baseIri, badges);
	}

	@Override
	public PropertyDocument getPropertyDocument(PropertyId propertyId,
			Map<String, String> labels, Map<String, String> descriptions,
			Map<String, List<String>> aliases, DatatypeId datatypeId) {
		return new PropertyDocumentImpl(propertyId, labels, descriptions,
				aliases, datatypeId);
	}

	@Override
	public ItemDocument getItemDocument(ItemId itemId,
			Map<String, String> labels, Map<String, String> descriptions,
			Map<String, List<String>> aliases, List<Statement> statements,
			Map<String, SiteLink> siteLinks) {
		return new ItemDocumentImpl(itemId, labels, descriptions, aliases,
				statements, siteLinks);
	}

}
