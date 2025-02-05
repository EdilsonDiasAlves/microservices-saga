package br.com.microservices.choreography.productvalidationservice.core.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendEvent(String event, String topic) {
        try {
            log.info("Sending event to topic {} with data {}", topic, event);
            kafkaTemplate.send(topic, event);
        } catch (Exception ex) {
            log.error("Error trying to send data to topic {} with data {}", topic, event, ex);
        }
    }

}
