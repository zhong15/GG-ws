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

package gg.ws.connect.auth;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhong
 * @since 0.0.1
 */
@Component
public class AuthServiceImpl implements AuthService {
    /**
     * key: auth
     */
    private Map<String, User> userMap;

    @PostConstruct
    public void init() {
        userMap = new HashMap<>();
        userMap.put("aa", new User(1L, "XiaoMing"));
        userMap.put("bb", new User(2L, "XiaoHong"));
        userMap.put("cc", new User(3L, "XiaoDong"));
        userMap.put("dd", new User(4L, "XiaoHuang"));
        userMap.put("ee", new User(5L, "XiaoBai"));
    }

    @Override
    public User getUser(String auth) {
        return userMap == null ? null : userMap.get(auth);
    }
}
