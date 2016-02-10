package org.wikidata.wdtk.examples.bots;

/*
 * #%L
 * Wikidata Toolkit Examples
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

/**
 * Simple class to store bot configurations to use for all bots.
 * <p>
 * NEVER COMMIT YOUR PERSONAL CREDENTIALS TO GIT.
 *
 * @author Markus Kroetzsch
 *
 */
public class BotSettings {

	/**
	 * User name to log in, or null to edit anonymously.
	 */
	static final String USERNAME = null;
	/**
	 * Password for the given user.
	 */
	static final String PASSWORD = null;
	/**
	 * True if the user should set a bot flag (if logged in and endowed with the
	 * required rights).
	 */
	static final boolean EDIT_AS_BOT = true;

}
