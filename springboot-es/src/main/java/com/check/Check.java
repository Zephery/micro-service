package com.check;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.cluster.node.stats.NodeStats;
import org.elasticsearch.action.admin.cluster.node.stats.NodesStatsRequest;
import org.elasticsearch.action.admin.cluster.node.stats.NodesStatsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Zephery
 * Time: 2018/5/6 22:16
 * Description:
 */
@Slf4j
public class Check {

    private static void test() throws UnknownHostException, InterruptedException {
        Client client = new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("119.29.188.224"), 9300));
        NodesStatsRequest nodesStatsRequest = new NodesStatsRequest().jvm(true).os(true);
        while (true) {
            NodesStatsResponse nodesStatsResponse = client.admin().cluster().nodesStats(nodesStatsRequest).actionGet();
            List<NodeStats> statsResponseNodes = nodesStatsResponse.getNodes();
            for (NodeStats stats : statsResponseNodes) {
                System.out.print(stats.getHostname() + " ");
                System.out.print("CPU:" + stats.getOs().getCpu().getPercent() + " ");
                System.out.println("HeapUse:" + stats.getJvm().getMem().getHeapUsedPercent() + " ");
            }
            System.out.println("----------");
            TimeUnit.SECONDS.sleep(10);
        }
    }

    public static void main(String[] args) throws Exception {
        test();
    }
}