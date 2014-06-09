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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;

/**
 * This models a property constraint that says that for every item that has this
 * property with some other item, the former has also property
 * <i>r<sub>1</sub></i>, optionally with value <i>s<sub>1</sub></i>, analogously
 * for <i>r<sub>2</sub></i> and <i>s<sub>2</sub></i>, and the list of items
 * <i>q<sub>1</sub></i> &hellip; <i>q<sub>n</sub></i>.
 * <p>
 * For example, property <i>mother (P25)</i> relates an item (the child) to
 * another one (the mother), and the former must also have a <i>date of birth
 * (P569)</i>.
 * <p>
 * Another example is property <i>IUCN conservation status (P141)</i>, the
 * conservation status assigned by the International Union for Conservation of
 * Nature, that relates items to another one such that the former has a <i>taxon
 * rank (P105)</i>, a level in a taxonomic hierarchy, which could be a
 * <i>species (Q7432)</i>, a <i>subspecies (Q68947)</i>, or a <i>variety
 * (Q767728)</i>.
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintItem implements Constraint {

	final PropertyIdValue constrainedProperty;
	final PropertyIdValue property;
	final ItemIdValue item;
	final PropertyIdValue property2;
	final ItemIdValue item2;
	final List<ItemIdValue> items = new ArrayList<ItemIdValue>();
	final List<ItemIdValue> exceptions = new ArrayList<ItemIdValue>();

	/**
	 * Constructs a new {@link ConstraintItem}.
	 * 
	 * @param constrainedProperty
	 *            constrained property
	 * @param property
	 *            a property
	 * @param item
	 *            an item
	 * @param property2
	 *            another property
	 * @param item2
	 *            another item
	 * @param items
	 *            list of items
	 * @param exceptions
	 *            list of exceptions
	 */
	public ConstraintItem(PropertyIdValue constrainedProperty,
			PropertyIdValue property, ItemIdValue item,
			PropertyIdValue property2, ItemIdValue item2,
			List<ItemIdValue> items, List<ItemIdValue> exceptions) {
		Validate.notNull(constrainedProperty, "Property cannot be null.");
		this.constrainedProperty = constrainedProperty;
		this.property = property;
		this.item = item;
		this.property2 = property2;
		this.item2 = item2;
		if (items != null) {
			this.items.addAll(items);
		}
		if (exceptions != null) {
			this.exceptions.addAll(exceptions);
		}
	}

	@Override
	public PropertyIdValue getConstrainedProperty() {
		return this.constrainedProperty;
	}

	/**
	 * Returns the (first) property.
	 * 
	 * @return the (first) property
	 */
	public PropertyIdValue getProperty() {
		return this.property;
	}

	/**
	 * Returns the (first) item.
	 * 
	 * @return the (first) item
	 */
	public ItemIdValue getItem() {
		return this.item;
	}

	/**
	 * Returns the second property.
	 * 
	 * @return the second property
	 */
	public PropertyIdValue getProperty2() {
		return this.property2;
	}

	/**
	 * Returns the second item.
	 * 
	 * @return the second item
	 */
	public ItemIdValue getItem2() {
		return this.item2;
	}

	public List<ItemIdValue> getItems() {
		return this.items;
	}

	/**
	 * Returns the exceptions.
	 * 
	 * @return the exceptions
	 */
	public List<ItemIdValue> getExceptions() {
		return this.exceptions;
	}

	@Override
	public <T> T accept(ConstraintVisitor<T> visitor) {
		Validate.notNull(visitor, "Visitor cannot be null.");
		return visitor.visit(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ConstraintItem)) {
			return false;
		}
		ConstraintItem other = (ConstraintItem) obj;
		boolean ret = this.constrainedProperty
				.equals(other.constrainedProperty)
				&& equals(this.property, other.property)
				&& equals(this.item, other.item)
				&& equals(this.property2, other.property2)
				&& equals(this.item2, other.item2)
				&& equals(this.items, other.items)
				&& equals(this.exceptions, other.exceptions);
		return ret;
	}

	private boolean equals(Object obj0, Object obj1) {
		if (obj0 == null) {
			return (obj1 == null);
		}
		return obj0.equals(obj1);
	}

	@Override
	public int hashCode() {
		return (this.constrainedProperty.hashCode() + (0x1F * this.property
				.hashCode()));
	}

}
