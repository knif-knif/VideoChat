package com.videochat.backend.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.videochat.backend.mapper.UserMapper;
import com.videochat.backend.pojo.User;
import com.videochat.backend.service.user.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public Map<String, String> register(String username, String password) {
        Map<String, String> map = new HashMap<>();
        if (username == null) {
            map.put("error_message", "用户名为空！");
            return map;
        }
        if (password == null) {
            map.put("error_message", "密码为空！");
            return map;
        }
        username = username.trim();
        if (username.length() < 3 || username.length() > 80) {
            map.put("error_message", "用户名长度不合法！");
            return map;
        }
        if (password.length() < 6 || password.length() > 80) {
            map.put("error_message", "密码长度不合法！");
            return map;
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        List<User> users = userMapper.selectList(queryWrapper);
        if (users.size() > 0) {
            map.put("error_message", "用户已存在！");
            return map;
        }
        String encodePassword = passwordEncoder.encode(password);
        String photo = "https://cdn.acwing.com/media/user/profile/photo/58358_lg_be31745d7c.jpg";
        User user = new User(null, username, encodePassword, photo);
        userMapper.insert(user);
        map.put("error_message", "success");
        return map;
    }
}
