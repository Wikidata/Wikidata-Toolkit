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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;

/**
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintItem implements Constraint {

	final PropertyIdValue property;
	final ItemIdValue item;
	final PropertyIdValue property2;
	final ItemIdValue item2;
	final List<ItemIdValue> items = new ArrayList<ItemIdValue>();
	final List<ItemIdValue> exceptions = new ArrayList<ItemIdValue>();

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
		this.items.addAll(items);
		this.exceptions.addAll(exceptions);
	}

	final PropertyIdValue constrainedProperty;

	@Override
	public PropertyIdValue getConstrainedProperty() {
		return this.constrainedProperty;
	}

	public PropertyIdValue getProperty() {
		return this.property;
	}

	public ItemIdValue getItem() {
		return this.item;
	}

	public PropertyIdValue getProperty2() {
		return this.property2;
	}

	public ItemIdValue getItem2() {
		return this.item2;
	}

	public List<ItemIdValue> getItems() {
		return this.items;
	}

	public List<ItemIdValue> getExceptions() {
		return this.exceptions;
	}

	@Override
	public <T> T accept(ConstraintVisitor<T> visitor) {
		Validate.notNull(visitor, "Visitor cannot be null.");
		return visitor.visit(this);
	}

}
