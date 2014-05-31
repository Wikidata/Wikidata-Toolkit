package org.wikidata.wdtk.dumpfiles.renderer.constraint;

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

import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.constraint.Constraint;
import org.wikidata.wdtk.dumpfiles.constraint.ConstraintMultiValue;
import org.wikidata.wdtk.dumpfiles.renderer.format.RendererFormat;

/**
 * 
 * @author Julian Mendez
 * 
 */
class ConstraintMultiValueRenderer implements ConstraintRenderer {

	final RendererFormat f;

	public ConstraintMultiValueRenderer(RendererFormat rendererFormat) {
		this.f = rendererFormat;
	}

	@Override
	public void renderConstraint(Constraint c) {
		if (c instanceof ConstraintMultiValue) {
			render((ConstraintMultiValue) c);
		}
	}

	public void render(ConstraintMultiValue c) {
		render(c.getConstrainedProperty());
	}

	public void render(PropertyIdValue p) {
		if (p == null) {
			return;
		}
		this.f.addInverseFunctionalObjectProperty(this.f.getPs(p));
		this.f.addSubClassOf(this.f.owlThing(), this.f
				.getObjectComplementOf(this.f.getObjectExactCardinality(1,
						this.f.getPs(p))));
	}

}
