package com.moblize.ms.dailyops.web;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    @MessageMapping("/wellActivity")
    public void wellActivity() {

    }
}
