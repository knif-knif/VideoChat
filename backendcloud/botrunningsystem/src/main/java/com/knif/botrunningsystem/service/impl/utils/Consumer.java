package com.knif.botrunningsystem.service.impl.utils;

import com.knif.botrunningsystem.utils.BotInterface;
import org.joor.Reflect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.channels.MulticastChannel;
import java.util.UUID;
import java.util.function.Supplier;

@Component
public class Consumer extends Thread{
    private Bot bot;
    private static RestTemplate restTemplate;
    private final static String receiveBotMoveUrl = "http://127.0.0.1:3000/pk/receive/bot/move/";

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) { Consumer.restTemplate = restTemplate; }

    public void startTimeout(long timeout, Bot bot) {
        this.bot = bot;
        this.start();
        try {
            this.join(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.interrupt();
        }
    }

    private String addUid(String code, String uid) {
        int k = code.indexOf(" Bot java.util.function.Supplier<String>");
        return code.substring(0, k) + uid + code.substring(k);
    }

    @Override
    public void run() {
        UUID uuid = UUID.randomUUID();
        String uid = uuid.toString().substring(0, 8);
        Supplier<String> botInterface = Reflect.compile(
            "com.knif.botrunningsystem.utils.Bot" + uid,
                addUid(bot.getBotCode(), uid)
        ).create().get();

        File file = new File("input.txt");
        try (PrintWriter fout = new PrintWriter(file)){
            fout.println(bot.getInput());
            fout.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", bot.getUserId().toString());
        data.add("operate", botInterface.get());
        restTemplate.postForObject(receiveBotMoveUrl, data, String.class);
    }
}
