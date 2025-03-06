package com.videochat.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import com.videochat.backend.pojo.Record;

@Repository
@Mapper
public interface RecordMapper extends BaseMapper<Record> {

}
