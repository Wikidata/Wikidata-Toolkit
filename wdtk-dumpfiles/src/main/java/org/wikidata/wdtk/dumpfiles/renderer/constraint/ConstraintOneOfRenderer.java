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
import org.wikidata.wdtk.dumpfiles.constraint.ConstraintOneOf;

/**
 * 
 * @author Julian Mendez
 * 
 */
class ConstraintOneOfRenderer implements ConstraintRenderer {

	public ConstraintOneOfRenderer() {
	}

	@Override
	public List<String> renderConstraint(Constraint c) {
		if (c instanceof ConstraintOneOf) {
			return render((ConstraintOneOf) c);
		}
		return null;
	}

	public List<String> render(ConstraintOneOf c) {
		return render(c.getConstrainedProperty(), c.getValues());
	}

	public List<String> render(PropertyIdValue p, List<ItemIdValue> values) {
		List<String> ret = new ArrayList<String>();
		if (p == null || values == null) {
			return ret;
		}
		OWLSymbolFactory f = new OWLSymbolFactory();
		ret.add(f.aInverseFunctionalObjectProperty(f.a_s(p)));
		ret.add(f.aObjectPropertyRange(f.a_v(p), f.aObjectOneOf(values)));
		return ret;
	}

}
