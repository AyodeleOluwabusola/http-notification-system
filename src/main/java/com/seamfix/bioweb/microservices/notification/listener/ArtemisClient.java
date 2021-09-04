package com.seamfix.bioweb.microservices.notification.listener;

import com.seamfix.bioweb.microservices.notification.listener.jms.Notification;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Queue;
import javax.jms.Session;

@Singleton
@Slf4j
public class ArtemisClient {

    private Connection connection;

    private String queueName = "";

    @PostConstruct
    public void init () throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("admin", "admin", "tcp://localhost:61616");
        this.queueName = "jms.queue.NotificationSystemQueue";
        connection = connectionFactory.createConnection();
        connection.setExceptionListener(new ExceptionListener() {
            @Override
            public void onException(JMSException exception) {
                log.error("ExceptionListener triggered", exception);
                try {
                    init();
                } catch (JMSException e) {
                    log.error("Error re-establishing connection", e);
                }
            }
        });
    }

    @PreDestroy
    public void destroy () {
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException e) {
                log.error("CloseJMSError", e);
            }
        }
    }

    @SuppressWarnings({"PMD.AvoidCatchingGenericException"})
    public void pushPayloadBackup(@Observes(notifyObserver = Reception.ALWAYS) Notification message) {

        try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);) {
            Queue queue = session.createQueue(queueName);
            MapMessage mapMessage = session.createMapMessage();

            try {
                mapMessage.setString("topic", message.getTopic());
                mapMessage.setString("data", message.getData());

                session.createProducer(queue).send(mapMessage);
                log.info("Push successful!");
            } catch (JMSException e) {
                log.error("Push not successful:", e);
            }
        } catch (Exception e) {
            log.error("ArtemisClientException:", e);
        }
    }
}