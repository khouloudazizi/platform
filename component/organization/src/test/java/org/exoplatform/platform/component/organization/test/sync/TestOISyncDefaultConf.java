package org.exoplatform.platform.component.organization.test.sync;

import exo.portal.component.identiy.opendsconfig.opends.PlfOpenDSService;
import org.exoplatform.component.test.AbstractKernelTest;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.platform.organization.integration.EventType;
import org.exoplatform.platform.organization.integration.OrganizationIntegrationService;
import org.exoplatform.platform.organization.integration.Util;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserHandler;
import org.exoplatform.services.organization.idm.PicketLinkIDMService;
import org.gatein.portal.idm.impl.repository.ExoFallbackIdentityStoreRepository;

import javax.jcr.Session;
import java.util.List;

/**
 * Created by Abdessattar Noissi on 13/11/17.
 */

public class TestOISyncDefaultConf extends AbstractKernelTest {

    private static Log log = ExoLogger.getLogger(TestOISyncDefaultConf.class.getName());

    private PlfOpenDSService openDSService = new PlfOpenDSService(null);
    private PortalContainer container;
    private OrganizationService organization;
    private PicketLinkIDMService picketLinkIDMService;
    private ExoFallbackIdentityStoreRepository exoFallbackISRepository;

    private OrganizationIntegrationService organizationIntegrationService;
    private UserHandler uHandler;

    private Session session;
    private List<String> activatedUsers;
    private final String USER_CONTAINER = "ou=People,o=test,dc=portal,dc=example,dc=com";

    @Override
    protected void beforeRunBare() {
        try {
            openDSService.start();
            openDSService.initLDAPServer();

        } catch (Exception e) {
            log.error("Error in starting up OPENDS", e);
        }
        super.beforeRunBare();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        begin();
        container = getContainer();
        organization = (OrganizationService) container.getComponentInstanceOfType(OrganizationService.class);
        uHandler = organization.getUserHandler();
        organizationIntegrationService = (OrganizationIntegrationService) container.getComponentInstanceOfType(OrganizationIntegrationService.class);
        organizationIntegrationService.syncAllUsers(EventType.ADDED.name());
        session = organizationIntegrationService.getRepositoryService().getCurrentRepository().getSystemSession(Util.WORKSPACE);
    }

    @Override
    protected void tearDown() throws Exception {
        deleteUser("anoissi");
        deleteUser("admin");
        deleteUser("testAdd");
        openDSService.cleanUpDN("dc=example,dc=com");
        organizationIntegrationService.stop();

        if (session != null) {
            session.logout();
        }
        end();
        super.tearDown();
    }

    @Override
    protected void afterRunBare() {
        try {
            openDSService.cleanUpDN("dc=example,dc=com");
            openDSService.stop();

        } catch (Exception e) {
            log.error("Error in stopping OPENDS", e);
        }
        super.afterRunBare();
    }



    public void testAdded() throws Exception {

        activatedUsers = Util.getActivatedUsers(session);
        assertEquals(4, activatedUsers.size());
        openDSService.addUserAccount("testAdd",USER_CONTAINER);
        organizationIntegrationService.syncAllUsers(EventType.ADDED.name());
        activatedUsers = Util.getActivatedUsers(session);
        assertEquals(5, activatedUsers.size());
    }

    public void testDeleted() throws Exception {
        activatedUsers = Util.getActivatedUsers(session);
        assertEquals(4, activatedUsers.size());
        deleteUserFromOpenDs("anoissi");
        organizationIntegrationService.syncAllUsers(EventType.DELETED.name());
        activatedUsers  = Util.getActivatedUsers(session);
        assertEquals(3, activatedUsers.size());
    }

    public void testUpdated() throws Exception{
        User anoissi = uHandler.findUserByName("anoissi");
        assertNotNull(anoissi);
        assertEquals(anoissi.getLastName(),"Noissi");
        deleteUserFromOpenDs("anoissi");
        openDSService.addUserAccount("anoissi",USER_CONTAINER);
        organizationIntegrationService.syncAllUsers(EventType.UPDATED.name());
        anoissi = uHandler.findUserByName("anoissi");
        assertNotNull(anoissi);
        assertEquals("surnameOfanoissi",anoissi.getLastName());
    }

    private  void deleteUser(String username) {
        try {
            uHandler.removeUser(username, true);
        } catch (Exception e) {

        }
    }
    private void deleteUserFromOpenDs(String username) throws Exception{
        openDSService.cleanUpDN("uid="+username+","+USER_CONTAINER);
    }
}
