package org.wikidata.wdtk.wikibaseapi;

/*
 * #%L
 * Wikidata Toolkit Wikibase API
 * %%
 * Copyright (C) 2014 - 2015 Wikidata Toolkit Developers
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.implementation.TermImpl;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.TermedStatementDocument;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import java.io.IOException;
import java.util.*;

/**
 * This class extends StatementUpdate to support update to terms (labels,
 * descriptions and aliases).
 * 
 * Various safeguards are implemented in this interface:
 * - aliases are added and deleted independently
 * - duplicate aliases cannot be added
 * - adding an alias in a language that does not have a label sets the label instead
 * 
 * @author antonin
 */
public class TermStatementUpdate extends StatementUpdate {
	static final Logger logger = LoggerFactory.getLogger(TermStatementUpdate.class);
    
    /**
     * Just adding a "write" field to keep track of whether
     * we have changed this value. That helps keep the edit cleaner.
     * 
     * @author antonin
     */
    private class NameWithUpdate {
        public MonolingualTextValue value;
        public boolean write;
        
        public NameWithUpdate(MonolingualTextValue value, boolean write) {
            this.value = value;
            this.write = write;
        }
    }
    
    /**
     * Keeps track of the current state of aliases after updates.
     * 
     * @author antonin
     */
    private class AliasesWithUpdate {
        public List<MonolingualTextValue> aliases;
        public List<MonolingualTextValue> added;
        public List<MonolingualTextValue> deleted;
        public boolean write;
        
        public AliasesWithUpdate(List<MonolingualTextValue> aliases, boolean write) {
            this.aliases = aliases;
            this.write = write;
            this.added = new ArrayList<>();
            this.deleted = new ArrayList<>();
        }
    }
    
    final protected TermedStatementDocument currentDocument;
    
    @JsonIgnore
    final Map<String, NameWithUpdate> newLabels;
    @JsonIgnore
    final Map<String, NameWithUpdate> newDescriptions;
    @JsonIgnore
    final Map<String, AliasesWithUpdate> newAliases;
    
    /**
     * Constructor. Plans an update on the statements and terms of a document.
     * Statements are merged according to StatementUpdate's logic. Labels and
     * descriptions will overwrite any existing values. The first aliases added 
     * on a language where no label is available yet will be treated as a label
     * instead. Duplicate aliases are ignored.
     * 
     * @param currentDocument
     * 			the current state of the entity
     * @param addStatements
     * 			the statements to be added to the entity.
     * @param deleteStatements
     *          the statements to be removed from the entity
     * @param addLabels
     *          the labels to be added to the entity
     * @param addDescriptions
     * 			the descriptions to be added to the entity
     * @param addAliases
     *          the aliases to be added to the entity
     * @param deleteAliases
     *          the aliases to be removed from the entity
     */
    public TermStatementUpdate(TermedStatementDocument currentDocument,
            List<Statement> addStatements,
            List<Statement> deleteStatements,
            List<MonolingualTextValue> addLabels,
            List<MonolingualTextValue> addDescriptions,
            List<MonolingualTextValue> addAliases,
            List<MonolingualTextValue> deleteAliases) {
        super(currentDocument, addStatements, deleteStatements);
        this.currentDocument = currentDocument;
        
        // Fill the terms with their current values
        newLabels = initUpdatesFromCurrentValues(currentDocument.getLabels().values());
        newDescriptions = initUpdatesFromCurrentValues(currentDocument.getDescriptions().values());
        newAliases = new HashMap<>();
        for(Map.Entry<String, List<MonolingualTextValue>> entry : currentDocument.getAliases().entrySet()) {
            newAliases.put(entry.getKey(),
                    new AliasesWithUpdate(
                    		new ArrayList<>(entry.getValue()), false));
        }
        
        // Add changes
        processLabels(addLabels);
        processDescriptions(addDescriptions);
        processAliases(addAliases, deleteAliases);
    }
    
    /**
     * Initializes the list of current values for a type of terms (label or description).
     * 
     * @param currentValues
     *      current values for the type of terms
     * @return a map of updates (where all terms are marked as not for write)
     */
    protected Map<String, NameWithUpdate> initUpdatesFromCurrentValues(Collection<MonolingualTextValue> currentValues) {
    	Map<String, NameWithUpdate> updates = new HashMap<>();
        for(MonolingualTextValue label: currentValues) {
            updates.put(label.getLanguageCode(),
                    new NameWithUpdate(label, false));
        }
        return updates;
    }

    /**
     * Processes changes on aliases, updating the planned state of the item.
     * 
     * @param addAliases
     * 		aliases that should be added to the document
     * @param deleteAliases
     * 		aliases that should be removed from the document
     */
    protected void processAliases(List<MonolingualTextValue> addAliases, List<MonolingualTextValue> deleteAliases) {
        for(MonolingualTextValue val : addAliases) {
            addAlias(val);
        }
        for(MonolingualTextValue val : deleteAliases) {
            deleteAlias(val);
        }
    }

    /**
     * Deletes an individual alias
     * 
     * @param alias
     * 		the alias to delete
     */
    protected void deleteAlias(MonolingualTextValue alias) {
        String lang = alias.getLanguageCode();
        AliasesWithUpdate currentAliases = newAliases.get(lang);
        if (currentAliases != null) {
            currentAliases.aliases.remove(alias);
            currentAliases.deleted.add(alias);
            currentAliases.write = true;
        }
    }

    /**
     * Adds an individual alias. It will be merged with the current
     * list of aliases, or added as a label if there is no label for
     * this item in this language yet.
     * 
     * @param alias
     * 		the alias to add
     */
    protected void addAlias(MonolingualTextValue alias) {
        String lang = alias.getLanguageCode();
        AliasesWithUpdate currentAliasesUpdate = newAliases.get(lang);
        
        NameWithUpdate currentLabel = newLabels.get(lang);
        // If there isn't any label for that language, put the alias there
        if (currentLabel == null) {
            newLabels.put(lang, new NameWithUpdate(alias, true));
        // If the new alias is equal to the current label, skip it
        } else if (!currentLabel.value.equals(alias)) {
        	if (currentAliasesUpdate == null) {
        		currentAliasesUpdate = new AliasesWithUpdate(new ArrayList<MonolingualTextValue>(), true);
        	}
        	List<MonolingualTextValue> currentAliases = currentAliasesUpdate.aliases;
        	if(!currentAliases.contains(alias)) {
        		currentAliases.add(alias);
        		currentAliasesUpdate.added.add(alias);
        		currentAliasesUpdate.write = true;
        	}
        	newAliases.put(lang, currentAliasesUpdate);
        }
    }

    /**
     * Adds descriptions to the item.
     * 
     * @param descriptions
     * 		the descriptions to add
     */
    protected void processDescriptions(List<MonolingualTextValue> descriptions) {
        for(MonolingualTextValue description : descriptions) {
        	NameWithUpdate currentValue = newDescriptions.get(description.getLanguageCode());
        	// only mark the description as added if the value we are writing is different from the current one
        	if (currentValue == null || !currentValue.value.equals(description)) {
        		newDescriptions.put(description.getLanguageCode(),
                    new NameWithUpdate(description, true));
        	}
        }
    }

    /**
     * Adds labels to the item
     * 
     * @param labels
     * 		the labels to add
     */
    protected void processLabels(List<MonolingualTextValue> labels) {
        for(MonolingualTextValue label : labels) {
        	String lang = label.getLanguageCode();
        	NameWithUpdate currentValue = newLabels.get(lang);
        	if (currentValue == null || !currentValue.value.equals(label)) {
	            newLabels.put(lang,
	                    new NameWithUpdate(label, true));
	            
	            // Delete any alias that matches the new label
	            AliasesWithUpdate currentAliases = newAliases.get(lang);
	            if (currentAliases != null && currentAliases.aliases.contains(label)) {
	            	deleteAlias(label);
	            }
        	}
        }
        
    }
    
    /**
     * Label accessor provided for JSON serialization only.
     */
    @JsonProperty("labels")
    @JsonInclude(Include.NON_EMPTY)
    public Map<String, TermImpl> getLabelUpdates() {
    	return getMonolingualUpdatedValues(newLabels);
    }
    
    /**
     * Description accessor provided for JSON serialization only.
     */
    @JsonProperty("descriptions")
    @JsonInclude(Include.NON_EMPTY)
    public Map<String, TermImpl> getDescriptionUpdates() {
    	return getMonolingualUpdatedValues(newDescriptions);
    }
    
    /**
     * Alias accessor provided for JSON serialization only
     */
    @JsonProperty("aliases")
    @JsonInclude(Include.NON_EMPTY)
    public Map<String, List<TermImpl>> getAliasUpdates() {
    	
    	Map<String, List<TermImpl>> updatedValues = new HashMap<>();
    	for(Map.Entry<String,AliasesWithUpdate> entry : newAliases.entrySet()) {
    		AliasesWithUpdate update = entry.getValue();
    		if (!update.write) {
    			continue;
    		}
    		List<TermImpl> convertedAliases = new ArrayList<>();
    		for(MonolingualTextValue alias : update.aliases) {
    			convertedAliases.add(monolingualToJackson(alias));
    		}
    		updatedValues.put(entry.getKey(), convertedAliases);
    	}
    	return updatedValues;
    }
    
    /**
     * Is this change null? (Which means that nothing at all
     * will be changed on the item.)
     */
    @Override
    @JsonIgnore
    public boolean isEmptyEdit() {
    	return (super.isEmptyEdit() && 
    			getLabelUpdates().isEmpty() &&
    			getDescriptionUpdates().isEmpty() &&
    			getAliasUpdates().isEmpty());
    }
    
    /**
     * Retrieves the list of aliases that will be added in a 
     * given language, after all the optimizations have been done
     * (replacing empty labels by new aliases in the same language,
     * for instance).
     * 
     * @param language the language code of the added aliases
     * @return the list of added aliases
     */
    public List<MonolingualTextValue> getAddedAliases(String language) {
		AliasesWithUpdate update = newAliases.get(language);
		if (update == null) {
			return Collections.<MonolingualTextValue>emptyList();
		}
		return update.added;
	}

    /**
     * Retrieves the list of aliases that will be removed in a 
     * given language, after all the optimizations have been done
     * (replacing empty labels by new aliases in the same language,
     * for instance).
     * 
     * @param language: the language code of the removed aliases
     * @return the list of removed aliases
     */
	public List<MonolingualTextValue> getRemovedAliases(String language) {
		AliasesWithUpdate update = newAliases.get(language);
		if (update == null) {
			return Collections.<MonolingualTextValue>emptyList();
		}
		return update.deleted;
	}

    
	/**
	 * Performs the update, selecting the appropriate API action depending on
	 * the nature of the change.
	 * 
	 * @return the new document after update with the API
	 * @throws MediaWikiApiErrorException 
	 * @throws IOException 
	 */
    @Override
	public TermedStatementDocument performEdit(WbEditingAction action, boolean editAsBot, String summary)
			throws IOException, MediaWikiApiErrorException {
		Map<String, TermImpl> labelUpdates = getLabelUpdates();
		Map<String, TermImpl> descriptionUpdates = getDescriptionUpdates();
		Map<String, List<TermImpl>> aliasUpdates = getAliasUpdates();
		if (labelUpdates.isEmpty() && descriptionUpdates.isEmpty() && aliasUpdates.isEmpty()) {
			return (TermedStatementDocument) super.performEdit(action, editAsBot, summary);	
		} else {
			if (super.isEmptyEdit()) {
				if(labelUpdates.size() == 1
					&& descriptionUpdates.isEmpty()
					&& aliasUpdates.isEmpty()) {
					// we only have a label in one language to update, so we use "wbsetlabel"
					String language = labelUpdates.keySet().iterator().next();
					MonolingualTextValue value = labelUpdates.get(language);
					
					JsonNode response = action.wbSetLabel(
							currentDocument.getEntityId().getId(),
							null, null, null, language, value.getText(), editAsBot,
							currentDocument.getRevisionId(), summary);
					
					MonolingualTextValue respondedLabel = getDatamodelObjectFromResponse(response,
							Arrays.asList("entity","labels",language), TermImpl.class);
					long revisionId = getRevisionIdFromResponse(response);
					
					return this.currentDocument.withRevisionId(revisionId).withLabel(respondedLabel);
				} else if (labelUpdates.isEmpty()
					&& descriptionUpdates.size() == 1
					&& aliasUpdates.isEmpty()) {
					// we only have a label in one language to update, so we use "wbsetlabel"
					String language = descriptionUpdates.keySet().iterator().next();
					MonolingualTextValue value = descriptionUpdates.get(language);
					
					JsonNode response = action.wbSetDescription(
							currentDocument.getEntityId().getId(),
							null, null, null, language, value.getText(), editAsBot,
							currentDocument.getRevisionId(), summary);
					
					MonolingualTextValue respondedDescription = getDatamodelObjectFromResponse(response,
							Arrays.asList("entity","descriptions",language), TermImpl.class);
					long revisionId = getRevisionIdFromResponse(response);
					
					return currentDocument.withRevisionId(revisionId).withDescription(respondedDescription);
				} else if (labelUpdates.isEmpty()
						&& descriptionUpdates.isEmpty()
						&& aliasUpdates.size() == 1) {
					// we only have aliases in one language to update, so we use "wbsetaliases"
					String language = aliasUpdates.keySet().iterator().next();
					List<MonolingualTextValue> addedValues = getAddedAliases(language);
					List<MonolingualTextValue> removedValues = getRemovedAliases(language);
					List<String> addedStrings = new ArrayList<>(addedValues.size());
					for(MonolingualTextValue v : addedValues) {
						addedStrings.add(v.getText());
					}
					List<String> removedStrings = new ArrayList<>(removedValues.size());
					for(MonolingualTextValue v : removedValues) {
						removedStrings.add(v.getText());
					}
					
					JsonNode response = action.wbSetAliases(
							currentDocument.getEntityId().getId(),
							null, null, null, language, addedStrings, removedStrings, null, editAsBot,
							currentDocument.getRevisionId(), summary);
					
					long revisionId = getRevisionIdFromResponse(response);

					TermImpl[] respondedAliases = getDatamodelObjectFromResponse(response,
							Arrays.asList("entity","aliases",language), TermImpl[].class);
					List<MonolingualTextValue> newAliases = Arrays.asList(respondedAliases);
					
					return currentDocument.withRevisionId(revisionId).withAliases(language, newAliases);
			    }
			}
			
			// All other cases: we do a full-blown "wbeditentity"
			EntityDocument response = action.wbEditEntity(currentDocument
				.getEntityId().getId(), null, null, null, getJsonUpdateString(),
				false, editAsBot, currentDocument
				.getRevisionId(), summary);
			return (TermedStatementDocument) response;
    	}
	}

	/**
     * Helper to format term updates as expected by the Wikibase API
     * @param updates
     * 		planned updates for the type of term
     * @return map ready to be serialized as JSON by Jackson
     */
    protected Map<String, TermImpl> getMonolingualUpdatedValues(Map<String, NameWithUpdate> updates) {
    	Map<String, TermImpl> updatedValues = new HashMap<>();
    	for(NameWithUpdate update : updates.values()) {
            if (!update.write) {
                continue;
            }
            updatedValues.put(update.value.getLanguageCode(), monolingualToJackson(update.value));
    	}
    	return updatedValues;
    }
    
    /**
     * Creates a monolingual value that is suitable for JSON serialization.
     * @param monolingualTextValue
     * 		target monolingual value for serialization
     * @return Jackson implementation that is serialized appropriately
     */
    protected TermImpl monolingualToJackson(MonolingualTextValue monolingualTextValue) {
    	return new TermImpl(monolingualTextValue.getLanguageCode(), monolingualTextValue.getText());
    }
}
