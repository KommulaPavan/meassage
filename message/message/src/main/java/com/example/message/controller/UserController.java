package com.example.message.controller;

import com.example.message.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    StringRedisTemplate redisTemplate;
    @PostMapping("/send")
    public ResponseEntity<?> getOtp(@RequestBody Map<String, String> req){
        String phoneNumber=req.get("phoneNumber");
        String res=userService.sendOtp(phoneNumber);
        return ResponseEntity.ok(Map.of("message",res,"status","Otp send sucess full"));

    }

    @PostMapping("/verfiy")
    public ResponseEntity<?>  getVerfiy(@RequestParam String phoneNumber,@RequestParam String otp){
        String response= String.valueOf(userService.verifyOtp(phoneNumber, otp));
        return ResponseEntity.ok(response);

    }

    @GetMapping("/test-redis")
    public ResponseEntity<?> testRedis() {
        try {
            String pong = redisTemplate.getConnectionFactory().getConnection().ping();
            if ("PONG".equals(pong)) {
                return ResponseEntity.ok("Redis connection successful");
            }
            return ResponseEntity.status(500).body("Unexpected Redis response");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Redis connection failed: " + e.getMessage());
        }
    }

    @GetMapping("/redis-save-test")
    public String redisSaveTest() {
        redisTemplate.opsForValue().set("hello", "world", 10, TimeUnit.MINUTES);
        return "saved";
    }


}
