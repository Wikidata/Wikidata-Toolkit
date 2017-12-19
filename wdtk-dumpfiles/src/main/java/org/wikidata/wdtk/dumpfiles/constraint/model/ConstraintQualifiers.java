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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.constraint.template.TemplateConstant;

/**
 * This models a property constraint that says that a property can have only the
 * listed qualifiers. If the list is empty, the property cannot have qualifiers.
 * <p>
 * For example, <i>head of government (P6)</i> and <i>flag image (P41)</i>
 * should be used only with qualifiers <i>start time (P580)</i> and <i>end time
 * (P582)</i>. Instead, <i>child (P40)</i> should be used without any qualifier.
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintQualifiers implements Constraint {

	final PropertyIdValue constrainedProperty;
	final List<PropertyIdValue> list = new ArrayList<PropertyIdValue>();

	/**
	 * Constructs a new {@link ConstraintQualifiers}.
	 * 
	 * @param constrainedProperty
	 *            constrained property
	 * @param list
	 *            list of properties
	 */
	public ConstraintQualifiers(PropertyIdValue constrainedProperty,
			List<PropertyIdValue> list) {
		Validate.notNull(list, "List cannot be null.");
		Validate.notNull(constrainedProperty, "Property cannot be null.");
		this.constrainedProperty = constrainedProperty;
		this.list.addAll(list);
	}

	@Override
	public PropertyIdValue getConstrainedProperty() {
		return this.constrainedProperty;
	}

	/**
	 * Returns a list of properties.
	 * 
	 * @return a list of properties
	 */
	public List<PropertyIdValue> getList() {
		return Collections.unmodifiableList(this.list);
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
		if (!(obj instanceof ConstraintQualifiers)) {
			return false;
		}
		ConstraintQualifiers other = (ConstraintQualifiers) obj;
		return (this.constrainedProperty.equals(other.constrainedProperty) && this.list
				.equals(other.list));
	}

	@Override
	public int hashCode() {
		return (this.constrainedProperty.hashCode() + (0x1F * this.list
				.hashCode()));
	}

	@Override
	public String getTemplate() {
		StringBuilder sb = new StringBuilder();
		sb.append(TemplateConstant.OPENING_BRACES);
		sb.append("Constraint:Qualifiers");
		sb.append(TemplateConstant.VERTICAL_BAR);
		sb.append("list");
		sb.append(TemplateConstant.EQUALS_SIGN);
		Iterator<PropertyIdValue> it = this.list.iterator();
		while (it.hasNext()) {
			PropertyIdValue property = it.next();
			sb.append(TemplateConstant.OPENING_BRACES);
			sb.append("P");
			sb.append(TemplateConstant.VERTICAL_BAR);
			String pNumber = property.getId().substring(1);
			sb.append(pNumber);
			sb.append(TemplateConstant.CLOSING_BRACES);
			if (it.hasNext()) {
				sb.append(TemplateConstant.COMMA);
				sb.append(TemplateConstant.SPACE);
			}
		}
		sb.append(TemplateConstant.CLOSING_BRACES);
		return sb.toString();
	}

	@Override
	public String toString() {
		return this.constrainedProperty.getId() + " " + getTemplate();
	}

}
