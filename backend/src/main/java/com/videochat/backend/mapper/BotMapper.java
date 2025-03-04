package com.videochat.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.videochat.backend.pojo.Bot;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface BotMapper extends BaseMapper<Bot> {
}
