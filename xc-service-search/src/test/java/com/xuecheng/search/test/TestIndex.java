package com.xuecheng.search.test;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestIndex {

    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private RestClient restClient;

    @Test
    public void testCreateIndex() throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("xc_course");
        //设置索引参数
        createIndexRequest.settings(Settings.builder().put("number_of_shards", 1).put("number_of_replicas", 0));
        createIndexRequest.mapping("doc", " {\n" + " \t\"properties\": {\n" + " \"name\": {\n" + " \"type\": \"text\",\n" + " \"analyzer\":\"ik_max_word\",\n" + " \"search_analyzer\":\"ik_smart\"\n" + " },\n" + " \"description\": {\n" + " \"type\": \"text\",\n" + " \"analyzer\":\"ik_max_word\",\n" + " \"search_analyzer\":\"ik_smart\"\n" + " },\n" + " \"studymodel\": {\n" + " \"type\": \"keyword\"\n" + " },\n" + " \"price\": {\n" + " \"type\": \"float\"\n" + " }\n" + " }\n" + "}", XContentType.JSON);
        //创建索引操作客户端
        IndicesClient indices = client.indices();
        CreateIndexResponse createIndexResponse = indices.create(createIndexRequest);
        System.out.println(createIndexResponse.isShardsAcknowledged());
    }
}
