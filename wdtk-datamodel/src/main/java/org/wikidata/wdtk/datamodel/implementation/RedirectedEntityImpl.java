package org.wikidata.wdtk.datamodel.implementation;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.wikidata.wdtk.datamodel.interfaces.RedirectedEntity;

/**
 * Jackson implementation of {@link RedirectedEntity}.
 *
 * @author Sneha Sinha
 */
public class RedirectedEntityImpl implements RedirectedEntity {
    private final String entity;

    private final String redirect;

    /**
     * Constructor.
     *
     * @param entity
     *            the id of the entity to be redirected
     * @param redirect
     *            the id of the redirection target entity
     */
    public RedirectedEntityImpl(
            String entity,
            String redirect) {
        this.entity = entity;
        this.redirect = redirect;
    }

    /**
     * Constructor. Creates an object that can be populated during JSON
     * deserialization. Should only be used by Jackson for this very purpose.
     */
    @JsonCreator
    public RedirectedEntityImpl(
            @JsonProperty("entity") String entity,
            @JsonProperty("redirect") String redirect,
            @JacksonInject("siteIri") String siteIri) {
        this.entity = entity;
        this.redirect = redirect;
    }

    public String getEntity() {
        return this.entity;
    }

    public String getRedirect() {
        return this.redirect;
    }
}