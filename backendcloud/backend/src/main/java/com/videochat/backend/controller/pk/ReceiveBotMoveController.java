package com.videochat.backend.controller.pk;

import com.videochat.backend.service.pk.ReceiveBotMoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReceiveBotMoveController {
    @Autowired
    private ReceiveBotMoveService receiveBotMoveService;

    @PostMapping("/pk/receive/bot/move/")
    public String receiveBotMove(@RequestParam MultiValueMap<String, String> data) {
        Integer userId = Integer.valueOf(data.getFirst("userId"));
        String operate = data.getFirst("operate");
        return receiveBotMoveService.receiveBotMove(userId, operate);
    }
}
