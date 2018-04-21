package com.myboot.service;

import com.alibaba.otter.canal.protocol.Message;
import org.elasticsearch.client.Client;

import java.util.Map;

//
//import java.util.List;
//
public interface ElasticsearchService {
//
//    void insertById(String index, String type, List<ElasticsearchMetadata.EsRowData> esRowDataList);
//
//    void update(String index, String type, List<ElasticsearchMetadata.EsRowData> esRowDataList);
//
//    void deleteById(String index, String type, List<ElasticsearchMetadata.EsRowData> esRowDataList);
//
//    void close();
//


    void insertByIndex(Message message);

}
