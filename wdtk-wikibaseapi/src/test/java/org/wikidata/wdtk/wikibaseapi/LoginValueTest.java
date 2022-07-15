package org.wikidata.wdtk.wikibaseapi;

/*-
 * #%L
 * Wikidata Toolkit Wikibase API
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class LoginValueTest {

    @Test
    public void testOf() {
        LoginValue needToken = LoginValue.of("NeedToken");
        assertEquals(LoginValue.LOGIN_NEEDTOKEN, needToken);
    }

    @Test
    public void testOfReturnsTopMatch() {
        LoginValue wrongpassword = LoginValue.of("wrongpassword");
        assertEquals(LoginValue.LOGIN_WRONG_PASS, wrongpassword);
        assertNotEquals(LoginValue.LOGIN_NOT_EXISTS, wrongpassword);
    }

    @Test
    public void testOfReturnsUnknownIfTextUnknown() {
        LoginValue unknown = LoginValue.of("dunno");
        assertEquals(LoginValue.UNKNOWN, unknown);
    }

    @Test
    public void testOfReturnsUnknownIfTextNull() {
        LoginValue unknown = LoginValue.of(null);
        assertEquals(LoginValue.UNKNOWN, unknown);
    }

    @Test
    public void testGetMessage() {
        assertEquals("NeedToken: Token or session ID is missing.", LoginValue.LOGIN_NEEDTOKEN.getMessage("NeedToken"));
    }
}
