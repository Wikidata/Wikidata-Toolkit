package org.wikidata.wdtk.storage.wdtktodb;

/*
 * #%L
 * Wikidata Toolkit Storage
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

import org.wikidata.wdtk.storage.datamodel.SortSchema;

public class WdtkAdaptorHelper {

	final SortSchema sortSchema;
	final ValueToValueVisitor valueAdaptor;
	final SnakToPropertyValuePairVisitor snakAdaptor;

	public WdtkAdaptorHelper(SortSchema sortSchema) {
		this.sortSchema = sortSchema;
		this.valueAdaptor = new ValueToValueVisitor();
		this.snakAdaptor = new SnakToPropertyValuePairVisitor(this);
	}

	public SortSchema getSortSchema() {
		return this.sortSchema;
	}

	public ValueToValueVisitor getValueTovalueVisitor() {
		return this.valueAdaptor;
	}

	public SnakToPropertyValuePairVisitor getSnakAdaptor() {
		return this.snakAdaptor;
	}

}
