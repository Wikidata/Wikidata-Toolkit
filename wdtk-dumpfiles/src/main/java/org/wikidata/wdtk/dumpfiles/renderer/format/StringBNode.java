package org.wikidata.wdtk.dumpfiles.renderer.format;

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

import org.openrdf.model.BNode;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

/**
 * 
 * @author Julian Mendez
 * 
 */
public class StringBNode implements BNode {

	private static final long serialVersionUID = 7443641686881565110L;

	final ValueFactory factory = ValueFactoryImpl.getInstance();

	final String str;
	final BNode bnode;

	public StringBNode(String str) {
		this.str = str;
		this.bnode = this.factory.createBNode();
	}

	@Override
	public String stringValue() {
		return this.str;
	}

	@Override
	public String getID() {
		return this.bnode.getID();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof StringBNode)) {
			return false;
		}
		StringBNode other = (StringBNode) obj;
		return stringValue().equals(other.stringValue())
				&& getID().equals(other.getID());
	}

	@Override
	public int hashCode() {
		return this.str.hashCode();
	}

	@Override
	public String toString() {
		return this.str;
	}

}
