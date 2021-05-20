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
package org.apache.pulsar.manager.service;

import org.apache.pulsar.manager.entity.EnvironmentEntity;
import javax.servlet.http.HttpServletRequest;

public interface EnvironmentCacheService {

    /**
     * Return the service url for a given http request.
     *
     * @param request http request
     * @return the service url that that http request should be forwarded to.
     */
    String getServiceUrl(HttpServletRequest request);

    /**
     * Return the bookie url for a given http request.
     * @param request
     * @return
     */
    String getBookieUrl(HttpServletRequest request);
    /**
     * Return the service url for a given http request for a given cluster.
     *
     * @param request http request
     * @param cluster cluster name
     * @return the service url that the http request should be forwarded to.
     */
    String getServiceUrl(HttpServletRequest request, String cluster);

    /**
     * Return the service url for a given cluster at a given environment.
     *
     * @param environment environment name
     * @param cluster cluster name
     * @return the service url that the http request should be forwarded to.
     */
    String getServiceUrl(String environment, String cluster);

    /**
     * Refresh all the environments.
     */
    void reloadEnvironments();

    /**
     * Refresh the environment in the cache.
     *
     * @param environment environment entity.
     */
    void reloadEnvironment(EnvironmentEntity environment);
}
