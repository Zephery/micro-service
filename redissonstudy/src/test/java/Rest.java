import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: Zephery
 * Time: 2018/4/10 22:57
 * Description:
 */
public class Rest {

    private static RestClient restClient;

    @Before
    public void getRest() {
        restClient = RestClient.builder(new HttpHost("119.29.188.224", 9200, "http")).build();
    }


    /**
     * 查看api信息
     *
     * @throws Exception
     */
    @Test
    public void CatApi() throws Exception {
        String method = "GET";
        String endpoint = "/_cat";
        Response response = restClient.performRequest(method, endpoint);
        System.out.println(EntityUtils.toString(response.getEntity()));
    }

    /**
     * 创建索引
     *
     * @throws Exception
     */
    @Test
    public void CreateIndex() throws Exception {
        String method = "PUT";
        String endpoint = "/test-index";
        Response response = restClient.performRequest(method, endpoint);
        System.out.println(EntityUtils.toString(response.getEntity()));
    }

    /**
     * 创建文档
     *
     * @throws Exception
     */
    @Test
    public void CreateDocument() throws Exception {

        String method = "PUT";
        String endpoint = "/test-index/test/2";
        HttpEntity entity = new NStringEntity(
                "{\"area\":\"广州\",\"referer\":\"\",\"ip\":\"119.29.188.224\",\"response_time\":\"1\",\"id\":\"988012778\",\"ip_time\":\"2018-04-11 22:57:50\",\"uri\":\"/\",\"visit_num\":\"1\",\"sid\":\"6bd1596b-2d5e-4ab7-a3a3-f6515dc40838\"}", ContentType.APPLICATION_JSON);

        Response response = restClient.performRequest(method, endpoint, Collections.emptyMap(), entity);
        System.out.println(EntityUtils.toString(response.getEntity()));
    }

    /**
     * 获取文档
     *
     * @throws Exception
     */
    @Test
    public void getDocument() throws Exception {
        String method = "GET";
        String endpoint = "/test-index/test/1";
        Response response = restClient.performRequest(method, endpoint);
        System.out.println(EntityUtils.toString(response.getEntity()));
    }


    /**
     * 查询所有数据
     *
     * @throws Exception
     */
    @Test
    public void QueryAll() throws Exception {
        String method = "POST";
        String endpoint = "/test-index/test/_search";
        HttpEntity entity = new NStringEntity("{\n" +
                "  \"query\": {\n" +
                "    \"match_all\": {}\n" +
                "  }\n" +
                "}", ContentType.APPLICATION_JSON);

        Response response = restClient.performRequest(method, endpoint, Collections.emptyMap(), entity);
        System.out.println(EntityUtils.toString(response.getEntity()));
    }

    /**
     * 根据ID获取
     *
     * @throws Exception
     */
    @Test
    public void QueryByField() throws Exception {
        String method = "POST";
        String endpoint = "/test-index/test/_search";
        HttpEntity entity = new NStringEntity("{\n" +
                "  \"query\": {\n" +
                "    \"match\": {\n" +
                "      \"user\": \"kimchy\"\n" +
                "    }\n" +
                "  }\n" +
                "}", ContentType.APPLICATION_JSON);

        Response response = restClient.performRequest(method, endpoint, Collections.emptyMap(), entity);
        System.out.println(EntityUtils.toString(response.getEntity()));
    }

    /**
     * 更新数据
     *
     * @throws Exception
     */
    @Test
    public void UpdateByScript() throws Exception {
        String method = "POST";
        String endpoint = "/test-index/test/1/_update";
        HttpEntity entity = new NStringEntity("{\n" +
                "  \"doc\": {\n" +
                "    \"user\":\"大美女\"\n" +
                "   }\n" +
                "}", ContentType.APPLICATION_JSON);
        Response response = restClient.performRequest(method, endpoint, Collections.emptyMap(), entity);
        System.out.println(EntityUtils.toString(response.getEntity()));
    }


    @Test
    public void GeoBoundingBox() throws IOException {
        String method = "POST";
        String endpoint = "/attractions/restaurant/_search";
        HttpEntity entity = new NStringEntity("{\n" +
                "  \"query\": {\n" +
                "    \"match_all\": {}\n" +
                "  },\n" +
                "  \"post_filter\": {\n" +
                "    \"geo_bounding_box\": {\n" +
                "      \"location\": {\n" +
                "        \"top_left\": {\n" +
                "          \"lat\": 39.990481,\n" +
                "          \"lon\": 116.277144\n" +
                "        },\n" +
                "        \"bottom_right\": {\n" +
                "          \"lat\": 39.927323,\n" +
                "          \"lon\": 116.405638\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}", ContentType.APPLICATION_JSON);
        Response response = restClient.performRequest(method, endpoint, Collections.emptyMap(), entity);
        System.out.println(EntityUtils.toString(response.getEntity()));
    }
}  