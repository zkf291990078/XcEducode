package com.xuecheng.test.freemarker.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/freemarker")
public class CreateFreeMarkerController {
    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/course")
    public String getFreeMarker(Map<String, Object> map) {
        String dataUrl = "http://localhost:31200/course/courseview/4028858162e0bc0a0162e0bfdf1a0000";
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        Map body = forEntity.getBody();
        map.putAll(body);
        return "course";
    }


}
