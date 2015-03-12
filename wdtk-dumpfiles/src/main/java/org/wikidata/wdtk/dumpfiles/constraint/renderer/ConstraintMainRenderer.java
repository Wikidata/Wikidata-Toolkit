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

import org.wikidata.wdtk.dumpfiles.constraint.builder.ConstraintBuilderConstant;
import org.wikidata.wdtk.dumpfiles.constraint.format.RendererFormat;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintCommonsLink;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintConflictsWith;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintDiffWithinRange;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintFormat;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintInverse;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintItem;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintMultiValue;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintOneOf;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintQualifier;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintQualifiers;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintRange;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintSingleValue;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintSymmetric;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintTargetRequiredClaim;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintUniqueValue;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintVisitor;

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
		return true;
	}

	@Override
	public Boolean visit(ConstraintInverse constraint) {
		return true;
	}

	@Override
	public Boolean visit(ConstraintCommonsLink constraint) {
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
	public Boolean visit(ConstraintRange constraint) {
		(new ConstraintRangeRenderer(this.rendererFormat)).render(constraint);
		return true;
	}

	@Override
	public Boolean visit(ConstraintDiffWithinRange constraint) {
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
	public Boolean visit(ConstraintQualifiers constraint) {
		return true;
	}

	@Override
	public Boolean visit(ConstraintQualifier constraint) {
		return true;
	}

	/**
	 * Returns the identifiers of all the constraints that are translated.
	 * 
	 * @return the identifiers of all the constraints that are translated
	 */
	public static List<String> getAcceptedConstraints() {
		List<String> ret = new ArrayList<String>();
		ret.add(ConstraintBuilderConstant.C_SINGLE_VALUE);
		ret.add(ConstraintBuilderConstant.C_UNIQUE_VALUE);
		ret.add(ConstraintBuilderConstant.C_FORMAT);
		ret.add(ConstraintBuilderConstant.C_ONE_OF);
		ret.add(ConstraintBuilderConstant.C_TARGET_REQUIRED_CLAIM);
		ret.add(ConstraintBuilderConstant.C_ITEM);
		ret.add(ConstraintBuilderConstant.C_RANGE);
		ret.add(ConstraintBuilderConstant.C_MULTI_VALUE);
		ret.add(ConstraintBuilderConstant.C_CONFLICTS_WITH);
		return ret;
	}

	/**
	 * Returns the identifiers of all the constraints that are ignored.
	 * 
	 * @return the identifiers of all the constraints that are ignored
	 */
	public static List<String> getIgnoredConstraints() {
		List<String> ret = new ArrayList<String>();
		ret.add(ConstraintBuilderConstant.C_SYMMETRIC);
		ret.add(ConstraintBuilderConstant.C_INVERSE);
		ret.add(ConstraintBuilderConstant.C_COMMONS_LINK);
		ret.add(ConstraintBuilderConstant.C_DIFF_WITHIN_RANGE);
		ret.add(ConstraintBuilderConstant.C_QUALIFIERS);
		ret.add(ConstraintBuilderConstant.C_QUALIFIER);
		return ret;
	}

	/**
	 * Returns the identifiers of all the constraints that are rewritten.
	 * 
	 * @return the identifiers of all the constraints that are rewritten
	 */
	public static List<String> getRewrittenConstraints() {
		List<String> ret = new ArrayList<String>();
		ret.add(ConstraintBuilderConstant.C_TYPE);
		ret.add(ConstraintBuilderConstant.C_VALUE_TYPE);
		return ret;
	}

}
