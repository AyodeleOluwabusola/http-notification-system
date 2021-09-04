import com.seamfix.bioweb.microservices.notification.listener.jms.Producer;
import com.seamfix.bioweb.microservices.notification.listener.message.MessageBody;
import com.seamfix.bioweb.microservices.notification.payload.PublishMessageResponse;
import com.seamfix.bioweb.microservices.notification.payload.SubscribeRequest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ProducerTest {

    private static Producer producer;
    @BeforeClass
    public static void setUpBeforeClass(){
        SubscribeRequest request = new SubscribeRequest();
        producer = new Producer(request, "topic1");
        producer.createSubscription();
    }


    @Test
    public void testMessageSendingResponse_NotNull() {

        MessageBody messageBody = new MessageBody();
        messageBody.setMsg("hello");
        PublishMessageResponse messageResponse = producer.sendMessageToConsumers(messageBody);
        Assert.assertNotNull(messageResponse);
    }

}
