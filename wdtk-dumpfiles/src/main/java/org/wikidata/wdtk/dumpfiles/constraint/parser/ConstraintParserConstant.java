package org.wikidata.wdtk.dumpfiles.constraint.parser;

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

/**
 * 
 * @author Julian Mendez
 * 
 */
public interface ConstraintParserConstant {

	String T_CONSTRAINT = "Constraint:";

	String C_SINGLE_VALUE = "Single value";
	String C_UNIQUE_VALUE = "Unique value";
	String C_FORMAT = "Format";
	String C_ONE_OF = "One of";
	String C_SYMMETRIC = "Symmetric";
	String C_INVERSE = "Inverse";
	String C_EXISTING_FILE = "Existing file";
	String C_TARGET_REQUIRED_CLAIM = "Target required claim";
	String C_ITEM = "Item";
	String C_TYPE = "Type";
	String C_VALUE_TYPE = "Value type";
	String C_RANGE = "Range";
	String C_MULTI_VALUE = "Multi value";
	String C_CONFLICTS_WITH = "Conflicts with";
	String C_QUALIFIER = "Qualifier";
	String C_PERSON = "Person";
	String C_TAXON = "Taxon";

	String P_CLASS = "class";
	String P_RELATION = "relation";
	String P_ITEM = "item";
	String P_PROPERTY = "property";
	String P_ITEM_2 = "item2";
	String P_PROPERTY_2 = "property2";
	String P_ITEMS = "items";
	String P_EXCEPTIONS = "exceptions";
	String P_LIST = "list";
	String P_MAX = "max";
	String P_MIN = "min";
	String P_VALUES = "values";
	String P_PATTERN = "pattern";

	String V_INSTANCE = "instance";
	String V_SUBCLASS = "subclass";

}
