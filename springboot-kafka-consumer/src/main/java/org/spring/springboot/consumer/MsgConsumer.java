package org.spring.springboot.consumer;

import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zephery
 * @since 2018/1/5 10:40
 */
@Component
public class MsgConsumer {
    //logger
    private static final Logger logger = LoggerFactory.getLogger(MsgConsumer.class);
    private static final JsonParser parser = new JsonParser();
    private static final ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

    //
//    @KafkaListener(topics = {"nginx-access-log"})
//    public void processMessage(String content) {
//        JsonObject object = parser.parse(content).getAsJsonObject();
//        String upstreamhost = object.get("upstreamhost").getAsString();
//        map.put(upstreamhost, map.get("upstreamhost") == null ? 1 : map.get("upstreamhost") + 1);
//        for (Map.Entry<String, Integer> entry : map.entrySet()) {
//            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
//        }
//    }
//
    @KafkaListener(topics = {"logs"})
    public void logs(String content) {
        logger.info(content);
    }
}