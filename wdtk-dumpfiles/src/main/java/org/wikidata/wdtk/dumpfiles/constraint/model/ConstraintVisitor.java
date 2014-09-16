package org.wikidata.wdtk.dumpfiles.constraint.model;

/*
 * #%L
 * Wikidata Toolkit Dump File Handling
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

/**
 * This class models a visitor of {@link Constraint}.
 *
 * @author Julian Mendez
 *
 * @see Constraint
 */
public interface ConstraintVisitor<T> {

	/**
	 * Visits a {@link ConstraintSingleValue}.
	 *
	 * @param constraint
	 *            constraint
	 * @return the result of the visit
	 */
	T visit(ConstraintSingleValue constraint);

	/**
	 * Visits a {@link ConstraintUniqueValue}.
	 *
	 * @param constraint
	 *            constraint
	 * @return the result of the visit
	 */
	T visit(ConstraintUniqueValue constraint);

	/**
	 * Visits a {@link ConstraintFormat}.
	 *
	 * @param constraint
	 *            constraint
	 * @return the result of the visit
	 */
	T visit(ConstraintFormat constraint);

	/**
	 * Visits a {@link ConstraintOneOf}.
	 *
	 * @param constraint
	 *            constraint
	 * @return the result of the visit
	 */
	T visit(ConstraintOneOf constraint);

	/**
	 * Visits a {@link ConstraintSymmetric}.
	 *
	 * @param constraint
	 *            constraint
	 * @return the result of the visit
	 */
	T visit(ConstraintSymmetric constraint);

	/**
	 * Visits a {@link ConstraintInverse}.
	 *
	 * @param constraint
	 *            constraint
	 * @return the result of the visit
	 */
	T visit(ConstraintInverse constraint);

	/**
	 * Visits a {@link ConstraintExistingFile}.
	 *
	 * @param constraint
	 *            constraint
	 * @return the result of the visit
	 */
	T visit(ConstraintExistingFile constraint);

	/**
	 * Visits a {@link ConstraintTargetRequiredClaim}.
	 *
	 * @param constraint
	 *            constraint
	 * @return the result of the visit
	 */
	T visit(ConstraintTargetRequiredClaim constraint);

	/**
	 * Visits a {@link ConstraintItem}.
	 *
	 * @param constraint
	 *            constraint
	 * @return the result of the visit
	 */
	T visit(ConstraintItem constraint);

	/**
	 * Visits a {@link ConstraintRange}.
	 *
	 * @param constraint
	 *            constraint
	 * @return the result of the visit
	 */
	T visit(ConstraintRange constraint);

	/**
	 * Visits a {@link ConstraintMultiValue}.
	 *
	 * @param constraint
	 *            constraint
	 * @return the result of the visit
	 */
	T visit(ConstraintMultiValue constraint);

	/**
	 * Visits a {@link ConstraintConflictsWith}.
	 *
	 * @param constraint
	 *            constraint
	 * @return the result of the visit
	 */
	T visit(ConstraintConflictsWith constraint);

	/**
	 * Visits a {@link ConstraintQualifier}.
	 *
	 * @param constraint
	 *            constraint
	 * @return the result of the visit
	 */
	T visit(ConstraintQualifier constraint);

}
