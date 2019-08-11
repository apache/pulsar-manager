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
package com.manager.pulsar.zuul;

import com.manager.pulsar.service.EnvironmentCacheService;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.REQUEST_URI_KEY;

/**
 * Handle http redirect and forward.
 */
@Component
public class EnvirmentForward extends ZuulFilter {

    private static final Logger log = LoggerFactory.getLogger(EnvirmentForward.class);

    @Autowired
    private EnvironmentCacheService environmentCacheService;

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

        if (redirect != null && redirect.equals("true")) {
            String redirectScheme = request.getParameter("redirect.scheme");
            String redirectHost = request.getParameter("redirect.host");
            String redirectPort = request.getParameter("redirect.port");
            String url = redirectScheme + "://" + redirectHost + ":" + redirectPort;
            ctx.put(REQUEST_URI_KEY, request.getRequestURI());
            try {
                ctx.setRouteHost(new URL(url));
            } catch(MalformedURLException mue) {
                log.error("Route redirect to {} path {} error: {}", url, request.getRequestURI(), mue.getMessage());
            }
            return null;
        }
        String environment = request.getHeader("environment");
        if (null == environment) {
            return null;
        }
        String serviceUrl = environmentCacheService.getServiceUrl(request);
        ctx.put(REQUEST_URI_KEY, request.getRequestURI());
        try {
            ctx.setRouteHost(new URL(serviceUrl));
        } catch (MalformedURLException e) {
            log.error("Route forward to {} path {} error: {}",
                serviceUrl, request.getRequestURI(), e.getMessage());
        }
        return null;
    }

}
