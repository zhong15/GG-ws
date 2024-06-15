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

import java.util.Objects;

/**
 * @author Zhong
 * @since 0.0.1
 */
public class ServiceUrl implements Comparable<ServiceUrl> {
    private String serviceName;
    private String serviceUrl;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ServiceUrl that = (ServiceUrl) o;
        return Objects.equals(serviceName, that.serviceName)
                && Objects.equals(serviceUrl, that.serviceUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceName, serviceUrl);
    }

    @Override
    public String toString() {
        return "ServiceUrl{" +
                "serviceName='" + serviceName + '\'' +
                ", serviceUrl='" + serviceUrl + '\'' +
                '}';
    }

    @Override
    public int compareTo(ServiceUrl o) {
        if (this == o) {
            return 0;
        }
        if (o == null) {
            return 1;
        }
        int val = compareString(this.serviceName, o.serviceName);
        if (val != 0) {
            return val;
        }
        return compareString(this.serviceUrl, o.serviceUrl);
    }

    private static int compareString(String a, String b) {
        if (a == b) {
            return 0;
        }
        if (a == null) {
            return -1;
        }
        if (b == null) {
            return 1;
        }
        return a.compareTo(b);
    }
}
