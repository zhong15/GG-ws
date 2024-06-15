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

package gg.ws.common;

import gg.ws.common.config.WebConfig;
import gg.ws.common.serviceUrl.ServiceUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Zhong
 * @since 0.0.1
 */
@RequestMapping(PublicController.URL_PUBLIC)
@RestController
public class PublicController {
    private static final Logger log = LoggerFactory.getLogger(PublicController.class);

    public static final String URL_PUBLIC = "/public";
    public static final String URL_PING = "/ping";

    @Autowired
    private WebConfig webConfig;

    @PostMapping(PublicController.URL_PING)
    public List<ServiceUrl> ping(@RequestBody List<ServiceUrl> serviceUrlList) {
        log.debug("ping serviceUrlList: {}", serviceUrlList == null ? "" : serviceUrlList.stream().map(ServiceUrl::toString).collect(Collectors.joining(",")));

        List<ServiceUrl> publishServiceUrlList = webConfig.getServiceUrlList();

        webConfig.addServiceUrlList(serviceUrlList);

        publishServiceUrlList.removeAll(serviceUrlList);
        return publishServiceUrlList;
    }
}
