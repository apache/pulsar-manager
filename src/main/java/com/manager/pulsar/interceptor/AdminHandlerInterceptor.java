package com.manager.pulsar.interceptor;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.manager.pulsar.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class AdminHandlerInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private JwtService jwtService;

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
