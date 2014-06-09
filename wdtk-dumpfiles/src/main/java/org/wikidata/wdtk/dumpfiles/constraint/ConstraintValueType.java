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

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;

/**
 * This models a property constraint that says that for every item that has a
 * property with some other item, the latter has also a relation <i>instance of
 * (P31)</i> or <i>subclass of (P279)</i> to an item <i>q</i>. This is a
 * particular case of {@link ConstraintTargetRequiredClaim}.
 * <p>
 * For example, property <i>continent (Q5107)</i>, continent of which the
 * subject is a part, is related to an item that is an instance of <i>continent
 * (Q5107)</i>.
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintValueType implements Constraint {

	final ItemIdValue classId;
	final RelationType relation;

	/**
	 * Constructs a new {@link ConstraintValueType}.
	 * 
	 * @param constrainedProperty
	 *            constrained property
	 * @param classId
	 *            class identifier
	 * @param relation
	 *            relation type (instance-of or subclass-of)
	 */
	public ConstraintValueType(PropertyIdValue constrainedProperty,
			ItemIdValue classId, RelationType relation) {
		Validate.notNull(classId, "Class cannot be null.");
		Validate.notNull(relation, "Relation cannot be null.");
		Validate.notNull(constrainedProperty, "Property cannot be null.");
		this.constrainedProperty = constrainedProperty;
		this.classId = classId;
		this.relation = relation;
	}

	final PropertyIdValue constrainedProperty;

	@Override
	public PropertyIdValue getConstrainedProperty() {
		return this.constrainedProperty;
	}

	/**
	 * Returns the class identifier.
	 * 
	 * @return the class identifier
	 */
	public ItemIdValue getClassId() {
		return this.classId;
	}

	/**
	 * Returns the relation type.
	 * 
	 * @return the relation type
	 */
	public RelationType getRelation() {
		return this.relation;
	}

	@Override
	public <T> T accept(ConstraintVisitor<T> visitor) {
		Validate.notNull(visitor, "Visitor cannot be null.");
		return visitor.visit(this);
	}

}
