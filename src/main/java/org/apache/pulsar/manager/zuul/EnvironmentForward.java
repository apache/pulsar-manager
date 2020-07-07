/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pulsar.manager.zuul;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.pulsar.manager.service.EnvironmentCacheService;
import org.apache.pulsar.manager.service.PulsarAdminService;
import org.apache.pulsar.manager.service.PulsarEvent;
import org.apache.pulsar.manager.service.RolesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.REQUEST_URI_KEY;

/**
 * Handle http redirect and forward.
 */
@Component
public class EnvironmentForward extends ZuulFilter {

    private static final Logger log = LoggerFactory.getLogger(EnvironmentForward.class);

    private final EnvironmentCacheService environmentCacheService;

    private final PulsarEvent pulsarEvent;

    private final RolesService rolesService;

    private final PulsarAdminService pulsarAdminService;

    @Autowired
    public EnvironmentForward(
            EnvironmentCacheService environmentCacheService, PulsarEvent pulsarEvent,
            RolesService rolesService, PulsarAdminService pulsarAdminService) {
        this.environmentCacheService = environmentCacheService;
        this.pulsarEvent = pulsarEvent;
        this.rolesService = rolesService;
        this.pulsarAdminService = pulsarAdminService;
    }

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.SEND_FORWARD_FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {

        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String redirect = request.getParameter("redirect");

        String requestUri = request.getServletPath();
        request.getServletPath();
        String token = request.getHeader("token");

        if (!rolesService.isSuperUser(token)) {
            if (!pulsarEvent.validateRoutePermission(requestUri, token)) {
                ctx.setResponseBody("This operation does not have permission");
                return null;
            }
            if (requestUri.startsWith("/admin/v2/tenants/")
                    || requestUri.startsWith("/admin/v2/namespaces")
                    || requestUri.startsWith("/admin/v2/persistent")
                    || requestUri.startsWith("/admin/v2/non-persistent")) {
                Map<String, String> result = pulsarEvent.validateTenantPermission(
                        requestUri, token);
                if (result.get("error") != null) {
                    log.error("This operation does not have permission");
                    ctx.setResponseBody(result.get("error"));
                    return null;
                }
            }
        }

        if (redirect != null && redirect.equals("true")) {
            String redirectScheme = request.getParameter("redirect.scheme");
            String redirectHost = request.getParameter("redirect.host");
            String redirectPort = request.getParameter("redirect.port");
            String url = redirectScheme + "://" + redirectHost + ":" + redirectPort;
            return forwardRequest(ctx, request, url);
        }

        String broker = request.getHeader("x-pulsar-broker");
        if (StringUtils.isNotBlank(broker)) { // the request should be forward to a pulsar broker
            // TODO: support https://
            String serviceUrl = "http://" + broker;
            return forwardRequest(ctx, request, serviceUrl);
        }

        String environment = request.getHeader("environment");
        if (StringUtils.isBlank(environment)) {
            return null;
        }
        String serviceUrl = environmentCacheService.getServiceUrl(request);
        return forwardRequest(ctx, request, serviceUrl);
    }

    private Object forwardRequest(RequestContext ctx, HttpServletRequest request, String serviceUrl) {
        ctx.put(REQUEST_URI_KEY, request.getServletPath());
        try {
            Map<String, String> authHeader = pulsarAdminService.getAuthHeader(serviceUrl);
            authHeader.entrySet().forEach(entry -> ctx.addZuulRequestHeader(entry.getKey(), entry.getValue()));
            ctx.setRouteHost(new URL(serviceUrl));
            pulsarEvent.parsePulsarEvent(request.getServletPath(), request);
            log.info("Forward request to {} @ path {}",
                    serviceUrl, request.getServletPath());
        } catch (MalformedURLException e) {
            log.error("Route forward to {} path {} error: {}",
                    serviceUrl, request.getServletPath(), e.getMessage());
        }
        return null;
    }
}
