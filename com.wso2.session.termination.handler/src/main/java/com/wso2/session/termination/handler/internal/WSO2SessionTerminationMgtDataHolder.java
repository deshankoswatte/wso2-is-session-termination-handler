package com.wso2.session.termination.handler.internal;

import org.wso2.carbon.identity.application.authentication.framework.UserSessionManagementService;
import org.wso2.carbon.user.core.service.RealmService;

/**
 * This class contains the services that are needed for the WSO2 session termination module.
 */
public class WSO2SessionTerminationMgtDataHolder {

    private static final WSO2SessionTerminationMgtDataHolder instance =
            new WSO2SessionTerminationMgtDataHolder();
    private RealmService realmService = null;
    private UserSessionManagementService userSessionManagementService = null;

    private WSO2SessionTerminationMgtDataHolder() {

    }

    public static WSO2SessionTerminationMgtDataHolder getInstance() {

        return instance;
    }

    public RealmService getRealmService() {

        return realmService;
    }

    public void setRealmService(RealmService realmservice) {

        this.realmService = realmservice;
    }

    public UserSessionManagementService getUserSessionManagementService() {

        return userSessionManagementService;
    }

    public void setUserSessionManagementService(UserSessionManagementService userSessionManagementService) {

        this.userSessionManagementService = userSessionManagementService;
    }
}
