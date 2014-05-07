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

import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.constraint.Constraint;
import org.wikidata.wdtk.dumpfiles.constraint.ConstraintMultiValue;

/**
 * 
 * @author Julian Mendez
 * 
 */
class ConstraintMultiValueRenderer implements ConstraintRenderer {

	public ConstraintMultiValueRenderer() {
	}

	@Override
	public List<String> renderConstraint(Constraint c) {
		if (c instanceof ConstraintMultiValue) {
			return render((ConstraintMultiValue) c);
		}
		return null;
	}

	public List<String> render(ConstraintMultiValue c) {
		return render(c.getConstrainedProperty());
	}

	public List<String> render(PropertyIdValue p) {
		List<String> ret = new ArrayList<String>();
		OWLSymbolFactory f = new OWLSymbolFactory();
		ret.add(f.aInverseFunctionalObjectProperty(f.a_s(p)));
		ret.add(f.aSubClassOf(f.owlThing(),
				f.aObjectComplementOf(f.aObjectExactCardinality("1", f.a_s(p)))));
		return ret;
	}

}
