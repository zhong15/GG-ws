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

import gg.ws.common.config.WebConfig;
import gg.ws.connect.cache.CacheKey;
import gg.ws.connect.core.server.WsContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.WebSocketSession;

import java.util.Objects;

/**
 * 同一个用户同一个时间只能保持一个连接
 *
 * @author Zhong
 * @since 0.0.1
 */
@Component
public class SingleWsOnlineStrategy implements WsOnlineStrategy {
    private static final Logger log = LoggerFactory.getLogger(SingleWsOnlineStrategy.class);

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private WebConfig webConfig;
    @Autowired
    private WsContext wsContext;

    @Override
    public void handleWsOnline(Long userId, WebSocketSession session) {
        String address = redisTemplate.opsForValue().get(CacheKey.WS_CONNECTION.getKey(userId));
        if (address == null) {
            return;
        }
        if (Objects.equals(address, webConfig.getCurrentServiceUrl())) {
            wsContext.offline(userId);
        } else {
            final String url = address + "/" + "offline/" + userId;
            ResponseEntity<Integer> responseEntity = restTemplate.postForEntity(url, null, Integer.class);
            log.info("post offline result: {}", responseEntity.getBody());
        }
    }
}
