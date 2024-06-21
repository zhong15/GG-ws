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

/**
 * ID生成器
 *
 * @author Zhong
 * @since 0.0.1
 */
public interface IdGenerator {
    /**
     * 获取下一个可用ID
     *
     * @param table 表名
     * @return 下一个可用ID
     */
    Long nextId(String table);

    /**
     * 批量获取连续的可用ID
     *
     * @param table 表名
     * @param delta 获取个数
     * @return 最小的可用ID
     */
    Long nextId(String table, int delta);
}
