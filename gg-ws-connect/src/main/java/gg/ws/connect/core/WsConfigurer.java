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

package gg.ws.connect.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * @author Zhong
 * @since 0.0.1
 */
@Configuration
@EnableWebSocket
public class WsConfigurer implements WebSocketConfigurer {
    private static final Logger log = LoggerFactory.getLogger(WsConfigurer.class);

    private static final String WS_PATH = "/ws/**";

    @Autowired
    private WsHandler wsHandler;
    @Autowired
    private WsInterceptor wsInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        log.info("registerWebSocketHandlers: {}", WS_PATH);

        registry.addHandler(wsHandler, WS_PATH)
                .addInterceptors(wsInterceptor)
                .setAllowedOrigins("*");
    }
}
