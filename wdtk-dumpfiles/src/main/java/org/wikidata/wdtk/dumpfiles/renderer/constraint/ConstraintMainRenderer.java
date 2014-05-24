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

import org.wikidata.wdtk.dumpfiles.constraint.ConstraintConflictsWith;
import org.wikidata.wdtk.dumpfiles.constraint.ConstraintExistingFile;
import org.wikidata.wdtk.dumpfiles.constraint.ConstraintFormat;
import org.wikidata.wdtk.dumpfiles.constraint.ConstraintInverse;
import org.wikidata.wdtk.dumpfiles.constraint.ConstraintItem;
import org.wikidata.wdtk.dumpfiles.constraint.ConstraintMultiValue;
import org.wikidata.wdtk.dumpfiles.constraint.ConstraintOneOf;
import org.wikidata.wdtk.dumpfiles.constraint.ConstraintPerson;
import org.wikidata.wdtk.dumpfiles.constraint.ConstraintQualifier;
import org.wikidata.wdtk.dumpfiles.constraint.ConstraintRange;
import org.wikidata.wdtk.dumpfiles.constraint.ConstraintSingleValue;
import org.wikidata.wdtk.dumpfiles.constraint.ConstraintSymmetric;
import org.wikidata.wdtk.dumpfiles.constraint.ConstraintTargetRequiredClaim;
import org.wikidata.wdtk.dumpfiles.constraint.ConstraintTaxon;
import org.wikidata.wdtk.dumpfiles.constraint.ConstraintType;
import org.wikidata.wdtk.dumpfiles.constraint.ConstraintUniqueValue;
import org.wikidata.wdtk.dumpfiles.constraint.ConstraintValueType;
import org.wikidata.wdtk.dumpfiles.constraint.ConstraintVisitor;
import org.wikidata.wdtk.dumpfiles.renderer.format.RendererFormat;

/**
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintMainRenderer implements ConstraintVisitor<Boolean> {

	final RendererFormat rendererFormat;

	public ConstraintMainRenderer(RendererFormat rendererFormat) {
		this.rendererFormat = rendererFormat;
	}

	@Override
	public Boolean visit(ConstraintSingleValue constraint) {
		(new ConstraintSingleValueRenderer(this.rendererFormat))
				.render(constraint);
		return true;
	}

	@Override
	public Boolean visit(ConstraintUniqueValue constraint) {
		(new ConstraintUniqueValueRenderer(this.rendererFormat))
				.render(constraint);
		return true;
	}

	@Override
	public Boolean visit(ConstraintFormat constraint) {
		(new ConstraintFormatRenderer(this.rendererFormat)).render(constraint);
		return true;

	}

	@Override
	public Boolean visit(ConstraintOneOf constraint) {
		(new ConstraintOneOfRenderer(this.rendererFormat)).render(constraint);
		return true;

	}

	@Override
	public Boolean visit(ConstraintSymmetric constraint) {
		(new ConstraintSymmetricRenderer()).render(constraint);
		return true;
	}

	@Override
	public Boolean visit(ConstraintInverse constraint) {
		(new ConstraintInverseRenderer()).render(constraint);
		return true;
	}

	@Override
	public Boolean visit(ConstraintExistingFile constraint) {
		(new ConstraintExistingFileRenderer()).render(constraint);
		return true;
	}

	@Override
	public Boolean visit(ConstraintTargetRequiredClaim constraint) {
		(new ConstraintTargetRequiredClaimRenderer(this.rendererFormat))
				.render(constraint);
		return true;
	}

	@Override
	public Boolean visit(ConstraintItem constraint) {
		(new ConstraintItemRenderer(this.rendererFormat)).render(constraint);
		return true;
	}

	@Override
	public Boolean visit(ConstraintType constraint) {
		(new ConstraintTypeRenderer(this.rendererFormat)).render(constraint);
		return true;
	}

	@Override
	public Boolean visit(ConstraintValueType constraint) {
		(new ConstraintValueTypeRenderer(this.rendererFormat))
				.render(constraint);
		return true;
	}

	@Override
	public Boolean visit(ConstraintRange constraint) {
		(new ConstraintRangeRenderer(this.rendererFormat)).render(constraint);
		return true;
	}

	@Override
	public Boolean visit(ConstraintMultiValue constraint) {
		(new ConstraintMultiValueRenderer(this.rendererFormat))
				.render(constraint);
		return true;
	}

	@Override
	public Boolean visit(ConstraintConflictsWith constraint) {
		(new ConstraintConflictsWithRenderer(this.rendererFormat))
				.render(constraint);
		return true;
	}

	@Override
	public Boolean visit(ConstraintQualifier constraint) {
		(new ConstraintQualifierRenderer()).render(constraint);
		return true;
	}

	@Override
	public Boolean visit(ConstraintPerson constraint) {
		(new ConstraintPersonRenderer(this.rendererFormat)).render(constraint);
		return true;
	}

	@Override
	public Boolean visit(ConstraintTaxon constraint) {
		(new ConstraintTaxonRenderer(this.rendererFormat)).render(constraint);
		return true;
	}

}
