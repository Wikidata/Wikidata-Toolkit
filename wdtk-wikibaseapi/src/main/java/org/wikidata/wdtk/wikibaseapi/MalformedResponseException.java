package org.wikidata.wdtk.wikibaseapi;

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
