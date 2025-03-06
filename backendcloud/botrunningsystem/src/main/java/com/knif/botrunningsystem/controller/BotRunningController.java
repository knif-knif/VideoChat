package com.knif.botrunningsystem.controller;

import com.knif.botrunningsystem.service.BotRunningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BotRunningController {
    @Autowired
    private BotRunningService botRunningService;

    @PostMapping("/bot/add/")
    public String addBot(@RequestParam MultiValueMap<String, String> params) {
        Integer userId = Integer.parseInt(params.getFirst("user_id"));
        String botCode = params.getFirst("bot_code");
        String input = params.getFirst("input");
        return botRunningService.addBot(userId, botCode, input);
    }
}
