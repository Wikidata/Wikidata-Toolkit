package org.wikidata.wdtk.wikibaseapi;

/*-
 * #%L
 * Wikidata Toolkit Wikibase API
 * %%
 * Copyright (C) 2014 - 2023 Wikidata Toolkit Developers
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

import java.util.Objects;
import java.util.OptionalLong;

/**
 * Holds information about a successful edit made via {@link WikibaseDataEditor}.
 * The state of the entity after edit is not provided here because it is not possible
 * for WDTK to determine it reliably from the response of the server. Indeed, it is
 * possible that the data in the entity after the edit differs from the data in the
 * entity before the edit plus the changes of the edit itself, because it can be that
 * another edit touched independent parts of the entity. This can happen even if the base
 * revision id is provided.
 */
public class EditingResult {
    
    private final long revisionId;

    public EditingResult(long revisionId) {
        super();
        this.revisionId = revisionId;
    }

    /**
     * The identifier of the revision of the last edit made by the editing action,
     * if any edit was made.
     */
    public OptionalLong getLastRevisionId() {
        return revisionId == 0 ? OptionalLong.empty() : OptionalLong.of(revisionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(revisionId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        EditingResult other = (EditingResult) obj;
        return revisionId == other.revisionId;
    }

    @Override
    public String toString() {
        return "EditingResult [revisionId=" + revisionId + "]";
    }
    
}
