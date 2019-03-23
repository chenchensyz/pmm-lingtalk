package cn.com.cybertech.controller;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping()
    public Map<String, Object> login(String username, String password) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("code", 200);
        map.put("username", username);
        map.put("password", password);
        return map;
    }
}
