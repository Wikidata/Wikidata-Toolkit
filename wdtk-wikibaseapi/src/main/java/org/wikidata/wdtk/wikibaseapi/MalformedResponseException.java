package org.wikidata.wdtk.wikibaseapi;

/*-
 * #%L
 * Wikidata Toolkit Wikibase API
 * %%
 * Copyright (C) 2014 - 2021 Wikidata Toolkit Developers
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

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Exception thrown when response from Wikibase API cannot be parsed.
 * This exception may be thrown in addition to other {@link JsonProcessingException} exceptions.
 */
public class MalformedResponseException extends JsonProcessingException {
	private static final long serialVersionUID = -4697019897395095678L;

	/**
	 * Constructs {@code MalformedResponseException} with the specified detail
	 * message.
	 * 
	 * @param message the detail message, which can be later retrieved via
	 *                {@link #getMessage()}
	 */
	public MalformedResponseException(String message) {
		super(message);
	}

	/**
	 * Constructs {@code MalformedResponseException} with the specified detail
	 * message and cause.
	 * 
	 * @param message the detail message, which can be later retrieved via
	 *                {@link #getMessage()}
	 * @param cause   the cause, which can be later retrieved via
	 *                {@link #getCause()}
	 */
	public MalformedResponseException(String message, Throwable cause) {
		super(message, cause);
	}
}
