package org.wikidata.wdtk.rdf;

/*-
 * #%L
 * Wikidata Toolkit RDF
 * %%
 * Copyright (C) 2014 - 2022 Wikidata Toolkit Developers
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

import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.implementation.DatatypeIdImpl;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;

import static org.junit.Assert.assertEquals;

public class AbstractRdfConverterTest {
    @Test
    public void testIriForItem() {
        assertEquals(
                AbstractRdfConverter.getDatatypeIri(Datamodel.makeDatatypeIdValueFromJsonString(DatatypeIdImpl.JSON_DT_ITEM)),
                Vocabulary.DT_ITEM);
    }

    @Test
    public void testIriForProperty() {
        assertEquals(
                AbstractRdfConverter.getDatatypeIri(Datamodel.makeDatatypeIdValueFromJsonString(DatatypeIdImpl.JSON_DT_PROPERTY)),
                Vocabulary.DT_PROPERTY);
    }

    @Test
    public void testIriForCoordinate() {
        assertEquals(
                AbstractRdfConverter.getDatatypeIri(Datamodel.makeDatatypeIdValueFromJsonString(DatatypeIdImpl.JSON_DT_GLOBE_COORDINATES)),
                Vocabulary.DT_GLOBE_COORDINATES);
    }

    @Test
    public void testIriForTime() {
        assertEquals(
                AbstractRdfConverter.getDatatypeIri(Datamodel.makeDatatypeIdValueFromJsonString(DatatypeIdImpl.JSON_DT_TIME)),
                Vocabulary.DT_TIME);
    }

    @Test
    public void testIriForString() {
        assertEquals(
                AbstractRdfConverter.getDatatypeIri(Datamodel.makeDatatypeIdValueFromJsonString(DatatypeIdImpl.JSON_DT_STRING)),
                Vocabulary.DT_STRING);
    }

    @Test
    public void testIriForQuantity() {
        assertEquals(
                AbstractRdfConverter.getDatatypeIri(Datamodel.makeDatatypeIdValueFromJsonString(DatatypeIdImpl.JSON_DT_QUANTITY)),
                Vocabulary.DT_QUANTITY);
    }

    @Test
    public void testIriForCommons() {
        assertEquals(
                AbstractRdfConverter.getDatatypeIri(Datamodel.makeDatatypeIdValueFromJsonString(DatatypeIdImpl.JSON_DT_COMMONS_MEDIA)),
                Vocabulary.DT_COMMONS_MEDIA);
    }

    @Test
    public void testIriForExternalId() {
        assertEquals(
                AbstractRdfConverter.getDatatypeIri(Datamodel.makeDatatypeIdValueFromJsonString(DatatypeIdImpl.JSON_DT_EXTERNAL_ID)),
                Vocabulary.DT_EXTERNAL_ID);
    }

    @Test
    public void testIriForMath() {
        assertEquals(
                AbstractRdfConverter.getDatatypeIri(Datamodel.makeDatatypeIdValueFromJsonString(DatatypeIdImpl.JSON_DT_MATH)),
                Vocabulary.DT_MATH);
    }

    @Test
    public void testIriForGeoShape() {
        assertEquals(
                AbstractRdfConverter.getDatatypeIri(Datamodel.makeDatatypeIdValueFromJsonString(DatatypeIdImpl.JSON_DT_GEO_SHAPE)),
                Vocabulary.DT_GEO_SHAPE);
    }

    @Test
    public void testIriForUrl() {
        assertEquals(
                AbstractRdfConverter.getDatatypeIri(Datamodel.makeDatatypeIdValueFromJsonString(DatatypeIdImpl.JSON_DT_URL)),
                Vocabulary.DT_URL);
    }

    @Test
    public void testIriForMonolingualText() {
        assertEquals(
                AbstractRdfConverter.getDatatypeIri(Datamodel.makeDatatypeIdValueFromJsonString(DatatypeIdImpl.JSON_DT_MONOLINGUAL_TEXT)),
                Vocabulary.DT_MONOLINGUAL_TEXT);
    }
    
    @Test
    public void testIriForEdtf() {
        assertEquals(
                AbstractRdfConverter.getDatatypeIri(Datamodel.makeDatatypeIdValueFromJsonString(DatatypeIdImpl.JSON_DT_EDTF)),
                Vocabulary.DT_EDTF);
    }
}
