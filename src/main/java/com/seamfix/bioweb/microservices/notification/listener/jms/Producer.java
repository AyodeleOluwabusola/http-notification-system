package com.seamfix.bioweb.microservices.notification.listener.jms;

import com.seamfix.bioweb.microservices.notification.listener.message.MessageBody;
import com.seamfix.bioweb.microservices.notification.payload.PublishMessageResponse;
import com.seamfix.bioweb.microservices.notification.payload.SubscribeRequest;
import com.seamfix.bioweb.microservices.notification.payload.SubscribeResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.enterprise.event.Event;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;

@Slf4j
public class Producer {

    private final String topic;
    private Event<Notification> event;
    private SubscribeRequest request;

    // Create a ConnectionFactory
    private static final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("admin", "admin", "tcp://localhost:61616");

    public Producer(String topic, Event event){
        this.topic = topic;
        this.event = event;
    }

    public Producer(SubscribeRequest request, String topic){
        this.topic = topic;
        this.request = request;
    }

    public SubscribeResponse createSubscription(){

        SubscribeResponse response = new SubscribeResponse();
        try {
            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue(topic);

            // Create a MessageProducer from the Session to the Topic or Queue
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);

            session.close();
            connection.close();

            response.setUrl(request.getUrl());
            response.setTopic(topic);
        }catch (JMSException e) {
            log.error("Error occurred while creating Subscription/Topic: ", e);
        }

        return response;
    }

    public PublishMessageResponse sendMessageToConsumers(MessageBody message){

        PublishMessageResponse response = new PublishMessageResponse();
        response.setCode(-1);
        response.setDescription("Error occurred while sending message");
        try {
            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue(topic);

            // Create a MessageProducer from the Session to the Topic or Queue
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);

            // Create message
            MapMessage mapMessage = session.createMapMessage();
            mapMessage.setString("topic", topic);
            mapMessage.setString("data", message.getMsg());

            // Tell the producer to send the message
            producer.send(mapMessage);
            event.fire(push(topic, message.getMsg())); //Consumer listens for this event/notification

            // Clean up
            session.close();
            connection.close();

            response.setCode(0);
            response.setDescription("Message successfully published!");
            response.setTopic(topic);
            MessageBody messageResponse = new MessageBody();
            messageResponse.setMsg(message.getMsg());
            response.setData(messageResponse);
        }catch (JMSException e) {
            log.error("Error occurred while sending Message/Notification : ", e);
        }
        return response;
    }

    @SuppressWarnings("PMD.UseObjectForClearerAPI")
    public Notification push(String topic, String data) {
        Notification notification = new Notification();
        notification.setData(data);
        notification.setTopic(topic);
        return notification;
    }

}
