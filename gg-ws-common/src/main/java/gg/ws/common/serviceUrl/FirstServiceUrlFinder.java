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

package gg.ws.common.serviceUrl;

import gg.ws.common.config.WebConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @author Zhong
 * @since 0.0.1
 */
@Component
public class FirstServiceUrlFinder implements ServiceUrlFinder {
    @Autowired
    private WebConfig webConfig;

    @Override
    public ServiceUrl findServiceUrl(String serviceName) {
        List<ServiceUrl> serviceUrlLit = webConfig.getServiceUrlList();
        if (serviceUrlLit == null) {
            return null;
        }
        for (ServiceUrl e : serviceUrlLit) {
            if (Objects.equals(e.getServiceName(), serviceName)) {
                return e;
            }
        }
        return null;
    }
}
