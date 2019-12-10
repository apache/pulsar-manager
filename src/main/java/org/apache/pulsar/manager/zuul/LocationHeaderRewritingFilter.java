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

import com.netflix.util.Pair;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UrlPathHelper;

import java.net.URI;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.*;

/**
 * Handle http redirection.
 */
@Component
public class LocationHeaderRewritingFilter extends ZuulFilter {

    private final UrlPathHelper urlPathHelper = new UrlPathHelper();

    @Value("${redirect.host}")
    private String host;

    @Value("${redirect.port}")
    private String port;

    @Value("${redirect.scheme}")
    private String scheme;

    private final RouteLocator routeLocator;

    @Autowired
    public LocationHeaderRewritingFilter(RouteLocator routeLocator) {
        this.routeLocator = routeLocator;
    }

    @Override
    public String filterType() {
        return POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return SEND_RESPONSE_FILTER_ORDER - 100;
    }

    private static final String LOCATION_HEADER = "Location";

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        int statusCode = ctx.getResponseStatusCode();
        return HttpStatus.valueOf(statusCode).is3xxRedirection();
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();

        Route route = routeLocator.getMatchingRoute(
                urlPathHelper.getPathWithinApplication(ctx.getRequest()));
        if (route != null) {
            Pair<String, String> lh = locationHeader(ctx);
            if (lh != null) {
                String location = lh.second();
                URI originalRequestUri = UriComponentsBuilder
                        .fromHttpRequest(new ServletServerHttpRequest(ctx.getRequest()))
                        .build().toUri();
                UriComponentsBuilder redirectedUriBuilder = UriComponentsBuilder
                        .fromUriString(location);

                UriComponents redirectedUriComps = redirectedUriBuilder.build();

                String modifiedLocation = redirectedUriBuilder
                        .scheme(scheme)
                        .host(host)
                        .port(port).replacePath(redirectedUriComps.getPath())
                        .queryParam("redirect", true)
                        .queryParam("redirect.scheme", redirectedUriComps.getScheme())
                        .queryParam("redirect.host", redirectedUriComps.getHost())
                        .queryParam("redirect.port", redirectedUriComps.getPort())
                        .toUriString();
                lh.setSecond(modifiedLocation);
            }
        }
        return null;
    }

    private Pair<String, String> locationHeader(RequestContext ctx) {
        if (ctx.getZuulResponseHeaders() != null) {
            for (Pair<String, String> pair : ctx.getZuulResponseHeaders()) {
                if (pair.first().equals(LOCATION_HEADER)) {
                    return pair;
                }
            }
        }
        return null;
    }

}
