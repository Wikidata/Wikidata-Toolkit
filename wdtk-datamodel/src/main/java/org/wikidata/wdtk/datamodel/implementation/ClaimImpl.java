package org.wikidata.wdtk.datamodel.implementation;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Snak;

/**
 * Implementation of {@link Claim}.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class ClaimImpl implements Claim {

	final EntityIdValue subject;
	final Snak mainSnak;
	final List<? extends Snak> qualifiers;

	/**
	 * Constructor.
	 * 
	 * @param subject
	 *            the subject the Claim refers to
	 * @param mainSnak
	 *            the main Snak of the Claim
	 * @param qualifiers
	 *            the qualifiers of the Claim
	 */
	ClaimImpl(EntityIdValue subject, Snak mainSnak,
			List<? extends Snak> qualifiers) {
		Validate.notNull(subject, "Statement subjects cannot be null");
		Validate.notNull(mainSnak, "Statement main Snaks cannot be null");
		Validate.notNull(qualifiers, "Statement qualifiers cannot be null");

		this.subject = subject;
		this.mainSnak = mainSnak;
		this.qualifiers = qualifiers;
	}

	@Override
	public EntityIdValue getSubject() {
		return subject;
	}

	@Override
	public Snak getMainSnak() {
		return mainSnak;
	}

	@Override
	public List<? extends Snak> getQualifiers() {
		return Collections.unmodifiableList(qualifiers);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder(31, 569).append(this.subject)
				.append(this.mainSnak).append(this.qualifiers).toHashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ClaimImpl)) {
			return false;
		}

		ClaimImpl other = (ClaimImpl) obj;
		return this.subject.equals(other.subject)
				&& this.mainSnak.equals(other.mainSnak)
				&& this.qualifiers.equals(other.qualifiers);
	}

}
