package com.seamfix.bioweb.microservices.notification.listener.jms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Notification {

    private String topic;
    private String data;
}
