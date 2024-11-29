package org.wikidata.wdtk.datamodel.helpers;

import java.util.ArrayList;
import java.util.Collection;

/*-
 * #%L
 * Wikidata Toolkit Data Model
 * %%
 * Copyright (C) 2014 - 2024 Wikidata Toolkit Developers
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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;

/**
 * Utility class for copying various data model elements.
 */
public class CopyUtils {

    /**
     * Copies a list of {@link ItemIdValue}.
     *
     * @param ids list of item IDs to copy
     * @return a new list of copied item IDs
     */
    public static List<ItemIdValue> copyItemIds(List<ItemIdValue> ids, DatamodelConverter converter) {
        return ids.stream()
                .map(converter::copy)
                .collect(Collectors.toList());
    }

    public static List<MonolingualTextValue> copyMonoLingualTextValues(
        Collection<MonolingualTextValue> monoLingualTextValues, 
        DatamodelConverter converter
    ) 
    {
        return monoLingualTextValues.stream()
            .map(converter::copy)
            .collect(Collectors.toList());
    }

    public static List<StatementGroup> copyStatementGroups(
        List<StatementGroup> statementGroups, 
        DatamodelConverter converter
    ) {
        return statementGroups.stream()
            .map(converter::copy)
            .collect(Collectors.toList());
    }
}
