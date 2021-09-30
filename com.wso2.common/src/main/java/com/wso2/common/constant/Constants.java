package com.wso2.common.constant;

/**
 * This class contains the constants that are required for the WSO2 modules.
 */
public class Constants {

    public static final String POST_UPDATE_ROLE_LIST_OF_USER = "POST_UPDATE_ROLE_LIST_OF_USER";
    public static final String CURRENT_SESSION_IDENTIFIER = "currentSessionIdentifier";
    public static final String PRESERVE_LOGGED_IN_SESSION_AT_PASSWORD_UPDATE =
            "PasswordUpdate.PreserveLoggedInSession";

    /**
     * Define the error messages that are relevant for WSO2 components.
     */
    public enum ErrorMessages {

        ERROR_USERNAME_NOT_FOUND(
                "60001",
                "The provided username/s could not be found in the request."
        ),
        ERROR_EMPTY_USER_STORE_MANAGER(
                "60002",
                "The user store manager does not exist for the given set of users."
        ),
        ERROR_USER_SESSION_TERMINATION_FAILED(
                "60003",
                "An error occurred while terminating the user sessions."
        );

        private final String code;
        private final String message;

        ErrorMessages(String code, String message) {

            this.code = code;
            this.message = message;
        }

        public String getCode() {

            return code;
        }

        public String getMessage() {

            return message;
        }
    }
}
