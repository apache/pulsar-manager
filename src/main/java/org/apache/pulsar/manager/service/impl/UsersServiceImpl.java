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
package org.apache.pulsar.manager.service.impl;

import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.pulsar.manager.entity.UserInfoEntity;
import org.apache.pulsar.manager.service.UsersService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Pattern;

@Service
public class UsersServiceImpl implements UsersService {

    private static final Pattern pattern = Pattern.compile("[A-Za-z0-9_]+");

    public Map<String, String> validateUserInfo(UserInfoEntity userInfoEntity) {
        Map<String, String> validateResult = Maps.newHashMap();
        if (StringUtils.isBlank(userInfoEntity.getName())) {
            validateResult.put("error", "User name cannot be empty");
            return validateResult;
        }
        if (!(pattern.matcher(userInfoEntity.getName()).matches())) {
            validateResult.put("error", "User name illegal");
            return validateResult;
        }

        if (StringUtils.isBlank(userInfoEntity.getEmail())) {
            validateResult.put("error", "User email cannot be empty");
            return validateResult;
        }

        if (!EmailValidator.getInstance().isValid(userInfoEntity.getEmail())) {
            validateResult.put("error", "Email address illegal");
            return validateResult;
        }
        if (StringUtils.isBlank(userInfoEntity.getPassword()) && StringUtils.isBlank(userInfoEntity.getAccessToken())) {
            validateResult.put("error", "Fields password and access token cannot be empty at the same time.");
            return validateResult;
        }
        if (userInfoEntity.getPassword().length() < 6) {
            validateResult.put("error", "The password can not be less than 6 digits.");
            return validateResult;
        }
        validateResult.put("message", "Validate user success");
        return validateResult;
    }
}
