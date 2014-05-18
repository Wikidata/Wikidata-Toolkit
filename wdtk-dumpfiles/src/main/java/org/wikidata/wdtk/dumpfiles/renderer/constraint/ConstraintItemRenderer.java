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
import org.wikidata.wdtk.dumpfiles.constraint.ConstraintItem;
import org.wikidata.wdtk.dumpfiles.renderer.format.RendererFormat;

/**
 * 
 * @author Julian Mendez
 * 
 */
class ConstraintItemRenderer implements ConstraintRenderer {

	final RendererFormat f;

	public ConstraintItemRenderer(RendererFormat rendererFormat) {
		this.f = rendererFormat;
	}

	@Override
	public List<String> renderConstraint(Constraint c) {
		if (c instanceof ConstraintItem) {
			return render((ConstraintItem) c);
		}
		return null;
	}

	public List<String> render(ConstraintItem c) {
		return render(c.getConstrainedProperty(), c.getProperty(), c.getItem(),
				c.getProperty2(), c.getItem2(), c.getItems(), c.getExceptions());
	}

	public List<String> render(PropertyIdValue p, PropertyIdValue r1,
			ItemIdValue q1, PropertyIdValue r2, ItemIdValue q2,
			List<ItemIdValue> values, List<ItemIdValue> exceptions) {
		List<String> ret = new ArrayList<String>();
		ret.add(f.aInverseFunctionalObjectProperty(f.a_s(p)));
		ret.addAll(renderPart(p, r1, q1));
		ret.addAll(renderPart(p, r2, q2));
		ret.addAll(renderPart(p, values));
		ret.addAll(renderPart(p, exceptions));
		return ret;
	}

	public List<String> renderPart(PropertyIdValue p, PropertyIdValue r,
			ItemIdValue q) {
		List<String> ret = new ArrayList<String>();
		if (p == null || r == null) {
			return ret;
		}
		if (q == null) {
			ret.add(f.aObjectPropertyDomain(f.a_s(p),
					f.aObjectSomeValuesFrom(f.a_s(r), f.owlThing())));
		} else {
			ret.add(f.aObjectPropertyDomain(
					f.a_s(p),
					f.aObjectSomeValuesFrom(f.a_s(r), f.aObjectSomeValuesFrom(
							f.a_v(r), f.aObjectOneOf(q)))));
		}
		return ret;
	}

	public List<String> renderPart(PropertyIdValue p, List<ItemIdValue> values) {
		List<String> ret = new ArrayList<String>();
		if (p == null || values == null) {
			return ret;
		}
		if (values.isEmpty()) {
			return ret;
		}
		ret.add(f.aObjectPropertyDomain(f.a_s(p), f.aObjectOneOf(values)));
		return ret;
	}

}
