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
public class ConstraintMainRenderer implements ConstraintVisitor<List<String>> {

	final RendererFormat rendererFormat;

	public ConstraintMainRenderer(RendererFormat rendererFormat) {
		this.rendererFormat = rendererFormat;
	}

	@Override
	public List<String> visit(ConstraintSingleValue constraint) {
		return (new ConstraintSingleValueRenderer(rendererFormat))
				.render(constraint);
	}

	@Override
	public List<String> visit(ConstraintUniqueValue constraint) {
		return (new ConstraintUniqueValueRenderer(rendererFormat))
				.render(constraint);
	}

	@Override
	public List<String> visit(ConstraintFormat constraint) {
		return (new ConstraintFormatRenderer(rendererFormat))
				.render(constraint);
	}

	@Override
	public List<String> visit(ConstraintOneOf constraint) {
		return (new ConstraintOneOfRenderer(rendererFormat)).render(constraint);
	}

	@Override
	public List<String> visit(ConstraintSymmetric constraint) {
		return (new ConstraintSymmetricRenderer()).render(constraint);
	}

	@Override
	public List<String> visit(ConstraintInverse constraint) {
		return (new ConstraintInverseRenderer()).render(constraint);
	}

	@Override
	public List<String> visit(ConstraintExistingFile constraint) {
		return (new ConstraintExistingFileRenderer()).render(constraint);
	}

	@Override
	public List<String> visit(ConstraintTargetRequiredClaim constraint) {
		return (new ConstraintTargetRequiredClaimRenderer(rendererFormat))
				.render(constraint);
	}

	@Override
	public List<String> visit(ConstraintItem constraint) {
		return (new ConstraintItemRenderer(rendererFormat)).render(constraint);
	}

	@Override
	public List<String> visit(ConstraintType constraint) {
		return (new ConstraintTypeRenderer(rendererFormat)).render(constraint);
	}

	@Override
	public List<String> visit(ConstraintValueType constraint) {
		return (new ConstraintValueTypeRenderer(rendererFormat))
				.render(constraint);
	}

	@Override
	public List<String> visit(ConstraintRange constraint) {
		return (new ConstraintRangeRenderer(rendererFormat)).render(constraint);
	}

	@Override
	public List<String> visit(ConstraintMultiValue constraint) {
		return (new ConstraintMultiValueRenderer(rendererFormat))
				.render(constraint);
	}

	@Override
	public List<String> visit(ConstraintConflictsWith constraint) {
		return (new ConstraintConflictsWithRenderer(rendererFormat))
				.render(constraint);
	}

	@Override
	public List<String> visit(ConstraintQualifier constraint) {
		return (new ConstraintQualifierRenderer()).render(constraint);
	}

	@Override
	public List<String> visit(ConstraintPerson constraint) {
		return (new ConstraintPersonRenderer(rendererFormat))
				.render(constraint);
	}

	@Override
	public List<String> visit(ConstraintTaxon constraint) {
		return (new ConstraintTaxonRenderer(rendererFormat)).render(constraint);
	}

}
