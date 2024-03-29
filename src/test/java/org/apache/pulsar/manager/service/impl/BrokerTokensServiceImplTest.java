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

import org.apache.pulsar.manager.PulsarManagerApplication;
import org.apache.pulsar.manager.profiles.HerdDBTestProfile;
import org.apache.pulsar.manager.service.impl.JwtServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;

import java.security.Key;

@RunWith(SpringRunner.class)
@TestPropertySource(
        properties = {
                "jwt.broker.token.mode=SECRET",
                "jwt.broker.secret.key=data:base64,cxkc+xPbcF/3E49I1HE4BJKtAwZ/FUER1h7wXk7qkLw="
        }
)
@SpringBootTest(
        classes = {
                PulsarManagerApplication.class,
                HerdDBTestProfile.class
        }
)
@ActiveProfiles("test")
public class BrokerTokensServiceImplTest {

    @Autowired
    private JwtServiceImpl jwtService;

    public Claims validateBrokerToken(String token) {
        Key validationKey = jwtService.getSigningKey();
        Jwt jwt = Jwts.parser()
                .setSigningKey(validationKey)
                .parse(token);
        return (Claims) jwt.getBody();
    }

    @Test
    public void createBrokerTokenTest() {
        String role = "test";
        String token = jwtService.createBrokerToken(role, null);
        Claims jwtBody = validateBrokerToken(token);
        Assert.assertEquals(role, jwtBody.getSubject());
    }
}
