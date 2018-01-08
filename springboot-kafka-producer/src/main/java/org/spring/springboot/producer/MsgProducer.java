package org.spring.springboot.producer;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Zephery
 * @since 2018/1/5 10:36
 */
@Component
public class MsgProducer {
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    public void send() {
        kafkaTemplate.send("my-replicated-topic", "xiaojf");
        kafkaTemplate.send("my-replicated-topic", "xiaojf");

        kafkaTemplate.metrics();

        kafkaTemplate.execute(producer -> {
            //这里可以编写kafka原生的api操作
            return null;
        });

        //消息发送的监听器，用于回调返回信息
        kafkaTemplate.setProducerListener(new ProducerListener<String, String>() {
            @Override
            public void onSuccess(String topic, Integer partition, String key, String value, RecordMetadata recordMetadata) {

            }

            @Override
            public void onError(String topic, Integer partition, String key, String value, Exception exception) {

            }

            @Override
            public boolean isInterestedInSuccess() {
                return false;
            }
        });
    }
}