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

package gg.ws.connect.controller;

import gg.ws.connect.core.server.WsContext;
import gg.ws.connect.core.server.WsPush;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Zhong
 * @since 0.0.1
 */
@RequestMapping("/")
@RestController
public class ConnectController {
    private static final Logger log = LoggerFactory.getLogger(ConnectController.class);

    @Autowired
    private WsPush wsPush;
    @Autowired
    private WsContext wsContext;
    /**
     * 刷新连接信息线程池
     */
    private ExecutorService refreshExecutorService;

    @PostConstruct
    public void init() {
        log.info("init");
        if (refreshExecutorService == null) {
            log.info("init refreshExecutorService");
            // 核心 0， 最大 1，存活时间 60s，默认抛弃最老的任务
            refreshExecutorService = new ThreadPoolExecutor(0, 1,
                    60L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(),
                    new ThreadPoolExecutor.DiscardOldestPolicy());
        }
    }

    @PreDestroy
    public void destroy() {
        log.info("destroy");
        if (refreshExecutorService != null && !refreshExecutorService.isShutdown()) {
            log.info("destroy refreshExecutorService");
            refreshExecutorService.shutdown();
        }
    }

    @PostMapping("/push")
    public Integer push(@RequestParam(name = "userId") Long userId,
                        @RequestParam(name = "message") String message) {
        log.info("push userId: {}, message: {}", userId, message);
        return wsPush.push(userId, message);
    }

    @PostMapping("/offline/{userId}")
    public Integer offline(@PathVariable(name = "userId") Long userId) {
        log.info("push userId: {}", userId);
        wsContext.offline(userId);
        return 1;
    }

    @PostMapping("/refresh")
    public Integer refresh() {
        log.info("refresh");
        refreshExecutorService.execute(() -> {
            log.info("开始 refresh");
            wsContext.refresh();
            log.info("结束 refresh");
        });
        return 1;
    }
}
