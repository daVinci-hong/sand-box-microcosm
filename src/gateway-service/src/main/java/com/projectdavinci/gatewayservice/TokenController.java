package com.projectdavinci.gatewayservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// 僅用於本地開發與測試的臨時端點
@RestController
public class TokenController {

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/generate-token/subject/{subject}/roles/{roles}")
    public String generateToken(@PathVariable String subject, @PathVariable List<String> roles) {
        return jwtUtil.generateToken(subject, roles);
    }
}