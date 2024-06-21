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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gg.ws.common.message.model.Message;
import gg.ws.connect.core.client.WsClient;
import gg.ws.connect.core.client.WsOnlineStrategy;
import gg.ws.connect.core.server.WsContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.stream.Collectors;

import static gg.ws.connect.core.server.WsPushServer.CONFLICT;

/**
 * @author Zhong
 * @since 0.0.1
 */
@Component
public class WsHandler extends //AbstractWebSocketHandler
        TextWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(WsHandler.class);

    @Autowired
    private WsContext wsContext;
    @Autowired
    private WsOnlineStrategy wsOnlineStrategy;
    @Autowired
    private WsClient wsClient;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("afterConnectionEstablished sid: {}, uri: {}", session.getId(), session.getUri());

        long userId = (long) session.getAttributes().get(WsInterceptor.ATTR_KEY_USER_ID);
        wsOnlineStrategy.handleWsOnline(userId, session);
        wsContext.set(userId, session);
        List<Message> messageList = wsClient.receive(userId);
        if (messageList != null && messageList.size() != 0) {
            String json = new ObjectMapper().writeValueAsString(messageList);
            session.sendMessage(new TextMessage(json));

            List<Long> messageIdList = messageList.stream()
                    .map(Message::getId)
                    .collect(Collectors.toList());
            wsClient.acknowledge(userId, messageIdList);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("handleMessage sid: {}, msg: {}", session.getId(), message);

        long userId = (long) session.getAttributes().get(WsInterceptor.ATTR_KEY_USER_ID);
        String response = wsClient.send(userId, message.getPayload());
        if (response != null && response.trim().length() != 0) {
            try {
                Message msg = new Message();
                msg.setContent(response);
                String json = new ObjectMapper().writeValueAsString(new Message[]{msg});
                session.sendMessage(new TextMessage(json));
            } catch (JsonProcessingException e) {
                log.error("to json string error");
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.info("handleTransportError sid: {}", session.getId());
        super.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("afterConnectionClosed sid: {}", session.getId());
        if (status == CONFLICT) {
            // 服务端主动关闭
            return;
        }
        long userId = (long) session.getAttributes().get(WsInterceptor.ATTR_KEY_USER_ID);
        wsContext.remove(userId);
    }

    @Override
    public boolean supportsPartialMessages() {
        log.info("supportsPartialMessages");
        return super.supportsPartialMessages();
    }
}
