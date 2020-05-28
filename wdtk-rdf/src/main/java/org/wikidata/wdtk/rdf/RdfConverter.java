package org.wikidata.wdtk.rdf;

/*-
 * #%L
 * Wikidata Toolkit RDF
 * %%
 * Copyright (C) 2014 - 2019 Wikidata Toolkit Developers
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

import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.wikidata.wdtk.datamodel.interfaces.*;

import java.util.Collection;
import java.util.Map;

public class RdfConverter extends AbstractRdfConverter {
    int tasks = RdfSerializer.TASK_ALL_ENTITIES
            | RdfSerializer.TASK_ALL_EXACT_DATA;

    public RdfConverter(RdfWriter rdfWriter, Sites sites, PropertyRegister propertyRegister) {
        super(rdfWriter, sites, propertyRegister);
    }

    /**
     * Sets the tasks that should be performed during export. The value should
     * be a combination of flags such as {@link RdfSerializer#TASK_STATEMENTS}.
     *
     * @param tasks
     *            the tasks to be performed
     */
    public void setTasks(int tasks) {
        this.tasks = tasks;
    }

    /**
     * Returns the tasks that should be performed during export. The value
     * should be a combination of flags such as
     * {@link RdfSerializer#TASK_STATEMENTS}.
     *
     * @return tasks to be performed
     */
    public int getTasks() {
        return this.tasks;
    }

    /**
     * Checks if the given task (or set of tasks) is to be performed.
     *
     * @param task
     *            the task (or set of tasks) to be checked
     * @return true if the tasks include the given task
     */
    boolean hasTask(int task) {
        return ((this.tasks & task) == task);
    }

    @Override
    public void writeTermTriples(Resource subject, TermKind kind, Collection<MonolingualTextValue> terms) throws RDFHandlerException {
        switch (kind) {
            case LABEL:
                if (!hasTask(RdfSerializer.TASK_LABELS)) return;
                break;
            case DESCRIPTION:
                if (!hasTask(RdfSerializer.TASK_DESCRIPTIONS)) return;
                break;
            case ALIAS:
                if (!hasTask(RdfSerializer.TASK_ALIASES)) return;
                break;
        }
        super.writeTermTriples(subject, kind, terms);
    }

    @Override
    public void writeSiteLinks(Resource subject, Map<String, SiteLink> siteLinks) throws RDFHandlerException {
        if (!hasTask(RdfSerializer.TASK_SITELINKS)) return;
        super.writeSiteLinks(subject, siteLinks);
    }

    @Override
    public void writePropertyDatatype(PropertyDocument document) {
        if (!hasTask(RdfSerializer.TASK_DATATYPES)) return;
        super.writePropertyDatatype(document);
    }

    @Override
    public void writeInterPropertyLinks(PropertyDocument document) throws RDFHandlerException {
        if (!hasTask(RdfSerializer.TASK_PROPERTY_LINKS)) return;
        super.writeInterPropertyLinks(document);
    }

    @Override
    public void writeSimpleStatement(Statement statement) {
        if (!hasTask(RdfSerializer.TASK_SIMPLE_STATEMENTS)) return;
        if (statement.getQualifiers().size() == 0) {
            super.writeSimpleStatement(statement);
        }
    }

    @Override
    public void writeFullStatement(Statement statement, boolean best) throws RDFHandlerException {
        if (!hasTask(RdfSerializer.TASK_STATEMENTS)) return;
        super.writeFullStatement(statement, best);
    }

    @Override
    public void writeItemDocument(ItemDocument document) throws RDFHandlerException {
        if (!hasTask(RdfSerializer.TASK_ITEMS)) return;
        super.writeItemDocument(document);
    }

    @Override
    public void writePropertyDocument(PropertyDocument document) throws RDFHandlerException {
        if (!hasTask(RdfSerializer.TASK_PROPERTIES)) return;
        super.writePropertyDocument(document);
    }

    @Override
    public void writeOWLDeclarations() {
        this.owlDeclarationBuffer.writePropertyDeclarations(this.rdfWriter,
                this.hasTask(RdfSerializer.TASK_STATEMENTS),
                this.hasTask(RdfSerializer.TASK_SIMPLE_STATEMENTS));
    }
}
