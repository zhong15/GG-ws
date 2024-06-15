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

package gg.ws.common.config;

import gg.ws.common.serviceUrl.ServiceUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author Zhong
 * @since 0.0.1
 */
@Component
public class WebConfig {
    private static final Logger log = LoggerFactory.getLogger(WebConfig.class);

    private String ip;
    private Integer port;

    @Autowired
    private ServerProperties serverProperties;

    @Value("${spring.application.name}")
    private String applicationName;
    @Value("${gg-ws.service-url}")
    private String serviceUrl;

    private ServiceUrl currentServiceUrl;

    private Set<ServiceUrl> serviceUrlSet;

    @PostConstruct
    public void init() {
        initCurrentServiceUrl();
        addOtherServiceUrl();
    }

    private void initCurrentServiceUrl() {
        InetAddress inetAddress = serverProperties.getAddress();
        if (inetAddress == null) {
            try {
                inetAddress = InetAddress.getLocalHost();
                ip = inetAddress.getHostAddress();
                log.info("ip: {}", ip);
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }

        port = serverProperties.getPort();
        if (port == null) {
            port = 8080;
        }
        log.info("port: {}", port);

        currentServiceUrl = new ServiceUrl();
        currentServiceUrl.setServiceName(applicationName);
        currentServiceUrl.setServiceUrl("http://" + ip + ":" + port);
    }

    private void addOtherServiceUrl() {
        if (serviceUrl == null) {
            return;
        }
        serviceUrl = serviceUrl.trim();
        if (serviceUrl.length() == 0) {
            return;
        }
        String[] ss = serviceUrl.split(" ");
        if (ss.length != 2 || ss[0].trim().length() == 0 || ss[1].trim().length() == 0) {
            throw new IllegalArgumentException("serviceUrl error");
        }

        ServiceUrl other = new ServiceUrl();
        other.setServiceName(ss[0].trim());
        other.setServiceUrl(ss[1].trim());

        addServiceUrlList(Arrays.asList(other));
    }

    public void addServiceUrlList(List<ServiceUrl> serviceUrlList) {
        if (serviceUrlList == null || serviceUrlList.isEmpty()) {
            return;
        }
        if (serviceUrlSet == null) {
            synchronized (this) {
                if (serviceUrlSet == null) {
                    serviceUrlSet = new ConcurrentSkipListSet<>();
                }
            }
        }
        serviceUrlSet.addAll(serviceUrlList);
        serviceUrlSet.remove(currentServiceUrl);
    }

    public List<ServiceUrl> getServiceUrlList() {
        if (serviceUrlSet == null || serviceUrlSet.size() == 0) {
            return Collections.emptyList();
        }
        return new ArrayList<>(serviceUrlSet);
    }

    public ServiceUrl getCurrentServiceUrl() {
        return currentServiceUrl;
    }
}
