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

package gg.ws.connect.core.client;

import gg.ws.common.message.model.Message;
import gg.ws.common.serviceUrl.ServiceUrlFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author Zhong
 * @since 0.0.1
 */
@Component
public class WsClient implements WsReceive, WsSend {
    private static final Logger log = LoggerFactory.getLogger(WsClient.class);

    @Autowired
    private ServiceUrlFinder serviceUrlFinder;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<Message> receive(Long userId) {
        log.info("receive userId: {}", userId);

        final String url = serviceUrlFinder.findServiceUrl("receive").getServiceUrl() + "/receive/" + userId;

        ResponseEntity<List<Message>> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Message>>() {
        });

        return response.getBody();
    }

    @Override
    public Integer acknowledge(Long userId, List<Long> messageIdList) {
        log.info("acknowledge userId: {}, messageIdList: {}", userId, messageIdList);

        final String url = serviceUrlFinder.findServiceUrl("receive").getServiceUrl() + "/acknowledge";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("userId", userId);
        map.addAll("messageIds", messageIdList);
        HttpEntity<MultiValueMap<String, Object>> formEntity = new HttpEntity<>(map, headers);

        ResponseEntity<Integer> response = restTemplate.postForEntity(url, formEntity, Integer.class);
        return response.getBody();
    }

    @Override
    public String send(Long userId, String message) {
        log.info("send userId: {}, message: {}", userId, message);

        final String url = serviceUrlFinder.findServiceUrl("send").getServiceUrl() + "/send";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("userId", userId);
        map.add("message", message);
        HttpEntity<MultiValueMap<String, Object>> formEntity = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, formEntity, String.class);
        return response.getBody();
    }
}
