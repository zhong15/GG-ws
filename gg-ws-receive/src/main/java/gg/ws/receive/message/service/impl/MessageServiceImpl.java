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

package gg.ws.receive.message.service.impl;

import gg.ws.common.message.model.Message;
import gg.ws.receive.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.cql.CqlTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Zhong
 * @since 0.0.1
 */
@Component
public class MessageServiceImpl implements MessageService {
    @Autowired
    private CqlTemplate cqlTemplate;

    @Override
    public List<Message> receive(Long userId, Integer size) {
        if (userId == null || size == null || size < 1) {
            return null;
        }

        List<Message> list = cqlTemplate.query("SELECT id, content FROM message WHERE is_deleted = 0 AND user_id = ? ORDER BY id LIMIT ? ALLOW FILTERING",
                (row, rowNum) -> {
                    Message msg = new Message();
                    msg.setId(row.getLong("id"));
                    msg.setContent(row.getString("content"));
                    return msg;
                }, userId, size);
        if (list != null) {
            for (Message e : list) {
                e.setUserId(userId);
            }
        }
        return list;
    }

    @Override
    public Boolean acknowledge(Long userId, Long[] messageIds) {
        if (userId == null || messageIds == null || messageIds.length == 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE message SET is_deleted = 1, update_time = toTimestamp(now()) WHERE user_id = ? AND id IN (");
        for (Long e : messageIds) {
            sb.append(e).append(",");
        }
        sb.setLength(sb.length() - 1);
        sb.append(")");

        return cqlTemplate.execute(sb.toString(), userId);
    }
}
