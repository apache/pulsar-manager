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
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.pulsar.manager.service.BookiesService;
import org.apache.pulsar.manager.service.EnvironmentCacheService;
import org.apache.pulsar.manager.utils.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BookiesServiceImpl implements BookiesService {

    private final EnvironmentCacheService environmentCacheService;
    private final HttpServletRequest request;
    @Value("${backend.directRequestBroker}")
    private boolean directRequestBroker;

    @Value("${backend.directRequestHost}")
    private String directRequestHost;

    @Value("${bookie.enable}")
    private Boolean bookieEnable;

    @Value("${backend.jwt.token}")
    private static String pulsarJwtToken;


    private static final Map<String, String> header = new HashMap<String, String>(){{
        put("Authorization", String.format("Bearer %s", pulsarJwtToken));
    }};

    private final Pattern pattern = Pattern.compile(" \\d+");;

    public BookiesServiceImpl(EnvironmentCacheService environmentCacheService,HttpServletRequest request) {
        this.environmentCacheService = environmentCacheService;
        this.request = request;
    }

    public Map<String, Object> getBookiesList(Integer pageNum, Integer pageSize, String cluster) {
        Map<String, Object> bookiesMap = Maps.newHashMap();
        List<Map<String, Object>> bookiesArray = new ArrayList<>();
        if (bookieEnable) {
            Gson gson = new Gson();
            Map<String, String> header = Maps.newHashMap();
            header.put("Content-Type", "application/json");
            if (StringUtils.isNotBlank(pulsarJwtToken)) {
                header.put("Authorization", String.format("Bearer %s", pulsarJwtToken));
            }

            String bookieUrl = this.environmentCacheService.getBookieUrl(request);
            if(StringUtils.isBlank(bookieUrl)){
                return bookiesMap;
            }
            String rwBookieList = HttpUtil.doGet(
                    bookieUrl + "/api/v1/bookie/list_bookies?type=rw&print_hostnames=true", header);
            Map<String, String> rwBookies = gson.fromJson(
                    rwBookieList, new TypeToken<Map<String, String>>() {}.getType());
            String roBookieList = HttpUtil.doGet(
                    bookieUrl + "/api/v1/bookie/list_bookies?type=ro&print_hostnames=true", header);
            Map<String, String> roBookies = gson.fromJson(
                    roBookieList, new TypeToken<Map<String, String>>() {}.getType());
            String listBookieInfo = HttpUtil.doGet(
                    bookieUrl + "/api/v1/bookie/list_bookie_info", header);
            Map<String, String> listBookies = gson.fromJson(
                    listBookieInfo, new TypeToken<Map<String, String>>() {}.getType());
            for (String key: listBookies.keySet()) {
                Map<String, Object> bookieEntity = Maps.newHashMap();
                if (rwBookies != null && rwBookies.containsKey(key)) {
                    bookieEntity.put("bookie", key);
                    bookieEntity.put("status", "rw");
                } else if (roBookies != null && roBookies.containsKey(key)) {
                    bookieEntity.put("bookie", key);
                    bookieEntity.put("status", "ro");
                }
                if ((rwBookies != null && rwBookies.containsKey(key)) || (roBookies != null && roBookies.containsKey(key))) {
                    Matcher matcher = pattern.matcher(listBookies.get(key));
                    List<String> storageSizeList = new ArrayList<>();
                    while (matcher.find()) {
                        String res = matcher.group();
                        storageSizeList.add(res.trim());
                    }
                    bookieEntity.put("storage", storageSizeList);
                    bookiesArray.add(bookieEntity);
                }
            }
            bookiesMap.put("isPage", false);
            bookiesMap.put("total", bookiesArray.size());
            bookiesMap.put("data", bookiesArray);
            bookiesMap.put("pageNum", 1);
            bookiesMap.put("pageSize", bookiesArray.size());
            bookiesMap.put("enableBookieHttp", true);
            return bookiesMap;
        }
        bookiesMap.put("enableBookieHttp", false);
        return bookiesMap;
    }

    public String forwardBookiesHeartbeat(String bookie) {
        bookie = checkBookie(bookie);
        return HttpUtil.doGet(bookie + "/heartbeat", header);
    }

    public void forwardGc(String bookie) {
        bookie = checkBookie(bookie);
        try {
            HttpUtil.doPut(bookie + "/api/v1/bookie/gc", header, "");
        } catch (UnsupportedEncodingException e) {

        }
    }

    public void forwardAutorecovery(List<String> bookieSrc, List<String> bookieDest, Boolean deleteBookie) {
        try {
            String bookieUrl = this.environmentCacheService.getBookieUrl(request);
            Gson gson = new Gson();
            Map<String, Object> body = Maps.newHashMap();
            body.put("bookie_src", bookieSrc);
            body.put("bookie_dest", bookieDest);
            HttpUtil.doPut(bookieUrl + "/api/v1/autorecovery/bookie/", header, gson.toJson(body));
        } catch (UnsupportedEncodingException e) {

        }
    }

    public void forwardBookieDecommission(String bookie) {
        bookie = checkBookie(bookie);
        Map<String, String> body = Maps.newHashMap();
        body.put("bookie_src", bookie);
        try {
            Gson gson = new Gson();
            HttpUtil.doPut(bookie + "/api/v1/autorecovery/decommission", header, gson.toJson(body));
        } catch (UnsupportedEncodingException e) {

        }
    }

    private String checkBookie(String bookie) {
        if (bookie == null || bookie.length() <= 0) {
            bookie = directRequestHost;
        }

        if (!bookie.startsWith("http")) {
            bookie = "http://" + bookie;
        }
        return bookie;
    }
}
