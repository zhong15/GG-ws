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

import org.springframework.web.socket.WebSocketSession;

/**
 * WebSocket连接上下文
 *
 * @author Zhong
 * @since 0.0.1
 */
public interface WsContext {
    /**
     * 刷新连接
     */
    void refresh();

    /**
     * 添加连接
     *
     * @param userId  用户ID
     * @param session 连接
     */
    void set(Long userId, WebSocketSession session);

    /**
     * 删除连接
     *
     * @param userId 用户ID
     * @return 删除的连接
     */
    WebSocketSession remove(Long userId);

    /**
     * 关闭并删除连接
     *
     * @param userId 用户ID
     */
    void offline(Long userId);

    /**
     * 删除所有连接
     */
    void clear();
}
