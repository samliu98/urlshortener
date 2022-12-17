package com.scltech.urlshortner.UrlshortnerResource;

import java.nio.charset.StandardCharsets;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.hash.Hashing;

@RequestMapping("/rest/url")
@RestController
public class UrlshortnerResource {

    @Autowired
    StringRedisTemplate redisTemplate;

    @GetMapping("/{id}")
    public String getUrl(@PathVariable String id){
        String url = redisTemplate.opsForValue().get(id);
        System.out.println("Url Retrieved: " + url);

        if(url == null){
            throw new RuntimeException("There is no shorten Url for: " + id);
        }
        return url;
    }

    @PostMapping
    public String create(@RequestBody String url){
        UrlValidator urlValidator = new UrlValidator(
            new String[]{"http", "https"}
        );
        if(urlValidator.isValid(url)){
            String id = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString();
            
            System.out.println("Url Id Generated: " + id);
            redisTemplate.opsForValue().set(id, url);
            return id;
        }
        throw new RuntimeException("Url invalid: " + url);
    }
}
