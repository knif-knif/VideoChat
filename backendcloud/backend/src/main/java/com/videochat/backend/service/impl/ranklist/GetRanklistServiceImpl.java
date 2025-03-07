package com.videochat.backend.service.impl.ranklist;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.videochat.backend.mapper.UserMapper;
import com.videochat.backend.pojo.User;
import com.videochat.backend.service.ranklist.GetRanklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetRanklistServiceImpl implements GetRanklistService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public JSONObject getList(Integer page) {
        IPage<User> userIpage = new Page<>(page, 10);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("rating");
        List<User> users = userMapper.selectPage(userIpage, queryWrapper).getRecords();
        JSONObject resp = new JSONObject();
        for (User user : users) user.setPassword("12345");
        resp.put("users", users);
        resp.put("users_count", userMapper.selectCount(null));
        return resp;
    }
}
