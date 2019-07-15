package com.manager.pulsar.utils;

import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * http client class, to get info from pulsar broker
 */
public class HttpUtil {

    private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);

    private static final CloseableHttpClient httpClient;

    private static int CONNECTION_TIMEOUT = 28*1000;

    private static int SO_TIMEOUT = 28*1000;

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
        CloseableHttpResponse response = null;
        try {

            for (Map.Entry<String, String> entry : header.entrySet()) {
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
        } catch (Exception e) {
            log.error("http request exception:{}",e.getMessage());
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
