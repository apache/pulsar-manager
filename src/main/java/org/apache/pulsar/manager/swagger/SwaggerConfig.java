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
package org.apache.pulsar.manager.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger config class.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    public static final String TOKEN = "token";
    public static final String USERNAME = "username";
    public static final String ENVIRONMENT = "environment";
    public static final String HEADER = "header";

    @Value(value = "${swagger.enabled}")
    private boolean swaggerEnabled;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(swaggerEnabled)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.apache.pulsar.manager.controller"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Pulsar manager custom rest api").version("0.1").build();
    }

    private List<ApiKey> securitySchemes() {
        List<ApiKey> arrayList = new ArrayList();
        arrayList.add(new ApiKey(TOKEN, TOKEN, HEADER));
        arrayList.add(new ApiKey(USERNAME, USERNAME, HEADER));
        arrayList.add(new ApiKey(ENVIRONMENT, ENVIRONMENT, HEADER));
        return arrayList;
    }

    private List<SecurityContext> securityContexts() {
        List<SecurityContext> securityContextsList = new ArrayList();
        securityContextsList.add(SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("/.*"))
                .build());
        return securityContextsList;
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        List<SecurityReference> defaultAuthList = new ArrayList<>();
        defaultAuthList.add(new SecurityReference(TOKEN, authorizationScopes));
        defaultAuthList.add(new SecurityReference(USERNAME, authorizationScopes));
        defaultAuthList.add(new SecurityReference(ENVIRONMENT, authorizationScopes));
        return defaultAuthList;
    }
}
