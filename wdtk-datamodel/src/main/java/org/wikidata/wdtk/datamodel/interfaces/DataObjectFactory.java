package org.wikidata.wdtk.datamodel.interfaces;

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

/**
 * Interface for factories that create data objects that implement the
 * interfaces from this package.
 * 
 * @author Markus Kroetzsch
 * 
 */
public interface DataObjectFactory {

	/**
	 * Create an {@link ItemId}.
	 * 
	 * @param id
	 *            the ID string, e.g., "Q1234"
	 * @param baseIri
	 *            the first part of the IRI of the site this belongs to, e.g.,
	 *            "http://www.wikidata.org/entity/"
	 * @return an {@link ItemId} corresponding to the input
	 */
	public ItemId getItemId(String id, String baseIri);

	/**
	 * Create a {@link PropertyId}.
	 * 
	 * @param id
	 *            the ID string, e.g., "P1234"
	 * @param baseIri
	 *            the first part of the IRI of the site this belongs to, e.g.,
	 *            "http://www.wikidata.org/entity/"
	 * @return a {@link PropertyId} corresponding to the input
	 */
	public PropertyId getPropertyId(String id, String baseIri);

	/**
	 * Create a {@link DatatypeId}. The datatype IRI is usually one of the
	 * constants defined in {@link DatatypeId}, but this is not enforced, since
	 * there might be extensions that provide additional types.
	 * 
	 * @param datatypeIri
	 *            the IRI string that identifies the datatype
	 * @return a {@link DatatypeId} corresponding to the input
	 */
	public DatatypeId getDatatypeId(String id);

	/**
	 * Create a {@link UrlValue}.
	 * 
	 * @param url
	 * @return a {@link UrlValue} corresponding to the input
	 */
	public UrlValue getUrlValue(String url);

	/**
	 * Create a {@link TimeValue}.
	 * 
	 * @param year
	 *            a year number, where 0 refers to 1BCE
	 * @param month
	 *            a month number between 1 and 12
	 * @param day
	 *            a day number between 1 and 31
	 * @param precision
	 *            a value in the range of {@link TimeValue#PREC_DAY}, ...,
	 *            {@link TimeValue#PREC_GY}
	 * @param calendarModel
	 *            the IRI of the calendar model preferred when displaying the
	 *            date; usually {@link TimeValue#CM_GREGORIAN_PRO} or
	 *            {@link TimeValue#CM_JULIAN_PRO}
	 * @return a {@link DatatypeId} corresponding to the input
	 */
	public TimeValue getTimeValue(int year, byte month, byte day,
			byte precision, String calendarModel);

	/**
	 * Create a {@link GlobeCoordinatesValue}.
	 * 
	 * @param latitude
	 *            the latitude of the coordinates in degrees
	 * @param longitude
	 *            the longitude of the coordinates in degrees
	 * @param precision
	 *            the precision of the coordinates in degrees
	 * @param globeIri
	 *            IRI specifying the celestial objects of the coordinates
	 * @return a {@link GlobeCoordinatesValue} corresponding to the input
	 */
	public GlobeCoordinatesValue getGlobeCoordinatesValue(double latitude,
			double longitude, double precision, String globeIri);

	/**
	 * Create a {@link StringValue}.
	 * 
	 * @param string
	 * @return a {@link StringValue} corresponding to the input
	 */
	public StringValue getStringValue(String string);

	/**
	 * Create a {@link QuantityValue}.
	 * 
	 * @param numericValue
	 *            the numeric value of this quantity
	 * @return a {@link QuantityValue} corresponding to the input
	 */
	public QuantityValue getQuantityValue(BigDecimal numericValue);

	/**
	 * Create a {@link ValueSnak}.
	 * 
	 * @param propertyId
	 * @param value
	 * @return a {@link ValueSnak} corresponding to the input
	 */
	public ValueSnak getValueSnak(PropertyId propertyId, Value value);

	/**
	 * Create a {@link SomeValueSnak}.
	 * 
	 * @param propertyId
	 * @return a {@link SomeValueSnak} corresponding to the input
	 */
	public SomeValueSnak getSomeValueSnak(PropertyId propertyId);

	/**
	 * Create a {@link NoValueSnak}.
	 * 
	 * @param propertyId
	 * @return a {@link NoValueSnak} corresponding to the input
	 */
	public NoValueSnak getNoValueSnak(PropertyId propertyId);

	/**
	 * Create a {@link Statement}.
	 * 
	 * @param subject
	 *            the subject the Statement refers to
	 * @param mainSnak
	 *            the main Snak of the Statement
	 * @param qualifiers
	 *            the qualifiers of the Statement
	 * @param references
	 *            the references for the Statement
	 * @param rank
	 *            the rank of the Statement
	 * @return a {@link Statement} corresponding to the input
	 */
	public Statement getStatement(EntityId subject, Snak mainSnak,
			List<? extends Snak> qualifiers,
			List<List<? extends Snak>> references, StatementRank rank);

	/**
	 * Create a {@link SiteLink}.
	 * 
	 * @param title
	 *            the title string of the linked article
	 * @param siteKey
	 *            the string key of the site of the linked article
	 * @param baseIri
	 *            the string key of the site of the linked article; this might
	 *            be computed from the site key in the future
	 * @param badges
	 *            the list of badges of the linked article
	 * @return a {@link SiteLink} corresponding to the input
	 */
	public SiteLink getSiteLink(String title, String siteKey, String baseIri,
			List<String> badges);

	/**
	 * Create a {@link PropertyRecord}.
	 * 
	 * @param propertyId
	 *            the id of the property that data is about
	 * @param labels
	 *            the labels of this property by language code
	 * @param descriptions
	 *            the descriptions of this property by language code
	 * @param aliases
	 *            the alias lists of this property by language code
	 * @param datatypeId
	 *            the datatype of that property
	 * @return a {@link PropertyRecord} corresponding to the input
	 */
	public PropertyRecord getPropertyRecord(PropertyId propertyId,
			Map<String, String> labels, Map<String, String> descriptions,
			Map<String, List<String>> aliases, DatatypeId datatypeId);

	/**
	 * Create an {@link ItemRecord}.
	 * 
	 * @param itemId
	 *            the id of the item that data is about
	 * @param labels
	 *            the labels of this item by language code
	 * @param descriptions
	 *            the descriptions of this item by language code
	 * @param aliases
	 *            the alias lists of this item by language code
	 * @param statements
	 *            the list of statements of this item
	 * @param siteLinks
	 *            the sitelinks of this item by site key
	 * @return an {@link ItemRecord} corresponding to the input
	 */
	public ItemRecord getItemRecord(ItemId itemId, Map<String, String> labels,
			Map<String, String> descriptions,
			Map<String, List<String>> aliases, List<Statement> statements,
			Map<String, SiteLink> siteLinks);

}
