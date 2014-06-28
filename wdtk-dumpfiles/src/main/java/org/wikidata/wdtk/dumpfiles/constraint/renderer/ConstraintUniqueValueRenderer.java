package org.wikidata.wdtk.dumpfiles.constraint.renderer;

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

import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.constraint.format.RendererFormat;
import org.wikidata.wdtk.dumpfiles.constraint.model.Constraint;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintUniqueValue;
import org.wikidata.wdtk.rdf.WikidataPropertyTypes;

/**
 * 
 * @author Julian Mendez
 * 
 */
class ConstraintUniqueValueRenderer implements ConstraintRenderer {

	final RendererFormat f;

	public ConstraintUniqueValueRenderer(RendererFormat rendererFormat) {
		this.f = rendererFormat;
	}

	@Override
	public void renderConstraint(Constraint c) {
		if (c instanceof ConstraintUniqueValue) {
			render((ConstraintUniqueValue) c);
		}
	}

	public void render(ConstraintUniqueValue c) {
		render(c.getConstrainedProperty());
	}

	public boolean isObjectProperty(PropertyIdValue constrainedProperty) {
		WikidataPropertyTypes wdPropertyTypes = new WikidataPropertyTypes();
		String propertyType = wdPropertyTypes
				.getPropertyType(constrainedProperty);
		return propertyType.equals(DatatypeIdValue.DT_ITEM);
	}

	public void render(PropertyIdValue p) {
		if (p == null) {
			return;
		}
		this.f.addDeclarationObjectProperty(this.f.getPs(p));
		this.f.addInverseFunctionalObjectProperty(this.f.getPs(p));

		if (isObjectProperty(p)) {
			this.f.addDeclarationObjectProperty(this.f.getPv(p));
			this.f.addInverseFunctionalObjectProperty(this.f.getPv(p));
		} else {
			this.f.addDeclarationDatatypeProperty(this.f.getPv(p));
			this.f.addHasKey(this.f.owlThing(), null, this.f.getPv(p));
		}
	}

}
