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
package org.apache.pulsar.manager.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * http client class, to get info from pulsar broker
 */
@Component
@Slf4j
public class HttpUtil {

    private static boolean tlsEnabled;

    private static String tlsKeystore;

    private static String tlsKeystorePassword;

    private static boolean tlsHostnameVerifier;

    private static CloseableHttpClient httpClient;

    private static int CONNECTION_TIMEOUT = 28 * 1000;

    private static int SO_TIMEOUT = 28 * 1000;

    public static PoolingHttpClientConnectionManager cm = null;

    @Value("${tls.enabled}")
    public void setTlsEnabled(boolean tlsEnabled) {
        HttpUtil.tlsEnabled = tlsEnabled;
    }

    @Value("${tls.keystore}")
    public void setTlsKeystore(String brokerKeystore) {
        HttpUtil.tlsKeystore = brokerKeystore;
    }

    @Value("${tls.keystore.password}")
    public void setTlsKeystorePassword(String brokerKeystorePassword) {
        HttpUtil.tlsKeystorePassword = brokerKeystorePassword;
    }

    @Value("${tls.hostname.verifier}")
    public void setTlsHostnameVerifier(boolean tlsHostnameVerifier) {
        HttpUtil.tlsHostnameVerifier = tlsHostnameVerifier;
    }

    public static void initHttpClient() {
        try {
            if (tlsEnabled) {
                Resource resource = new FileSystemResource(tlsKeystore);
                File trustStoreFile = resource.getFile();
                HostnameVerifier hostnameVerifier = (s, sslSession) -> {
                    // Custom logic to verify host name, tlsHostnameVerifier is false for test
                    if (!tlsHostnameVerifier) {
                        return true;
                    } else {
                        HostnameVerifier hv= HttpsURLConnection.getDefaultHostnameVerifier();
                        return hv.verify(s, sslSession);
                    }
                };

                SSLContext sslcontext = SSLContexts.custom()
                        .loadTrustMaterial(
                                trustStoreFile,
                                tlsKeystorePassword.toCharArray(),
                                new TrustSelfSignedStrategy())
                        .build();

                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, hostnameVerifier);
                cm = new PoolingHttpClientConnectionManager(RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", PlainConnectionSocketFactory.getSocketFactory())
                        .register("https", sslsf)
                        .build());
            } else {
                cm = new PoolingHttpClientConnectionManager();
            }

            cm.setDefaultMaxPerRoute(10);
            cm.setMaxTotal(100);
            RequestConfig config = RequestConfig.custom().setConnectTimeout(CONNECTION_TIMEOUT)
                    .setSocketTimeout(SO_TIMEOUT).build();
            httpClient = HttpClients.custom()
                    .setConnectionManager(cm).setDefaultRequestConfig(config).build();

        } catch (Exception e) {
            log.error("Failed init http client error message: {}, error stack trace: {}", e.getMessage(), e.getCause());
        }
    }

    public static String doGet(String url, Map<String, String> header){
        HttpGet request = new HttpGet(url);
        return httpRequest(request, header);
    }

    /**
     * HTTP post method.
     * @param url Destination host
     * @param header Header information
     * @param body Body information
     * @return HTTP response information
     * @throws UnsupportedEncodingException
     */
    public static String doPost(String url, Map<String, String> header, String body)
            throws UnsupportedEncodingException {
        HttpPost request = new HttpPost(url);
        request.setEntity(new StringEntity(body));
        return httpRequest(request, header);
    }

    public static String doPut(String url, Map<String, String> header, String body)
            throws UnsupportedEncodingException {
        HttpPut request = new HttpPut(url);
        request.setEntity(new StringEntity(body));
        return httpRequest(request, header);
    }

    public static String httpRequest(HttpUriRequest request, Map<String, String> header) {
        CloseableHttpResponse response = null;
        try {
            for (Map.Entry<String, String> entry: header.entrySet()) {
                request.setHeader(entry.getKey(), entry.getValue());
            }
            if (httpClient == null ) {
                initHttpClient();
            }
            response = httpClient.execute(request);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String strResult = EntityUtils.toString(response.getEntity());
                response.close();
                return strResult;
            } else {
                request.abort();
            }
        } catch (Throwable cause) {
            log.error("http request exception message: {}, http request error stack: {}",
                    cause.getMessage(), cause.getCause());
        } finally {
            try{
                if (response != null) {
                    response.close();
                }
            }catch (Exception e){
                log.error("Don't handle exception: {}", e.getMessage());
            }

        }

        return null;
    }
}
