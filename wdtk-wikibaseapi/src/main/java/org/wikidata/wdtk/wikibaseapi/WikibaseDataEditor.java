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
package org.wikidata.wdtk.wikibaseapi;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.wikidata.wdtk.datamodel.helpers.EntityUpdateBuilder;
import org.wikidata.wdtk.datamodel.helpers.JsonSerializer;
import org.wikidata.wdtk.datamodel.helpers.LabeledDocumentUpdateBuilder;
import org.wikidata.wdtk.datamodel.helpers.LexemeUpdateBuilder;
import org.wikidata.wdtk.datamodel.helpers.StatementDocumentUpdateBuilder;
import org.wikidata.wdtk.datamodel.helpers.StatementUpdateBuilder;
import org.wikidata.wdtk.datamodel.helpers.TermUpdateBuilder;
import org.wikidata.wdtk.datamodel.helpers.TermedDocumentUpdateBuilder;
import org.wikidata.wdtk.datamodel.interfaces.AliasUpdate;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityUpdate;
import org.wikidata.wdtk.datamodel.interfaces.FormUpdate;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.LabeledStatementDocumentUpdate;
import org.wikidata.wdtk.datamodel.interfaces.LexemeDocument;
import org.wikidata.wdtk.datamodel.interfaces.LexemeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.LexemeUpdate;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SenseUpdate;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementDocument;
import org.wikidata.wdtk.datamodel.interfaces.StatementDocumentUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermedStatementDocument;
import org.wikidata.wdtk.datamodel.interfaces.TermedStatementDocumentUpdate;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Class that provides high-level editing functionality for Wikibase data.
 *
 * @author Markus Kroetzsch
 *
 */
public class WikibaseDataEditor {

	/**
	 * API Action to edit data.
	 */
	final WbEditingAction wbEditingAction;

	/**
	 * Helper class to read data. Used for checking the state of the online data
	 * before editing.
	 */
	final WikibaseDataFetcher wikibaseDataFetcher;
	
	/**
	 * GUID generator, for editing actions that require generating fresh GUIDs
	 * client-side.
	 */
	final GuidGenerator guidGenerator;

	/**
	 * The IRI that identifies the site that the data is from.
	 */
	final String siteIri;

	/**
	 * If true, the bot flag will be set for all edits. This will only have
	 * effect when logged in with a user account that is in the bot group.
	 */
	boolean editAsBot = false;

	/**
	 * Creates an object to edit data via the Web API of the given
	 * {@link ApiConnection} object. The site URI is necessary to create data
	 * objects from API responses, since it is not contained in the data
	 * retrieved from the URI.
	 *
	 * @param connection
	 *            ApiConnection
	 * @param siteUri
	 *            the URI identifying the site that is accessed (usually the
	 *            prefix of entity URIs), e.g.,
	 *            "http://www.wikidata.org/entity/"
	 */
	public WikibaseDataEditor(ApiConnection connection, String siteUri) {
		this.wbEditingAction = new WbEditingAction(connection, siteUri);
		this.wikibaseDataFetcher = new WikibaseDataFetcher(connection, siteUri);
		this.siteIri = siteUri;
		this.guidGenerator = new RandomGuidGenerator();
	}
	
	/**
	 * Creates an object to edit data via the Web API of the given
	 * {@link ApiConnection} object. The site URI is necessary to create data
	 * objects from API responses, since it is not contained in the data
	 * retrieved from the URI.
	 *
	 * @param connection
	 *            ApiConnection
	 * @param siteUri
	 *            the URI identifying the site that is accessed (usually the
	 *            prefix of entity URIs), e.g.,
	 *            "http://www.wikidata.org/entity/"
	 * @param generator
	 * 			  the generator to use when creating fresh GUIDs for statements,
	 *            snaks or references
	 */
	public WikibaseDataEditor(ApiConnection connection, String siteUri, GuidGenerator generator) {
		this.wbEditingAction = new WbEditingAction(connection, siteUri);
		this.wikibaseDataFetcher = new WikibaseDataFetcher(connection, siteUri);
		this.siteIri = siteUri;
		this.guidGenerator = generator;
	}

	WikibaseDataEditor(WbEditingAction action, WikibaseDataFetcher fetcher, String siteUri, GuidGenerator generator) {
		this.wbEditingAction = action;
		this.wikibaseDataFetcher = fetcher;
		this.siteIri = siteUri;
		this.guidGenerator = generator;
	}

	/**
	 * Returns true if edits should be flagged as bot edits. See
	 * {@link #setEditAsBot(boolean)} for details.
	 *
	 * @return whether to flag edits as bot
	 */
	public boolean editAsBot() {
		return this.editAsBot;
	}

	/**
	 * Switches the use of the bot parameter on or of. When set to true, the bot
	 * flag will be set for all edits. This will only have effect when logged in
	 * with a user account that is in the bot group. Bot users should set this
	 * to true in almost every case.
	 *
	 * @param editAsBot
	 */
	public void setEditAsBot(boolean editAsBot) {
		this.editAsBot = editAsBot;
	}

	/**
	 * Returns the current value of the maxlag parameter. It specifies the
	 * number of seconds. To save actions causing any more site replication lag,
	 * this parameter can make the client wait until the replication lag is less
	 * than the specified value. In case of excessive lag, error code "maxlag"
	 * is returned upon API requests.
	 *
	 * @return current setting of the maxlag parameter
	 */
	public int getMaxLag() {
		return this.wbEditingAction.getMaxLag();
	}

	/**
	 * Set the value of the maxlag parameter. If unsure, keep the default. See
	 * {@link WikibaseDataEditor#getMaxLag()} for details.
	 *
	 * @param maxLag
	 *            the new value in seconds
	 */
	public void setMaxLag(int maxLag) {
		this.wbEditingAction.setMaxLag(maxLag);
	}
	
	/**
	 * Number of times we should retry if an editing action fails because
	 * the lag is too high.
	 */
	public int getMaxLagMaxRetries() {
		return this.wbEditingAction.getMaxLagMaxRetries();
	}

	/**
	 * Number of times we should retry if an editing action fails because
	 * the lag is too high.
	 */
	public void setMaxLagMaxRetries(int retries) {
		this.wbEditingAction.setMaxLagMaxRetries(retries);
	}

	/**
	 * Initial wait time in milliseconds, when an edit fails for the first
	 * time because of a high lag. This wait time is going to be multiplied
	 * by maxLagBackOffFactor for the subsequent waits. 
	 */
	public int getMaxLagFirstWaitTime() {
		return this.wbEditingAction.getMaxLagFirstWaitTime();
	}

	/**
	 * Initial wait time in milliseconds, when an edit fails for the first
	 * time because of a high lag. This wait time is going to be multiplied
	 * by maxLagBackOffFactor for the subsequent waits. 
	 */
	public void setMaxLagFirstWaitTime(int time) {
		this.wbEditingAction.setMaxLagFirstWaitTime(time);
	}

	/**
	 * Factor by which the wait time between two maxlag retries should be
	 * multiplied at each attempt.
	 */
	public double getMaxLagBackOffFactor() {
		return this.wbEditingAction.getMaxLagBackOffFactor();
	}

	/**
	 * Factor by which the wait time between two maxlag retries should be
	 * multiplied at each attempt.
	 */
	public void setMaxLagBackOffFactor(double value) {
		this.wbEditingAction.setMaxLagBackOffFactor(value);
	}

	/**
	 * Returns the average time that a single edit should take, measured in
	 * milliseconds. See {@link WbEditingAction#getAverageTimePerEdit()} for
	 * details.
	 *
	 * @return average time per edit in milliseconds
	 */
	public int getAverageTimePerEdit() {
		return this.wbEditingAction.getAverageTimePerEdit();
	}

	/**
	 * Sets the average time that a single edit should take, measured in
	 * milliseconds. See {@link WbEditingAction#getAverageTimePerEdit()} for
	 * details.
	 *
	 * @param milliseconds
	 *            the new value in milliseconds
	 */
	public void setAverageTimePerEdit(int milliseconds) {
		this.wbEditingAction.setAverageTimePerEdit(milliseconds);
	}

	/**
	 * Returns the number of edits that will be performed before entering
	 * simulation mode, or -1 if there is no limit on the number of edits
	 * (default). See {@link WbEditingAction#getRemainingEdits()} for
	 * details.
	 *
	 * @return number of remaining edits
	 */
	public int getRemainingEdits() {
		return this.wbEditingAction.getRemainingEdits();
	}

	/**
	 * Sets the number of edits that this object can still perform. See
	 * {@link WbEditingAction#setRemainingEdits(int)} for details.
	 *
	 * @param remainingEdits
	 *            number of edits that can still be performed, or -1 to disable
	 *            this limit (default setting)
	 */
	public void setRemainingEdits(int remainingEdits) {
		this.wbEditingAction.setRemainingEdits(remainingEdits);
	}

	/**
	 * Sets the remaining edits for this component to 0, so that all edits are
	 * simulated but not actually send to the API.
	 */
	public void disableEditing() {
		this.wbEditingAction.setRemainingEdits(0);
	}

	private EntityDocument createDocument(
			String type, EntityDocument document, String summary, List<String> tags)
			throws IOException, MediaWikiApiErrorException {
		String data = JsonSerializer.getJsonString(document);
		return this.wbEditingAction.wbEditEntity(
				null, null, null, type, data, false, editAsBot, 0, summary, tags);
	}
	
	/**
	 * Creates new entity document. Provided entity document must use a local item ID,
	 * such as {@link ItemIdValue#NULL}, and its revision ID must be 0.
	 * <p>
	 * The newly created document is returned. It will contain the new item ID and
	 * revision ID. Note that the site IRI used in the item ID is not part of the
	 * API response. The site IRI given when constructing this object is used in its
	 * place.
	 * <p>
	 * Statements in the provided document must not have IDs.
	 * <p>
	 * Summary message will be prepended by an automatically generated comment. The
	 * length limit of the autocomment together with the summary is 260 characters.
	 * Everything above that limit will be cut off.
	 *
	 * @param document
	 *            document that contains the data to be written
	 * @param summary
	 *            summary for the edit
	 * @param tags
	 *            string identifiers of the tags to apply to the edit, {@code null}
	 *            or empty for no tags
	 * @return newly created item document or {@code null} for simulated edit (see
	 *         {@link #disableEditing()}
	 * @throws IOException
	 *             if there was an IO problem, such as missing network connection
	 * @throws MediaWikiApiErrorException
	 *             if MediaWiki API returned an error response
	 */
	public EntityDocument createEntityDocument(
			EntityDocument document, String summary, List<String> tags) throws IOException, MediaWikiApiErrorException {
		if (document instanceof ItemDocument) {
			return createItemDocument((ItemDocument) document, summary, tags);
		} else if (document instanceof PropertyDocument) {
			return createPropertyDocument((PropertyDocument) document, summary, tags);
		} else if (document instanceof LexemeDocument) {
			return createLexemeDocument((LexemeDocument) document, summary, tags);
		} else {
			throw new UnsupportedOperationException("Creation of entities of this type is not supported");
		}
	}

	/**
	 * Creates new item document. Provided item document must use a local item ID,
	 * such as {@link ItemIdValue#NULL}, and its revision ID must be 0.
	 * <p>
	 * The newly created document is returned. It will contain the new item ID and
	 * revision ID. Note that the site IRI used in the item ID is not part of the
	 * API response. The site IRI given when constructing this object is used in its
	 * place.
	 * <p>
	 * Statements in the provided document must not have IDs.
	 * <p>
	 * Summary message will be prepended by an automatically generated comment. The
	 * length limit of the autocomment together with the summary is 260 characters.
	 * Everything above that limit will be cut off.
	 *
	 * @param document
	 *            document that contains the data to be written
	 * @param summary
	 *            summary for the edit
	 * @param tags
	 *            string identifiers of the tags to apply to the edit, {@code null}
	 *            or empty for no tags
	 * @return newly created item document or {@code null} for simulated edit (see
	 *         {@link #disableEditing()}
	 * @throws IOException
	 *             if there was an IO problem, such as missing network connection
	 * @throws MediaWikiApiErrorException
	 *             if MediaWiki API returned an error response
	 */
	public ItemDocument createItemDocument(
			ItemDocument document, String summary, List<String> tags)
			throws IOException, MediaWikiApiErrorException {
		return (ItemDocument) createDocument("item", document, summary, tags);
	}

	/**
	 * Creates new property document. Provided property document must use a local
	 * property ID, such as {@link PropertyIdValue#NULL}, and its revision ID must
	 * be 0.
	 * <p>
	 * The newly created document is returned. It will contain the new property ID
	 * and revision ID. Note that the site IRI used in the property ID is not part
	 * of the API response. The site IRI given when constructing this object is used
	 * in its place.
	 * <p>
	 * Statements in the provided document must not have IDs.
	 * <p>
	 * Summary message will be prepended by an automatically generated comment. The
	 * length limit of the autocomment together with the summary is 260 characters.
	 * Everything above that limit will be cut off.
	 *
	 * @param document
	 *            document that contains the data to be written
	 * @param summary
	 *            summary for the edit
	 * @param tags
	 *            string identifiers of the tags to apply to the edit, {@code null}
	 *            or empty for no tags
	 * @return newly created property document or {@code null} for simulated edit
	 *         (see {@link #disableEditing()}
	 * @throws IOException
	 *             if there was an IO problem, such as missing network connection
	 * @throws MediaWikiApiErrorException
	 *             if MediaWiki API returned an error response
	 */
	public PropertyDocument createPropertyDocument(
			PropertyDocument document, String summary, List<String> tags)
			throws IOException, MediaWikiApiErrorException {
		return (PropertyDocument) createDocument("property", document, summary, tags);
	}

	/**
	 * Creates new lexeme document. Provided lexeme document must use a local lexeme
	 * ID, such as {@link LexemeIdValue#NULL}, and its revision ID must be 0.
	 * <p>
	 * The newly created document is returned. It will contain the new lexeme ID and
	 * revision ID. Note that the site IRI used in the lexeme ID is not part of the
	 * API response. The site IRI given when constructing this object is used in its
	 * place.
	 * <p>
	 * Statements, senses, and forms in the provided document must not have IDs.
	 * <p>
	 * Summary message will be prepended by an automatically generated comment. The
	 * length limit of the autocomment together with the summary is 260 characters.
	 * Everything above that limit will be cut off.
	 *
	 * @param document
	 *            document that contains the data to be written
	 * @param summary
	 *            summary for the edit
	 * @param tags
	 *            string identifiers of the tags to apply to the edit, {@code null}
	 *            or empty for no tags
	 * @return newly created lexeme document or {@code null} for simulated edit (see
	 *         {@link #disableEditing()}
	 * @throws IOException
	 *             if there was an IO problem, such as missing network connection
	 * @throws MediaWikiApiErrorException
	 *             if MediaWiki API returned an error response
	 */
	public LexemeDocument createLexemeDocument(
			LexemeDocument document, String summary, List<String> tags)
			throws IOException, MediaWikiApiErrorException {
		return (LexemeDocument) createDocument("lexeme", document, summary, tags);
	}

	/**
	 * @deprecated Use {@link #editEntityDocument(EntityUpdate, boolean, String, List)} instead.
	 * Writes the data for the given item document with the summary message as
	 * given. Optionally, the existing data is cleared (deleted).
	 * <p>
	 * The id of the given item document is used to specify which item document
	 * should be changed. The site IRI will be ignored for this.
	 * <p>
	 * The revision id of the given item document is used to specify the base
	 * revision, enabling the API to detect edit conflicts. The value 0 can be
	 * used to omit this. It is strongly recommended to give a revision id when
	 * making edits where the outcome depends on the previous state of the data
	 * (i.e., any edit that does not use "clear").
	 * <p>
	 * If the data is not cleared, then the existing data will largely be
	 * preserved. Statements with empty ids will be added without checking if
	 * they exist already; statements with (valid) ids will replace any existing
	 * statements with these ids or just be added if there are none. Labels,
	 * descriptions, and aliases will be preserved for all languages for which
	 * no data is given at all. For aliases this means that writing one alias in
	 * a language will overwrite all aliases in this language, so some care is
	 * needed.
	 *
	 * @param itemDocument
	 *            the document that contains the data to be written
	 * @param clear
	 *            if true, the existing data will be replaced by the given data;
	 *            if false, the given data will be added to the existing data,
	 *            overwriting only parts that are set to new values
	 * @param summary
	 *            summary for the edit; will be prepended by an automatically
	 *            generated comment; the length limit of the autocomment
	 *            together with the summary is 260 characters: everything above
	 *            that limit will be cut off
	 * @param tags
	 *            string identifiers of the tags to apply to the edit.
	 * @return the modified item document, or null if there was an error
	 * @throws IOException
	 *             if there was an IO problem, such as missing network
	 *             connection
	 * @throws MediaWikiApiErrorException
	 */
	@Deprecated
	public ItemDocument editItemDocument(ItemDocument itemDocument,
			boolean clear, String summary, List<String> tags) throws IOException,
			MediaWikiApiErrorException {
		String data = JsonSerializer.getJsonString(itemDocument);
		return (ItemDocument) this.wbEditingAction.wbEditEntity(itemDocument
				.getEntityId().getId(), null, null, null, data, clear,
				this.editAsBot, itemDocument.getRevisionId(), summary, tags);
	}

	/**
	 * Updates {@link EntityDocument} entity. ID of the entity to update is taken
	 * from the update object. Its site IRI is ignored. No action is taken if the
	 * update is empty.
	 * <p>
	 * If the update object references base revision of the document, its revision
	 * ID is used to specify the base revision in the API request, enabling the API
	 * to detect edit conflicts. It is strongly recommended to specify base revision
	 * document in the update object.
	 * <p>
	 * Summary message will be prepended by an automatically generated comment. The
	 * length limit of the autocomment together with the summary is 260 characters.
	 * Everything above that limit will be cut off.
	 *
	 * @param update
	 *            collection of changes to be written
	 * @param clear
	 *            if set to {@code true}, existing entity data will be removed and
	 *            the update will be applied to empty entity
	 * @param summary
	 *            summary for the edit
	 * @param tags
	 *            string identifiers of the tags to apply to the edit, {@code null}
	 *            or empty for no tags
	 * @throws IOException
	 *             if there was an IO problem, such as missing network connection
	 * @throws MediaWikiApiErrorException
	 *             if MediaWiki API returned an error response
	 */
	public EditingResult editEntityDocument(
			EntityUpdate update, boolean clear, String summary, List<String> tags)
			throws IOException, MediaWikiApiErrorException {
		long revisionId = update.getBaseRevisionId();
		if (!clear) {
			if (update.isEmpty())
				return new EditingResult(0L);
			if (update instanceof StatementDocumentUpdate) {
				StatementDocumentUpdate typed = (StatementDocumentUpdate) update;
				if (typed.getStatements().getAdded().size() == 1) {
					StatementDocumentUpdateBuilder builder = StatementDocumentUpdateBuilder
							.forBaseRevisionId(typed.getEntityId(), typed.getBaseRevisionId());
					Statement statement = typed.getStatements().getAdded().stream().findFirst().get();
					builder.updateStatements(StatementUpdateBuilder.create().add(statement).build());
					if (builder.build().equals(update)) {
						String statementId = guidGenerator.freshStatementId(typed.getEntityId().getId());
						Statement prepared = statement.withStatementId(statementId);
						JsonNode response = wbEditingAction.wbSetClaim(JsonSerializer.getJsonString(prepared),
								editAsBot, revisionId, summary, tags);
						return new EditingResult(getRevisionIdFromResponse(response));
					}
				}
				if (typed.getStatements().getReplaced().size() == 1) {
					StatementDocumentUpdateBuilder builder = StatementDocumentUpdateBuilder
							.forBaseRevisionId(typed.getEntityId(), typed.getBaseRevisionId());
					Statement statement = typed.getStatements().getReplaced().values().stream().findFirst().get();
					builder.updateStatements(StatementUpdateBuilder.create().replace(statement).build());
					if (builder.build().equals(update)) {
						JsonNode response = wbEditingAction.wbSetClaim(JsonSerializer.getJsonString(statement),
								editAsBot, revisionId, summary, tags);
						return new EditingResult(getRevisionIdFromResponse(response));
					}
				}
				if (!typed.getStatements().getRemoved().isEmpty()
						&& typed.getStatements().getRemoved().size() <= 50) {
					StatementDocumentUpdateBuilder builder = StatementDocumentUpdateBuilder
							.forBaseRevisionId(typed.getEntityId(), typed.getBaseRevisionId());
					List<String> statementIds = new ArrayList<>(typed.getStatements().getRemoved());
					StatementUpdateBuilder statementBuilder = StatementUpdateBuilder.create();
					for (String statementId : statementIds) {
						statementBuilder.remove(statementId);
					}
					builder.updateStatements(statementBuilder.build());
					if (builder.build().equals(update)) {
					    JsonNode response = wbEditingAction.wbRemoveClaims(statementIds, editAsBot, revisionId, summary, tags);
						return new EditingResult(getRevisionIdFromResponse(response));
					}
				}
			}
			if (update instanceof LabeledStatementDocumentUpdate) {
				LabeledStatementDocumentUpdate typed = (LabeledStatementDocumentUpdate) update;
				if (typed.getLabels().getModified().size() == 1) {
					LabeledDocumentUpdateBuilder builder = LabeledDocumentUpdateBuilder
							.forBaseRevisionId(typed.getEntityId(), typed.getBaseRevisionId());
					MonolingualTextValue label = typed.getLabels().getModified().values().stream().findFirst().get();
					builder.updateLabels(TermUpdateBuilder.create().put(label).build());
					if (builder.build().equals(update)) {
						JsonNode response = wbEditingAction.wbSetLabel(update.getEntityId().getId(), null, null, null,
								label.getLanguageCode(), label.getText(), editAsBot, revisionId, summary, tags);
						return new EditingResult(getRevisionIdFromResponse(response));
					}
				}
				if (typed.getLabels().getRemoved().size() == 1) {
					LabeledDocumentUpdateBuilder builder = LabeledDocumentUpdateBuilder
							.forBaseRevisionId(typed.getEntityId(), typed.getBaseRevisionId());
					String language = typed.getLabels().getRemoved().stream().findFirst().get();
					builder.updateLabels(TermUpdateBuilder.create().remove(language).build());
					if (builder.build().equals(update)) {
						JsonNode response = wbEditingAction.wbSetLabel(update.getEntityId().getId(), null, null, null,
								language, null, editAsBot, revisionId, summary, tags);
						return new EditingResult(getRevisionIdFromResponse(response));
					}
				}
			}
			if (update instanceof TermedStatementDocumentUpdate) {
				TermedStatementDocumentUpdate typed = (TermedStatementDocumentUpdate) update;
				if (typed.getDescriptions().getModified().size() == 1) {
					TermedDocumentUpdateBuilder builder = TermedDocumentUpdateBuilder
							.forBaseRevisionId(typed.getEntityId(), typed.getBaseRevisionId());
					MonolingualTextValue description = typed.getDescriptions().getModified()
							.values().stream().findFirst().get();
					builder.updateDescriptions(TermUpdateBuilder.create().put(description).build());
					if (builder.build().equals(update)) {
						JsonNode response = wbEditingAction.wbSetDescription(update.getEntityId().getId(), null, null, null,
								description.getLanguageCode(), description.getText(),
								editAsBot, revisionId, summary, tags);
						return new EditingResult(getRevisionIdFromResponse(response));
					}
				}
				if (typed.getDescriptions().getRemoved().size() == 1) {
					TermedDocumentUpdateBuilder builder = TermedDocumentUpdateBuilder
							.forBaseRevisionId(typed.getEntityId(), typed.getBaseRevisionId());
					String language = typed.getDescriptions().getRemoved().stream().findFirst().get();
					builder.updateDescriptions(TermUpdateBuilder.create().remove(language).build());
					if (builder.build().equals(update)) {
						JsonNode response = wbEditingAction.wbSetDescription(update.getEntityId().getId(), null, null, null,
								language, null, editAsBot, revisionId, summary, tags);
						return new EditingResult(getRevisionIdFromResponse(response));
					}
				}
				if (typed.getAliases().size() == 1) {
					TermedDocumentUpdateBuilder builder = TermedDocumentUpdateBuilder
							.forBaseRevisionId(typed.getEntityId(), typed.getBaseRevisionId());
					String language = typed.getAliases().keySet().stream().findFirst().get();
					AliasUpdate aliases = typed.getAliases().get(language);
					builder.updateAliases(language, aliases);
					if (builder.build().equals(update)) {
						List<String> added = !aliases.getAdded().isEmpty()
								? aliases.getAdded().stream().map(a -> a.getText()).collect(toList())
								: null;
						List<String> removed = !aliases.getRemoved().isEmpty()
								? aliases.getRemoved().stream().map(a -> a.getText()).collect(toList())
								: null;
						List<String> recreated = aliases.getRecreated()
								.map(l -> l.stream().map(a -> a.getText()).collect(toList()))
								.orElse(null);
						JsonNode response = wbEditingAction.wbSetAliases(update.getEntityId().getId(), null, null, null,
								language, added, removed, recreated, editAsBot, revisionId, summary, tags);
						return new EditingResult(getRevisionIdFromResponse(response));
					}
				}
			}
			if (update instanceof LexemeUpdate) {
				LexemeUpdate typed = (LexemeUpdate) update;
				if (typed.getUpdatedSenses().size() == 1) {
					LexemeUpdateBuilder builder = LexemeUpdateBuilder
							.forBaseRevisionId(typed.getEntityId(), typed.getBaseRevisionId());
					SenseUpdate sense = typed.getUpdatedSenses().values().stream().findFirst().get();
					builder.updateSense(sense);
					if (builder.build().equals(update)) {
						return editEntityDocument(sense, false, summary, tags);
					}
				}
				if (typed.getUpdatedForms().size() == 1) {
					LexemeUpdateBuilder builder = LexemeUpdateBuilder
							.forBaseRevisionId(typed.getEntityId(), typed.getBaseRevisionId());
					FormUpdate form = typed.getUpdatedForms().values().stream().findFirst().get();
					builder.updateForm(form);
					if (builder.build().equals(update)) {
						return editEntityDocument(form, false, summary, tags);
					}
				}
			}
		}
		String data = JsonSerializer.getJsonString(update);
		EntityDocument document = wbEditingAction.wbEditEntity(
				update.getEntityId().getId(), null, null, null, data, clear, editAsBot, revisionId, summary, tags);
		return new EditingResult(document.getRevisionId());
	}

	/**
	 * @deprecated Use {@link #editEntityDocument(EntityUpdate, boolean, String, List)} instead.
	 * Writes the data for the given property document with the summary message
	 * as given. Optionally, the existing data is cleared (deleted).
	 * <p>
	 * The id of the given property document is used to specify which property
	 * document should be changed. The site IRI will be ignored for this.
	 * <p>
	 * The revision id of the given property document is used to specify the
	 * base revision, enabling the API to detect edit conflicts. The value 0 can
	 * be used to omit this. It is strongly recommended to give a revision id
	 * when making edits where the outcome depends on the previous state of the
	 * data (i.e., any edit that does not use "clear").
	 * <p>
	 * If the data is not cleared, then the existing data will largely be
	 * preserved. Statements with empty ids will be added without checking if
	 * they exist already; statements with (valid) ids will replace any existing
	 * statements with these ids or just be added if there are none. Labels,
	 * descriptions, and aliases will be preserved for all languages for which
	 * no data is given at all. For aliases this means that writing one alias in
	 * a language will overwrite all aliases in this language, so some care is
	 * needed.
	 *
	 * @param propertyDocument
	 *            the document that contains the data to be written
	 * @param clear
	 *            if true, the existing data will be replaced by the given data;
	 *            if false, the given data will be added to the existing data,
	 *            overwriting only parts that are set to new values
	 * @param summary
	 *            summary for the edit; will be prepended by an automatically
	 *            generated comment; the length limit of the autocomment
	 *            together with the summary is 260 characters: everything above
	 *            that limit will be cut off
	 * @param tags
	 *            string identifiers of the tags to apply to the edit.
	 * @return the modified property document, or null if there was an error
	 * @throws IOException
	 *             if there was an IO problem, such as missing network
	 *             connection
	 * @throws MediaWikiApiErrorException
	 */
	@Deprecated
	public PropertyDocument editPropertyDocument(
			PropertyDocument propertyDocument, boolean clear, String summary,
			List<String> tags)
			throws IOException, MediaWikiApiErrorException {
		String data = JsonSerializer.getJsonString(propertyDocument);
		return (PropertyDocument) this.wbEditingAction.wbEditEntity(
				propertyDocument.getEntityId().getId(), null, null, null,
				data, clear, this.editAsBot, propertyDocument.getRevisionId(),
				summary, tags);
	}

	/**
	 * @deprecated Use {@link #editEntityDocument(EntityUpdate, boolean, String, List)} instead.
	 * Writes the data for the given media info document with the summary message
	 * as given. Optionally, the existing data is cleared (deleted).
	 * It creates the media info if needed.
	 * <p>
	 * The id of the given media info document is used to specify which media info
	 * document should be changed or created. The site IRI will be ignored for this.
	 * <p>
	 * The revision id of the given media info document is used to specify the
	 * base revision, enabling the API to detect edit conflicts. The value 0 can
	 * be used to omit this. It is strongly recommended to give a revision id
	 * when making edits where the outcome depends on the previous state of the
	 * data (i.e., any edit that does not use "clear").
	 * <p>
	 * If the data is not cleared, then the existing data will largely be
	 * preserved. Statements with empty ids will be added without checking if
	 * they exist already; statements with (valid) ids will replace any existing
	 * statements with these ids or just be added if there are none. Labels
	 * will be preserved for all languages for which no data is given at all.
	 * For aliases this means that writing one alias in
	 *
	 * @param mediaInfoDocument
	 *            the document that contains the data to be written
	 * @param clear
	 *            if true, the existing data will be replaced by the given data;
	 *            if false, the given data will be added to the existing data,
	 *            overwriting only parts that are set to new values
	 * @param summary
	 *            summary for the edit; will be prepended by an automatically
	 *            generated comment; the length limit of the autocomment
	 *            together with the summary is 260 characters: everything above
	 *            that limit will be cut off
	 * @param tags
	 *            string identifiers of the tags to apply to the edit.
	 * @return the modified media info document, or null if there was an error
	 * @throws IOException
	 *             if there was an IO problem, such as missing network
	 *             connection
	 */
	@Deprecated
	public MediaInfoDocument editMediaInfoDocument(
			MediaInfoDocument mediaInfoDocument, boolean clear, String summary,
			List<String> tags)
			throws IOException, MediaWikiApiErrorException {
		String data = JsonSerializer.getJsonString(mediaInfoDocument);
		return (MediaInfoDocument) this.wbEditingAction.wbEditEntity(
				mediaInfoDocument.getEntityId().getId(), null, null, null,
				data, clear, this.editAsBot, mediaInfoDocument.getRevisionId(),
				summary, tags);
	}

	/**
	 * @deprecated Use {@link #editEntityDocument(EntityUpdate, boolean, String, List)} instead.
	 * Updates the statements of the item document identified by the given item
	 * id. The updates are computed with respect to the current data found
	 * online, making sure that no redundant deletions or duplicate insertions
	 * happen. The references of duplicate statements will be merged.
	 *
	 * @param itemIdValue
	 *            id of the document to be updated
	 * @param addStatements
	 *            the list of statements to be added or updated; statements with
	 *            empty statement id will be added; statements with non-empty
	 *            statement id will be updated (if such a statement exists)
	 * @param deleteStatements
	 *            the list of statements to be deleted; statements will only be
	 *            deleted if they are present in the current document (in
	 *            exactly the same form, with the same id)
	 * @param summary
	 *            summary for the edit; will be prepended by an automatically
	 *            generated comment; the length limit of the autocomment
	 *            together with the summary is 260 characters: everything above
	 *            that limit will be cut off
	 * @param tags
	 *            string identifiers of the tags to apply to the edit.
	 * @return the updated document
	 * @throws MediaWikiApiErrorException
	 *             if the API returns errors
	 * @throws IOException
	 *             if there are IO problems, such as missing network connection
	 */
	@Deprecated
	public ItemDocument updateStatements(ItemIdValue itemIdValue,
			List<Statement> addStatements, List<Statement> deleteStatements,
			String summary, List<String> tags)
					throws MediaWikiApiErrorException, IOException {

		ItemDocument currentDocument = (ItemDocument) this.wikibaseDataFetcher
				.getEntityDocument(itemIdValue.getId());

		return updateStatements(currentDocument, addStatements,
				deleteStatements, summary, tags);
	}
	
	
	/**
	 * @deprecated Use {@link #editEntityDocument(EntityUpdate, boolean, String, List)} instead.
	 * Updates the terms and statements of the item document identified by the
	 * given item id. The updates are computed with respect to the current data
	 * found online, making sure that no redundant deletions or duplicate insertions
	 * happen. The references of duplicate statements will be merged. The labels
	 * and aliases in a given language are kept distinct.
	 * 
	 * @param itemIdValue
	 * 			id of the document to be updated
	 * @param addLabels
	 * 			labels to be set on the item. They will overwrite existing values
	 * 			in the same language.
	 * @param addDescriptions
	 * 		    description to be set on the item. They will overwrite existing values
	 * 	 		in the same language.
	 * @param addAliases
	 * 			aliases to be added. Existing aliases will be kept.
	 * @param deleteAliases
	 * 		    aliases to be deleted.
	 * @param addStatements
	 *          the list of statements to be added or updated; statements with
	 *          empty statement id will be added; statements with non-empty
	 *          statement id will be updated (if such a statement exists)
	 * @param deleteStatements
	 *          the list of statements to be deleted; statements will only be
	 *          deleted if they are present in the current document (in
	 *          exactly the same form, with the same id)
	 * @param summary
	 *            summary for the edit; will be prepended by an automatically
	 *            generated comment; the length limit of the autocomment
	 *            together with the summary is 260 characters: everything above
	 *            that limit will be cut off
	 * @param tags
	 *            string identifiers of the tags to apply to the edit.
	 * @return the updated document
	 * @throws MediaWikiApiErrorException
	 * 			if the API returns errors
	 * @throws IOException
	 *          if there are any IO errors, such as missing network connection
	 */
	@Deprecated
	public ItemDocument updateTermsStatements(ItemIdValue itemIdValue,
			List<MonolingualTextValue> addLabels,
			List<MonolingualTextValue> addDescriptions,
			List<MonolingualTextValue> addAliases,
			List<MonolingualTextValue> deleteAliases,
			List<Statement> addStatements,
			List<Statement> deleteStatements,
			String summary,
			List<String> tags) throws MediaWikiApiErrorException, IOException {
		ItemDocument currentDocument = (ItemDocument) this.wikibaseDataFetcher
				.getEntityDocument(itemIdValue.getId());
		
		return updateTermsStatements(currentDocument, addLabels,
				addDescriptions, addAliases, deleteAliases,
				addStatements, deleteStatements, summary, tags);
	}

	/**
	 * @deprecated Use {@link #editEntityDocument(EntityUpdate, boolean, String, List)} instead.
	 * Updates the statements of the property document identified by the given
	 * property id. The computation of updates is the same as for
	 * {@link #updateStatements(ItemIdValue, List, List, String, List)}.
	 *
	 * @param propertyIdValue
	 *            id of the document to be updated
	 * @param addStatements
	 *            the list of statements to be added or updated; statements with
	 *            empty statement id will be added; statements with non-empty
	 *            statement id will be updated (if such a statement exists)
	 * @param deleteStatements
	 *            the list of statements to be deleted; statements will only be
	 *            deleted if they are present in the current document (in
	 *            exactly the same form, with the same id)
	 * @param summary
	 *            summary for the edit; will be prepended by an automatically
	 *            generated comment; the length limit of the autocomment
	 *            together with the summary is 260 characters: everything above
	 *            that limit will be cut off
	 * @param tags
	 *            string identifiers of the tags to apply to the edit.
	 * @return the updated document
	 * @throws MediaWikiApiErrorException
	 *             if the API returns errors
	 * @throws IOException
	 *             if there are IO problems, such as missing network connection
	 */
	@Deprecated
	public PropertyDocument updateStatements(PropertyIdValue propertyIdValue,
			List<Statement> addStatements, List<Statement> deleteStatements,
			String summary, List<String> tags)
					throws MediaWikiApiErrorException, IOException {

		PropertyDocument currentDocument = (PropertyDocument) this.wikibaseDataFetcher
				.getEntityDocument(propertyIdValue.getId());

		return updateStatements(currentDocument, addStatements,
				deleteStatements, summary, tags);
	}

	/**
	 * @deprecated Use {@link #editEntityDocument(EntityUpdate, boolean, String, List)} instead.
	 * Updates statements of the given document. The document should be the
	 * current revision of the data that is to be updated. The updates are
	 * computed with respect to the data found in the document, making sure that
	 * no redundant deletions or duplicate insertions happen. The references of
	 * duplicate statements will be merged.
	 * <p>
	 * The generic type T of this method must be a general interface such as
	 * {@link ItemDocument}, {@link PropertyDocument}, or
	 * {@link StatementDocument}. Specific implementations of these interfaces
	 * are not permitted.
	 *
	 * @param currentDocument
	 *            the document that is to be updated; needs to have a correct
	 *            revision id and entity id
	 * @param addStatements
	 *            the list of statements to be added or updated; statements with
	 *            empty statement id will be added; statements with non-empty
	 *            statement id will be updated (if such a statement exists)
	 * @param deleteStatements
	 *            the list of statements to be deleted; statements will only be
	 *            deleted if they are present in the current document (in
	 *            exactly the same form, with the same id)
	 * @param summary
	 *            summary for the edit; will be prepended by an automatically
	 *            generated comment; the length limit of the autocomment
	 *            together with the summary is 260 characters: everything above
	 *            that limit will be cut off
	 * @param tags
	 *            string identifiers of the tags to apply to the edit.
	 *            Ignored if null or empty.
	 * @return the updated document
	 * @throws MediaWikiApiErrorException
	 *             if the API returns errors
	 * @throws IOException
	 *             if there are IO problems, such as missing network connection
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public <T extends StatementDocument> T updateStatements(T currentDocument,
			List<Statement> addStatements, List<Statement> deleteStatements,
			String summary, List<String> tags)
					throws MediaWikiApiErrorException, IOException {

		StatementUpdate statementUpdate = new StatementUpdate(currentDocument,
				addStatements, deleteStatements);
		statementUpdate.setGuidGenerator(guidGenerator);
		
		if (statementUpdate.isEmptyEdit()) {
			return currentDocument;
		} else {
			return (T) this.wbEditingAction.wbEditEntity(currentDocument
				.getEntityId().getId(), null, null, null, statementUpdate
				.getJsonUpdateString(), false, this.editAsBot, currentDocument
				.getRevisionId(), summary, tags);
		}
	}
	
	/**
	 * @deprecated Use {@link #editEntityDocument(EntityUpdate, boolean, String, List)} instead.
	 * Updates the terms and statements of the current document.
	 * The updates are computed with respect to the current data in the document,
	 * making sure that no redundant deletions or duplicate insertions
	 * happen. The references of duplicate statements will be merged. The labels
	 * and aliases in a given language are kept distinct.
	 * 
     * @param currentDocument
	 * 			the document to be updated; needs to have a correct revision id and
	 * 			entity id
	 * @param addLabels
	 * 			labels to be set on the item. They will overwrite existing values
	 * 			in the same language.
	 * @param addDescriptions
	 * 		    description to be set on the item. They will overwrite existing values
	 * 	 		in the same language.
	 * @param addAliases
	 * 			aliases to be added. Existing aliases will be kept.
	 * @param deleteAliases
	 * 		    aliases to be deleted.
	 * @param addStatements
	 *          the list of statements to be added or updated; statements with
	 *          empty statement id will be added; statements with non-empty
	 *          statement id will be updated (if such a statement exists)
	 * @param deleteStatements
	 *          the list of statements to be deleted; statements will only be
	 *          deleted if they are present in the current document (in
	 *          exactly the same form, with the same id)
	 * @param summary
	 *            summary for the edit; will be prepended by an automatically
	 *            generated comment; the length limit of the autocomment
	 *            together with the summary is 260 characters: everything above
	 *            that limit will be cut off
	 * @param tags
	 *            string identifiers of the tags to apply to the edit.
	 *            Ignored if null or empty.
	 * @return the updated document
	 * @throws MediaWikiApiErrorException
	 * 			if the API returns errors
	 * @throws IOException
	 *          if there are any IO errors, such as missing network connection
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public <T extends TermedStatementDocument> T updateTermsStatements(T currentDocument,
			List<MonolingualTextValue> addLabels,
			List<MonolingualTextValue> addDescriptions,
			List<MonolingualTextValue> addAliases,
			List<MonolingualTextValue> deleteAliases,
			List<Statement> addStatements, List<Statement> deleteStatements,
			String summary, List<String> tags)
					throws MediaWikiApiErrorException, IOException {
		
		TermStatementUpdate termStatementUpdate = new TermStatementUpdate(
				currentDocument,
				addStatements, deleteStatements,
				addLabels, addDescriptions, addAliases, deleteAliases);
		termStatementUpdate.setGuidGenerator(guidGenerator);
		
		return  (T) termStatementUpdate.performEdit(wbEditingAction, editAsBot, summary, tags);
	}
	
	/**
	 * Performs a null edit on an entity. This has some effects on Wikibase, such as
	 * refreshing the labels of the referred items in the UI.
	 * 
	 * @param entityId
	 *            the document to perform a null edit on
	 * @throws MediaWikiApiErrorException
	 *             if the API returns errors
	 * @throws IOException
	 *             if there are any IO errors, such as missing network connection
	 */
	public void nullEdit(EntityIdValue entityId) throws IOException, MediaWikiApiErrorException {
		nullEdit(wikibaseDataFetcher.getEntityDocument(entityId.getId()));
	}

	/**
	 * @deprecated Use {@link #nullEdit(EntityIdValue)} instead.
	 * Performs a null edit on an item. This has some effects on Wikibase,
	 * such as refreshing the labels of the referred items in the UI.
	 * 
	 * @param itemId
	 * 			the document to perform a null edit on
	 * @throws MediaWikiApiErrorException
	 * 	        if the API returns errors
	 * @throws IOException 
	 * 		    if there are any IO errors, such as missing network connection
	 */
	@Deprecated
	public <T extends StatementDocument> void nullEdit(ItemIdValue itemId)
			throws IOException, MediaWikiApiErrorException {
		ItemDocument currentDocument = (ItemDocument) this.wikibaseDataFetcher
				.getEntityDocument(itemId.getId());
		
		nullEdit(currentDocument);
	}
	
	/**
	 * @deprecated Use {@link #nullEdit(EntityIdValue)} instead.
	 * Performs a null edit on a property. This has some effects on Wikibase,
	 * such as refreshing the labels of the referred items in the UI.
	 * 
	 * @param propertyId
	 * 			the document to perform a null edit on
	 * @throws MediaWikiApiErrorException
	 * 	        if the API returns errors
	 * @throws IOException 
	 * 		    if there are any IO errors, such as missing network connection
	 */
	@Deprecated
	public <T extends StatementDocument> void nullEdit(PropertyIdValue propertyId)
			throws IOException, MediaWikiApiErrorException {
		PropertyDocument currentDocument = (PropertyDocument) this.wikibaseDataFetcher
				.getEntityDocument(propertyId.getId());
		
		nullEdit(currentDocument);
	}
	
	/**
	 * Performs a null edit on an entity. This has some effects on Wikibase, such as
	 * refreshing the labels of the referred items in the UI.
	 * 
	 * @param currentDocument
	 *            the document to perform a null edit on
	 * @return new version of the document returned by Wikibase API
	 * @throws MediaWikiApiErrorException
	 *             if the API returns errors
	 * @throws IOException
	 *             if there are any IO errors, such as missing network connection
	 */
	@SuppressWarnings("unchecked")
	public <T extends EntityDocument> T nullEdit(T currentDocument)
			throws IOException, MediaWikiApiErrorException {
		EntityUpdate update = EntityUpdateBuilder.forBaseRevision(currentDocument).build();
		return (T) wbEditingAction.wbEditEntity(currentDocument.getEntityId().getId(), null, null, null,
				JsonSerializer.getJsonString(update), false, editAsBot, currentDocument.getRevisionId(), null, null);
	}

    /**
     * Extracts the last revision id from the JSON response returned
     * by the API after an edit
     * 
     * @param response
     *      the response as returned by Mediawiki
     * @return
     *      the new revision id of the edited entity
     * @throws JsonProcessingException 
     */
    protected long getRevisionIdFromResponse(JsonNode response) throws JsonProcessingException {
        if(response == null) {
            throw new MalformedResponseException("API response is null");
        }
        JsonNode entity = null;
        if(response.has("entity")) {
            entity = response.path("entity");
        } else if(response.has("pageinfo")) {
            entity = response.path("pageinfo");
        } 
        if(entity != null && entity.has("lastrevid")) {
            return entity.path("lastrevid").asLong();
        }
        throw new MalformedResponseException("The last revision id could not be found in API response");
    }
}
