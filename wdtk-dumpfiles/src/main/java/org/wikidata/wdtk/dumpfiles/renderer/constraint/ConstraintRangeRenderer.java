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
import org.wikidata.wdtk.dumpfiles.constraint.ConstraintRange;
import org.wikidata.wdtk.dumpfiles.constraint.DateAndNow;

/**
 * 
 * @author Julian Mendez
 * 
 */
class ConstraintRangeRenderer implements ConstraintRenderer {

	public ConstraintRangeRenderer() {
	}

	@Override
	public List<String> renderConstraint(Constraint c) {
		if (c instanceof ConstraintRange) {
			return render((ConstraintRange) c);
		}
		return null;
	}

	public List<String> render(ConstraintRange c) {
		if (c.isQuantity()) {
			return renderQuantity(c.getConstrainedProperty(), c.getMinNum(),
					c.getMaxNum());
		} else {
			return renderTime(c.getConstrainedProperty(), c.getMinDate(),
					c.getMaxDate());
		}
	}

	public List<String> renderQuantity(PropertyIdValue p, Double min, Double max) {
		OWLSymbolFactory f = new OWLSymbolFactory();
		return render(p, "value", min.toString(), max.toString(),
				f.xsdDecimal()); // FIXME fix parameter
	}

	public List<String> renderTime(PropertyIdValue p, DateAndNow min,
			DateAndNow max) {
		OWLSymbolFactory f = new OWLSymbolFactory();
		return render(p, "time", min.toString(), max.toString(),
				f.xsdDateTime()); // FIXME fix parameter
	}

	public List<String> render(PropertyIdValue p, String param, String min,
			String max, String type) {
		List<String> ret = new ArrayList<String>();
		if (p ==null || param==null) {
			return ret;
		}
		OWLSymbolFactory f = new OWLSymbolFactory();
		ret.add(f.aInverseFunctionalObjectProperty(f.a_s(p)));
		ret.add(f.aDataPropertyRange(
				f.a_v(p),
				f.aDataSomeValuesFrom(
						param,
						f.aDataIntersectionOf(
								f.aDatatypeRestriction(type,
										f.xsdMinInclusive(),
										f.aLiteral(min, type)),
								f.aDatatypeRestriction(type,
										f.xsdMaxInclusive(),
										f.aLiteral(max, type))))));
		return ret;
	}

}
