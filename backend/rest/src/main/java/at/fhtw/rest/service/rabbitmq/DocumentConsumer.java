/*package at.fhtw.rest.service.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class DocumentConsumer {
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveDocumentEvent(String documentMessage){
        System.out.println("Received message: " + documentMessage);
    }
}
*/