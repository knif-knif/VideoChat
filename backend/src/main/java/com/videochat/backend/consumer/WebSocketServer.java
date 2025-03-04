package com.videochat.backend.consumer;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.videochat.backend.consumer.utils.ChessOperate;
import com.videochat.backend.consumer.utils.Game;
import com.videochat.backend.consumer.utils.JwtAuthentication;
import com.videochat.backend.consumer.utils.Player;
import com.videochat.backend.mapper.RecordMapper;
import com.videochat.backend.mapper.UserMapper;
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
    final private static CopyOnWriteArraySet<User> matchpool = new CopyOnWriteArraySet<>();
    private User user;
    private Session session = null;
    public Game game = null;
    private static UserMapper userMapper;
    public static RecordMapper recordMapper;
    public static RestTemplate restTemplate;
    private final static String removePlayerurl = "http://127.0.0.1:3001/player/remove";
    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }

    @Autowired
    public void setRecordMapper(RecordMapper recordMapper) {
        WebSocketServer.recordMapper = recordMapper;
    }

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
            matchpool.remove(this.user);
        }
    }

    private void startMatching() {
        System.out.println("Start matching");
        matchpool.add(this.user);
        while (matchpool.size() > 1) {
            Iterator<User> it = matchpool.iterator();
            User a = it.next(), b = it.next();
            matchpool.remove(a);
            matchpool.remove(b);

            Game game = new Game(8, 8, a.getId(), b.getId());
            game.createMap();
            users.get(a.getId()).game = game;
            users.get(b.getId()).game = game;
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
            users.get(a.getId()).sendMessage(resA.toJSONString());
            JSONObject resB = new JSONObject();
            resB.put("event", "start-matching");
            resB.put("opponent_username", a.getUsername());
            resB.put("opponent_photo", a.getPhoto());
            resB.put("game", respGame);
            users.get(b.getId()).sendMessage(resB.toJSONString());
        }
    }

    private void stopMatching() {
        System.out.println("Stop matching");
        matchpool.remove(this.user);
//        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
//        data.add("user_id", this.user.getId().toString());
//        restTemplate.postForObject(removePlayerurl, data, String.class);
    }

    private void move(JSONObject opt) {
        game.setNextStep(new ChessOperate(
                user.getId(), opt.getInteger("op"),
                opt.getInteger("x"), opt.getInteger("y"),
                opt.getInteger("nx"), opt.getInteger("ny")
        ));
        System.out.println(opt.toJSONString());
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        JSONObject msg_json = JSONObject.parseObject(message);
        String event = msg_json.getString("event");
        if ("start-matching".equals(event)) {
            startMatching();
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
