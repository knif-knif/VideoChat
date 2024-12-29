package com.videochat.backend.service.impl.user.account;

import com.videochat.backend.service.impl.utils.UserDetailsImpl;
import com.videochat.backend.service.user.account.LoginService;
import com.videochat.backend.utils.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.videochat.backend.pojo.User;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {

    private final AuthenticationManager authenticationManager;

    public LoginServiceImpl(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Map<String, String> getToken(String username, String password) throws Exception {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Map<String, String> map = new HashMap<>();

        try {
            Authentication authenticate = authenticationManager.authenticate(authenticationToken);

            UserDetailsImpl loginUser = (UserDetailsImpl) authenticate.getPrincipal();
            User user = loginUser.getUser();
            String jwt = JwtUtil.createJWT(user.getId().toString());
            map.put("error_message", "success");
            map.put("token", jwt);
            return map;
        } catch (Exception e) {
            map.put("error_message", "error:" + e.getMessage());
            return map;
        }
    }
}
