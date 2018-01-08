package org.spring.springboot.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author Zephery
 * @since 2018/1/5 10:40
 */
@Component
public class MsgConsumer {
    @KafkaListener(topics = {"my-replicated-topic", "my-replicated-topic2"})
    public void processMessage(String content) {
        System.out.println(content);
    }
}