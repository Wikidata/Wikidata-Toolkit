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

import org.wikidata.wdtk.datamodel.interfaces.*;

/**
 * Static class for checking the equality of arbitrary data objects using only
 * their interfaces. This can be used to implement the equals() method of
 * arbitrary interface implementations. More efficient solutions might exist if
 * the object that implements an interface is of a specific known type, but the
 * methods here could always be used as a fallback or default.
 * <p>
 * Note that it is desired that different implementations of the same interface
 * are treated as equal if they contain the same data.
 *
 * @author Markus Kroetzsch
 *
 */
public class Equality {

	/**
	 * Returns true if the parameters are two {@link EntityIdValue} objects with
	 * exactly the same data. It does not matter if they are different
	 * implementations of the interface as long as their content is the same.
	 *
	 * @param o1
	 *            the first object to compare
	 * @param o2
	 *            the second object to compare
	 * @return true if both objects are equal
	 */
	public static boolean equalsEntityIdValue(EntityIdValue o1, Object o2) {
		if (o2 == null) {
			return false;
		}
		if (o2 == o1) {
			return true;
		}
		if (!(o2 instanceof EntityIdValue)) {
			return false;
		}

		EntityIdValue other = (EntityIdValue) o2;
		return o1.getId().equals(other.getId())
				&& o1.getSiteIri().equals(other.getSiteIri())
				&& o1.getEntityType().equals(other.getEntityType());
	}

	/**
	 * Returns true if the parameters are two {@link DatatypeIdValue} objects
	 * with exactly the same data. It does not matter if they are different
	 * implementations of the interface as long as their content is the same.
	 *
	 * @param o1
	 *            the first object to compare
	 * @param o2
	 *            the second object to compare
	 * @return true if both objects are equal
	 */
	public static boolean equalsDatatypeIdValue(DatatypeIdValue o1, Object o2) {
		if (o2 == null) {
			return false;
		}
		if (o2 == o1) {
			return true;
		}
		return o2 instanceof DatatypeIdValue
			&& o1.getIri().equals(((DatatypeIdValue) o2).getIri());
	}

	/**
	 * Returns true if the parameters are two {@link TimeValue} objects with
	 * exactly the same data. It does not matter if they are different
	 * implementations of the interface as long as their content is the same.
	 *
	 * @param o1
	 *            the first object to compare
	 * @param o2
	 *            the second object to compare
	 * @return true if both objects are equal
	 */
	public static boolean equalsTimeValue(TimeValue o1, Object o2) {
		if (o2 == null) {
			return false;
		}
		if (o2 == o1) {
			return true;
		}
		if (!(o2 instanceof TimeValue)) {
			return false;
		}
		TimeValue other = (TimeValue) o2;
		return o1.getYear() == other.getYear()
				&& o1.getMonth() == other.getMonth()
				&& o1.getDay() == other.getDay()
				&& o1.getHour() == other.getHour()
				&& o1.getMinute() == other.getMinute()
				&& o1.getSecond() == other.getSecond()
				&& o1.getPrecision() == other.getPrecision()
				&& o1.getBeforeTolerance() == other.getBeforeTolerance()
				&& o1.getAfterTolerance() == other.getAfterTolerance()
				&& o1.getTimezoneOffset() == other.getTimezoneOffset()
				&& o1.getPreferredCalendarModel().equals(
						other.getPreferredCalendarModel());
	}

	/**
	 * Returns true if the parameters are two {@link GlobeCoordinatesValue}
	 * objects with exactly the same data. It does not matter if they are
	 * different implementations of the interface as long as their content is
	 * the same.
	 *
	 * @param o1
	 *            the first object to compare
	 * @param o2
	 *            the second object to compare
	 * @return true if both objects are equal
	 */
	public static boolean equalsGlobeCoordinatesValue(GlobeCoordinatesValue o1,
			Object o2) {
		if (o2 == null) {
			return false;
		}
		if (o2 == o1) {
			return true;
		}
		if (!(o2 instanceof GlobeCoordinatesValue)) {
			return false;
		}
		GlobeCoordinatesValue other = (GlobeCoordinatesValue) o2;
		return o1.getLatitude() == other.getLatitude()
				&& o1.getLongitude() == other.getLongitude()
				&& o1.getPrecision() == other.getPrecision()
				&& o1.getGlobe().equals(other.getGlobe());
	}

	/**
	 * Returns true if the parameters are two {@link StringValue} objects with
	 * exactly the same data. It does not matter if they are different
	 * implementations of the interface as long as their content is the same.
	 *
	 * @param o1
	 *            the first object to compare
	 * @param o2
	 *            the second object to compare
	 * @return true if both objects are equal
	 */
	public static boolean equalsStringValue(StringValue o1, Object o2) {
		if (o2 == null) {
			return false;
		}
		if (o2 == o1) {
			return true;
		}
		return o2 instanceof StringValue
			&& o1.getString().equals(((StringValue) o2).getString());
	}

	/**
	 * Returns true if the parameters are two {@link MonolingualTextValue}
	 * objects with exactly the same data. It does not matter if they are
	 * different implementations of the interface as long as their content is
	 * the same.
	 *
	 * @param o1
	 *            the first object to compare
	 * @param o2
	 *            the second object to compare
	 * @return true if both objects are equal
	 */
	public static boolean equalsMonolingualTextValue(MonolingualTextValue o1,
			Object o2) {
		if (o2 == null) {
			return false;
		}
		if (o2 == o1) {
			return true;
		}
		if (!(o2 instanceof MonolingualTextValue)) {
			return false;
		}
		MonolingualTextValue other = (MonolingualTextValue) o2;
		return o1.getText().equals(other.getText())
				&& o1.getLanguageCode().equals(other.getLanguageCode());
	}

	/**
	 * Returns true if the parameters are two {@link QuantityValue} objects with
	 * exactly the same data. It does not matter if they are different
	 * implementations of the interface as long as their content is the same.
	 *
	 * @param o1
	 *            the first object to compare
	 * @param o2
	 *            the second object to compare
	 * @return true if both objects are equal
	 */
	public static boolean equalsQuantityValue(QuantityValue o1, Object o2) {
		if (o2 == null) {
			return false;
		}
		if (o2 == o1) {
			return true;
		}
		if (!(o2 instanceof QuantityValue)) {
			return false;
		}
		QuantityValue other = (QuantityValue) o2;
		return o1.getNumericValue().equals(other.getNumericValue())
				&& equalsNullable(o1.getLowerBound(), other.getLowerBound())
				&& equalsNullable(o1.getUpperBound(), other.getUpperBound())
				&& o1.getUnit().equals(other.getUnit());
	}

	private static boolean equalsNullable(Object o1, Object o2) {
		return o1 == null ? o2 == null : o1.equals(o2);
	}

	/**
	 * Returns true if the parameters are two {@link ValueSnak} objects with
	 * exactly the same data. It does not matter if they are different
	 * implementations of the interface as long as their content is the same.
	 *
	 * @param o1
	 *            the first object to compare
	 * @param o2
	 *            the second object to compare
	 * @return true if both objects are equal
	 */
	public static boolean equalsValueSnak(ValueSnak o1, Object o2) {
		if (o2 == null) {
			return false;
		}
		if (o2 == o1) {
			return true;
		}
		return o2 instanceof ValueSnak
				&& o1.getPropertyId().equals(((ValueSnak) o2).getPropertyId())
				&& o1.getValue().equals(((ValueSnak) o2).getValue());
	}

	/**
	 * Returns true if the parameters are two {@link SomeValueSnak} objects with
	 * exactly the same data. It does not matter if they are different
	 * implementations of the interface as long as their content is the same.
	 *
	 * @param o1
	 *            the first object to compare
	 * @param o2
	 *            the second object to compare
	 * @return true if both objects are equal
	 */
	public static boolean equalsSomeValueSnak(SomeValueSnak o1, Object o2) {
		if (o2 == null) {
			return false;
		}
		if (o2 == o1) {
			return true;
		}
		return o2 instanceof SomeValueSnak
			&& o1.getPropertyId().equals(((SomeValueSnak) o2).getPropertyId());
	}

	/**
	 * Returns true if the parameters are two {@link NoValueSnak} objects with
	 * exactly the same data. It does not matter if they are different
	 * implementations of the interface as long as their content is the same.
	 *
	 * @param o1
	 *            the first object to compare
	 * @param o2
	 *            the second object to compare
	 * @return true if both objects are equal
	 */
	public static boolean equalsNoValueSnak(NoValueSnak o1, Object o2) {
		if (o2 == null) {
			return false;
		}
		if (o2 == o1) {
			return true;
		}
		return o2 instanceof NoValueSnak
			&& o1.getPropertyId().equals(((NoValueSnak) o2).getPropertyId());
	}

	/**
	 * Returns true if the parameters are two {@link SnakGroup} objects with
	 * exactly the same data. It does not matter if they are different
	 * implementations of the interface as long as their content is the same.
	 *
	 * @param o1
	 *            the first object to compare
	 * @param o2
	 *            the second object to compare
	 * @return true if both objects are equal
	 */
	public static boolean equalsSnakGroup(SnakGroup o1, Object o2) {
		if (o2 == null) {
			return false;
		}
		if (o2 == o1) {
			return true;
		}
		return o2 instanceof SnakGroup
			&& o1.getSnaks().equals(((SnakGroup) o2).getSnaks());
	}

	/**
	 * Returns true if the parameters are two {@link Claim} objects with exactly
	 * the same data. It does not matter if they are different implementations
	 * of the interface as long as their content is the same.
	 *
	 * @param o1
	 *            the first object to compare
	 * @param o2
	 *            the second object to compare
	 * @return true if both objects are equal
	 */
	public static boolean equalsClaim(Claim o1, Object o2) {
		if (o2 == null) {
			return false;
		}
		if (o2 == o1) {
			return true;
		}
		if (!(o2 instanceof Claim)) {
			return false;
		}
		Claim other = (Claim) o2;
		return o1.getSubject().equals(other.getSubject())
				&& o1.getMainSnak().equals(other.getMainSnak())
				&& o1.getQualifiers().equals(other.getQualifiers());
	}

	/**
	 * Returns true if the parameters are two {@link Reference} objects with
	 * exactly the same data. It does not matter if they are different
	 * implementations of the interface as long as their content is the same.
	 *
	 * @param o1
	 *            the first object to compare
	 * @param o2
	 *            the second object to compare
	 * @return true if both objects are equal
	 */
	public static boolean equalsReference(Reference o1, Object o2) {
		if (o2 == null) {
			return false;
		}
		if (o2 == o1) {
			return true;
		}
		return o2 instanceof Reference
			&& o1.getSnakGroups().equals(((Reference) o2).getSnakGroups());
	}

	/**
	 * Returns true if the parameters are two {@link Statement} objects with
	 * exactly the same data. It does not matter if they are different
	 * implementations of the interface as long as their content is the same.
	 *
	 * @param o1
	 *            the first object to compare
	 * @param o2
	 *            the second object to compare
	 * @return true if both objects are equal
	 */
	public static boolean equalsStatement(Statement o1, Object o2) {
		if (o2 == null) {
			return false;
		}
		if (o2 == o1) {
			return true;
		}
		if (!(o2 instanceof Statement)) {
			return false;
		}
		Statement other = (Statement) o2;
		return o1.getSubject().equals(other.getSubject())
				&& o1.getMainSnak().equals(other.getMainSnak())
				&& o1.getQualifiers().equals(other.getQualifiers())
				&& o1.getReferences().equals(other.getReferences())
				&& o1.getRank() == other.getRank()
				&& o1.getStatementId().equals(other.getStatementId());
	}

	/**
	 * Returns true if the parameters are two {@link StatementGroup} objects
	 * with exactly the same data. It does not matter if they are different
	 * implementations of the interface as long as their content is the same.
	 * Note that this includes the statement id, so that two statement objects
	 * that "say the same thing" might still be unequal if they have different
	 * ids.
	 *
	 * @param o1
	 *            the first object to compare
	 * @param o2
	 *            the second object to compare
	 * @return true if both objects are equal
	 */
	public static boolean equalsStatementGroup(StatementGroup o1, Object o2) {
		if (o2 == null) {
			return false;
		}
		if (o2 == o1) {
			return true;
		}
		return o2 instanceof StatementGroup
			&& o1.getStatements().equals(((StatementGroup) o2).getStatements());
	}

	/**
	 * Returns true if the parameters are two {@link SiteLink} objects with
	 * exactly the same data. It does not matter if they are different
	 * implementations of the interface as long as their content is the same.
	 *
	 * @param o1
	 *            the first object to compare
	 * @param o2
	 *            the second object to compare
	 * @return true if both objects are equal
	 */
	public static boolean equalsSiteLink(SiteLink o1, Object o2) {
		if (o2 == null) {
			return false;
		}
		if (o2 == o1) {
			return true;
		}
		if (!(o2 instanceof SiteLink)) {
			return false;
		}
		SiteLink other = (SiteLink) o2;
		return o1.getPageTitle().equals(other.getPageTitle())
				&& o1.getSiteKey().equals(other.getSiteKey())
				&& o1.getBadges().equals(other.getBadges());
	}

	/**
	 * Returns true if the parameters are two {@link PropertyDocument} objects
	 * with exactly the same data. It does not matter if they are different
	 * implementations of the interface as long as their content is the same.
	 *
	 * @param o1
	 *            the first object to compare
	 * @param o2
	 *            the second object to compare
	 * @return true if both objects are equal
	 */
	public static boolean equalsPropertyDocument(PropertyDocument o1, Object o2) {
		if (o2 == o1) {
			return true;
		}
		if (!(o2 instanceof PropertyDocument)) {
			return false;
		}
		PropertyDocument other = (PropertyDocument) o2;
		// Note: property id already compared by equalsTermedDocument()
		return equalsTermedDocument(o1, other)
				&& o1.getDatatype().equals(other.getDatatype())
				&& o1.getStatementGroups().equals(other.getStatementGroups());
	}

	/**
	 * Returns true if the parameters are two {@link ItemDocument} objects with
	 * exactly the same data. It does not matter if they are different
	 * implementations of the interface as long as their content is the same.
	 *
	 * @param o1
	 *            the first object to compare
	 * @param o2
	 *            the second object to compare
	 * @return true if both objects are equal
	 */
	public static boolean equalsItemDocument(ItemDocument o1, Object o2) {
		if (o2 == o1) {
			return true;
		}
		if (!(o2 instanceof ItemDocument)) {
			return false;
		}
		ItemDocument other = (ItemDocument) o2;
		// Note: item id already compared by equalsTermedDocument()
		return equalsTermedDocument(o1, other)
				&& o1.getSiteLinks().equals(other.getSiteLinks())
				&& o1.getStatementGroups().equals(other.getStatementGroups());
	}

	/**
	 * Returns true if the parameters are two {@link LexemeDocument} objects with
	 * exactly the same data. It does not matter if they are different
	 * implementations of the interface as long as their content is the same.
	 *
	 * @param o1
	 *            the first object to compare
	 * @param o2
	 *            the second object to compare
	 * @return true if both objects are equal
	 */
	public static boolean equalsLexemeDocument(LexemeDocument o1, Object o2) {
		if (o2 == o1) {
			return true;
		}
		if (!(o2 instanceof LexemeDocument)) {
			return false;
		}
		LexemeDocument other = (LexemeDocument) o2;
		return o1.getEntityId().equals(other.getEntityId())
				&& o1.getLanguage().equals(other.getLanguage())
				&& o1.getLexicalCategory().equals(other.getLexicalCategory())
				&& o1.getLemmas().equals(other.getLemmas())
				&& o1.getStatementGroups().equals(other.getStatementGroups())
				&& o1.getForms().equals(other.getForms())
				&& o1.getSenses().equals(other.getSenses())
				&& (o1.getRevisionId() == other.getRevisionId());
	}

	/**
	 * Returns true if the parameters are two {@link FormDocument} objects with
	 * exactly the same data. It does not matter if they are different
	 * implementations of the interface as long as their content is the same.
	 *
	 * @param o1
	 *            the first object to compare
	 * @param o2
	 *            the second object to compare
	 * @return true if both objects are equal
	 */
	public static boolean equalsFormDocument(FormDocument o1, Object o2) {
		if (o2 == o1) {
			return true;
		}
		if (!(o2 instanceof FormDocument)) {
			return false;
		}
		FormDocument other = (FormDocument) o2;
		return o1.getEntityId().equals(other.getEntityId())
				&& o1.getGrammaticalFeatures().equals(other.getGrammaticalFeatures())
				&& o1.getRepresentations().equals(other.getRepresentations())
				&& o1.getStatementGroups().equals(other.getStatementGroups())
				&& (o1.getRevisionId() == other.getRevisionId());
	}

	/**
	 * Returns true if the parameters are two {@link SenseDocument} objects with
	 * exactly the same data. It does not matter if they are different
	 * implementations of the interface as long as their content is the same.
	 *
	 * @param o1
	 *            the first object to compare
	 * @param o2
	 *            the second object to compare
	 * @return true if both objects are equal
	 */
	public static boolean equalsSenseDocument(SenseDocument o1, Object o2) {
		if (o2 == o1) {
			return true;
		}
		if (!(o2 instanceof SenseDocument)) {
			return false;
		}
		SenseDocument other = (SenseDocument) o2;
		return o1.getEntityId().equals(other.getEntityId())
				&& o1.getGlosses().equals(other.getGlosses())
				&& o1.getStatementGroups().equals(other.getStatementGroups())
				&& (o1.getRevisionId() == other.getRevisionId());
	}

	/**
	 * Returns true if the parameters are two {@link MediaInfoDocument} objects with
	 * exactly the same data. It does not matter if they are different
	 * implementations of the interface as long as their content is the same.
	 *
	 * @param o1
	 *            the first object to compare
	 * @param o2
	 *            the second object to compare
	 * @return true if both objects are equal
	 */
	public static boolean equalsMediaInfoDocument(MediaInfoDocument o1, Object o2) {
		if (o2 == o1) {
			return true;
		}
		if (!(o2 instanceof MediaInfoDocument)) {
			return false;
		}
		MediaInfoDocument other = (MediaInfoDocument) o2;
		return o1.getEntityId().equals(other.getEntityId())
				&& o1.getLabels().equals(other.getLabels())
				&& o1.getStatementGroups().equals(other.getStatementGroups())
				&& (o1.getRevisionId() == other.getRevisionId());
	}

	private static boolean equalsTermedDocument(TermedDocument o1, TermedDocument other) {
		return o1.getEntityId().equals(other.getEntityId())
				&& o1.getAliases().equals(other.getAliases())
				&& o1.getDescriptions().equals(other.getDescriptions())
				&& o1.getLabels().equals(other.getLabels())
				&& (o1.getRevisionId() == other.getRevisionId());
	}

	/**
	 * Returns true if the parameters are two {@link EntityRedirectDocument} objects with
	 * exactly the same data. It does not matter if they are different
	 * implementations of the interface as long as their content is the same.
	 *
	 * @param o1
	 *            the first object to compare
	 * @param o2
	 *            the second object to compare
	 * @return true if both objects are equal
	 */
	public static boolean equalsEntityRedirectDocument(EntityRedirectDocument o1, Object o2) {
		if (o2 == o1) {
			return true;
		}
		if (!(o2 instanceof EntityRedirectDocument)) {
			return false;
		}
		EntityRedirectDocument other = (EntityRedirectDocument) o2;
		return o1.getEntityId().equals(other.getEntityId())
				&& o1.getTargetId().equals(other.getTargetId())
				&& o1.getRevisionId() == other.getRevisionId();
	}
}
