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
package org.apache.pulsar.manager.interceptor;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.apache.pulsar.manager.entity.EnvironmentEntity;
import org.apache.pulsar.manager.entity.EnvironmentsRepository;
import org.apache.pulsar.manager.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;

@Component
public class AdminHandlerInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EnvironmentsRepository environmentsRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        String saveToken = jwtService.getToken(request.getSession().getId());
        if (token == null || !token.equals(saveToken)) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("message", "Please login.");
            Gson gson = new Gson();
            response.setStatus(401);
            response.getWriter().append(gson.toJson(map));
            return false;
        }
        String environment = request.getHeader("environment");
        Optional<EnvironmentEntity> environmentEntityOptional = environmentsRepository.findByName(environment);
        if (!request.getRequestURI().startsWith("/pulsar-manager/environments") && !environmentEntityOptional.isPresent()) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("message", "Currently there is no active environment, please set one");
            Gson gson = new Gson();
            response.setStatus(400);
            response.getWriter().append(gson.toJson(map));
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           @Nullable ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {}

}
