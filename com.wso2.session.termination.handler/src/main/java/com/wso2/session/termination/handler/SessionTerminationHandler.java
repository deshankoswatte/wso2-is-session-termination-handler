package com.wso2.session.termination.handler;

import com.wso2.common.constant.Constants;
import com.wso2.common.exception.WSO2Exception;
import com.wso2.session.termination.handler.internal.WSO2SessionTerminationMgtDataHolder;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.base.MultitenantConstants;
import org.wso2.carbon.identity.application.authentication.framework.UserSessionManagementService;
import org.wso2.carbon.identity.application.authentication.framework.exception.UserSessionException;
import org.wso2.carbon.identity.core.bean.context.MessageContext;
import org.wso2.carbon.identity.event.IdentityEventConstants;
import org.wso2.carbon.identity.event.IdentityEventException;
import org.wso2.carbon.identity.event.event.Event;
import org.wso2.carbon.identity.event.handler.AbstractEventHandler;
import org.wso2.carbon.user.core.UserCoreConstants;
import org.wso2.carbon.user.core.UserStoreManager;
import org.wso2.carbon.user.core.util.UserCoreUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Handler which processes the session termination events.
 */
public class SessionTerminationHandler extends AbstractEventHandler {

    private static final Log log = LogFactory.getLog(SessionTerminationHandler.class);
    private final UserSessionManagementService userSessionManagementService = WSO2SessionTerminationMgtDataHolder.
            getInstance().getUserSessionManagementService();

    /**
     * Handles the session termination event which is captured by this handler.
     *
     * @param event The session termination event which has been captured by the handler.
     * @throws IdentityEventException If there is an error while handling the captured session termination event.
     */
    @Override
    public void handleEvent(Event event) throws IdentityEventException {

        // Fetching event properties.
        Map<String, Object> eventProperties = event.getEventProperties();

        try {
            // Retrieve the required attributes of the user.
            List<String> usernamesList = extractUsernames(event, eventProperties);
            UserStoreManager userStoreManager = extractUserStoreManager(eventProperties, usernamesList);

            String tenantDomain = (String) eventProperties.get(IdentityEventConstants.EventProperty.TENANT_DOMAIN);
            if (StringUtils.isBlank(tenantDomain)) {
                tenantDomain = MultitenantConstants.SUPER_TENANT_DOMAIN_NAME;
            }

            String userStoreDomain = UserCoreUtil.getDomainName(userStoreManager.getRealmConfiguration());
            if (StringUtils.isBlank(userStoreDomain)) {
                userStoreDomain = UserCoreConstants.PRIMARY_DEFAULT_DOMAIN_NAME;
            }

            // Terminate the sessions of each user.
            for (String username : usernamesList) {
                userSessionManagementService.terminateSessionsOfUser(username, userStoreDomain, tenantDomain);
            }

        } catch (WSO2Exception | UserSessionException exception) {
            throw new IdentityEventException(
                    Constants.ErrorMessages.ERROR_USER_SESSION_TERMINATION_FAILED.getCode(),
                    Constants.ErrorMessages.ERROR_USER_SESSION_TERMINATION_FAILED.getMessage(),
                    exception
            );
        }
    }

    /**
     * Extracts and validates the usernames depending on the event type and the event properties.
     *
     * @param event           The event which is handled by the SessionTerminationHandler.
     * @param eventProperties The properties that align with the received event.
     * @return A list of extracted and validated usernames for whom which the active sessions should be terminated.
     * @throws WSO2Exception If the username is not found within the event properties.
     */
    private List<String> extractUsernames(Event event, Map<String, Object> eventProperties) throws WSO2Exception {

        List<String> usernamesList = new ArrayList<>();

        if (event.getEventName().equals(Constants.POST_UPDATE_ROLE_LIST_OF_USER)) {
            String username = eventProperties.get(IdentityEventConstants.EventProperty.USER_NAME) == null ?
                    null : (String) eventProperties.get(IdentityEventConstants.EventProperty.USER_NAME);
            if (StringUtils.isBlank(username)) {
                if (log.isDebugEnabled()) {
                    log.debug("The username of the updated user could not be found.");
                }
                throw new WSO2Exception(
                        Constants.ErrorMessages.ERROR_USERNAME_NOT_FOUND.getCode(),
                        Constants.ErrorMessages.ERROR_USERNAME_NOT_FOUND.getMessage()
                );
            }
            usernamesList.add(username);
        } else {
            // Considers the event as "POST_UPDATE_USER_LIST_OF_ROLE".
            String[] newUsernames = eventProperties.get(IdentityEventConstants.EventProperty.NEW_USERS) == null ?
                    new String[0] : (String[]) eventProperties.get(IdentityEventConstants.EventProperty.NEW_USERS
            );
            String[] deletedUsernames = eventProperties.get(IdentityEventConstants.EventProperty.DELETED_USERS) == null
                    ? new String[0] : (String[]) eventProperties.get(IdentityEventConstants.EventProperty.DELETED_USERS
            );
            if (ArrayUtils.isEmpty(newUsernames) && ArrayUtils.isEmpty(deletedUsernames)) {
                if (log.isDebugEnabled()) {
                    log.debug("The usernames of the users belonging to the specific role could not be found.");
                }
                throw new WSO2Exception(
                        Constants.ErrorMessages.ERROR_USERNAME_NOT_FOUND.getCode(),
                        Constants.ErrorMessages.ERROR_USERNAME_NOT_FOUND.getMessage()
                );
            }
            Collections.addAll(usernamesList, (String[]) ArrayUtils.addAll(deletedUsernames, newUsernames));
        }

        return usernamesList;
    }

    /**
     * Extracts and validates the user store manager based on the event properties received.
     *
     * @param eventProperties The properties that align with the event received by the SesionTerminationHandler.
     * @param usernamesList   A list of extracted usernames for whom which the user store manager aligns with.
     * @return The extracted and validated user store manager.
     * @throws WSO2Exception If there is no user store manager within the event properties that matches with the list of
     *                      users.
     */
    private UserStoreManager extractUserStoreManager(Map<String, Object> eventProperties, List<String> usernamesList)
            throws WSO2Exception {

        UserStoreManager userStoreManager = eventProperties.get(
                IdentityEventConstants.EventProperty.USER_STORE_MANAGER) == null ?
                null : (UserStoreManager) eventProperties.get(IdentityEventConstants.EventProperty.USER_STORE_MANAGER);

        if (userStoreManager == null) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("The user store manager does not exist for the users: %s.", usernamesList));
            }
            throw new WSO2Exception(
                    Constants.ErrorMessages.ERROR_EMPTY_USER_STORE_MANAGER.getCode(),
                    Constants.ErrorMessages.ERROR_EMPTY_USER_STORE_MANAGER.getMessage()
            );
        }

        return userStoreManager;
    }

    /**
     * Retrieves the name of the handler.
     *
     * @return Name of the handler.
     */
    @Override
    public String getName() {

        return "sessionTermination";
    }

    /**
     * Returns the priority level of the handler.
     *
     * @param messageContext The message context.
     * @return Priority level of the handler.
     */
    @Override
    public int getPriority(MessageContext messageContext) {

        return 50;
    }
}
