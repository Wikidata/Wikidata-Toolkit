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
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintTargetRequiredClaim implements Constraint {

	final PropertyIdValue property;
	final ItemIdValue item;

	public ConstraintTargetRequiredClaim(PropertyIdValue property) {
		Validate.notNull(property, "Property cannot be null.");
		this.property = property;
		this.item = null;
	}

	public ConstraintTargetRequiredClaim(PropertyIdValue property,
			ItemIdValue item) {
		Validate.notNull(property, "Property cannot be null.");
		this.property = property;
		this.item = item;
	}

	public PropertyIdValue getProperty() {
		return property;
	}

	public ItemIdValue getItem() {
		return item;
	}

	@Override
	public <T> T accept(ConstraintVisitor<T> visitor) {
		Validate.notNull(visitor, "Visitor cannot be null.");
		return visitor.visit(this);
	}

}
