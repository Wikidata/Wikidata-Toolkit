package org.wikidata.wdtk.wikibaseapi;

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