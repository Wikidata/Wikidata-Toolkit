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

import org.openrdf.model.BNode;
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

	public static final String Q_TAXON = "Q16521";
	public static final String P_TAXON_NAME = "P225";
	public static final String P_PARENT_TAXON = "P171";
	public static final String P_TAXON_RANK = "P105";

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

		ItemIdValue qTaxon = factory.getItemIdValue(Q_TAXON,
				ConstraintMainParser.PREFIX_WIKIDATA);
		PropertyIdValue pTaxonName = factory.getPropertyIdValue(P_TAXON_NAME,
				ConstraintMainParser.PREFIX_WIKIDATA);
		PropertyIdValue pParentTaxon = factory.getPropertyIdValue(
				P_PARENT_TAXON, ConstraintMainParser.PREFIX_WIKIDATA);
		PropertyIdValue pTaxonRank = factory.getPropertyIdValue(P_TAXON_RANK,
				ConstraintMainParser.PREFIX_WIKIDATA);

		ret.add(new ConstraintType(constrainedProperty, qTaxon,
				RelationType.INSTANCE));
		ret.add(new ConstraintItem(constrainedProperty, pTaxonName, null, null,
				null, null, null));
		ret.add(new ConstraintItem(constrainedProperty, pParentTaxon, null,
				null, null, null, null));
		ret.add(new ConstraintItem(constrainedProperty, pTaxonRank, null, null,
				null, null, null));

		return ret;
	}

	public ConstraintTaxonRenderer(RendererFormat rendererFormat) {
		this.f = rendererFormat;
	}

	@Override
	public void renderConstraint(Constraint c) {
		if (c instanceof ConstraintTaxon) {
			render((ConstraintTaxon) c);
		}
	}

	public List<BNode> render(ConstraintTaxon c) {
		ConstraintMainRenderer mainRenderer = new ConstraintMainRenderer(this.f);
		List<BNode> ret = new ArrayList<BNode>();
		List<Constraint> sequence = getConstraintSequence(c
				.getConstrainedProperty());
		for (Constraint constraint : sequence) {
			constraint.accept(mainRenderer);
		}
		return ret;
	}

}
