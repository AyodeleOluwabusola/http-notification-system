package com.seamfix.bioweb.microservices.notification.payload;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscribeRequest {

    private String url;
}
