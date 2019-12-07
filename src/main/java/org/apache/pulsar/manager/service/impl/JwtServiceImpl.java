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

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.apache.pulsar.manager.service.JwtService;

import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.broker.authentication.utils.AuthTokenUtils;
import org.apache.pulsar.common.util.RelativeTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class JwtServiceImpl implements JwtService {

    private int sessionTime;

    @Value("${jwt.broker.token.mode}")
    private String jwtBrokerTokenMode;

    @Value("${jwt.broker.secret.key}")
    private String jwtBrokerSecretKey;

    @Value("${jwt.broker.private.key}")
    private String jwtBrokerPrivateKey;

    @Value("${jwt.broker.public.key}")
    private String jwtBrokerPublicKey;

    private final Map<String, String> tokens = new ConcurrentHashMap<>();

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @Autowired
    public JwtServiceImpl(@Value("${jwt.sessionTime}") int sessionTime) {
        this.sessionTime = sessionTime;
    }

    @Override
    public String toToken(String id) {
        return Jwts.builder()
                .setSubject(id)
                .setExpiration(expireTimeFromNow())
                .signWith(key)
                .compact();
    }

    @Override
    public Optional<String> getSubFromToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            return Optional.ofNullable(claimsJws.getBody().getSubject());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Date expireTimeFromNow() {
        return new Date(System.currentTimeMillis() + sessionTime * 1000);
    }

    @Override
    public void setToken(String key, String value) {
        synchronized (this.tokens) {
            this.tokens.put(key, value);
        }
    }

    @Override
    public String getToken(String key) {
        return this.tokens.get(key);
    }

    @Override
    public void removeToken(String key) {
        synchronized (this.tokens) {
            this.tokens.remove(key);
        }
    }

    private Key decodeBySecretKey() {
        try {
            byte[] encodedKey = AuthTokenUtils.readKeyFromUrl(jwtBrokerSecretKey);
            return AuthTokenUtils.decodeSecretKey(encodedKey);
        } catch (IOException e) {
            log.error("Decode failed by secrete key, error: {}", e.getMessage());
            return null;
        }
    }

    public String createBrokerToken(String role, String expiryTime) {
        Key signingKey;
        if (jwtBrokerTokenMode.equals("SECRET")) {
            signingKey = decodeBySecretKey();
        } else if (jwtBrokerTokenMode.equals("PRIVATE")){
            signingKey = decodeByPrivateKey();
        } else {
            log.info("Default disable JWT auth, please set jwt.broker.token.mode.");
            return null;
        }
        if (signingKey == null) {
            log.error("JWT Auth failed, signingKey is not empty");
            return null;
        }
        Optional<Date> optExpiryTime = Optional.empty();
        if (expiryTime != null) {
            long relativeTimeMillis = TimeUnit.SECONDS
                    .toMillis(RelativeTimeUtil.parseRelativeTimeInSeconds(expiryTime));
            optExpiryTime = Optional.of(new Date(System.currentTimeMillis() + relativeTimeMillis));
        }
        return AuthTokenUtils.createToken(signingKey, role, optExpiryTime);
    }

    private Key decodeByPrivateKey() {
        try {
            byte[] encodedKey = AuthTokenUtils.readKeyFromUrl(jwtBrokerPrivateKey);
            SignatureAlgorithm algorithm = SignatureAlgorithm.RS256;
            return AuthTokenUtils.decodePrivateKey(encodedKey, algorithm);
        } catch (IOException e) {
            log.error("Decode failed by private key, error: {}", e.getMessage());
            return null;
        }
    }

    public Claims validateBrokerToken(String token) {
        Key validationKey;
        if (jwtBrokerTokenMode.equals("SECRET")) {
            validationKey = decodeBySecretKey();
        } else if (jwtBrokerTokenMode.equals("PRIVATE")){
            validationKey = decodeByPrivateKey();
        } else {
            log.info("Default disable JWT auth, please set jwt.broker.token.mode.");
            return null;
        }
        Jwt<?, Claims> jwt = Jwts.parser()
                .setSigningKey(validationKey)
                .parse(token);
        return jwt.getBody();
    }
}
