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

import java.util.List;

/**
 * @author Zhong
 * @since 0.0.1
 */
public interface WsReceive {
    List<Message> receive(Long userId);

    Integer acknowledge(Long userId, List<Long> messageIdList);
}
