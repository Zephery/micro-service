import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Zephery
 * Time: 2018/4/7 15:14
 * Description:
 */
public class TestAPI {

    public static void main(String args[]) throws IOException {
        // 创建链接
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress("47.95.10.139",
                11111), "example", "", "");
        int batchSize = 1000;
        try {
            connector.connect();
            connector.subscribe(".*\\..*");
            connector.rollback();
            while (true) {
                Message message = connector.getWithoutAck(batchSize); // 获取指定数量的数据
                long batchId = message.getId();
                int size = message.getEntries().size();
//                if (batchId == -1 || size == 0) {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                    }
//                } else {
                // System.out.printf("message[batchId=%s,size=%s] \n", batchId, size);
                printEntry(message.getEntries());
//                }

                connector.ack(batchId); // 提交确认
                // connector.rollback(batchId); // 处理失败, 回滚数据
            }

        } finally {
            connector.disconnect();
        }
    }


    private static void printEntry(List<CanalEntry.Entry> entrys) throws IOException {
        for (CanalEntry.Entry entry : entrys) {
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                continue;
            }

            CanalEntry.RowChange rowChage = null;
            try {
                rowChage = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(),
                        e);
            }
            CanalEntry.EventType eventType = rowChage.getEventType();
            System.out.println(String.format("================>>binlog[%s:%s] , name[%s,%s] , eventType : %s",
                    entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
                    entry.getHeader().getSchemaName(), entry.getHeader().getTableName(),
                    eventType));

            for (CanalEntry.RowData rowData : rowChage.getRowDatasList()) {
                if (eventType == CanalEntry.EventType.DELETE) {
                    printColumn(rowData.getBeforeColumnsList());
                } else if (eventType == CanalEntry.EventType.INSERT) {
                    RestClient restClient = RestClient.builder(new HttpHost("119.29.188.224", 9200, "http")).build();
                    String schemeTable = entry.getHeader().getSchemaName() + entry.getHeader().getTableName();
                    Gson gson = new Gson();
                    Map<String, String> map = new HashMap<>();
                    rowData.getAfterColumnsList().forEach(column -> {
                        map.put(column.getName(), column.getValue());
                    });

                    String str = gson.toJson(map);
                    HttpEntity entity = new NStringEntity(
                            "{\"referer\":\"\",\"ip\":\"119.29.188.224\",\"response_time\":\"1\",\"ip_time\":\"2018-04-11 22:57:50\",\"uri\":\"/\",\"visit_num\":\"1\",\"sid\":\"6bd1596b-2d5e-4ab7-a3a3-f6515dc40838\"}", ContentType.APPLICATION_JSON);
                    System.out.println(schemeTable);
                    restClient.performRequest("PUT", "table" + schemeTable, Collections.emptyMap(), entity);
                    printColumn(rowData.getAfterColumnsList());
                } else {
                    System.out.println("------->>before");
                    printColumn(rowData.getBeforeColumnsList());
                    System.out.println("------->>after");
                    printColumn(rowData.getAfterColumnsList());
                }
            }
        }
    }

    private static void printColumn(List<CanalEntry.Column> columns) {
        for (CanalEntry.Column column : columns) {
            System.out.println(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
        }
    }
}