package org.exoplatform.platform.component.organization.test;

import exo.portal.component.identiy.opendsconfig.opends.OpenDSService;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.platform.organization.injector.DataInjectorService;
import org.exoplatform.platform.organization.injector.JMXDataInjector;
import org.exoplatform.services.organization.*;
import org.exoplatform.test.BasicTestCase;
import org.picocontainer.Startable;

import java.io.*;

public class TestOrganizationInjector extends BasicTestCase {
  PortalContainer container = null;
  OrganizationService organizationService = null;
  JMXDataInjector dataInjector = null;
  DataInjectorService dataInjectorService = null;
  private OpenDSService OpenDSService = new OpenDSService(null);
  private MembershipHandler membershipHandler;


  @Override
  protected void setUp() throws Exception {
    OpenDSService.start();
    OpenDSService.initLDAPServer();
    container = PortalContainer.getInstance();
    organizationService = (OrganizationService) container.getComponentInstanceOfType(OrganizationService.class);
    membershipHandler = organizationService.getMembershipHandler();
    dataInjector = (JMXDataInjector) container.getComponentInstanceOfType(JMXDataInjector.class);
    dataInjectorService = (DataInjectorService) container.getComponentInstanceOfType(DataInjectorService.class);

    ((Startable) organizationService).start();
  }

  @Override
  protected void tearDown() throws Exception {
    deleteUser("jduke2");
    deleteUser("jduke2");
    OpenDSService.cleanUpDN("dc=portal,dc=example,dc=com");
    OpenDSService.stop();
  }
  public void testDataInjectorService() throws Exception {
    User user1;
    User user2;
    MembershipType membershipType;
    Membership membership;
    Group group ;

    user1 = getUser("jduke1");
    user2 = getUser("jduke2");
    membershipType = getMembershipType("dataMT");
    group = getGroup("/dataGroup");

    membershipHandler.linkMembership(user1,group,membershipType,true);
    membershipHandler.linkMembership(user2,group,membershipType,true);
    File file = new File("target/test.zip");
    dataInjector.extractData(file.getAbsolutePath());
    deleteMembershipType("dataMT");
    deleteGroup("dataGroup");
    deleteUser("jduke1");
    deleteUser("jduke2");
    dataInjector.injectData(file.getAbsolutePath());

    user1 = getUser("jduke1");
    assertNotNull(user1);
    user2 = getUser("jduke2");
    assertNotNull(user2);

    group = getGroup("/dataGroup");
    assertNotNull(group);

    membershipType = getMembershipType("dataMT");
    assertNotNull(membershipType);

    membership = getMembership("dataMT", "jduke1", "/dataGroup");
    assertNotNull(membership);

    membership = getMembership("dataMT", "jduke2", "/dataGroup");
    assertNotNull(membership);
  }

  private Membership getMembership(String membershipTypeId, String userName, String groupId) throws Exception {
    RequestLifeCycle.begin(PortalContainer.getInstance());
    try {
      return organizationService.getMembershipHandler().findMembershipByUserGroupAndType(userName, groupId, membershipTypeId);
    } finally {
      RequestLifeCycle.end();
    }
  }

  private MembershipType getMembershipType(String membershipTypeId) throws Exception {
    RequestLifeCycle.begin(PortalContainer.getInstance());
    try {
      return organizationService.getMembershipTypeHandler().findMembershipType(membershipTypeId);
    } finally {
      RequestLifeCycle.end();
    }
  }

  private Group getGroup(String groupId) throws Exception {
    RequestLifeCycle.begin(PortalContainer.getInstance());
    try {
      return organizationService.getGroupHandler().findGroupById(groupId);
    } finally {
      RequestLifeCycle.end();
    }
  }

  private User getUser(String userId) throws Exception {
    RequestLifeCycle.begin(PortalContainer.getInstance());
    try {
      return organizationService.getUserHandler().findUserByName(userId);
    } finally {
      RequestLifeCycle.end();
    }
  }

  private void deleteMembershipType(String membershipTypeId) throws Exception {
    RequestLifeCycle.begin(PortalContainer.getInstance());
    try {
      organizationService.getMembershipTypeHandler().removeMembershipType(membershipTypeId, false);
    } finally {
      RequestLifeCycle.end();
    }
  }

  private void deleteUser(String username) throws Exception {
    RequestLifeCycle.begin(PortalContainer.getInstance());
    try {
      organizationService.getUserHandler().removeUser(username, false);
    } finally {
      RequestLifeCycle.end();
    }
  }

  private void deleteGroup(String groupId) throws Exception {
    RequestLifeCycle.begin(PortalContainer.getInstance());
    try {
      organizationService.getGroupHandler().removeGroup(organizationService.getGroupHandler().findGroupById(groupId), false);
    } finally {
      RequestLifeCycle.end();
    }
  }

}
