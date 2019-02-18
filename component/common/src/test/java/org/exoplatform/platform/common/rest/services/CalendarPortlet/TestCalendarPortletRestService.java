package org.exoplatform.platform.common.rest.services.CalendarPortlet;

import org.exoplatform.platform.common.rest.services.BaseRestServicesTestCase;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.test.mock.MockHttpServletRequest;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
        Map<String, Object> spacesMap = new HashMap<String, Object>();
        Space space1 = new Space();
        space1.setPrettyName("space1");
        space1.setId("space1");
        space1.setGroupId("/spaces/space1");
        space1.setVisibility(Space.HIDDEN);
        space1.setMembers(new String[] { "root" });
        spacesMap.put("getSpaceById", new MockListAccess<Space>(new Space[] { space1 }));
        SpaceService ss = createProxy(SpaceService.class, spacesMap);
        getContainer().registerComponentInstance("SpaceService", ss);
        Identity identity = new Identity("root");
        ConversationState.setCurrent(new ConversationState(identity));
        ContainerResponse resp =
                launcher.service("GET", path, "", null, null, envctx);
        assertEquals(200, resp.getStatus());
        String response = resp.getEntity().toString();
        JSONObject responseObject = new JSONObject(response);
        assertEquals(responseObject.get("date_act").toString(), DateFormat.getDateInstance(DateFormat.SHORT, new Locale("en")).format(new Date()));

    }
}
