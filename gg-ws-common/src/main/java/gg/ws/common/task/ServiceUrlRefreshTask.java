/*
 * Copyright 2024 the original author or authors.
 *
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

package gg.ws.common.task;

import gg.ws.common.PublicController;
import gg.ws.common.config.WebConfig;
import gg.ws.common.serviceUrl.ServiceUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Zhong
 * @since 0.0.1
 */
@Component
public class ServiceUrlRefreshTask {
    private static final Logger log = LoggerFactory.getLogger(ServiceUrlRefreshTask.class);

    @Autowired
    private WebConfig webConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Scheduled(fixedRate = 5000)
    public void refresh() {
        List<ServiceUrl> oldServiceUrlList = webConfig.getServiceUrlList();
        if (oldServiceUrlList == null || oldServiceUrlList.size() == 0) {
            log.debug("serviceUrl is empty");
            return;
        }

        log.debug("refresh serviceUrl: {}", oldServiceUrlList.stream().map(ServiceUrl::toString).collect(Collectors.joining(",")));

        HttpEntity<List<ServiceUrl>> x = serviceUrlListHttpEntity();

        for (ServiceUrl e : oldServiceUrlList) {
            final String url = e.getServiceUrl() + PublicController.URL_PUBLIC + PublicController.URL_PING;

            try {
                ResponseEntity<List<ServiceUrl>> response = restTemplate.exchange(url, HttpMethod.POST,
                        x, new ParameterizedTypeReference<List<ServiceUrl>>() {
                        });
                List<ServiceUrl> receiveServiceUrlList = response.getBody();
                log.debug("receive serviceUrl: {}", receiveServiceUrlList == null ? "" : receiveServiceUrlList.stream().map(ServiceUrl::toString).collect(Collectors.joining(",")));
                webConfig.addServiceUrlList(receiveServiceUrlList);
            } catch (Exception exception) {
                log.error("服务不可达：" + e, exception);
            }
        }
    }

    private HttpEntity<List<ServiceUrl>> serviceUrlListHttpEntity() {
        List<ServiceUrl> list = webConfig.getServiceUrlList();
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(webConfig.getCurrentServiceUrl());
        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Content-Type", "application/json; charset=UTF-8");
        return new HttpEntity<>(list, headers);
    }
}
