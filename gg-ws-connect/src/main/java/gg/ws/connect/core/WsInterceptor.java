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

import gg.ws.connect.auth.AuthService;
import gg.ws.connect.auth.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

/**
 * @author Zhong
 * @since 0.0.1
 */
@Component
public class WsInterceptor extends HttpSessionHandshakeInterceptor {
    private static final Logger log = LoggerFactory.getLogger(WsInterceptor.class);

    static final String ATTR_KEY_USER_ID = "userId";

    @Autowired
    private AuthService authService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.info("beforeHandshake uri: {}", request.getURI());
        String auth = ((ServletServerHttpRequest) request).getServletRequest().getParameter("auth");
        log.info("auth: {}", auth);
        if (auth == null || auth.trim().length() == 0) {
            log.info("Handshake failure, auth is empty", auth);
            return false;
        }
        User user = authService.getUser(auth);
        if (user == null) {
            log.info("Handshake failure, auth: {} error", auth);
        } else {
            attributes.put(ATTR_KEY_USER_ID, user.getId());
        }
        return user != null;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
        log.info("afterHandshake");
        super.afterHandshake(request, response, wsHandler, ex);
    }
}
