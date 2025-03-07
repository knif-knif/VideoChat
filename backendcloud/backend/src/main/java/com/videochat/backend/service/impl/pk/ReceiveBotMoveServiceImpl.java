package com.videochat.backend.service.impl.pk;

import com.alibaba.fastjson.JSONObject;
import com.videochat.backend.consumer.WebSocketServer;
import com.videochat.backend.consumer.utils.ChessOperate;
import com.videochat.backend.consumer.utils.Game;
import com.videochat.backend.service.pk.ReceiveBotMoveService;
import org.springframework.stereotype.Service;

@Service
public class ReceiveBotMoveServiceImpl implements ReceiveBotMoveService {
    @Override
    public String receiveBotMove(Integer userId, String operate) {
        JSONObject opt = JSONObject.parseObject(operate);

        if (WebSocketServer.users.get(userId) != null) {
            Game game = WebSocketServer.users.get(userId).game;
            if (game != null) {
                if ((game.getPlayerA().getId().equals(userId) && !game.getPlayerA().getBotId().equals(-1))
                        || (game.getPlayerB().getId().equals(userId) && !game.getPlayerB().getBotId().equals(-1))) {
                    game.setNextStep(new ChessOperate(
                            userId, opt.getInteger("op"),
                            opt.getInteger("x"), opt.getInteger("y"),
                            opt.getInteger("nx"), opt.getInteger("ny")
                    ));
                }
            }
            return "receive bot move success";
        }
        return "receive bot move fail";
    }
}
