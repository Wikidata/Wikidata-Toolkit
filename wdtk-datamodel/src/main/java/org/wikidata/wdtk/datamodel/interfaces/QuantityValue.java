package org.wikidata.wdtk.datamodel.interfaces;

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

import java.math.BigDecimal;

/**
 * A quantity value represents a number, possibly under some unit. The number
 * can be of arbitrary precision.
 * 
 * As of Jan 2014, units of measurement are not supported yet. The interface may
 * be extended to account for this and other additional components in the
 * future.
 * 
 * @author Markus Kroetzsch
 * 
 */
public interface QuantityValue extends Value {

	/**
	 * Get the numeric value of this quantity.
	 * 
	 * @return numeric value as a decimal value of arbitrary precision
	 */
	public BigDecimal getNumericValue();

}
