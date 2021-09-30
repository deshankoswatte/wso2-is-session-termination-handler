package com.wso2.session.termination.handler.internal;

import com.wso2.session.termination.handler.SessionTerminationHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.wso2.carbon.identity.application.authentication.framework.UserSessionManagementService;
import org.wso2.carbon.identity.event.handler.AbstractEventHandler;
import org.wso2.carbon.user.core.service.RealmService;

/**
 * OSGi bundle for the WSO2 session termination handler module.
 */
@Component(
        name = "com.wso2.session.termination.handler.internal.WSO2SessionTerminationMgtComponent",
        immediate = true
)
public class WSO2SessionTerminationMgtComponent {

    private static final Log log = LogFactory.getLog(WSO2SessionTerminationMgtComponent.class);

    @Activate
    protected void activate(ComponentContext context) {

        try {
            if (log.isDebugEnabled()) {
                log.debug("The WSO2 session termination handler component is enabled.");
            }
            BundleContext bundleContext = context.getBundleContext();
            bundleContext.registerService(AbstractEventHandler.class.getName(),
                    new SessionTerminationHandler(), null);
        } catch (Throwable throwable) {
            log.error("Error while activating the WSO2 session termination handler component.", throwable);
        }
    }

    @Deactivate
    protected void deactivate(ComponentContext context) {

        if (log.isDebugEnabled()) {
            log.debug("The WSO2 session termination handler component is de-activated.");
        }
    }

    @Reference(
            name = "realm.service",
            service = RealmService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetRealmService")
    protected void setRealmService(RealmService realmService) {

        WSO2SessionTerminationMgtDataHolder.getInstance().setRealmService(realmService);
        if (log.isDebugEnabled()) {
            log.debug("Setting the Realm Service.");
        }
    }

    protected void unsetRealmService(RealmService realmService) {

        WSO2SessionTerminationMgtDataHolder.getInstance().setRealmService(null);
        if (log.isDebugEnabled()) {
            log.debug("Unset the Realm Service.");
        }
    }

    @Reference(
            name = "org.wso2.carbon.identity.application.authentication.framework.UserSessionManagementService",
            service = UserSessionManagementService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetUserSessionManagementService"
    )
    protected void setUserSessionManagementService(UserSessionManagementService userSessionManagementService) {

        if (log.isDebugEnabled()) {
            log.debug("Setting the User Session Management Service.");
        }
        WSO2SessionTerminationMgtDataHolder.getInstance()
                .setUserSessionManagementService(userSessionManagementService);
    }

    protected void unsetUserSessionManagementService(UserSessionManagementService userSessionManagementService) {

        if (log.isDebugEnabled()) {
            log.debug("Unsetting the User Session Management Service.");
        }
        WSO2SessionTerminationMgtDataHolder.getInstance().setUserSessionManagementService(null);
    }
}
