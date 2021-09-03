package com.seamfix.bioweb.microservices.notification.payload;

import com.seamfix.bioweb.microservices.notification.listener.message.MessageBody;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PublishMessageResponse extends ResponseData{

    private MessageBody data;
    private String topic;
}
