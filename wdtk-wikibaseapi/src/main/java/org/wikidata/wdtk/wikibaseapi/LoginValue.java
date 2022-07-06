package org.wikidata.wdtk.wikibaseapi;

import java.util.Arrays;
import java.util.Optional;

public enum LoginValue {
    /**
     * Name of the HTTP parameter to submit a password to the API.
     */
    PARAM_LOGIN_USERNAME("lgname", "username", ""),
    /**
     * Name of the HTTP parameter to submit a password to the API.
     */
    PARAM_LOGIN_PASSWORD("lgpassword", "password", ""),
    /**
     * Name of the HTTP parameter to submit a login token to the API.
     */
    PARAM_LOGIN_TOKEN("lgtoken", "logintoken", ""),

    /**
     * String value in the result field of the JSON response if the login was
     * successful.
     */
    LOGIN_RESULT_SUCCESS("Success", "PASS", ""),
    /**
     * String value in the result field of the JSON response if the password was
     * wrong.
     */
    LOGIN_WRONG_PASS("WrongPass", "wrongpassword", "Wrong Password."),
    /**
     * String value in the result field of the JSON response if the password was
     * rejected by an authentication plugin.
     */
    LOGIN_WRONG_PLUGIN_PASS("WrongPluginPass", "wrongpluginpass", "Wrong Password. An authentication plugin rejected the password."), // not sure about this one
    /**
     * String value in the result field of the JSON response if no username was
     * given.
     */
    LOGIN_NO_NAME("NoName", "authmanager-authn-no-primary", "No user name given."),
    /**
     * String value in the result field of the JSON response if given username
     * does not exist.
     */
    LOGIN_NOT_EXISTS("NotExists", "wrongpassword", "Username does not exist."), // no distinction for clientLogin
    /**
     * String value in the result field of the JSON response if the username is
     * illegal.
     */
    LOGIN_ILLEGAL("Illegal", "wrongpassword", "Username is illegal."), // no distinction for clientLogin
    /**
     * String value in the result field of the JSON response if there were too
     * many logins in a short time.
     */
    LOGIN_THROTTLED("Throttled", "throttled", "Too many login attempts in a short time."), // not sure about this one
    /**
     * String value in the result field of the JSON response if password is
     * empty.
     */
    LOGIN_EMPTY_PASS("EmptyPass", "authmanager-authn-no-primary", "Password is empty."),
    /**
     * String value in the result field of the JSON response if the wiki tried
     * to automatically create a new account for you, but your IP address has
     * been blocked from account creation.
     */
    LOGIN_CREATE_BLOCKED("CreateBlocked", "createblocked", "The wiki tried to automatically create a new account for you, "
            + "but your IP address has been blocked from account creation."), // not sure about this one
    /**
     * String value in the result field of the JSON response if the user is
     * blocked.
     */
    LOGIN_BLOCKED("Blocked", "blocked", "User is blocked."), // not sure about this one
    /**
     * String value in the result field of the JSON response if token or session
     * ID is missing.
     */
    LOGIN_NEEDTOKEN("NeedToken", "missingparam", "Token or session ID is missing."),
    /**
     * String value in the result field of the JSON response if token is wrong.
     */
    LOGIN_WRONG_TOKEN("WrongToken", "badtoken", "Token is wrong."),
    /**
     * Value for unknown response text
     */
    UNKNOWN("unknown", "unknown", "Error text not recognized");
    
    private final String loginText;
    private final String clientLoginText;
    private final String message;
    
    LoginValue(String loginText, String clientLoginText, String message) {
        this.loginText = loginText;
        this.clientLoginText = clientLoginText;
        this.message = message;
    }
    
    public static LoginValue of(String text) {
        Optional<LoginValue> optionalLoginValue = Arrays.stream(LoginValue.values())
                .filter(loginValue -> text != null && (text.equals(loginValue.loginText) || text.equals(loginValue.clientLoginText))).findFirst();
        return optionalLoginValue.orElse(UNKNOWN);
    }

    public String getLoginText() {
        return loginText;
    }

    public String getClientLoginText() {
        return clientLoginText;
    }
    
    public String getMessage(String loginType) {
        return loginType + ": " + message;
    }
    
}
