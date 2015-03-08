package org.wikidata.wdtk.dumpfiles.constraint.model;

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

import org.junit.Assert;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;

/**
 * This class contains convenience methods used in the constraint test classes.
 *
 * @author Julian Mendez
 *
 */
public class ConstraintTestHelper {

	public static final String PREFIX_WIKIDATA = "http://www.wikidata.org/entity/";

	/**
	 * Returns the property id value for the specified property name.
	 *
	 * @param propertyName
	 *            property name
	 * @return the property id value for the specified property name
	 */
	public static PropertyIdValue getPropertyIdValue(String propertyName) {
		return (new DataObjectFactoryImpl()).getPropertyIdValue(propertyName,
				PREFIX_WIKIDATA);
	}

	/**
	 * Returns the item id value for the specified property name.
	 *
	 * @param itemName
	 *            item name
	 * @return the item id value for the specified property name
	 */
	public static ItemIdValue getItemIdValue(String itemName) {
		return (new DataObjectFactoryImpl()).getItemIdValue(itemName,
				PREFIX_WIKIDATA);
	}

	/**
	 * Tests that two constraints are equal and both different from a third one.
	 *
	 * @param firstTwin
	 *            the first property, which is equal to the second one
	 * @param secondTwin
	 *            the second property, which is equal to the first one
	 * @param third
	 *            the third property, which is different from the first one and
	 *            the second one
	 */
	public static void testEquals(Constraint firstTwin, Constraint secondTwin,
			Constraint third) {

		Assert.assertEquals(firstTwin, firstTwin);
		Assert.assertEquals(firstTwin, secondTwin);
		Assert.assertEquals(secondTwin, firstTwin);

		Assert.assertEquals(firstTwin.hashCode(), secondTwin.hashCode());

		Assert.assertNotEquals(firstTwin, null);
		Assert.assertNotEquals(firstTwin, new Object());

		Assert.assertNotEquals(firstTwin, third);
		Assert.assertNotEquals(third, firstTwin);

		Assert.assertNotEquals(secondTwin, third);
		Assert.assertNotEquals(third, secondTwin);
	}

	public static void testVisit(Constraint constraint) {
		ConstraintVisitor<String> visitor = getVisitor();
		String name = constraint.accept(visitor);
		Assert.assertEquals(constraint.getClass().getSimpleName(), name);
	}

	static ConstraintVisitor<String> getVisitor() {
		return new ConstraintVisitor<String>() {

			@Override
			public String visit(ConstraintSingleValue constraint) {
				return "ConstraintSingleValue";
			}

			@Override
			public String visit(ConstraintUniqueValue constraint) {
				return "ConstraintUniqueValue";
			}

			@Override
			public String visit(ConstraintFormat constraint) {
				return "ConstraintFormat";
			}

			@Override
			public String visit(ConstraintOneOf constraint) {
				return "ConstraintOneOf";
			}

			@Override
			public String visit(ConstraintSymmetric constraint) {
				return "ConstraintSymmetric";
			}

			@Override
			public String visit(ConstraintInverse constraint) {
				return "ConstraintInverse";
			}

			@Override
			public String visit(ConstraintCommonsLink constraint) {
				return "ConstraintExistingFile";
			}

			@Override
			public String visit(ConstraintTargetRequiredClaim constraint) {
				return "ConstraintTargetRequiredClaim";
			}

			@Override
			public String visit(ConstraintItem constraint) {
				return "ConstraintItem";
			}

			@Override
			public String visit(ConstraintRange constraint) {
				return "ConstraintRange";
			}

			@Override
			public String visit(ConstraintMultiValue constraint) {
				return "ConstraintMultiValue";
			}

			@Override
			public String visit(ConstraintConflictsWith constraint) {
				return "ConstraintConflictsWith";
			}

			@Override
			public String visit(ConstraintQualifier constraint) {
				return "ConstraintQualifier";
			}

		};
	}

}
