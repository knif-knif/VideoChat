package com.videochat.backend.service.impl.user.bot;

import com.videochat.backend.mapper.BotMapper;
import com.videochat.backend.pojo.Bot;
import com.videochat.backend.pojo.User;
import com.videochat.backend.service.impl.utils.UserDetailsImpl;
import com.videochat.backend.service.user.bot.AddService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AddServiceImpl implements AddService {
    @Autowired
    private BotMapper botMapper;
    @Override
    public Map<String, String> add(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();
        String title = data.get("title");
        String description = data.get("description");
        String content = data.get("content");
        Map<String, String> map = new HashMap<>();
        if (title == null || title.length() == 0 || title.length() > 100) {
            map.put("error_message", "标题不合法！");
            return map;
        }
        if (description == null || description.length() == 0) {
            description = "比得过我么？";
        }
        if (description != null && description.length() > 300) {
            map.put("error_message", "描述不合法！");
            return map;
        }
        if (content == null || content.length() == 0 || content.length() > 10000) {
            map.put("error_message", "代码不合法！");
            return map;
        }
        Date now = new Date();
        Bot bot = new Bot(null, user.getId(), title, description, content, 1500, now, now);
        botMapper.insert(bot);
        map.put("error_message", "success");
        return map;
    }
}
