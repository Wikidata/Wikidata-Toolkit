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
	 * Create an {@link ItemIdValue}.
	 * 
	 * @param id
	 *            the ID string, e.g., "Q1234"
	 * @param baseIri
	 *            the first part of the IRI of the site this belongs to, e.g.,
	 *            "http://www.wikidata.org/entity/"
	 * @return an {@link ItemIdValue} corresponding to the input
	 */
	ItemIdValue getItemIdValue(String id, String baseIri);

	/**
	 * Create a {@link PropertyIdValue}.
	 * 
	 * @param id
	 *            the ID string, e.g., "P1234"
	 * @param baseIri
	 *            the first part of the IRI of the site this belongs to, e.g.,
	 *            "http://www.wikidata.org/entity/"
	 * @return a {@link PropertyIdValue} corresponding to the input
	 */
	PropertyIdValue getPropertyIdValue(String id, String baseIri);

	/**
	 * Create a {@link DatatypeIdValue}. The datatype IRI is usually one of the
	 * constants defined in {@link DatatypeIdValue}, but this is not enforced,
	 * since there might be extensions that provide additional types.
	 * 
	 * @param datatypeIri
	 *            the IRI string that identifies the datatype
	 * @return a {@link DatatypeIdValue} corresponding to the input
	 */
	DatatypeIdValue getDatatypeIdValue(String id);

	/**
	 * Create a {@link TimeValue}.
	 * 
	 * @param year
	 *            a year number, where 0 refers to 1BCE
	 * @param month
	 *            a month number between 1 and 12
	 * @param day
	 *            a day number between 1 and 31
	 * @param hour
	 *            an hour number between 0 and 23
	 * @param minute
	 *            a minute number between 0 and 59
	 * @param second
	 *            a second number between 0 and 60 (possible leap second)
	 * @param precision
	 *            a value in the range of {@link TimeValue#PREC_DAY}, ...,
	 *            {@link TimeValue#PREC_GY}
	 * @param beforeTolerance
	 *            non-negative integer tolerance before the value; see
	 *            {@link TimeValue#getBeforeTolerance()}
	 * @param afterTolerance
	 *            non-zero, positive integer tolerance before the value; see
	 *            {@link TimeValue#getAfterTolerance()}
	 * @param calendarModel
	 *            the IRI of the calendar model preferred when displaying the
	 *            date; usually {@link TimeValue#CM_GREGORIAN_PRO} or
	 *            {@link TimeValue#CM_JULIAN_PRO}
	 * @param timezoneOffset
	 *            offset in minutes that should be applied when displaying this
	 *            time
	 * @return a {@link DatatypeIdValue} corresponding to the input
	 */
	TimeValue getTimeValue(int year, byte month, byte day, byte hour,
			byte minute, byte second, byte precision, int beforeTolerance,
			int afterTolerance, int timezoneOffset, String calendarModel);

	/**
	 * Create a {@link GlobeCoordinatesValue}.
	 * 
	 * @param latitude
	 *            the latitude of the coordinates in nanodegrees
	 * @param longitude
	 *            the longitude of the coordinates in nanodegrees
	 * @param precision
	 *            the precision of the coordinates in nanodegrees
	 * @param globeIri
	 *            IRI specifying the celestial objects of the coordinates
	 * @return a {@link GlobeCoordinatesValue} corresponding to the input
	 */
	GlobeCoordinatesValue getGlobeCoordinatesValue(long latitude,
			long longitude, long precision, String globeIri);

	/**
	 * Create a {@link StringValue}.
	 * 
	 * @param string
	 * @return a {@link StringValue} corresponding to the input
	 */
	StringValue getStringValue(String string);

	/**
	 * Create a {@link MonolingualTextValue}.
	 * 
	 * @param text
	 *            the text of the value
	 * @param languageCode
	 *            the language code of the value
	 * @return a {@link MonolingualValue} corresponding to the input
	 */
	MonolingualTextValue getMonolingualTextValue(String text,
			String languageCode);

	/**
	 * Create a {@link QuantityValue}.
	 * 
	 * @param numericValue
	 *            the numeric value of this quantity
	 * @param lowerBound
	 *            the lower bound of the numeric value of this quantity
	 * @param upperBound
	 *            the upper bound of the numeric value of this quantity
	 * @return a {@link QuantityValue} corresponding to the input
	 */
	public QuantityValue getQuantityValue(BigDecimal numericValue,
			BigDecimal lowerBound, BigDecimal upperBound);

	/**
	 * Create a {@link ValueSnak}.
	 * 
	 * @param propertyId
	 * @param value
	 * @return a {@link ValueSnak} corresponding to the input
	 */
	ValueSnak getValueSnak(PropertyIdValue propertyId, Value value);

	/**
	 * Create a {@link SomeValueSnak}.
	 * 
	 * @param propertyId
	 * @return a {@link SomeValueSnak} corresponding to the input
	 */
	SomeValueSnak getSomeValueSnak(PropertyIdValue propertyId);

	/**
	 * Create a {@link NoValueSnak}.
	 * 
	 * @param propertyId
	 * @return a {@link NoValueSnak} corresponding to the input
	 */
	NoValueSnak getNoValueSnak(PropertyIdValue propertyId);

	/**
	 * Create a {@link Claim}.
	 * 
	 * @param subject
	 *            the subject the Statement refers to
	 * @param mainSnak
	 *            the main Snak of the Statement
	 * @param qualifiers
	 *            the qualifiers of the Statement
	 * @return a {@link Claim} corresponding to the input
	 */
	Claim getClaim(EntityIdValue subject, Snak mainSnak,
			List<? extends Snak> qualifiers);

	/**
	 * Create a {@link Reference}.
	 * 
	 * @param valueSnaks
	 *            list of property-value pairs
	 * @return a {@link Reference} corresponding to the input
	 */
	Reference getReference(List<? extends ValueSnak> valueSnaks);

	/**
	 * Create a {@link Statement}.
	 * 
	 * @param claim
	 *            the main claim the Statement refers to
	 * @param references
	 *            the references for the Statement
	 * @param rank
	 *            the rank of the Statement
	 * @return a {@link Statement} corresponding to the input
	 */
	Statement getStatement(Claim claim, List<? extends Reference> references,
			StatementRank rank);

	/**
	 * Create a {@link StatementGroup}.
	 * 
	 * @param statements
	 *            a non-empty list of statements that use the same subject and
	 *            main-snak property in their claim
	 * @return a {@link StatementGroup} corresponding to the input
	 */
	StatementGroup getStatementGroup(List<Statement> statements);

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
	SiteLink getSiteLink(String title, String siteKey, String baseIri,
			List<String> badges);

	/**
	 * Create a {@link PropertyDocument}.
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
	 * @return a {@link PropertyDocument} corresponding to the input
	 */
	PropertyDocument getPropertyDocument(PropertyIdValue propertyId,
			Map<String, MonolingualTextValue> labels,
			Map<String, MonolingualTextValue> descriptions,
			Map<String, List<MonolingualTextValue>> aliases,
			DatatypeIdValue datatypeId);

	/**
	 * Create an {@link ItemDocument}.
	 * 
	 * @param itemIdValue
	 *            the id of the item that data is about
	 * @param labels
	 *            the labels of this item by language code
	 * @param descriptions
	 *            the descriptions of this item by language code
	 * @param aliases
	 *            the alias lists of this item by language code
	 * @param statementGroups
	 *            the list of statement groups of this item; all of them must
	 *            have the given itemIdValue as their subject
	 * @param siteLinks
	 *            the sitelinks of this item by site key
	 * @return an {@link ItemDocument} corresponding to the input
	 */
	ItemDocument getItemDocument(ItemIdValue itemIdValue,
			Map<String, MonolingualTextValue> labels,
			Map<String, MonolingualTextValue> descriptions,
			Map<String, List<MonolingualTextValue>> aliases,
			List<StatementGroup> statementGroups,
			Map<String, SiteLink> siteLinks);

}
