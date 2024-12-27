package com.videochat.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.videochat.backend.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
