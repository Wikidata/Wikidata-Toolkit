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

import org.wikidata.wdtk.datamodel.helpers.ItemDocumentBuilder;
import org.wikidata.wdtk.datamodel.helpers.PropertyDocumentBuilder;
import org.wikidata.wdtk.datamodel.helpers.ReferenceBuilder;
import org.wikidata.wdtk.datamodel.helpers.StatementBuilder;

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
	 * @param siteIri
	 *            IRI to identify the site, usually the first part of the entity
	 *            IRI of the site this belongs to, e.g.,
	 *            "http://www.wikidata.org/entity/"
	 * @return an {@link ItemIdValue} corresponding to the input
	 */
	ItemIdValue getItemIdValue(String id, String siteIri);

	/**
	 * Creates a {@link PropertyIdValue}.
	 *
	 * @param id
	 *            a string of the form Pn... where n... is the string
	 *            representation of a positive integer number
	 * @param siteIri
	 *            IRI to identify the site, usually the first part of the entity
	 *            IRI of the site this belongs to, e.g.,
	 *            "http://www.wikidata.org/entity/"
	 * @return a {@link PropertyIdValue} corresponding to the input
	 */
	PropertyIdValue getPropertyIdValue(String id, String siteIri);

	/**
	 * Creates a {@link LexemeIdValue}.
	 *
	 * @param id
	 *            a string of the form Ln... where n... is the string
	 *            representation of a positive integer number
	 * @param siteIri
	 *            IRI to identify the site, usually the first part of the entity
	 *            IRI of the site this belongs to, e.g.,
	 *            "http://www.wikidata.org/entity/"
	 * @return a {@link LexemeIdValue} corresponding to the input
	 */
	LexemeIdValue getLexemeIdValue(String id, String siteIri);

	/**
	 * Creates a {@link FormIdValue}.
	 *
	 * @param id
	 *            a string of the form Ln...-Fm... where n... and m... are the string
	 *            representation of a positive integer number
	 * @param siteIri
	 *            IRI to identify the site, usually the first part of the entity
	 *            IRI of the site this belongs to, e.g.,
	 *            "http://www.wikidata.org/entity/"
	 * @return a {@link FormIdValue} corresponding to the input
	 */
	FormIdValue getFormIdValue(String id, String siteIri);

	/**
	 * Creates a {@link SenseIdValue}.
	 *
	 * @param id
	 *            a string of the form Ln...-Sm... where n... and m... are the string
	 *            representation of a positive integer number
	 * @param siteIri
	 *            IRI to identify the site, usually the first part of the entity
	 *            IRI of the site this belongs to, e.g.,
	 *            "http://www.wikidata.org/entity/"
	 * @return a {@link SenseIdValue} corresponding to the input
	 */
	SenseIdValue getSenseIdValue(String id, String siteIri);

	/**
	 * Creates a {@link MediaInfoIdValue}.
	 *
	 * @param id
	 *            a string of the form Mn... where n... is the string
	 *            representation of a positive integer number
	 * @param siteIri
	 *            IRI to identify the site, usually the first part of the entity
	 *            IRI of the site this belongs to, e.g.,
	 *            "http://www.wikidata.org/entity/"
	 * @return a {@link MediaInfoIdValue} corresponding to the input
	 */
	MediaInfoIdValue getMediaInfoIdValue(String id, String siteIri);

	/**
	 * Creates a {@link DatatypeIdValue}. The datatype IRI is usually one of the
	 * constants defined in {@link DatatypeIdValue}, but this is not enforced,
	 * since there might be extensions that provide additional types.
	 *
	 * @param id
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
	 *            {@link TimeValue#PREC_1GY}
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
	 *            the latitude of the coordinates in degrees
	 * @param longitude
	 *            the longitude of the coordinates in degrees
	 * @param precision
	 *            the precision of the coordinates in degrees
	 * @param globeIri
	 *            IRI specifying the celestial objects of the coordinates
	 * @return a {@link GlobeCoordinatesValue} corresponding to the input
	 */
	GlobeCoordinatesValue getGlobeCoordinatesValue(double latitude,
			double longitude, double precision, String globeIri);

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
	 * @return a {@link MonolingualTextValue} corresponding to the input
	 */
	MonolingualTextValue getMonolingualTextValue(String text,
			String languageCode);

	/**
	 * Creates a {@link QuantityValue} without a unit of measurement and bounds.
	 *
	 * @param numericValue
	 *            the numeric value of this quantity
	 * @return a {@link QuantityValue} corresponding to the input
	 */
	QuantityValue getQuantityValue(BigDecimal numericValue);

	/**
	 * Creates a {@link QuantityValue} without a unit of measurement.
	 *
	 * @param numericValue
	 *            the numeric value of this quantity
	 * @param lowerBound
	 *            the lower bound of the numeric value of this quantity
	 * @param upperBound
	 *            the upper bound of the numeric value of this quantity
	 * @return a {@link QuantityValue} corresponding to the input
	 */
	QuantityValue getQuantityValue(BigDecimal numericValue,
			BigDecimal lowerBound, BigDecimal upperBound);

	/**
	 * Creates a {@link QuantityValue} without bounds.
	 *
	 * @param numericValue
	 *            the numeric value of this quantity
	 * @param unit
	 *            the unit of this quantity, or the empty string if there is no
	 *            unit
	 * @return a {@link QuantityValue} corresponding to the input
	 */
	QuantityValue getQuantityValue(BigDecimal numericValue, String unit);

	/**
	 * Creates a {@link QuantityValue}.
	 *
	 * @param numericValue
	 *            the numeric value of this quantity
	 * @param lowerBound
	 *            the lower bound of the numeric value of this quantity
	 * @param upperBound
	 *            the upper bound of the numeric value of this quantity
	 * @param unit
	 *            the unit of this quantity, or the empty string if there is no
	 *            unit
	 * @return a {@link QuantityValue} corresponding to the input
	 */
	QuantityValue getQuantityValue(BigDecimal numericValue,
			BigDecimal lowerBound, BigDecimal upperBound, String unit);

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
	 * Creates a {@link Claim}. It might be more convenient to use
	 * {@link #getStatement} directly if you want to build a statement.
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
	 * Creates a {@link Reference}. It might be more convenient to use
	 * {@link ReferenceBuilder} instead.
	 *
	 * @param snakGroups
	 *            list of snak groups
	 * @return a {@link Reference} corresponding to the input
	 */
	Reference getReference(List<SnakGroup> snakGroups);

	/**
	 * Creates a {@link Statement}. It might be more convenient to use
	 * {@link StatementBuilder} instead.
	 * <p>
	 * The string id is used mainly for communication with a Wikibase site, in
	 * order to refer to statements of that site. When creating new statements
	 * that are not on any site, the empty string can be used.
	 *
	 * @param subject
	 *            the subject the Statement refers to
	 * @param mainSnak
	 *            the main Snak of the Statement
	 * @param qualifiers
	 *            the qualifiers of the Statement, grouped in SnakGroups
	 * @param references
	 *            the references for the Statement
	 * @param rank
	 *            the rank of the Statement
	 * @param statementId
	 *            the string id of the Statement
	 * @return a {@link Statement} corresponding to the input
	 */
	Statement getStatement(EntityIdValue subject, Snak mainSnak,
			List<SnakGroup> qualifiers, List<Reference> references,
			StatementRank rank, String statementId);

	/**
	 * Creates a {@link Statement}. It might be more convenient to use
	 * {@link StatementBuilder} instead.
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
	Statement getStatement(Claim claim, List<Reference> references,
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
	SiteLink getSiteLink(String title, String siteKey, List<ItemIdValue> badges);

	/**
	 * Creates a {@link PropertyDocument}. It might be more convenient to use
	 * the {@link PropertyDocumentBuilder} instead.
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
	 * @param statementGroups
	 *            the list of statement groups of this item; all of them must
	 *            have the given itemIdValue as their subject
	 * @param datatypeId
	 *            the datatype of that property
	 * @param revisionId
	 *            the revision ID or 0 if not known; see
	 *            {@link EntityDocument#getRevisionId()}
	 * @return a {@link PropertyDocument} corresponding to the input
	 */
	PropertyDocument getPropertyDocument(PropertyIdValue propertyId,
			List<MonolingualTextValue> labels,
			List<MonolingualTextValue> descriptions,
			List<MonolingualTextValue> aliases,
			List<StatementGroup> statementGroups, DatatypeIdValue datatypeId,
			long revisionId);

	/**
	 * Creates an {@link ItemDocument}. It might be more convenient to use the
	 * {@link ItemDocumentBuilder} instead.
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
	 * @param revisionId
	 *            the revision ID or 0 if not known; see
	 *            {@link EntityDocument#getRevisionId()}
	 * @return an {@link ItemDocument} corresponding to the input
	 */
	ItemDocument getItemDocument(ItemIdValue itemIdValue,
			List<MonolingualTextValue> labels,
			List<MonolingualTextValue> descriptions,
			List<MonolingualTextValue> aliases,
			List<StatementGroup> statementGroups,
			Map<String, SiteLink> siteLinks, long revisionId);

	/**
	 * Creates an {@link LexemeDocument}.
	 *
	 * @param lexemeIdValue
	 *            the id of the lexeme that data is about
	 * @param lexicalCategory
	 *            the lexical category to which the lexeme belongs
	 *            (noun, verb...)
	 * @param language
	 *            the language to which the lexeme belongs
	 * 	          (French, British English...)
	 * @param lemmas
	 *            the human readable representations of the lexeme
	 * @param statementGroups
	 *            the list of statement groups of this lexeme; all of them must
	 *            have the given lexemeIdValue as their subject
	 * @param forms
	 *            the forms of the lexeme
	 * @param senses
	 *            the senses of the lexeme
	 * @param revisionId
	 *            the revision ID or 0 if not known; see
	 *            {@link EntityDocument#getRevisionId()}
	 * @return a {@link LexemeDocument} corresponding to the input
	 */
	LexemeDocument getLexemeDocument(LexemeIdValue lexemeIdValue,
			ItemIdValue lexicalCategory,
			ItemIdValue language,
			List<MonolingualTextValue> lemmas,
			List<StatementGroup> statementGroups,
			List<FormDocument> forms,
			List<SenseDocument> senses,
			long revisionId);


	/**
	 * Creates an {@link FormDocument}.
	 *
	 * @param formIdValue
	 *            the id of the form that data is about
	 * @param representations
	 *            the list of representations of this lexeme, with at most one
	 *            lemma for each language code
	 * @param grammaticalFeatures
	 *            the grammatical features of the lexeme
	 * @param statementGroups
	 *            the list of statement groups of this lexeme; all of them must
	 *            have the given lexemeIdValue as their subject
	 * @param revisionId
	 *            the revision ID or 0 if not known; see
	 *            {@link EntityDocument#getRevisionId()}
	 * @return a {@link FormDocument} corresponding to the input
	 */
	FormDocument getFormDocument(FormIdValue formIdValue,
			List<MonolingualTextValue> representations,
			List<ItemIdValue> grammaticalFeatures,
			List<StatementGroup> statementGroups,
			long revisionId);

	/**
	 * Creates a {@link SenseDocument}.
	 *
	 * @param senseIdValue
	 *            the id of the form that data is about
	 * @param glosses
	 *            the list of glosses of this lexeme, with at most one
	 *            gloss for each language code
	 * @param statementGroups
	 *            the list of statement groups of this lexeme; all of them must
	 *            have the given lexemeIdValue as their subject
	 * @param revisionId
	 *            the revision ID or 0 if not known; see
	 *            {@link EntityDocument#getRevisionId()}
	 * @return a {@link SenseDocument} corresponding to the input
	 */
	SenseDocument getSenseDocument(SenseIdValue senseIdValue,
								 List<MonolingualTextValue> glosses,
								 List<StatementGroup> statementGroups,
								 long revisionId);

	/**
	 * Creates a {@link MediaInfoDocument}.
	 *
	 * @param mediaInfoIdValue
	 *            the id of the form that data is about
	 * @param labels
	 *            the list of labels of this media info, with at most one label for
	 *            each language code
	 * @param statementGroups
	 *            the list of statement groups of this lexeme; all of them must
	 *            have the given mediaInfoIdValue as their subject
	 * @param revisionId
	 *            the revision ID or 0 if not known; see
	 *            {@link EntityDocument#getRevisionId()}
	 * @return a {@link MediaInfoDocument} corresponding to the input
	 */
	MediaInfoDocument getMediaInfoDocument(MediaInfoIdValue mediaInfoIdValue,
										   List<MonolingualTextValue> labels,
										   List<StatementGroup> statementGroups,
										   long revisionId);

}
