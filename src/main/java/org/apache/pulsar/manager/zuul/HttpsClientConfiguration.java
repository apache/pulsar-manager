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
package org.apache.pulsar.manager.zuul;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.File;

@Configuration
public class HttpsClientConfiguration {

    @Value("${tls.enabled}")
    private boolean tlsEnabled;

    @Value("${tls.keystore}")
    private String tlsKeystore;

    @Value("${tls.keystore.password}")
    private String tlsKeystorePassword;

    @Value("${tls.hostname.verifier}")
    private boolean tlsHostnameVerifier;

    @Bean
    public CloseableHttpClient httpClient() throws Exception {
        if (tlsEnabled) {
            Resource resource = new FileSystemResource(tlsKeystore);
            File trustStoreFile = resource.getFile();
            SSLContext sslcontext = SSLContexts.custom()
                    .loadTrustMaterial(trustStoreFile, tlsKeystorePassword.toCharArray(),
                            new TrustSelfSignedStrategy())
                    .build();
            HostnameVerifier hostnameVerifier = (s, sslSession) -> {
                // Custom logic to verify host name, tlsHostnameVerifier is false for test
                if (!tlsHostnameVerifier) {
                    return true;
                } else {
                    HostnameVerifier hv= HttpsURLConnection.getDefaultHostnameVerifier();
                    return hv.verify(s, sslSession);
                }
            };

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslcontext,
                    hostnameVerifier);

            return HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    .build();
        }
        return HttpClients.custom().build();
    }
}
