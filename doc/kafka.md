# Spring Boot与Kafka
**开启外网访问**
只需要在config/server.properties里加入：
```html
advertised.host.name=119.29.188.224
```
改变默认端口：
```html
advertised.host.port=9200
```
启动步骤：
（1）ZooKeeper启动
bin/zookeeper-server-start.sh config/zookeeper.properties
（2）启动Kafka
nohup bin/kafka-server-start.sh config/server.properties  &
（3）创建一个topic
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
查看topic数量
bin/kafka-topics.sh --list --zookeeper localhost:2181
（4）生产者发送消息
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test
（5）消费者接收消息
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning

# 多模块的Spring Boot与Kafka
（1）在父pom.xml中添加：
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.data</groupId>
            <artifactId>spring-data-releasetrain</artifactId>
            <version>Fowler-SR2</version>
            <scope>import</scope>
            <type>pom</type>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>1.5.9.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```
（2）生产者模块：
```xml
<parent>
    <artifactId>micro-service</artifactId>
    <groupId>micro-service</groupId>
    <version>1.0-SNAPSHOT</version>
</parent>
```
配置文件：
```html
# 指定kafka地址和端口（必填）
spring.kafka.bootstrap-servers=119.29.188.224:9092
# 指定默认消费者group id（必填）
spring.kafka.consumer.group-id=myGroup
# 指定默认topic id（可选，可在代码中添加）
spring.kafka.template.default-topic=nginx-access-log
# 指定listener 容器中的线程数，用于提高并发量（可选）
spring.kafka.listener.concurrency=3
# 每次批量发送消息的数量（可选）
spring.kafka.producer.batch-size=1000
```


（3）发送消息：
```java
@RestController
public class MsgProducer {
    //logger
    private static final Logger logger = LoggerFactory.getLogger(MsgProducer.class);
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @RequestMapping("/send")
    public void send(HttpServletRequest request) {
        String word = request.getParameter("word");
        kafkaTemplate.send("nginx-access-log", word);
    }

    @KafkaListener(topics = "myTopic")
    public void listen(ConsumerRecord<?, ?> cr) throws Exception {
        logger.info(cr.toString());
    }
}
```
（4）在消费者模块中添加：
```xml
    <parent>
        <artifactId>micro-service</artifactId>
        <groupId>micro-service</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
```

（5）接收消息
```java
@Component
public class MsgConsumer {
    @KafkaListener(topics = {"nginx-access-log"})
    public void processMessage(String content) {
        System.out.println(content);
    }
}
```

（6）测试
在浏览器上：
```html
http://localhost:8081/send.do?word=9y787y87y8gg
```
观察消费者：
```html
9y787y87y8gg
```




















# 错误记录
```html
Error starting ApplicationContext. To display the auto-configuration report re-run your application with 'debug' enabled.
2018-01-05 11:10:47.947 ERROR 251848 --- [           main] o.s.boot.SpringApplication               : Application startup failed

org.springframework.context.ApplicationContextException: Unable to start embedded container; nested exception is org.springframework.boot.context.embedded.EmbeddedServletContainerException: Unable to start embedded Tomcat
	at org.springframework.boot.context.embedded.EmbeddedWebApplicationContext.onRefresh(EmbeddedWebApplicationContext.java:137) ~[spring-boot-1.5.9.RELEASE.jar:1.5.9.RELEASE]
	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:537) ~[spring-context-4.3.11.RELEASE.jar:4.3.11.RELEASE]
	at org.springframework.boot.context.embedded.EmbeddedWebApplicationContext.refresh(EmbeddedWebApplicationContext.java:122) ~[spring-boot-1.5.9.RELEASE.jar:1.5.9.RELEASE]
	at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:693) [spring-boot-1.5.9.RELEASE.jar:1.5.9.RELEASE]
	at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:360) [spring-boot-1.5.9.RELEASE.jar:1.5.9.RELEASE]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:303) [spring-boot-1.5.9.RELEASE.jar:1.5.9.RELEASE]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1118) [spring-boot-1.5.9.RELEASE.jar:1.5.9.RELEASE]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1107) [spring-boot-1.5.9.RELEASE.jar:1.5.9.RELEASE]
```






