package org.wikidata.wdtk.dumpfiles.constraint;

import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;

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
 * This models a property constraint. It is a restriction applied to a property.
 * <p>
 * Constraints are actually templates transcluded from the property talk page.
 * They help understand the semantics of the different properties and are a
 * useful tool available to detect possible mistakes. For example, property
 * <i>occupation (P106)</i> has a constraint that says that all elements related
 * by that property have to be instances of item <i>occupation (Q13516667)</i>.
 * <p>
 * Constraints are defined and used according to common sense. They are designed
 * to be flexible regarding language ambiguity and polysemy. Exceptions in
 * constraints are at some extent beyond the purpose of this modeling.
 * <p>
 * Some constraints are violated because the exceptions are rare. Every
 * constraint has a category of properties using it and a list of violations.
 * Both, the category and the list, are useful tools to detect mistakes or
 * anomalies.
 * <p>
 * Other constraints are violated because properties used to describe fictional
 * things are the same as the ones used for real-world things. An item in a
 * fictional universe does not need to follow a real-world behavior.
 * 
 * @author Julian Mendez
 * 
 */
public interface Constraint {

	/**
	 * Accepts a visitor for this constraint.
	 * 
	 * @param <T>
	 *            the return type of the visitor's methods
	 * @param visitor
	 *            visitor
	 * @return the visit result
	 */
	<T> T accept(ConstraintVisitor<T> visitor);

	/**
	 * Returns the constrained property.
	 * 
	 * @return the constrained property
	 */
	PropertyIdValue getConstrainedProperty();

}
