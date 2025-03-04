package com.videochat.backend.consumer.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.videochat.backend.consumer.WebSocketServer;
import com.videochat.backend.pojo.Record;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;


public class Game extends Thread {
    private final Integer rows;
    private final Integer cols;
    private int[][] g;
    private final Player playerA, playerB;
    private ChessOperate nextStep; // (id, op, x, y, nx, ny)
    private Integer nextPlayer;
    private String status = "playing";
    private String winner = ""; // all, A, B
    private ReentrantLock lock = new ReentrantLock();
    private final int[] dx = {0, 0, 1, -1, 1, 1, -1, -1};
    private final int[] dy = {1, -1, 0, 0, 1, -1, 1, -1};
    private int max_dep = 0;

    public Game(Integer rows, Integer cols, Integer idA, Integer idB) {
        this.rows = rows;
        this.cols = cols;
        this.g = new int[rows][cols];
        // 0. nothing 1. forbidden 2. playerA 3. playerB 4. A can use 5. B can use
        playerA = new Player(idA, new ArrayList<>());
        playerB = new Player(idB, new ArrayList<>());
        nextPlayer = idA;
    }

    public int[][] getG() {
        return g;
    }

    public String getGString() {
        String res = "";
        for (int i = 0; i < this.rows; ++i) {
            for (int j = 0; j < this.cols; ++j) {
                res += String.valueOf(g[i][j]);
            }
        }
        return res;
    }

    public Player getPlayerA() {
        return playerA;
    }
    public Player getPlayerB() {
        return playerB;
    }

    public void createMap() {
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                g[i][j] = 0;
            }
        }
        g[0][0] = g[0][cols - 1] = g[rows - 1][0] = g[rows - 1][cols - 1] = 1;
        for (int i = 1; i < cols - 1; ++i) {
            g[0][i] = g[rows - 1][i] = 4;
        }
        for (int i = 1; i < rows - 1; ++i) {
            g[i][0] = g[i][cols - 1] = 5;
        }
    }

    public void setNextStep(ChessOperate nextStep) {
        lock.lock();
        try {
            this.nextStep = nextStep;
        } finally {
            lock.unlock();
        }
    }

    private boolean getNextStep() {
        for (int i = 0; i < 500; ++i) {
            try {
                Thread.sleep(100);
                lock.lock();
                try {
                    if (nextStep != null && nextStep.id == nextPlayer) {
                        if (nextPlayer == playerA.getId()) playerA.getCps().add(nextStep);
                        else playerB.getCps().add(nextStep);
                        return true;
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean check_valid(List<Cell> cells, int id) {
        if (cells.size() > 10) return false;
        Cell oc = cells.get(cells.size() - 1);
        int tmp = g[oc.x][oc.y];

        if (tmp != 0 && tmp != id + 2) return false;
        g[oc.x][oc.y] = id;
        for (Cell cell : cells) {
            int cnt = 1;
            for (int i = 0; i < 8; ++i) {
                int nx = cell.x + dx[i], ny = cell.y + dy[i];
                if (nx < 0 || ny < 0 || nx >= rows || ny >= cols) continue;

                if (g[nx][ny] == id) ++cnt;
            }
            if (cnt >= 3) {
                g[oc.x][oc.y] = tmp;
                return false;
            }
        }

        return true;
    }

    private boolean check_win(List<Cell> cells,  int id) {
        int[][] st = g.clone();
        max_dep = 0;
        for (Cell cell : cells) {
            int x = cell.getX(), y = cell.getY();
            if (x == 0 || y == 0) {
                g[x][y] = -1;
                dfs(cells, x, y, id, 1);
                g[x][y] = id;
            }
        }
        g = st;
        if (max_dep >= 6) return true;
        return false;
    }

    private void dfs(List<Cell> cells, int x, int y, int id, int cur_dep) {
        if (x == rows - 1 || y == cols - 1) {
            max_dep = Math.max(max_dep, cur_dep);
            return;
        }
        for (Cell cell : cells) {
            int nx = cell.getX(), ny = cell.getY();
            boolean f = true;
            if (g[nx][ny] != id || nx == 0 || ny == 0) f = false;
            if (x == nx) {
                for (int i = Math.min(y, ny) + 1; i < Math.max(y, ny); ++i)
                    if (g[x][i] == 5 - id) {
                        f = false; break;
                    }
            }
            else if (y == ny) {
                for (int i = Math.min(x, nx) + 1; i < Math.max(x, nx); ++i)
                    if (g[i][y] == 5 - id) {
                        f = false; break;
                    }
            }
            else if (Math.abs(x - nx) == Math.abs(y - ny)) {
                for (int i = 1; i < Math.abs(x - nx); ++i) {
                    int mx, my;
                    if (x > nx) mx = x - i;
                    else mx = x + i;
                    if (y > ny) my = y - i;
                    else my = y + i;
                    if (g[mx][my] == 5 - id) {
                        f = false; break;
                    }
                }
            } else f = false;
            if (f) {
                g[nx][ny] = -1;
                dfs(cells, nx, ny, id, cur_dep + 1);
                g[nx][ny] = id;
            }
        }
    }

    private boolean judge() {
        List<Cell> cellsA = playerA.getCells();
        List<Cell> cellsB = playerB.getCells();
        if (nextPlayer == playerA.getId()) {
            if (!check_valid(cellsA, 2)) return false;
            if (check_win(cellsA, 2)) {
                status = "finished";
                winner = "A";
            }
        }
        else if (nextPlayer == playerB.getId()) {
            if (!check_valid(cellsB, 3)) return false;
            if (check_win(cellsB, 3)) {
                status = "finished";
                winner = "B";
            }
        }
        return true;
    }

    private void sendAllMessage(String message) {
        WebSocketServer.users.get(playerA.getId()).sendMessage(message);
        WebSocketServer.users.get(playerB.getId()).sendMessage(message);
    }

    private void sendMove() {
        lock.lock();
        try {
            JSONObject resp = new JSONObject();
            resp.put("event", "move");
            resp.put("operate", JSON.toJSONString(nextStep));
            resp.put("next", nextPlayer.toString());
            sendAllMessage(resp.toJSONString());
            nextStep = null;
        } finally {
            lock.unlock();
        }
    }

    private void saveToDataBase() {
        Record record = new Record(
                null,
                playerA.getId(),
                playerB.getId(),
                playerA.getStepString(),
                playerB.getStepString(),
                getGString(),
                winner,
                new Date()
        );
        WebSocketServer.recordMapper.insert(record);
    }

    public void sendResult() {
        JSONObject resp = new JSONObject();
        resp.put("event", "result");
        resp.put("winner", this.winner);
        saveToDataBase();
        sendAllMessage(resp.toJSONString());
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; ++i) {
            if (getNextStep()) {
                if (!judge()) {
                    lock.lock();
                    if (nextPlayer == playerA.getId()) playerA.getCps().remove(playerA.getCps().size() - 1);
                    else if (nextPlayer == playerB.getId()) playerB.getCps().remove(playerB.getCps().size() - 1);
                    --i;
                    lock.unlock();
                    continue;
                }
                if (status.equals("playing")) {
                    lock.lock();
                    if (nextPlayer == playerA.getId() || i == 1) nextPlayer = playerB.getId();
                    else nextPlayer = playerA.getId();
                    sendMove();
                    lock.unlock();
                }
                else {
                    lock.lock();
                    sendMove();
                    sendResult();
                    lock.unlock();
                    break;
                }
            }
            else {
                status = "finished";
                lock.lock();
                try {
                    if (nextPlayer == playerA.getId()) {
                        winner = "B";
                    } else if (nextPlayer == playerB.getId()) {
                        winner = "A";
                    }
                } finally {
                    lock.unlock();
                }
            }
        }
        if (status.equals("playing")) {
            status = "finished";
            lock.lock();
            try {
                winner = "ALL";
            } finally {
                lock.unlock();
            }
        }
    }
}
