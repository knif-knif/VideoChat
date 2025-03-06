package com.knif.botrunningsystem.service.impl;

import com.knif.botrunningsystem.service.BotRunningService;
import com.knif.botrunningsystem.service.impl.utils.BotPool;
import org.springframework.stereotype.Service;

@Service
public class BotRunningServiceImpl implements BotRunningService {
    public final static BotPool botPool = new BotPool();

    @Override
    public String addBot(Integer userId, String botCode, String input) {
        botPool.addBot(userId, botCode, input);
        return "add bot success";
    }
}
