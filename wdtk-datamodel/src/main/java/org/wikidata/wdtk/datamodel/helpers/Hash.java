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
package org.wikidata.wdtk.datamodel.helpers;

import java.util.Objects;

import org.wikidata.wdtk.datamodel.interfaces.AliasUpdate;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityRedirectDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityUpdate;
import org.wikidata.wdtk.datamodel.interfaces.FormDocument;
import org.wikidata.wdtk.datamodel.interfaces.FormUpdate;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemUpdate;
import org.wikidata.wdtk.datamodel.interfaces.LabeledStatementDocumentUpdate;
import org.wikidata.wdtk.datamodel.interfaces.LexemeDocument;
import org.wikidata.wdtk.datamodel.interfaces.LexemeUpdate;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoDocument;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoUpdate;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyUpdate;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.SenseDocument;
import org.wikidata.wdtk.datamodel.interfaces.SenseUpdate;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementDocumentUpdate;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermedDocument;
import org.wikidata.wdtk.datamodel.interfaces.TermedStatementDocumentUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

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
	private static final int PRIME = 31;

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
		result = PRIME * result + o.getSiteIri().hashCode();
		result = PRIME * result + o.getEntityType().hashCode();
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
		result = PRIME * result + o.getMonth();
		result = PRIME * result + o.getDay();
		result = PRIME * result + o.getHour();
		result = PRIME * result + o.getMinute();
		result = PRIME * result + o.getSecond();
		result = PRIME * result + o.getPrecision();
		result = PRIME * result + o.getBeforeTolerance();
		result = PRIME * result + o.getAfterTolerance();
		result = PRIME * result + o.getTimezoneOffset();
		result = PRIME * result + o.getPreferredCalendarModel().hashCode();
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
		result = PRIME * result + (int) (value ^ (value >>> 32));
		value = Double.valueOf(o.getLongitude()).hashCode();
		result = PRIME * result + (int) (value ^ (value >>> 32));
		value = Double.valueOf(o.getPrecision()).hashCode();
		result = PRIME * result + (int) (value ^ (value >>> 32));
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
		result = PRIME * result + o.getText().hashCode();
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
		result = PRIME * result + o.getUnit().hashCode();
		if(o.getLowerBound() != null) {
			result = PRIME * result + o.getLowerBound().hashCode();
		}
		if(o.getUpperBound() != null) {
			result = PRIME * result + o.getUpperBound().hashCode();
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
		result = PRIME * result + o.getPropertyId().hashCode();
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
		result = PRIME * result + o.getMainSnak().hashCode();
		result = PRIME * result + o.getQualifiers().hashCode();
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
		result = PRIME * result + o.getMainSnak().hashCode();
		result = PRIME * result + o.getQualifiers().hashCode();
		result = PRIME * result + o.getReferences().hashCode();
		result = PRIME * result + o.getRank().hashCode();
		result = PRIME * result + o.getStatementId().hashCode();
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
		result = PRIME * result + o.getPageTitle().hashCode();
		result = PRIME * result + o.getSiteKey().hashCode();
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
		result = PRIME * result + o.getStatementGroups().hashCode();
		result = PRIME * result + o.getDatatype().hashCode();
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
		result = PRIME * result + o.getStatementGroups().hashCode();
		result = PRIME * result + o.getSiteLinks().hashCode();
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
		result = PRIME * result + o.getLanguage().hashCode();
		result = PRIME * result + o.getLemmas().hashCode();
		result = PRIME * result + Long.hashCode(o.getRevisionId());
		result = PRIME * result + o.getStatementGroups().hashCode();
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
		result = PRIME * result + o.getRepresentations().hashCode();
		result = PRIME * result + Long.hashCode(o.getRevisionId());
		result = PRIME * result + o.getStatementGroups().hashCode();
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
		result = PRIME * result + Long.hashCode(o.getRevisionId());
		result = PRIME * result + o.getStatementGroups().hashCode();
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
		result = PRIME * result + o.getStatementGroups().hashCode();
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
		result = PRIME * result + o.getDescriptions().hashCode();
		result = PRIME * result + o.getLabels().hashCode();
		result = PRIME * result + Long.hashCode(o.getRevisionId());
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
		result = PRIME * result + o.getTargetId().hashCode();
		result = PRIME * result + Long.hashCode(o.getRevisionId());
		return result;
	}

	/**
	 * Calculates hash code for given {@link TermUpdate} object.
	 *
	 * @see Object#hashCode()
	 * @param o
	 *            the object to create a hash for
	 * @return object's hash code
	 */
	public static int hashCode(TermUpdate o) {
		return Objects.hash(o.getModified(), o.getRemoved());
	}

	/**
	 * Calculates hash code for given {@link AliasUpdate} object.
	 *
	 * @see Object#hashCode()
	 * @param o
	 *            the object to create a hash for
	 * @return object's hash code
	 */
	public static int hashCode(AliasUpdate o) {
		return Objects.hash(o.getRecreated(), o.getAdded(), o.getRemoved());
	}

	/**
	 * Calculates hash code for given {@link StatementUpdate} object.
	 *
	 * @see Object#hashCode()
	 * @param o
	 *            the object to create a hash for
	 * @return object's hash code
	 */
	public static int hashCode(StatementUpdate o) {
		return Objects.hash(o.getAdded(), o.getReplaced(), o.getRemoved());
	}

	private static int hashCodeForEntityUpdate(EntityUpdate o) {
		return Objects.hash(o.getEntityId(), o.getBaseRevisionId());
	}

	private static int hashCodeForStatementDocumentUpdate(StatementDocumentUpdate o) {
		return hashCodeForEntityUpdate(o) * PRIME + Objects.hash(o.getStatements());
	}

	private static int hashCodeForLabeledStatementDocumentUpdate(LabeledStatementDocumentUpdate o) {
		return hashCodeForStatementDocumentUpdate(o) * PRIME + Objects.hash(o.getLabels());
	}

	private static int hashCodeForTermedStatementDocumentUpdate(TermedStatementDocumentUpdate o) {
		return hashCodeForLabeledStatementDocumentUpdate(o) * PRIME + Objects.hash(o.getDescriptions(), o.getAliases());
	}

	/**
	 * Calculates hash code for given {@link MediaInfoUpdate} object.
	 *
	 * @see Object#hashCode()
	 * @param o
	 *            the object to create a hash for
	 * @return object's hash code
	 */
	public static int hashCode(MediaInfoUpdate o) {
		return hashCodeForLabeledStatementDocumentUpdate(o);
	}

	/**
	 * Calculates hash code for given {@link ItemUpdate} object.
	 *
	 * @see Object#hashCode()
	 * @param o
	 *            the object to create a hash for
	 * @return object's hash code
	 */
	public static int hashCode(ItemUpdate o) {
		return hashCodeForTermedStatementDocumentUpdate(o) * PRIME
				+ Objects.hash(o.getModifiedSiteLinks(), o.getRemovedSiteLinks());
	}

	/**
	 * Calculates hash code for given {@link PropertyUpdate} object.
	 *
	 * @see Object#hashCode()
	 * @param o
	 *            the object to create a hash for
	 * @return object's hash code
	 */
	public static int hashCode(PropertyUpdate o) {
		return hashCodeForTermedStatementDocumentUpdate(o);
	}

	/**
	 * Calculates hash code for given {@link SenseUpdate} object.
	 *
	 * @see Object#hashCode()
	 * @param o
	 *            the object to create a hash for
	 * @return object's hash code
	 */
	public static int hashCode(SenseUpdate o) {
		return hashCodeForStatementDocumentUpdate(o) * PRIME + Objects.hash(o.getGlosses());
	}

	/**
	 * Calculates hash code for given {@link FormUpdate} object.
	 *
	 * @see Object#hashCode()
	 * @param o
	 *            the object to create a hash for
	 * @return object's hash code
	 */
	public static int hashCode(FormUpdate o) {
		return hashCodeForStatementDocumentUpdate(o) * PRIME
				+ Objects.hash(o.getRepresentations(), o.getGrammaticalFeatures());
	}

	/**
	 * Calculates hash code for given {@link LexemeUpdate} object.
	 *
	 * @see Object#hashCode()
	 * @param o
	 *            the object to create a hash for
	 * @return object's hash code
	 */
	public static int hashCode(LexemeUpdate o) {
		return hashCodeForStatementDocumentUpdate(o) * PRIME + Objects.hash(
				o.getLanguage(),
				o.getLexicalCategory(),
				o.getLemmas(),
				o.getAddedSenses(),
				o.getUpdatedSenses(),
				o.getRemovedSenses(),
				o.getAddedForms(),
				o.getUpdatedForms(),
				o.getRemovedForms());
	}

}
