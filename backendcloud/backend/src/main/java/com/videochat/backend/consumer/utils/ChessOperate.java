package com.videochat.backend.consumer.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChessOperate {
    public Integer id, op;
    public Integer x, y, nx, ny;
}
