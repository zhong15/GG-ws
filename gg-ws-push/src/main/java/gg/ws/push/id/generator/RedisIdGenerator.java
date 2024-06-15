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

package gg.ws.push.id.generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author Zhong
 * @since 0.0.1
 */
@Component
public class RedisIdGenerator implements IdGenerator {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Long nextId(String table) {
        return redisTemplate.opsForValue().increment(table);
    }

    @Override
    public Long nextId(String table, int delta) {
        Long id = redisTemplate.opsForValue().increment(table, delta);
        return id == null ? null : id - delta + 1;
    }
}
