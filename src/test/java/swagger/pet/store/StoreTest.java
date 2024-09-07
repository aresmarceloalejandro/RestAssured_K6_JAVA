package swagger.pet.store;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Test;

import commons.CommonFunctions;
import io.restassured.response.Response;


public class StoreTest {
    @Test
    public void getInventory() {
        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "application/json");
        headers.put("Content-Type", "application/json");
        Response response = CommonFunctions.performRequest("GET", "/store/inventory", null, headers, null, 200);
        int approvedValue = response.jsonPath().getInt("approved");
        assertEquals("The approved value response its not the expected", 57, approvedValue);   
    }

    @Test
    public void postOrder() throws IOException {
        String jsonBody = new String(Files.readAllBytes(Paths.get("src/test/resources/json/postOrder.json")));

        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "application/json");
        headers.put("Content-Type", "application/json");
        Response response = CommonFunctions.performRequest("POST", "/store/order", jsonBody, headers, null, 200);
        String statusValue = response.jsonPath().getString("status");
        assertEquals("The status value response its not the expected", "approved", statusValue);   

        //Negative Scenarios
        JSONObject jsonObject = new JSONObject(jsonBody);
        jsonObject.put("shipDate", true);
        String updatedJsonBody = jsonObject.toString();
        CommonFunctions.performRequest("POST", "/store/order", updatedJsonBody, headers, null, 400);
        CommonFunctions.performRequest("POST", "/store/order", "", headers, null, 400);
    }

    @Test
    public void getOrder() {
        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "application/xml");
        Response response = CommonFunctions.performRequest("GET", "/store/order/2", null, headers, null, 200);  
        int idValue = response.xmlPath().getInt("Order.id");
        assertEquals("The id value response its not the expected", 2, idValue);

        //Negative Scenarios
        CommonFunctions.performRequest("GET", "/store/order/2222", null, headers, null, 404);  
        CommonFunctions.performRequest("GET", "/store/order/2.6", null, headers, null, 400);  
    }

    @Test
    public void deleteOrder() {
        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "*/*");
        CommonFunctions.performRequest("DELETE", "/store/order/10", null, headers, null, 200);   
    }
}
