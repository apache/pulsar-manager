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

import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * http client class, to get info from pulsar broker
 */
public class HttpUtil {

    private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);

    private static final CloseableHttpClient httpClient;

    private static int CONNECTION_TIMEOUT = 28 * 1000;

    private static int SO_TIMEOUT = 28 * 1000;

    public static PoolingHttpClientConnectionManager cm = null;

    static {
        cm = new PoolingHttpClientConnectionManager();
        cm.setDefaultMaxPerRoute(10);
        cm.setMaxTotal(100);
        RequestConfig config = RequestConfig.custom().setConnectTimeout(CONNECTION_TIMEOUT)
                .setSocketTimeout(SO_TIMEOUT).build();
        httpClient = HttpClients.custom().setConnectionManager(cm).setDefaultRequestConfig(config).build();
    }

    public static String doGet(String url, Map<String, String> header){
        HttpGet request = new HttpGet(url);
        return httpRequest(request, header);
    }

    public static String doPut(String url, Map<String, String> header, String body) throws UnsupportedEncodingException {
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
            response = httpClient.execute(request);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String strResult = EntityUtils.toString(response.getEntity());
                response.close();
                return strResult;
            } else {
                request.abort();
            }
        } catch (Throwable cause) {
            log.error("http request exception:{}", cause.getMessage());
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
