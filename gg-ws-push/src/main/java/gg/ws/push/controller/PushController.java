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

package gg.ws.push.controller;

import gg.ws.push.cache.CacheKey;
import gg.ws.push.message.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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

/**
 * @author Zhong
 * @since 0.0.1
 */
@RequestMapping("/")
@RestController
public class PushController {
    private static final Logger log = LoggerFactory.getLogger(PushController.class);

    @Autowired
    private MessageService messageService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/push")
    public Integer push(@RequestParam(name = "userId") Long userId,
                        @RequestParam(name = "message") String message) {
        log.info("push userId: {}, message: {}", userId, message);

        String address = redisTemplate.opsForValue().get(CacheKey.WS_CONNECTION.getKey(userId));

        if (address == null || address.trim().length() == 0) {
            messageService.store(userId, message);
            return 1;
        }

        final String url = address + "/push";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("userId", userId);
        map.add("message", message);
        HttpEntity<MultiValueMap<String, Object>> formEntity = new HttpEntity<>(map, headers);

        ResponseEntity<Integer> response = restTemplate.postForEntity(url, formEntity, Integer.class);
        return response.getBody();
    }
}
