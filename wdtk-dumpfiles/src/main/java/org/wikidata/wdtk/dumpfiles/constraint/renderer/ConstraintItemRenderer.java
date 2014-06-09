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

import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Resource;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.constraint.format.RendererFormat;
import org.wikidata.wdtk.dumpfiles.constraint.model.Constraint;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintItem;

/**
 * 
 * @author Julian Mendez
 * 
 */
class ConstraintItemRenderer implements ConstraintRenderer {

	final RendererFormat f;

	public ConstraintItemRenderer(RendererFormat rendererFormat) {
		this.f = rendererFormat;
	}

	@Override
	public void renderConstraint(Constraint c) {
		if (c instanceof ConstraintItem) {
			render((ConstraintItem) c);
		}
	}

	public void render(ConstraintItem c) {
		render(c.getConstrainedProperty(), c.getProperty(), c.getItem(),
				c.getProperty2(), c.getItem2(), c.getItems(), c.getExceptions());
	}

	public void render(PropertyIdValue p, PropertyIdValue r1, ItemIdValue q1,
			PropertyIdValue r2, ItemIdValue q2, List<ItemIdValue> values,
			List<ItemIdValue> exceptions) {
		this.f.addDeclarationObjectProperty(this.f.getPs(p));

		this.f.addInverseFunctionalObjectProperty(this.f.getPs(p));
		renderPart(p, r1, q1);
		renderPart(p, r2, q2);
		renderPart(p, values);
		renderPart(p, exceptions);
	}

	public void renderPart(PropertyIdValue p, PropertyIdValue r, ItemIdValue q) {
		if ((p == null) || (r == null)) {
			return;
		}
		if (q == null) {
			this.f.addDeclarationObjectProperty(this.f.getPs(p));
			this.f.addDeclarationObjectProperty(this.f.getPs(r));
			this.f.addObjectPropertyDomain(
					this.f.getPs(p),
					this.f.getObjectSomeValuesFrom(this.f.getPs(r),
							this.f.owlThing()));
		} else {
			this.f.addDeclarationObjectProperty(this.f.getPs(p));
			this.f.addDeclarationObjectProperty(this.f.getPs(r));
			this.f.addDeclarationObjectProperty(this.f.getPv(r));
			this.f.addDeclarationNamedIndividual(this.f.getItem(q));
			this.f.addObjectPropertyDomain(this.f.getPs(p), this.f
					.getObjectSomeValuesFrom(this.f.getPs(r), this.f
							.getObjectSomeValuesFrom(this.f.getPv(r),
									this.f.getObjectOneOf(this.f.getItem(q)))));
		}
	}

	public static List<Resource> getListAndDeclareItems(RendererFormat f,
			List<ItemIdValue> list) {
		List<Resource> ret = new ArrayList<Resource>();
		for (ItemIdValue q : list) {
			f.addDeclarationNamedIndividual(f.getItem(q));
			ret.add(f.getItem(q));
		}
		return ret;
	}

	public void renderPart(PropertyIdValue p, List<ItemIdValue> values) {
		if ((p == null) || (values == null)) {
			return;
		}
		if (values.isEmpty()) {
			return;
		}
		this.f.addDeclarationObjectProperty(this.f.getPs(p));
		this.f.addObjectPropertyDomain(this.f.getPs(p),
				this.f.getObjectOneOf(getListAndDeclareItems(this.f, values)));
	}

}
