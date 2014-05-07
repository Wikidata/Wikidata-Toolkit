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

import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.constraint.Constraint;
import org.wikidata.wdtk.dumpfiles.constraint.ConstraintConflictsWith;

/**
 * 
 * @author Julian Mendez
 * 
 */
class ConstraintConflictsWithRenderer implements ConstraintRenderer {

	public ConstraintConflictsWithRenderer() {
	}

	@Override
	public List<String> renderConstraint(Constraint c) {
		if (c instanceof ConstraintConflictsWith) {
			return render((ConstraintConflictsWith) c);
		}
		return null;
	}

	public List<String> render(ConstraintConflictsWith c) {
		List<String> ret = new ArrayList<String>();
		OWLSymbolFactory f = new OWLSymbolFactory();
		PropertyIdValue p = c.getConstrainedProperty();
		ret.add(f.aInverseFunctionalObjectProperty(f.a_s(p)));

		// TODO

		return ret;
	}
	
	public List<String> renderPart(PropertyIdValue p, PropertyIdValue r,
			List<ItemIdValue> q) {
		List<String> ret = new ArrayList<String>();
		if (p==null || r==null) {
			return ret;
		}
		OWLSymbolFactory f = new OWLSymbolFactory();
		if (q == null) {
			ret.add(f.aDisjointClasses(
					f.aObjectSomeValuesFrom(f.a_v(p), f.owlThing()),
					f.aObjectSomeValuesFrom(f.a_s(r), f.owlThing())));
		} else {
			ret.add(f.aDisjointClasses(
					f.aObjectSomeValuesFrom(f.a_v(p), f.owlThing()),
					f.aObjectSomeValuesFrom(f.a_s(r), f.aObjectSomeValuesFrom(
							f.a_v(r), f.aObjectOneOf(q)))));
		}
		return ret;
	}

}
