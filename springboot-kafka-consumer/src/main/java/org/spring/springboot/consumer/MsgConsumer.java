package org.spring.springboot.consumer;

import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author Zephery
 * @since 2018/1/5 10:40
 */
@Component
public class MsgConsumer {
    //logger
    private static final Logger logger = LoggerFactory.getLogger(MsgConsumer.class);
    private static final JsonParser parser = new JsonParser();

    @KafkaListener(topics = {"nginx-access-log"})
    public void processMessage(String content) {
        System.out.println(parser.parse(content));
    }
}