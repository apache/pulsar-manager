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

import org.apache.pulsar.manager.entity.UserInfoEntity;

import java.util.Map;

public interface ThirdLoginService {

    /**
     * Obtaining an authentication token from a third-party platform.
     * @param parameters For a kv type map, different third-party platforms may need to pass different parameters,
     *                  which are passed according to the actual situation and analyzed in their implementation classes.
     * @return String format access token information
     */
    String getAuthToken(Map<String, String> parameters);

    /**
     * Acquiring user information according to an authentication token.
     * @param authenticationMap For a kv type map, different third-party platforms need different parameters,
     *                          which are passed through the map structure.
     * @return UserInfoEntity
     */
    UserInfoEntity getUserInfo(Map<String, String> authenticationMap);
}
