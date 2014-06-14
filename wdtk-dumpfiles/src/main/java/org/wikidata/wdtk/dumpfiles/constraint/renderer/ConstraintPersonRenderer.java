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

import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.constraint.builder.ConstraintMainBuilder;
import org.wikidata.wdtk.dumpfiles.constraint.format.RendererFormat;
import org.wikidata.wdtk.dumpfiles.constraint.model.Constraint;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintItem;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintPerson;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintType;
import org.wikidata.wdtk.dumpfiles.constraint.model.RelationType;

/**
 * 
 * @author Julian Mendez
 * 
 */
class ConstraintPersonRenderer implements ConstraintRenderer {

	public static final String Q_PERSON = "Q215627";
	public static final String P_SEX_OR_GENDER = "P21";
	public static final String P_PLACE_OF_BIRTH = "P19";
	public static final String P_DATE_OF_BIRTH = "P569";

	final RendererFormat f;

	/**
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

		ItemIdValue qPerson = factory.getItemIdValue(Q_PERSON,
				ConstraintMainBuilder.PREFIX_WIKIDATA);
		PropertyIdValue pSexOrGender = factory.getPropertyIdValue(
				P_SEX_OR_GENDER, ConstraintMainBuilder.PREFIX_WIKIDATA);
		PropertyIdValue pPlaceOfBirth = factory.getPropertyIdValue(
				P_PLACE_OF_BIRTH, ConstraintMainBuilder.PREFIX_WIKIDATA);
		PropertyIdValue pDateOfBirth = factory.getPropertyIdValue(
				P_DATE_OF_BIRTH, ConstraintMainBuilder.PREFIX_WIKIDATA);

		ret.add(new ConstraintType(constrainedProperty, qPerson,
				RelationType.INSTANCE));
		ret.add(new ConstraintItem(constrainedProperty, pSexOrGender, null,
				null, null, null, null));
		ret.add(new ConstraintItem(constrainedProperty, pPlaceOfBirth, null,
				null, null, null, null));
		ret.add(new ConstraintItem(constrainedProperty, pDateOfBirth, null,
				null, null, null, null));

		return ret;
	}

	public ConstraintPersonRenderer(RendererFormat rendererFormat) {
		this.f = rendererFormat;
	}

	@Override
	public void renderConstraint(Constraint c) {
		if (c instanceof ConstraintPerson) {
			render((ConstraintPerson) c);
		}
	}

	public void render(ConstraintPerson c) {
		ConstraintMainRenderer mainRenderer = new ConstraintMainRenderer(this.f);
		List<Constraint> sequence = getConstraintSequence(c
				.getConstrainedProperty());
		for (Constraint constraint : sequence) {
			constraint.accept(mainRenderer);
		}
	}

}
