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

import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.constraint.Constraint;
import org.wikidata.wdtk.dumpfiles.constraint.ConstraintValueType;
import org.wikidata.wdtk.dumpfiles.constraint.RelationType;
import org.wikidata.wdtk.dumpfiles.parser.constraint.ConstraintMainParser;
import org.wikidata.wdtk.dumpfiles.renderer.format.RendererFormat;

/**
 * 
 * @author Julian Mendez
 * 
 */
class ConstraintValueTypeRenderer implements ConstraintRenderer {

	public static final String P_SUBCLASS_OF = "P279";

	final PropertyIdValue subclassOf;

	final RendererFormat f;

	public ConstraintValueTypeRenderer(RendererFormat rendererFormat) {
		this.f = rendererFormat;
		DataObjectFactoryImpl factory = new DataObjectFactoryImpl();
		this.subclassOf = factory.getPropertyIdValue(P_SUBCLASS_OF,
				ConstraintMainParser.DEFAULT_BASE_IRI);
	}

	@Override
	public void renderConstraint(Constraint c) {
		if (c instanceof ConstraintValueType) {
			render((ConstraintValueType) c);
		}
	}

	public void render(ConstraintValueType c) {
		if (c.getRelation().equals(RelationType.INSTANCE)) {
			renderInstanceOf(c.getConstrainedProperty(), c.getClassId());
		} else {
			renderSubclassOf(c.getConstrainedProperty(), c.getClassId());
		}
	}

	public void renderInstanceOf(PropertyIdValue p, ItemIdValue q) {
		if ((p == null) || (q == null)) {
			return;
		}
		this.f.addInverseFunctionalObjectProperty(this.f.getPs(p));
		this.f.addObjectPropertyRange(this.f.getPv(p),
				this.f.getObjectOneOf(this.f.getItem(q)));
	}

	public void renderSubclassOf(PropertyIdValue p, ItemIdValue q) {
		if ((p == null) || (q == null)) {
			return;
		}
		ConstraintTargetRequiredClaimRenderer otherRenderer = new ConstraintTargetRequiredClaimRenderer(
				this.f);
		otherRenderer.render(p, this.subclassOf, q);
	}

}
