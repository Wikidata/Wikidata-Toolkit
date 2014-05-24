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

import java.util.List;

import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.constraint.Constraint;
import org.wikidata.wdtk.dumpfiles.constraint.ConstraintOneOf;
import org.wikidata.wdtk.dumpfiles.renderer.format.RendererFormat;

/**
 * 
 * @author Julian Mendez
 * 
 */
class ConstraintOneOfRenderer implements ConstraintRenderer {

	final RendererFormat f;

	public ConstraintOneOfRenderer(RendererFormat rendererFormat) {
		this.f = rendererFormat;
	}

	@Override
	public void renderConstraint(Constraint c) {
		if (c instanceof ConstraintOneOf) {
			render((ConstraintOneOf) c);
		}
	}

	public void render(ConstraintOneOf c) {
		render(c.getConstrainedProperty(), c.getValues());
	}

	public void render(PropertyIdValue p, List<ItemIdValue> values) {
		if ((p == null) || (values == null)) {
			return;
		}
		this.f.addInverseFunctionalObjectProperty(this.f.a_s(p));
		this.f.addObjectPropertyRange(this.f.a_v(p),
				this.f.getObjectOneOf(values));
	}

}
