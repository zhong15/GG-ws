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

package gg.ws.push.message.service.impl;

import gg.ws.common.message.model.Message;
import gg.ws.push.cache.CacheKey;
import gg.ws.push.id.generator.IdGenerator;
import gg.ws.push.message.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.cql.CqlTemplate;
import org.springframework.stereotype.Service;

/**
 * @author Zhong
 * @since 0.0.1
 */
@Service
public class MessageServiceImpl implements MessageService {
    private static final Logger log = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Autowired
    private IdGenerator idGenerator;
    @Autowired
    private CqlTemplate cqlTemplate;

    @Override
    public Message store(Long userId, String message) {
        if (userId == null) {
            return null;
        }
        if (message == null || message.trim().length() == 0) {
            return null;
        }

        Long id = idGenerator.nextId(CacheKey.WS_MESSAGE_ID.getKey());
        if (id == null) {
            log.error("id generator error");
            return null;
        }

        boolean success = cqlTemplate.execute("INSERT INTO message (id, user_id, content, create_time, update_time, is_deleted) VALUES (?, ?, ?, toTimeStamp(now()), toTimeStamp(now()), 0)",
                id, userId, message);
        if (!success) {
            log.error("store failure, [id={},userId={},message={}]", id, userId, message);
            return null;
        }

        Message msg = new Message();
        msg.setId(id);
        msg.setUserId(userId);
        msg.setContent(message);
        return msg;
    }
}
