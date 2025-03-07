package com.knif.matchingsystem.service.impl.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Player {
    private Integer userId;
    private Integer rating;
    private Integer botId;
    private Integer waitingTime;

    Player(Integer userId, Integer rating, Integer botId, Integer waitingTime) {
        this.userId = userId;
        this.rating = rating;
        this.botId = botId;
        this.waitingTime = waitingTime;
    }
}
