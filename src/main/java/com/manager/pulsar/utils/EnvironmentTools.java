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
package com.manager.pulsar.utils;

import com.manager.pulsar.entity.EnvironmentEntity;
import com.manager.pulsar.entity.EnvironmentsRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class EnvironmentTools {

    public static String getEnvironment(HttpServletRequest request, EnvironmentsRepository environmentsRepository) {
        String environment = request.getHeader("environment");
        Optional<EnvironmentEntity> environmentEntityOptional = environmentsRepository.findByName(environment);
        EnvironmentEntity environmentEntity = environmentEntityOptional.get();
        String directRequestHost = environmentEntity.getBroker();
        return directRequestHost;
    }
}
