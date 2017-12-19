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

import java.util.List;

import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.constraint.format.RendererFormat;
import org.wikidata.wdtk.dumpfiles.constraint.model.Constraint;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintConflictsWith;
import org.wikidata.wdtk.dumpfiles.constraint.model.PropertyValues;

/**
 * 
 * @author Julian Mendez
 * 
 */
class ConstraintConflictsWithRenderer implements ConstraintRenderer {

	final RendererFormat f;

	public ConstraintConflictsWithRenderer(RendererFormat rendererFormat) {
		this.f = rendererFormat;
	}

	@Override
	public void renderConstraint(Constraint c) {
		if (c instanceof ConstraintConflictsWith) {
			render((ConstraintConflictsWith) c);
		}
	}

	public void render(ConstraintConflictsWith c) {
		render(c.getConstrainedProperty(), c.getList());
	}

	public void render(PropertyIdValue p, List<PropertyValues> list) {
		if ((p == null) || (list == null) || list.isEmpty()) {
			return;
		}
		this.f.addDeclarationObjectProperty(this.f.getPs(p));

		this.f.addInverseFunctionalObjectProperty(this.f.getPs(p));
		for (PropertyValues propertyValues : list) {
			renderPart(p, propertyValues.getProperty(),
					propertyValues.getItems());
		}
	}

	public void renderPart(PropertyIdValue p, PropertyIdValue r,
			List<ItemIdValue> values) {
		if ((p == null) || (r == null)) {
			return;
		}
		if ((values == null) || values.isEmpty()) {
			this.f.addDeclarationObjectProperty(this.f.getPv(p));
			this.f.addDeclarationObjectProperty(this.f.getPs(r));
			this.f.addDisjointClasses(
					this.f.getObjectSomeValuesFrom(this.f.getPv(p),
							this.f.owlThing()),
					this.f.getObjectSomeValuesFrom(this.f.getPs(r),
							this.f.owlThing()));
		} else {
			this.f.addDeclarationObjectProperty(this.f.getPv(p));
			this.f.addDeclarationObjectProperty(this.f.getPs(r));
			this.f.addDeclarationObjectProperty(this.f.getPv(r));
			this.f.addDisjointClasses(this.f.getObjectSomeValuesFrom(
					this.f.getPv(p), this.f.owlThing()), this.f
					.getObjectSomeValuesFrom(this.f.getPs(r), this.f
							.getObjectSomeValuesFrom(this.f.getPv(r), this.f
									.getObjectOneOf(ConstraintItemRenderer
											.getListAndDeclareItems(this.f,
													values)))));
		}
	}

}
