package com.videochat.backend.consumer.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private Integer id;
    private List<ChessOperate> cps;

    public List<Cell> getCells() {
        List<Cell> res = new ArrayList<>();
        for (ChessOperate op : cps) {
            if (op.getOp() == 0) {
                res.add(new Cell(op.getX(), op.getY()));
            }
            else {
                res.removeIf(s -> s.getX() == op.getX() && s.getY() == op.getY());
                res.add(new Cell(op.getNx(), op.getNy()));
            }
        }
        return res;
    }

    public String getStepString() {
        JSONObject res = new JSONObject();
        for (int i = 0; i < cps.size(); ++i) {
            res.put(String.valueOf(i), JSON.toJSONString(cps.get(i)));
        }
        return res.toJSONString();
    }
}
