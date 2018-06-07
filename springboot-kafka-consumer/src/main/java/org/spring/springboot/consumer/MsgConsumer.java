package org.spring.springboot.consumer;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.RestClient;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
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
    RestClient restClient = RestClient.builder(
            new HttpHost("119.29.188.224", 9200, "http")).build();

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
    @KafkaListener(topics = {"newbloglogs"})
    public void logs(String content) throws IOException {
        JsonElement element = parser.parse(content);
        logger.info(content);
        HttpEntity entity = new NStringEntity(element.toString(), ContentType.APPLICATION_JSON);
        String id = DateTime.now().toString("yyyyMMddHHmmss") + RandomStringUtils.randomAlphanumeric(10);
        restClient.performRequest("PUT", "/newbloglogs_write/blog/" + id, Collections.emptyMap(), entity);
    }

}