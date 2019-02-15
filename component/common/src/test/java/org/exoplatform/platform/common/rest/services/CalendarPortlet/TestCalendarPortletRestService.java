package org.exoplatform.platform.common.rest.services.CalendarPortlet;

import org.exoplatform.platform.common.rest.services.BaseRestServicesTestCase;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.test.mock.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;

public class TestCalendarPortletRestService extends BaseRestServicesTestCase {

    protected Class<?> getComponentClass() {
        return CalendarPortletRestService.class;
    }

    public void testInit() throws Exception {
        String path = "/portlet/calendar/init";
        EnvironmentContext envctx = new EnvironmentContext();
        HttpServletRequest httpRequest =
                new MockHttpServletRequest(path, null, 0, "GET", null);

        envctx.put(HttpServletRequest.class, httpRequest);

        Identity identity = new Identity("root");
        ConversationState.setCurrent(new ConversationState(identity));
        ContainerResponse resp =
                launcher.service("GET", path, "", null, null, envctx);
        assertEquals(500, resp.getStatus());
    }
}
