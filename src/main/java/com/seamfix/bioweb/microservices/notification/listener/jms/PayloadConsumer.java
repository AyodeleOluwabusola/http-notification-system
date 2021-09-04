/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.seamfix.bioweb.microservices.notification.listener.jms;

import org.jboss.ejb3.annotation.ResourceAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;


@ResourceAdapter("remoteAmqConnectionFactory")
@MessageDriven(name = "NotificationSystemListener", activationConfig = {
        @ActivationConfigProperty(propertyName = "useJNDI", propertyValue = "false"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "NotificationSystemQueue"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")})
public class PayloadConsumer implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(PayloadConsumer.class);

    @Resource
    private MessageDrivenContext messageDrivenContext;


    @Override
    public void onMessage(Message message) {
        log.info("Message Received.");
        MapMessage mapMessage = (MapMessage) message;
        try {
            System.out.println("Message Received From Topic : " + mapMessage.getString("topic") + " Message Details: " + mapMessage.getString("data"));

            // Do Whatever here
        } catch (JMSException e) {
            log.error("JMSConsumerException:", e);
            messageDrivenContext.setRollbackOnly();
        }
    }


}
