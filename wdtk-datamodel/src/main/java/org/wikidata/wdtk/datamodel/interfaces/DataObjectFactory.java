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
	 * Creates an {@link ItemIdValue}.
	 * 
	 * @param id
	 *            a string of the form Qn... where n... is the string
	 *            representation of a positive integer number
	 * @param baseIri
	 *            the first part of the entity IRI of the site this belongs to,
	 *            e.g., "http://www.wikidata.org/entity/"
	 * @return an {@link ItemIdValue} corresponding to the input
	 */
	ItemIdValue getItemIdValue(String id, String baseIri);

	/**
	 * Creates a {@link PropertyIdValue}.
	 * 
	 * @param id
	 *            a string of the form Pn... where n... is the string
	 *            representation of a positive integer number
	 * @param baseIri
	 *            the first part of the entity IRI of the site this belongs to,
	 *            e.g., "http://www.wikidata.org/entity/"
	 * @return a {@link PropertyIdValue} corresponding to the input
	 */
	PropertyIdValue getPropertyIdValue(String id, String baseIri);

	/**
	 * Creates a {@link DatatypeIdValue}. The datatype IRI is usually one of the
	 * constants defined in {@link DatatypeIdValue}, but this is not enforced,
	 * since there might be extensions that provide additional types.
	 * 
	 * @param datatypeIri
	 *            the IRI string that identifies the datatype
	 * @return a {@link DatatypeIdValue} corresponding to the input
	 */
	DatatypeIdValue getDatatypeIdValue(String id);

	/**
	 * Creates a {@link TimeValue}.
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
	 * @return a {@link TimeValue} corresponding to the input
	 */
	TimeValue getTimeValue(long year, byte month, byte day, byte hour,
			byte minute, byte second, byte precision, int beforeTolerance,
			int afterTolerance, int timezoneOffset, String calendarModel);

	/**
	 * Creates a {@link GlobeCoordinatesValue}.
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
	 * Creates a {@link StringValue}.
	 * 
	 * @param string
	 * @return a {@link StringValue} corresponding to the input
	 */
	StringValue getStringValue(String string);

	/**
	 * Creates a {@link MonolingualTextValue}.
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
	 * Creates a {@link QuantityValue}.
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
	 * Creates a {@link ValueSnak}.
	 * 
	 * @param propertyId
	 * @param value
	 * @return a {@link ValueSnak} corresponding to the input
	 */
	ValueSnak getValueSnak(PropertyIdValue propertyId, Value value);

	/**
	 * Creates a {@link SomeValueSnak}.
	 * 
	 * @param propertyId
	 * @return a {@link SomeValueSnak} corresponding to the input
	 */
	SomeValueSnak getSomeValueSnak(PropertyIdValue propertyId);

	/**
	 * Creates a {@link NoValueSnak}.
	 * 
	 * @param propertyId
	 * @return a {@link NoValueSnak} corresponding to the input
	 */
	NoValueSnak getNoValueSnak(PropertyIdValue propertyId);

	/**
	 * Creates a {@link SnakGroup}.
	 * 
	 * @param snaks
	 *            a non-empty list of snaks that use the same property
	 * @return a {@link SnakGroup} corresponding to the input
	 */
	SnakGroup getSnakGroup(List<? extends Snak> snaks);

	/**
	 * Creates a {@link Claim}.
	 * 
	 * @param subject
	 *            the subject the Statement refers to
	 * @param mainSnak
	 *            the main Snak of the Statement
	 * @param qualifiers
	 *            the qualifiers of the Statement, grouped in SnakGroups
	 * @return a {@link Claim} corresponding to the input
	 */
	Claim getClaim(EntityIdValue subject, Snak mainSnak,
			List<SnakGroup> qualifiers);

	/**
	 * Creates a {@link Reference}.
	 * 
	 * @param snakGroups
	 *            list of snak groups
	 * @return a {@link Reference} corresponding to the input
	 */
	Reference getReference(List<SnakGroup> snakGroups);

	/**
	 * Creates a {@link Statement}.
	 * <p>
	 * The string id is used mainly for communication with a Wikibase site, in
	 * order to refer to statements of that site. When creating new statements
	 * that are not on any site, the empty string can be used.
	 * 
	 * @param claim
	 *            the main claim the Statement refers to
	 * @param references
	 *            the references for the Statement
	 * @param rank
	 *            the rank of the Statement
	 * @param statementId
	 *            the string id of the Statement
	 * @return a {@link Statement} corresponding to the input
	 */
	Statement getStatement(Claim claim, List<? extends Reference> references,
			StatementRank rank, String statementId);

	/**
	 * Creates a {@link StatementGroup}.
	 * 
	 * @param statements
	 *            a non-empty list of statements that use the same subject and
	 *            main-snak property in their claim
	 * @return a {@link StatementGroup} corresponding to the input
	 */
	StatementGroup getStatementGroup(List<Statement> statements);

	/**
	 * Creates a {@link SiteLink}.
	 * 
	 * @param title
	 *            the title string of the linked page, including namespace
	 *            prefixes if any
	 * @param siteKey
	 *            the string key of the site of the linked article
	 * @param badges
	 *            the list of badges of the linked article
	 * @return a {@link SiteLink} corresponding to the input
	 */
	SiteLink getSiteLink(String title, String siteKey, List<String> badges);

	/**
	 * Creates a {@link PropertyDocument}.
	 * 
	 * @param propertyId
	 *            the id of the property that data is about
	 * @param labels
	 *            the list of labels of this property, with at most one label
	 *            for each language code
	 * @param descriptions
	 *            the list of descriptions of this property, with at most one
	 *            description for each language code
	 * @param aliases
	 *            the list of aliases of this property
	 * @param datatypeId
	 *            the datatype of that property
	 * @return a {@link PropertyDocument} corresponding to the input
	 */
	PropertyDocument getPropertyDocument(PropertyIdValue propertyId,
			List<MonolingualTextValue> labels,
			List<MonolingualTextValue> descriptions,
			List<MonolingualTextValue> aliases, DatatypeIdValue datatypeId);

	/**
	 * Creates an {@link ItemDocument}.
	 * 
	 * @param itemIdValue
	 *            the id of the item that data is about
	 * @param labels
	 *            the list of labels of this item, with at most one label for
	 *            each language code
	 * @param descriptions
	 *            the list of descriptions of this item, with at most one
	 *            description for each language code
	 * @param aliases
	 *            the list of aliases of this item
	 * @param statementGroups
	 *            the list of statement groups of this item; all of them must
	 *            have the given itemIdValue as their subject
	 * @param siteLinks
	 *            the sitelinks of this item by site key
	 * @return an {@link ItemDocument} corresponding to the input
	 */
	ItemDocument getItemDocument(ItemIdValue itemIdValue,
			List<MonolingualTextValue> labels,
			List<MonolingualTextValue> descriptions,
			List<MonolingualTextValue> aliases,
			List<StatementGroup> statementGroups,
			Map<String, SiteLink> siteLinks);

}
