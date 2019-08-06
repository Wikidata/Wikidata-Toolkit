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
 * Static class for computing a hashcode of arbitrary data objects using only
 * their interfaces. This can be used to implement the hashCode() method of
 * arbitrary interface implementations. More efficient solutions might exist if
 * the object that implements an interface is of a specific known type, but the
 * methods here could always be used as a fallback or default.
 *
 * @author Markus Kroetzsch
 *
 */
public class Hash {

	/**
	 * Prime number used to build hashes.
	 */
	final static int prime = 31;

	/**
	 * Returns a hash code for the given object.
	 *
	 * @see java.lang.Object#hashCode()
	 * @param o
	 *            the object to create a hash for
	 * @return the hash code of the object
	 */
	public static int hashCode(EntityIdValue o) {
		int result;
		result = o.getId().hashCode();
		result = prime * result + o.getSiteIri().hashCode();
		result = prime * result + o.getEntityType().hashCode();
		return result;
	}

	/**
	 * Returns a hash code for the given object.
	 *
	 * @see java.lang.Object#hashCode()
	 * @param o
	 *            the object to create a hash for
	 * @return the hash code of the object
	 */
	public static int hashCode(DatatypeIdValue o) {
		return o.getIri().hashCode();
	}

	/**
	 * Returns a hash code for the given object.
	 *
	 * @see java.lang.Object#hashCode()
	 * @param o
	 *            the object to create a hash for
	 * @return the hash code of the object
	 */
	public static int hashCode(TimeValue o) {
		int result;
		result = Long.hashCode(o.getYear());
		result = prime * result + o.getMonth();
		result = prime * result + o.getDay();
		result = prime * result + o.getHour();
		result = prime * result + o.getMinute();
		result = prime * result + o.getSecond();
		result = prime * result + o.getPrecision();
		result = prime * result + o.getBeforeTolerance();
		result = prime * result + o.getAfterTolerance();
		result = prime * result + o.getTimezoneOffset();
		result = prime * result + o.getPreferredCalendarModel().hashCode();
		return result;
	}

	/**
	 * Returns a hash code for the given object.
	 *
	 * @see java.lang.Object#hashCode()
	 * @param o
	 *            the object to create a hash for
	 * @return the hash code of the object
	 */
	public static int hashCode(GlobeCoordinatesValue o) {
		int result;
		result = o.getGlobe().hashCode();
		long value;
		value = Double.valueOf(o.getLatitude()).hashCode();
		result = prime * result + (int) (value ^ (value >>> 32));
		value = Double.valueOf(o.getLongitude()).hashCode();
		result = prime * result + (int) (value ^ (value >>> 32));
		value = Double.valueOf(o.getPrecision()).hashCode();
		result = prime * result + (int) (value ^ (value >>> 32));
		return result;
	}

	/**
	 * Returns a hash code for the given object.
	 *
	 * @see java.lang.Object#hashCode()
	 * @param o
	 *            the object to create a hash for
	 * @return the hash code of the object
	 */
	public static int hashCode(StringValue o) {
		return o.getString().hashCode();
	}

	/**
	 * Returns a hash code for the given object.
	 *
	 * @see java.lang.Object#hashCode()
	 * @param o
	 *            the object to create a hash for
	 * @return the hash code of the object
	 */
	public static int hashCode(MonolingualTextValue o) {
		int result;
		result = o.getLanguageCode().hashCode();
		result = prime * result + o.getText().hashCode();
		return result;
	}

	/**
	 * Returns a hash code for the given object.
	 *
	 * @see java.lang.Object#hashCode()
	 * @param o
	 *            the object to create a hash for
	 * @return the hash code of the object
	 */
	public static int hashCode(QuantityValue o) {
		int result;
		result = o.getNumericValue().hashCode();
		result = prime * result + o.getUnit().hashCode();
		if(o.getLowerBound() != null) {
			result = prime * result + o.getLowerBound().hashCode();
		}
		if(o.getUpperBound() != null) {
			result = prime * result + o.getUpperBound().hashCode();
		}
		return result;
	}

	/**
	 * Returns a hash code for the given object.
	 *
	 * @see java.lang.Object#hashCode()
	 * @param o
	 *            the object to create a hash for
	 * @return the hash code of the object
	 */
	public static int hashCode(ValueSnak o) {
		int result;
		result = o.getValue().hashCode();
		result = prime * result + o.getPropertyId().hashCode();
		return result;
	}

	/**
	 * Returns a hash code for the given object.
	 *
	 * @see java.lang.Object#hashCode()
	 * @param o
	 *            the object to create a hash for
	 * @return the hash code of the object
	 */
	public static int hashCode(SomeValueSnak o) {
		return o.getPropertyId().hashCode();
	}

	/**
	 * Returns a hash code for the given object.
	 *
	 * @see java.lang.Object#hashCode()
	 * @param o
	 *            the object to create a hash for
	 * @return the hash code of the object
	 */
	public static int hashCode(NoValueSnak o) {
		return o.getPropertyId().hashCode();
	}

	/**
	 * Returns a hash code for the given object.
	 *
	 * @see java.lang.Object#hashCode()
	 * @param o
	 *            the object to create a hash for
	 * @return the hash code of the object
	 */
	public static int hashCode(SnakGroup o) {
		return o.getSnaks().hashCode();
	}

	/**
	 * Returns a hash code for the given object.
	 *
	 * @see java.lang.Object#hashCode()
	 * @param o
	 *            the object to create a hash for
	 * @return the hash code of the object
	 */
	public static int hashCode(Claim o) {
		int result;
		result = o.getSubject().hashCode();
		result = prime * result + o.getMainSnak().hashCode();
		result = prime * result + o.getQualifiers().hashCode();
		return result;
	}

	/**
	 * Returns a hash code for the given object.
	 *
	 * @see java.lang.Object#hashCode()
	 * @param o
	 *            the object to create a hash for
	 * @return the hash code of the object
	 */
	public static int hashCode(Reference o) {
		return o.getSnakGroups().hashCode();
	}

	/**
	 * Returns a hash code for the given object.
	 *
	 * @see java.lang.Object#hashCode()
	 * @param o
	 *            the object to create a hash for
	 * @return the hash code of the object
	 */
	public static int hashCode(Statement o) {
		int result;
		result = o.getSubject().hashCode();
		result = prime * result + o.getMainSnak().hashCode();
		result = prime * result + o.getQualifiers().hashCode();
		result = prime * result + o.getReferences().hashCode();
		result = prime * result + o.getRank().hashCode();
		result = prime * result + o.getStatementId().hashCode();
		return result;
	}

	/**
	 * Returns a hash code for the given object.
	 *
	 * @see java.lang.Object#hashCode()
	 * @param o
	 *            the object to create a hash for
	 * @return the hash code of the object
	 */
	public static int hashCode(StatementGroup o) {
		return o.getStatements().hashCode();
	}

	/**
	 * Returns a hash code for the given object.
	 *
	 * @see java.lang.Object#hashCode()
	 * @param o
	 *            the object to create a hash for
	 * @return the hash code of the object
	 */
	public static int hashCode(SiteLink o) {
		int result;
		result = o.getBadges().hashCode();
		result = prime * result + o.getPageTitle().hashCode();
		result = prime * result + o.getSiteKey().hashCode();
		return result;
	}

	/**
	 * Returns a hash code for the given object.
	 *
	 * @see java.lang.Object#hashCode()
	 * @param o
	 *            the object to create a hash for
	 * @return the hash code of the object
	 */
	public static int hashCode(PropertyDocument o) {
		int result;
		result = hashCodeForTermedDocument(o);
		result = prime * result + o.getStatementGroups().hashCode();
		result = prime * result + o.getDatatype().hashCode();
		return result;
	}

	/**
	 * Returns a hash code for the given object.
	 *
	 * @see java.lang.Object#hashCode()
	 * @param o
	 *            the object to create a hash for
	 * @return the hash code of the object
	 */
	public static int hashCode(ItemDocument o) {
		int result;
		result = hashCodeForTermedDocument(o);
		result = prime * result + o.getStatementGroups().hashCode();
		result = prime * result + o.getSiteLinks().hashCode();
		return result;
	}

	/**
	 * Returns a hash code for the given object.
	 *
	 * @see java.lang.Object#hashCode()
	 * @param o
	 *            the object to create a hash for
	 * @return the hash code of the object
	 */
	public static int hashCode(LexemeDocument o) {
		int result;
		result = o.getLexicalCategory().hashCode();
		result = prime * result + o.getLanguage().hashCode();
		result = prime * result + o.getLemmas().hashCode();
		result = prime * result + Long.hashCode(o.getRevisionId());
		result = prime * result + o.getStatementGroups().hashCode();
		return result;
	}

	/**
	 * Returns a hash code for the given object.
	 *
	 * @see java.lang.Object#hashCode()
	 * @param o
	 *            the object to create a hash for
	 * @return the hash code of the object
	 */
	public static int hashCode(FormDocument o) {
		int result;
		result = o.getGrammaticalFeatures().hashCode();
		result = prime * result + o.getRepresentations().hashCode();
		result = prime * result + Long.hashCode(o.getRevisionId());
		result = prime * result + o.getStatementGroups().hashCode();
		return result;
	}

	/**
	 * Returns a hash code for the given object.
	 *
	 * @see java.lang.Object#hashCode()
	 * @param o
	 *            the object to create a hash for
	 * @return the hash code of the object
	 */
	public static int hashCode(SenseDocument o) {
		int result;
		result = o.getGlosses().hashCode();
		result = prime * result + Long.hashCode(o.getRevisionId());
		result = prime * result + o.getStatementGroups().hashCode();
		return result;
	}

	/**
	 * Returns a hash code for the given object.
	 *
	 * @see java.lang.Object#hashCode()
	 * @param o
	 *            the object to create a hash for
	 * @return the hash code of the object
	 */
	public static int hashCode(MediaInfoDocument o) {
		int result;
		result = o.getLabels().hashCode();
		result = prime * result + o.getStatementGroups().hashCode();
		return result;
	}

	/**
	 * Returns a hash code for the given object.
	 *
	 * @see java.lang.Object#hashCode()
	 * @param o
	 *            the object to create a hash for
	 * @return the hash code of the object
	 */
	private static int hashCodeForTermedDocument(TermedDocument o) {
		int result;
		result = o.getAliases().hashCode();
		result = prime * result + o.getDescriptions().hashCode();
		result = prime * result + o.getLabels().hashCode();
		result = prime * result + Long.hashCode(o.getRevisionId());
		return result;
	}

	/**
	 * Returns a hash code for the given object.
	 *
	 * @see java.lang.Object#hashCode()
	 * @param o
	 *            the object to create a hash for
	 * @return the hash code of the object
	 */
	public static int hashCode(EntityRedirectDocument o) {
		int result;
		result = o.getEntityId().hashCode();
		result = prime * result + o.getTargetId().hashCode();
		result = prime * result + Long.hashCode(o.getRevisionId());
		return result;
	}
}
