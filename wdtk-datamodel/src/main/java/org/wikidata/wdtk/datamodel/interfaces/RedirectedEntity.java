package org.wikidata.wdtk.datamodel.interfaces;

/**
 * Interface for datasets that describe redirected entities.
 *
 * @author Sneha Sinha
 *
 */
public interface RedirectedEntity {

    /**
     * Return the source entity which is getting redirected
     *
     * @return source entity Id
     */
    String getEntity();

    /**
     * Get redirection target
     *
     * @return redirection target id
     */
    String getRedirect();
}