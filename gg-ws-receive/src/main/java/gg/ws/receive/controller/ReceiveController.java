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

package gg.ws.receive.controller;

import gg.ws.common.message.model.Message;
import gg.ws.receive.message.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Zhong
 * @since 0.0.1
 */
@RequestMapping("/")
@RestController
public class ReceiveController {
    private static final Logger log = LoggerFactory.getLogger(ReceiveController.class);

    @Autowired
    private MessageService messageService;

    @GetMapping("/receive/{userId}")
    public List<Message> receive(@PathVariable(name = "userId") Long userId) {
        log.info("receive userId: {}", userId);

        return messageService.receive(userId, 100);
    }

    @PostMapping("/acknowledge")
    public Integer acknowledge(@RequestParam(name = "userId") Long userId,
                               @RequestParam(name = "messageIds") Long[] messageIds) {
        log.info("acknowledge userId: {}, messageId: {}", userId, messageIds);

        Boolean success = messageService.acknowledge(userId, messageIds);
        return success == true ? 1 : 0;
    }
}
