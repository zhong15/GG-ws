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

package gg.ws.connect.core.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import gg.ws.common.config.WebConfig;
import gg.ws.common.message.model.Message;
import gg.ws.connect.cache.CacheKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Zhong
 * @since 0.0.1
 */
@Component
public class WsPushServer implements WsServer, WsPush {
    private static final Logger log = LoggerFactory.getLogger(WsPushServer.class);

    /**
     * WS连接的过期时间，存储在 WebSocketSession attributes
     */
    private static final String ATTR_WS_CONTEXT_EXPIRATION_TIME = "wsContextExpirationTime";
    /**
     * WS连接的过期时长，单位：分钟
     */
    private static final int TIMEOUT = 15;
    /**
     * WS连接的过期时长单位：分钟
     */
    private static final TimeUnit TIMEOUT_UNIT = TimeUnit.MINUTES;
    /**
     * WS连接的过期刷新定时任务时长，单位：毫秒
     */
    private static final int REFRESH_TIME_MS = 1 * 60 * 1000;

    private Map<Long, WebSocketSession> sessionMap;

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private WebConfig webConfig;

    @PostConstruct
    public void init() {
        log.info("init");
        if (sessionMap == null) {
            synchronized (this) {
                if (sessionMap == null) {
                    sessionMap = new ConcurrentHashMap<>();
                }
            }
        }
    }

    @PreDestroy
    @Override
    public void destroy() {
        log.info("destroy");
        if (sessionMap == null || sessionMap.isEmpty()) {
            return;
        }
        for (Long e : sessionMap.keySet()) {
            remove(e);
        }
    }

    @Scheduled(fixedRate = REFRESH_TIME_MS)
    @Override
    public void refresh() {
        if (sessionMap == null || sessionMap.isEmpty()) {
            return;
        }
        final long currentTime = System.currentTimeMillis();
        for (Long e : sessionMap.keySet()) {
            WebSocketSession session = sessionMap.get(e);
            if (session == null || !session.isOpen()) {
                sessionMap.remove(e, session);
                redisTemplate.delete(CacheKey.WS_CONNECTION.getKey(e));
            } else {
                long expirationTime = (long) session.getAttributes().get(ATTR_WS_CONTEXT_EXPIRATION_TIME);
                // 这里采用 1.5 倍系数，防止 Cache 和 Java 出现 NPC 问题
                if (expirationTime - currentTime <= 1.5 * REFRESH_TIME_MS) {
                    redisTemplate.expire(CacheKey.WS_CONNECTION.getKey(e), TIMEOUT, TIMEOUT_UNIT);
                    session.getAttributes().put(ATTR_WS_CONTEXT_EXPIRATION_TIME, currentTime + TIMEOUT_UNIT.toMillis(TIMEOUT));
                }
            }
        }
    }

    @Override
    public void set(Long userId, WebSocketSession session) {
        session.getAttributes().put(ATTR_WS_CONTEXT_EXPIRATION_TIME, System.currentTimeMillis() + TIMEOUT_UNIT.toMillis(TIMEOUT));
        sessionMap.put(userId, session);
        String address = webConfig.getCurrentServiceUrl().getServiceUrl();
        log.info("ip:port: {}", address);
        redisTemplate.opsForValue().set(CacheKey.WS_CONNECTION.getKey(userId), address, TIMEOUT, TIMEOUT_UNIT);
    }

    @Override
    public void remove(Long userId) {
        sessionMap.remove(userId);
        redisTemplate.delete(CacheKey.WS_CONNECTION.getKey(userId));
    }

    @Override
    public Integer push(Long userId, String message) {
        final int success = 0;
        final int unknow = 1;
        final int closed = 2;
        try {
            WebSocketSession session = sessionMap.get(userId);
            if (session == null) {
                return closed;
            }
            if (!session.isOpen()) {
                return closed;
            }

            Message msg = new Message();
            msg.setContent(message);
            String json = new ObjectMapper().writeValueAsString(new Message[]{msg});

            session.sendMessage(new TextMessage(json));
            return success;
        } catch (IOException e) {
            log.error("push error, userId: {}", userId, e);
            return unknow;
        }
    }
}
