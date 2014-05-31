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

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.constraint.Constraint;
import org.wikidata.wdtk.dumpfiles.constraint.ConstraintFormat;
import org.wikidata.wdtk.dumpfiles.renderer.format.RendererFormat;
import org.wikidata.wdtk.dumpfiles.renderer.format.StringResource;

/**
 * 
 * @author Julian Mendez
 * 
 */
class ConstraintFormatRenderer implements ConstraintRenderer {

	public static final String C_QUOTATION_MARK = "\"";

	final RendererFormat f;

	public ConstraintFormatRenderer(RendererFormat rendererFormat) {
		this.f = rendererFormat;
	}

	@Override
	public void renderConstraint(Constraint c) {
		if (c instanceof ConstraintFormat) {
			render((ConstraintFormat) c);
		}
	}

	private Resource transform(String pattern) {
		// FIXME this does not cover all cases
		String newPattern = pattern.replace(C_QUOTATION_MARK, "");
		return new StringResource(newPattern);
	}

	public void render(ConstraintFormat c) {
		render(c.getConstrainedProperty(), c.getPattern());
	}

	public void render(PropertyIdValue p, String pattern) {
		if ((p == null) || (pattern == null)) {
			return;
		}
		URI rp = this.f.aRp(p);
		this.f.addInverseFunctionalObjectProperty(this.f.a_s(p));
		this.f.addDatatypeDefinition(
				rp,
				this.f.getDatatypeRestriction(this.f.xsdString(),
						this.f.xsdPattern(), transform(pattern)));
		this.f.addDataPropertyRange(this.f.a_v(p), rp);
	}

}
