package com.myboot;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.Message;
import com.myboot.config.Config;
import com.myboot.service.ElasticsearchService;
import com.myboot.service.Impl.NewESServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author wenzhihuai
 * @since 2018/4/11 14:44
 */
@Slf4j
public class Launcher {
    private static final ElasticsearchService elasticsearchService = new NewESServiceImpl();

    private static void init() {
        String str = Config.getProperty("canal.cluster.url");
//        CanalConnector connector = CanalConnectors
//                .newClusterConnector(str,
//                        "example",
//                        "",
//                        "");
        CanalConnector connector = CanalConnectors.newSingleConnector(
                new InetSocketAddress(Config.getProperty("canal.ip"), Config.getIntProperty("canal.port")),
                Config.getProperty("canal.destination"),
                Config.getProperty("canal.username"),
                Config.getProperty("canal.password"));
        int batchSize = 500;
        try {
            connector.connect();
            connector.subscribe(".\\..*");
            connector.rollback();
            int retryCount = 0;
            while (true) {
                Message message = connector.getWithoutAck(batchSize);
                long batchId = message.getId();
                int size = message.getEntries().size();
                if (batchId != -1 || size != 0) {
                    //TODO
                    try {
                        elasticsearchService.insertByIndex(message);
                        connector.ack(batchId); // 提交确认
                    } catch (Exception e) {
                        //重试
                        if (retryCount < 4) {
                            log.error("第" + String.valueOf(retryCount) + "次出现错误");
                            log.error("出错，回滚数据:" + String.valueOf(batchId), e);
                            connector.rollback(batchId); // 处理失败, 回滚数据
                            retryCount++;
                        } else {
                            log.error("重试超过4次，丢弃数据");
                            connector.ack(batchId);
                            retryCount = 0;
                        }
                    }
                }
            }
        } finally {
            connector.disconnect();
            log.info("连接已断开");
        }
    }

    public static void main(String[] args) {
        init();
    }
}