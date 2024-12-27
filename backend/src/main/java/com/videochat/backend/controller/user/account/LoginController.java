package com.videochat.backend.controller.user.account;

import com.videochat.backend.service.impl.user.account.LoginServiceImpl;
import com.videochat.backend.service.user.account.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class LoginController {
    @Autowired
    private LoginServiceImpl loginService;

    @PostMapping("/user/account/token/")
    public Map<String, String> getToken(@RequestParam Map<String, String> req) throws Exception {
        String username = req.get("username");
        String password = req.get("password");
        return loginService.getToken(username, password);
    }
}
