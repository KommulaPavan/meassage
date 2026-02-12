package com.example.message.service;

import com.vonage.client.VonageClient;
import com.vonage.client.sms.MessageStatus;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.messages.TextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    private static final String FAST2SMS_URL = "https://www.fast2sms.com/dev/bulkV2";
    private static final String API_KEY = "6GafhdytFYKC2x7p1XoRIZicA3PmzJbWLEON8qwMrklTBD5gQHyCeWpnkL5JI6HfgqFVOiPuBMUd24oA";

    @Autowired
    OtpUtil otpUtil;

    @Autowired
    StringRedisTemplate redisTemplate;

    private final VonageClient vonageClient;

    public UserService(
            @Value("${nexmo.api.key}") String apiKey,
            @Value("${nexmo.api.secret}") String apiSecret
    ) {
        this.vonageClient = VonageClient.builder()
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .build();
    }

//    public String sendOtp(String phoneNumber) {
//        String from = "YourApp";  // Sender ID (alphanumeric, max 11 chars)
//        String Otp=otpUtil.generateOtp();
//        String messageText = "Your OTP is: " + Otp;
//
//        TextMessage message = new TextMessage(from, phoneNumber, messageText);
//
//        String cleanPhoneNumber = phoneNumber.replaceAll("\\s+", "");
//        redisTemplate.opsForValue().set("otp:" + cleanPhoneNumber, Otp, 5, TimeUnit.MINUTES);
//
//
//        System.out.println("OTP saved with key: otp:" + phoneNumber + " value: " + Otp);
//
//
//
//        try {
//            SmsSubmissionResponse response = vonageClient.getSmsClient().submitMessage(message);
//            if (response.getMessages().get(0).getStatus() == MessageStatus.OK) {
//                return "OTP sent successfully";
//            } else {
//                return "Failed to send OTP: " + response.getMessages().get(0).getErrorText();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Error sending OTP: " + e.getMessage();
//        }
//    }

    public String sendOtp(String phoneNumber) {

        String from = "YourApp";
        String otp = otpUtil.generateOtp();

        // clean number (VERY important)
        String cleanPhone = phoneNumber.replaceAll("\\s+", "");

        String key = "otp:" + cleanPhone;

        // save OTP in redis
        redisTemplate.opsForValue().set(key, otp, 1, TimeUnit.HOURS);

        System.out.println("OTP stored in Redis DB 0 with key = " + key);


        // LOG (correct key)
        System.out.println("OTP saved in Redis: " + key + " = " + otp);

        // send sms
        TextMessage message = new TextMessage(from, cleanPhone, "Your OTP is: " + otp);

        try {
            SmsSubmissionResponse response =
                    vonageClient.getSmsClient().submitMessage(message);

            if (response.getMessages().get(0).getStatus() == MessageStatus.OK) {
                return "OTP sent successfully";
            } else {
                return "Failed to send OTP: " +
                        response.getMessages().get(0).getErrorText();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error sending OTP: " + e.getMessage();
        }
    }


//    public boolean verfiyOtp(String phoneNumber, String intOtp){
//        String validOtp=redisTemplate.opsForValue().get("otp:"+phoneNumber);
//        if(validOtp==null){
//            return false;
//        }
//        if (validOtp.equals(intOtp)){
//            redisTemplate.delete(phoneNumber);
//            return true;
//        }
//        return false;
//    }

    public boolean verifyOtp(String phoneNumber, String intOtp) {
        String cleanPhone = phoneNumber.replaceAll("\\s+", "");
        String key = "otp:" + cleanPhone;

        String validOtp = redisTemplate.opsForValue().get(key);

        System.out.println("Verifying OTP for key: " + key);
        System.out.println("Stored OTP: " + validOtp);
        System.out.println("Received OTP: " + intOtp);

        if (validOtp == null) {
            System.out.println("OTP expired or not found.");
            return false;
        }

        if (validOtp.equals(intOtp)) {
            redisTemplate.delete(key);
            System.out.println("OTP matched and deleted.");
            return true;
        }

        System.out.println("OTP mismatch.");
        return false;
    }

}