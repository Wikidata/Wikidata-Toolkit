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
import org.wikidata.wdtk.dumpfiles.constraint.ConstraintPerson;
import org.wikidata.wdtk.dumpfiles.constraint.ConstraintType;
import org.wikidata.wdtk.dumpfiles.constraint.RelationType;
import org.wikidata.wdtk.dumpfiles.parser.constraint.ConstraintMainParser;

/**
 * 
 * @author Julian Mendez
 * 
 */
class ConstraintPersonRenderer implements ConstraintRenderer {

	/**
	 * 
	 * Constructs a sequence of constraints according to the following list:
	 * {{Constraint:Type|class=Q215627|relation=instance}}
	 * {{Constraint:Item|property=P21}} {{Constraint:Item|property=P19}}
	 * {{Constraint:Item|property=P569}}
	 * 
	 * @param constrainedProperty
	 *            constrained property
	 * @return a sequence of constraints
	 */
	List<Constraint> getConstraintSequence(PropertyIdValue constrainedProperty) {
		List<Constraint> ret = new ArrayList<Constraint>();
		DataObjectFactoryImpl factory = new DataObjectFactoryImpl();

		ItemIdValue q215627 = factory.getItemIdValue("Q215627",
				ConstraintMainParser.DEFAULT_BASE_IRI);
		PropertyIdValue p21 = factory.getPropertyIdValue("P21",
				ConstraintMainParser.DEFAULT_BASE_IRI);
		PropertyIdValue p19 = factory.getPropertyIdValue("P19",
				ConstraintMainParser.DEFAULT_BASE_IRI);
		PropertyIdValue p569 = factory.getPropertyIdValue("P569",
				ConstraintMainParser.DEFAULT_BASE_IRI);

		ret.add(new ConstraintType(constrainedProperty, q215627,
				RelationType.INSTANCE));
		ret.add(new ConstraintItem(constrainedProperty, p21, null, null, null,
				null, null));
		ret.add(new ConstraintItem(constrainedProperty, p19, null, null, null,
				null, null));
		ret.add(new ConstraintItem(constrainedProperty, p569, null, null, null,
				null, null));

		return ret;
	}

	public ConstraintPersonRenderer() {
	}

	@Override
	public List<String> renderConstraint(Constraint c) {
		if (c instanceof ConstraintPerson) {
			return render((ConstraintPerson) c);
		}
		return null;
	}

	public List<String> render(ConstraintPerson c) {
		ConstraintMainRenderer mainRenderer = new ConstraintMainRenderer();
		List<String> ret = new ArrayList<String>();
		List<Constraint> sequence = getConstraintSequence(c
				.getConstrainedProperty());
		for (Constraint constraint : sequence) {
			ret.addAll(constraint.accept(mainRenderer));
		}
		return ret;
	}

}
