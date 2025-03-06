package com.videochat.backend.controller.pk;

import com.videochat.backend.service.pk.StartGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StartGameController {
    @Autowired
    private StartGameService startGameService;

    @PostMapping("/pk/start/game/")
    public String startGame(@RequestParam MultiValueMap<String, String> param) {
        Integer aId = Integer.parseInt(param.getFirst("aId"));
        Integer bId = Integer.parseInt(param.getFirst("bId"));
        Integer aBotId = Integer.parseInt(param.getFirst("a_bot_id"));
        Integer bBotId = Integer.parseInt(param.getFirst("b_bot_id"));
        return startGameService.startGame(aId, bId, aBotId, bBotId);
    }

}
