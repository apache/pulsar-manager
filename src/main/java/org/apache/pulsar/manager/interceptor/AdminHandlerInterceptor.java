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
import org.apache.commons.lang.StringUtils;
import org.apache.pulsar.manager.entity.EnvironmentEntity;
import org.apache.pulsar.manager.entity.EnvironmentsRepository;
import org.apache.pulsar.manager.entity.UserInfoEntity;
import org.apache.pulsar.manager.entity.UsersRepository;
import org.apache.pulsar.manager.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;

@Component
public class AdminHandlerInterceptor extends HandlerInterceptorAdapter {

    private final JwtService jwtService;
    private final EnvironmentsRepository environmentsRepository;

    @Autowired
    public AdminHandlerInterceptor(JwtService jwtService, EnvironmentsRepository environmentsRepository) {
        this.jwtService = jwtService;
        this.environmentsRepository = environmentsRepository;
    }

    @Autowired
    private UsersRepository usersRepository;

    @Value("${user.management.enable}")
    private boolean userManagementEnable;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        String saveToken = jwtService.getToken(request.getSession().getId());
        Map<String, Object> map = Maps.newHashMap();
        Gson gson = new Gson();
        if (saveToken == null ) {
            // Get token from database
            String username = request.getHeader("username");
            Optional<UserInfoEntity> userInfoEntityOptional = usersRepository.findByUserName(username);
            if (!userInfoEntityOptional.isPresent()) {
                map.put("message", "Please login.");
                response.setStatus(401);
                response.getWriter().append(gson.toJson(map));
                return false;
            }
            UserInfoEntity userInfoEntity = userInfoEntityOptional.get();
            if (StringUtils.isBlank(userInfoEntity.getAccessToken())) {
                map.put("message", "The user token no find, please login");
                response.setStatus(401);
                response.getWriter().append(gson.toJson(map));
                return false;
            }
            jwtService.setToken(request.getSession().getId(), userInfoEntity.getAccessToken());
            saveToken = jwtService.getToken(request.getSession().getId());
        }
        if (token == null && !token.equals(saveToken)) {
            map.put("message", "Please login.");
            response.setStatus(401);
            response.getWriter().append(gson.toJson(map));
            return false;
        }
        if (userManagementEnable) {
            Optional<UserInfoEntity> optionalUserInfoEntity = usersRepository.findByAccessToken(token);
            if (!optionalUserInfoEntity.isPresent()) {
                map.put("message", "Please login.");
                response.setStatus(401);
                response.getWriter().append(gson.toJson(map));
                return false;
            }
            String username = request.getHeader("username");
            UserInfoEntity userInfoEntity = optionalUserInfoEntity.get();
            if (!userInfoEntity.getName().equals(username)) {
                map.put("message", "Please login.");
                response.setStatus(401);
                response.getWriter().append(gson.toJson(map));
                return false;
            }
        }
        String environment = request.getHeader("environment");
        Optional<EnvironmentEntity> environmentEntityOptional = environmentsRepository.findByName(environment);
        if (!request.getRequestURI().startsWith("/pulsar-manager/environments") && !environmentEntityOptional.isPresent()) {
            map.put("message", "Currently there is no active environment, please set one");
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
