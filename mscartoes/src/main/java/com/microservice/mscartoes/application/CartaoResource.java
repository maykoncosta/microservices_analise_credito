package com.microservice.mscartoes.application;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("cartoes")
public class CartaoResource {

    @GetMapping
    public String status(){
        return "OK";
    }
}
