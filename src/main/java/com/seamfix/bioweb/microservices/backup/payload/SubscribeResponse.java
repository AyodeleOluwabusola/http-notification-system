package com.seamfix.bioweb.microservices.backup.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscribeResponse{

    private String url;
    private String topic;
}
