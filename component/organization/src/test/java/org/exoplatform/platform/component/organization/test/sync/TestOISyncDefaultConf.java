package org.exoplatform.platform.component.organization.test.sync;

import exo.portal.component.identiy.opendsconfig.opends.OpenDSService;
import org.exoplatform.component.test.ConfigurationUnit;
import org.exoplatform.component.test.ConfiguredBy;
import org.exoplatform.component.test.ContainerScope;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.platform.organization.integration.EventType;
import org.exoplatform.platform.organization.integration.OrganizationIntegrationService;
import org.exoplatform.platform.organization.integration.Util;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserHandler;
import org.exoplatform.test.BasicTestCase;

import javax.jcr.Session;
import java.util.List;

/**
 * Created by Abdessattar Noissi on 13/11/17.
 */
@ConfiguredBy({
        @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/portal/test-configuration.xml")})
public class TestOISyncDefaultConf extends BasicTestCase {

    private static Log log = ExoLogger.getLogger(TestOISyncDefaultConf.class.getName());

    private OpenDSService openDSService = new OpenDSService(null);
    private PortalContainer container;
    private OrganizationService organization;

    private OrganizationIntegrationService organizationIntegrationService;
    private UserHandler uHandler;

    private Session session;
    private List<String> activatedUsers;
    private final String USER_CONTAINER = "ou=People,o=test,dc=portal,dc=example,dc=com";


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        try {
            openDSService.start();
            openDSService.initLDAPServer();

        } catch (Exception e) {
            log.error("Error in starting up OPENDS", e);
        }
        container = PortalContainer.getInstance();
        organization = (OrganizationService) container.getComponentInstanceOfType(OrganizationService.class);
        uHandler = organization.getUserHandler();
        organizationIntegrationService = (OrganizationIntegrationService) container.getComponentInstanceOfType(OrganizationIntegrationService.class);
        session = organizationIntegrationService.getRepositoryService().getCurrentRepository().getSystemSession(Util.WORKSPACE);
        organizationIntegrationService.syncAllUsers(EventType.ADDED.name());
    }

    @Override
    protected void tearDown() throws Exception {
        deleteUser("anoissi");
        deleteUser("admin");
        deleteUser("testAdd");
        if (session != null) {
            session.logout();
        }
        openDSService.cleanUpDN("dc=portal,dc=example,dc=com");
        openDSService.stop();

        super.tearDown();
    }



    public void testAdded() throws Exception {
        activatedUsers = Util.getActivatedUsers(session);
        assertEquals(11, activatedUsers.size());
        openDSService.addUserAccount("testAdd",USER_CONTAINER);
        organizationIntegrationService.syncAllUsers(EventType.ADDED.name());
        activatedUsers = Util.getActivatedUsers(session);
        assertEquals(12, activatedUsers.size());
    }

    public void testDeleted() throws Exception {
        activatedUsers = Util.getActivatedUsers(session);
        assertEquals(11, activatedUsers.size());
        deleteUserFromOpenDs("User");
        organizationIntegrationService.syncAllUsers(EventType.DELETED.name());
        activatedUsers  = Util.getActivatedUsers(session);
        assertEquals(10, activatedUsers.size());
    }

    public void testUpdated() throws Exception{
        User jduke = uHandler.findUserByName("jduke1");
        assertNotNull(jduke);
        assertEquals(jduke.getLastName(),"Duke1");
        deleteUserFromOpenDs("jduke1");
        openDSService.addUserAccount("jduke1",USER_CONTAINER);
        organizationIntegrationService.syncAllUsers(EventType.UPDATED.name());
        jduke = uHandler.findUserByName("jduke1");
        assertNotNull(jduke);
        assertEquals("surnameOfjduke1",jduke.getLastName());
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
