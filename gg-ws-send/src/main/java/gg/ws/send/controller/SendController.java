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

package gg.ws.send.controller;

import gg.ws.common.serviceUrl.ServiceUrlFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhong
 * @since 0.0.1
 */
@RequestMapping("/")
@RestController
public class SendController {
    private static final Logger log = LoggerFactory.getLogger(SendController.class);

    @Autowired
    private ServiceUrlFinder serviceUrlFinder;
    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/send")
    public String send(@RequestParam(name = "userId") Long userId,
                       @RequestParam(name = "message") String message) {
        log.info("send userId: {}, message: {}", userId, message);

        final String url = serviceUrlFinder.findServiceUrl("biz").getServiceUrl() + "/receive";

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
