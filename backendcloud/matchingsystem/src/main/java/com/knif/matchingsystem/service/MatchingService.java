package com.knif.matchingsystem.service;

public interface MatchingService {
    String addPlayer(Integer userId, Integer rating, Integer botId);

    String removePlayer(Integer userI);
}
