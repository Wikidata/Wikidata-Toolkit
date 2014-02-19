package org.wikidata.wdtk.datamodel.implementation;

/*
 * #%L
 * Wikidata Toolkit Data Model
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

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

/**
 * Implementation of {@link Reference}.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class ReferenceImpl implements Reference {

	List<? extends ValueSnak> valueSnaks;

	ReferenceImpl(List<? extends ValueSnak> valueSnaks) {
		Validate.notNull(valueSnaks, "List of value snaks cannot be null");
		this.valueSnaks = valueSnaks;
	}

	@Override
	public List<? extends ValueSnak> getSnaks() {
		return Collections.unmodifiableList(this.valueSnaks);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return valueSnaks.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ReferenceImpl)) {
			return false;
		}
		ReferenceImpl other = (ReferenceImpl) obj;
		return other.valueSnaks.equals(this.valueSnaks);
	}

}
