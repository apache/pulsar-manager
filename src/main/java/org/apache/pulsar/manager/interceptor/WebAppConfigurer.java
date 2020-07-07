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

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import javax.annotation.Resource;

@Configuration
public class WebAppConfigurer implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(WebAppConfigurer.class);

    @Resource
    private AdminHandlerInterceptor adminHandlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminHandlerInterceptor).addPathPatterns("/**")
                .excludePathPatterns("/pulsar-manager/login")
                .excludePathPatterns("/pulsar-manager/users/superuser")
                .excludePathPatterns("/pulsar-manager/csrf-token")
                .excludePathPatterns("/pulsar-manager/third-party-login/**")
                // static front-end resources
                .excludePathPatterns("/ui")
                .excludePathPatterns("/static")
                .excludePathPatterns("/error")
                // BKVM
                .excludePathPatterns("/bkvm")
                ;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        File ui = new File("ui");
        if (ui.isDirectory()) {
            log.info("Found front-end at " + ui.getAbsolutePath());
            String uipath = ui.toURI().toString();
            String uistaticpath = new File(ui, "static").toURI().toString();

            registry.addResourceHandler("/static/**")
                    .addResourceLocations("/", uistaticpath);
            registry.addResourceHandler("/ui/**")
                    .addResourceLocations("/", uipath);
        } else {
            log.info("Front-end not found at " + ui.getAbsolutePath()
                    + ". Maybe you are deploying the front-end as a separate process");
        }
    }
}
