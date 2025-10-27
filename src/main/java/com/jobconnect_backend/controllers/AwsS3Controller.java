package com.jobconnect_backend.controllers;

import com.jobconnect_backend.config.AwsS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/aws/s3")
public class AwsS3Controller {
    @Autowired
    private AwsS3Service awsS3Service;

//  API sinh URL tạm thời (Pre-signed URL) cho phép client (frontend hoặc Postman)
//  upload file trực tiếp lên Amazon S3 mà không cần gửi file qua server backend
    @GetMapping("/presigned-url")
    public ResponseEntity<Map<String, String>> getPreSignedUrl(@RequestParam String fileName, @RequestParam String contentType) {
        String preSignedUrl = awsS3Service.generatePreSignedUrl(fileName, contentType);

        Map<String, String> response = new HashMap<>();
        response.put("url", preSignedUrl);
        return ResponseEntity.ok(response);
    }
}
