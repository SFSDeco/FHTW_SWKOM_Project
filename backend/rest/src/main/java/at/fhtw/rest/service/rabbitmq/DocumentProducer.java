package at.fhtw.rest.service.rabbitmq;

import at.fhtw.rest.service.dtos.DocumentDto;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class DocumentProducer {

    private static final Logger log = LoggerFactory.getLogger(DocumentProducer.class);
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public DocumentProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendDocumentEvent(DocumentDto documentDto) {
        try {
            log.info("Sending DocumentDTO to RabbitMQ: {}", documentDto);
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    RabbitMQConfig.ROUTING_KEY,
                    documentDto
            );
            log.info("DocumentDTO sent successfully.");
        } catch (Exception e) {
            log.error("Failed to send DocumentDTO to RabbitMQ", e);
            throw new RuntimeException("Failed to send DocumentDTO to RabbitMQ", e);
        }
    }
}
