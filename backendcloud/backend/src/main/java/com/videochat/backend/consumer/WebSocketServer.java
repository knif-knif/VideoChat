package com.videochat.backend.consumer;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.videochat.backend.consumer.utils.ChessOperate;
import com.videochat.backend.consumer.utils.Game;
import com.videochat.backend.consumer.utils.JwtAuthentication;
import com.videochat.backend.consumer.utils.Player;
import com.videochat.backend.mapper.BotMapper;
import com.videochat.backend.mapper.RecordMapper;
import com.videochat.backend.mapper.UserMapper;
import com.videochat.backend.pojo.Bot;
import com.videochat.backend.pojo.User;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/ws/{token}")
public class WebSocketServer {
    final public static ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();
    private User user;
    private Session session = null;
    public Game game = null;
    public static UserMapper userMapper;
    public static RecordMapper recordMapper;
    private static BotMapper botMapper;
    public static RestTemplate restTemplate;
    private final static String addPlayerUrl = "http://127.0.0.1:3001/player/add/";
    private final static String removePlayerUrl = "http://127.0.0.1:3001/player/remove/";
    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }

    @Autowired
    public void setRecordMapper(RecordMapper recordMapper) {
        WebSocketServer.recordMapper = recordMapper;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) { WebSocketServer.restTemplate = restTemplate; }

    @Autowired
    public void setBotMapper(BotMapper botMapper) { WebSocketServer.botMapper = botMapper; }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException{
        this.session = session;
        Integer userId = JwtAuthentication.getUserId(token);
        this.user = userMapper.selectById(userId);
        if (this.user != null) {
            users.put(userId, this);
        }
        else {
            this.session.close();
        }
    }

    @OnClose
    public void onClose() {
        if (this.user != null) {
            users.remove(this.user.getId());
            MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
            data.add("user_id", this.user.getId().toString());
            restTemplate.postForObject(removePlayerUrl, data, String.class);
        }
    }

    public static void startGame(Integer aId, Integer bId, Integer aBotId, Integer bBotId) {
        User a = userMapper.selectById(aId), b = userMapper.selectById(bId);
        Bot botA = botMapper.selectById(aBotId), botB = botMapper.selectById(bBotId);
        Game game = new Game(
                8, 8,
                a.getId(), b.getId(),
                botA, botB
        );
        game.createMap();
        if (users.get(a.getId()) != null) {
            users.get(a.getId()).game = game;
        }
        if (users.get(b.getId()) != null) {
            users.get(b.getId()).game = game;
        }

        game.start();


        JSONObject respGame = new JSONObject();
        respGame.put("a_id", game.getPlayerA().getId());
        respGame.put("b_id", game.getPlayerB().getId());
        respGame.put("map", game.getG());

        JSONObject resA = new JSONObject();
        resA.put("event", "start-matching");
        resA.put("opponent_username", b.getUsername());
        resA.put("opponent_photo", b.getPhoto());
        resA.put("game", respGame);
        if (users.get(a.getId()) != null) {
            users.get(a.getId()).sendMessage(resA.toJSONString());
        }

        JSONObject resB = new JSONObject();
        resB.put("event", "start-matching");
        resB.put("opponent_username", a.getUsername());
        resB.put("opponent_photo", a.getPhoto());
        resB.put("game", respGame);
        if (users.get(b.getId()) != null) {
            users.get(b.getId()).sendMessage(resB.toJSONString());
        }
    }

    private void startMatching(Integer botId) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("user_id", user.getId().toString());
        params.add("rating", user.getRating().toString());
        params.add("bot_id", botId.toString());
        restTemplate.postForObject(addPlayerUrl, params, String.class);
    }

    private void stopMatching() {
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());
        restTemplate.postForObject(removePlayerUrl, data, String.class);
    }

    private void move(JSONObject opt) {
        if ((game.getPlayerA().getId().equals(user.getId()) && game.getPlayerA().getBotId().equals(-1))
        || (game.getPlayerB().getId().equals(user.getId()) && game.getPlayerB().getBotId().equals(-1))) {
            game.setNextStep(new ChessOperate(
                    user.getId(), opt.getInteger("op"),
                    opt.getInteger("x"), opt.getInteger("y"),
                    opt.getInteger("nx"), opt.getInteger("ny")
            ));
        }
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        JSONObject msg_json = JSONObject.parseObject(message);
        String event = msg_json.getString("event");
        if ("start-matching".equals(event)) {
            startMatching(msg_json.getInteger("bot_id"));
        }
        else if ("stop-matching".equals(event)) {
            stopMatching();
        }
        else if ("move".equals(event)) {
            move(msg_json.getJSONObject("opt"));
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(String message) {
        synchronized (this.session) {
            try {
                this.session.getBasicRemote().sendText(message);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
