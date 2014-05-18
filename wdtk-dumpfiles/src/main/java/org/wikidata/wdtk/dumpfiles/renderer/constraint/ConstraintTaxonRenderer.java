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

import java.util.ArrayList;
import java.util.List;

import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.constraint.Constraint;
import org.wikidata.wdtk.dumpfiles.constraint.ConstraintItem;
import org.wikidata.wdtk.dumpfiles.constraint.ConstraintTaxon;
import org.wikidata.wdtk.dumpfiles.constraint.ConstraintType;
import org.wikidata.wdtk.dumpfiles.constraint.RelationType;
import org.wikidata.wdtk.dumpfiles.parser.constraint.ConstraintMainParser;
import org.wikidata.wdtk.dumpfiles.renderer.format.RendererFormat;

/**
 * 
 * @author Julian Mendez
 * 
 */
class ConstraintTaxonRenderer implements ConstraintRenderer {

	final RendererFormat f;

	/**
	 * Constructs a sequence of constraints according to the following list:
	 * {{Constraint:Type|class=Q16521|relation=instance}}
	 * {{Constraint:Item|property=P225}} {{Constraint:Item|property=P171}}
	 * {{Constraint:Item|property=P105}}
	 * 
	 * @param constrainedProperty
	 *            constrained property
	 * @return a sequence of constraints
	 */
	List<Constraint> getConstraintSequence(PropertyIdValue constrainedProperty) {
		List<Constraint> ret = new ArrayList<Constraint>();
		DataObjectFactoryImpl factory = new DataObjectFactoryImpl();

		ItemIdValue q16521 = factory.getItemIdValue("Q16521",
				ConstraintMainParser.DEFAULT_BASE_IRI);
		PropertyIdValue p225 = factory.getPropertyIdValue("P225",
				ConstraintMainParser.DEFAULT_BASE_IRI);
		PropertyIdValue p171 = factory.getPropertyIdValue("P171",
				ConstraintMainParser.DEFAULT_BASE_IRI);
		PropertyIdValue p105 = factory.getPropertyIdValue("P105",
				ConstraintMainParser.DEFAULT_BASE_IRI);

		ret.add(new ConstraintType(constrainedProperty, q16521,
				RelationType.INSTANCE));
		ret.add(new ConstraintItem(constrainedProperty, p225, null, null, null,
				null, null));
		ret.add(new ConstraintItem(constrainedProperty, p171, null, null, null,
				null, null));
		ret.add(new ConstraintItem(constrainedProperty, p105, null, null, null,
				null, null));

		return ret;
	}

	public ConstraintTaxonRenderer(RendererFormat rendererFormat) {
		this.f = rendererFormat;
	}

	@Override
	public List<String> renderConstraint(Constraint c) {
		if (c instanceof ConstraintTaxon) {
			return render((ConstraintTaxon) c);
		}
		return null;
	}

	public List<String> render(ConstraintTaxon c) {
		ConstraintMainRenderer mainRenderer = new ConstraintMainRenderer(f);
		List<String> ret = new ArrayList<String>();
		List<Constraint> sequence = getConstraintSequence(c
				.getConstrainedProperty());
		for (Constraint constraint : sequence) {
			ret.addAll(constraint.accept(mainRenderer));
		}
		return ret;
	}

}
