package org.wikidata.wdtk.dumpfiles.constraint;

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
 * This class models a visitor of <code>Constraint</code>.
 * 
 * @author Julian Mendez
 * 
 * @see Constraint
 */
public interface ConstraintVisitor<T> {

	T visit(ConstraintSingleValue constraint);

	T visit(ConstraintUniqueValue constraint);

	T visit(ConstraintFormat constraint);

	T visit(ConstraintOneOf constraint);

	T visit(ConstraintSymmetric constraint);

	T visit(ConstraintInverse constraint);

	T visit(ConstraintExistingFile constraint);

	T visit(ConstraintTargetRequiredClaim constraint);

	T visit(ConstraintItem constraint);

	T visit(ConstraintType constraint);

	T visit(ConstraintValueType constraint);

	T visit(ConstraintRange constraint);

	T visit(ConstraintMultiValue constraint);

	T visit(ConstraintConflictsWith constraint);

	T visit(ConstraintQualifier constraint);

	T visit(ConstraintPerson constraint);

	T visit(ConstraintTaxon constraint);

}
