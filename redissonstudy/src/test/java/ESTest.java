import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.Test;

import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: Zephery
 * Time: 2018/4/7 21:44
 * Description:
 */
public class ESTest {
    RestClient restClient =
            RestClient.builder(
                    new HttpHost("119.29.188.224", 9200, "http")).build();

    @Test
    public void setUp() throws Exception {


        String method = "PUT";
        String endpoint = "/test-index/test/1";
        HttpEntity entity = new NStringEntity(
                "{\n" +
                        "    \"user\" : \"kimchy\",\n" +
                        "    \"post_date\" : \"2009-11-15T14:12:12\",\n" +
                        "    \"message\" : \"trying out Elasticsearch\"\n" +
                        "}", ContentType.APPLICATION_JSON);
        Response response = restClient.performRequest(method, endpoint, Collections.emptyMap(), entity);
        System.out.println(EntityUtils.toString(response.getEntity()));
    }

}