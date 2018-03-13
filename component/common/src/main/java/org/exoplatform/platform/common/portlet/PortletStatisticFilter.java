package org.exoplatform.platform.common.portlet;

import java.io.IOException;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.filter.FilterChain;
import javax.portlet.filter.FilterConfig;
import javax.portlet.filter.RenderFilter;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class PortletStatisticFilter implements RenderFilter {

  private static final Log     LOG         = ExoLogger.getExoLogger(PortletStatisticFilter.class);

  private static final int     MAX_WARN_RT = Integer.parseInt(System.getProperty("exo.portlet.statistic.max.rt.warn", "2000"));

  private static final boolean ENABLED     =
                                       Boolean.parseBoolean(System.getProperty("exo.portlet.statistic.max.rt.enabled", "false"));

  private PortletContext       context;

  public void init(FilterConfig filterConfig) throws PortletException {
    context = filterConfig.getPortletContext();
  }

  public void doFilter(RenderRequest request, RenderResponse response, FilterChain chain) throws IOException, PortletException {
    if (ENABLED) {
      long delta = -System.currentTimeMillis();
      try {
        chain.doFilter(request, response);
      } catch (Exception e) {
        throw e;
      } finally {
        delta += System.currentTimeMillis();
        if (delta > MAX_WARN_RT) {
          PortletConfig portletConfig = (PortletConfig) request.getAttribute("javax.portlet.config");
          if (portletConfig != null) {
            String portletName = portletConfig.getPortletName();
            String portletID = context.getPortletContextName() + "/" + portletName;
            LOG.info("Response time for portlet {} has exceeded max RT {}, rt = {}", portletID, MAX_WARN_RT, delta);
          }
        }
      }
    } else {
      chain.doFilter(request, response);
    }
  }

  public void destroy() {
  }
}
