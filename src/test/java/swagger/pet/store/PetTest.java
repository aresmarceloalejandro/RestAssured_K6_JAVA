package swagger.pet.store;

import org.junit.Ignore;
import org.json.JSONObject;
import org.junit.Test;

import commons.CommonFunctions;
import io.restassured.response.Response;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

public class PetTest {
    @Test
    public void putPet() throws IOException {
        String jsonBody = new String(Files.readAllBytes(Paths.get("src/test/resources/json/putPet.json")));

        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "application/xml");
        headers.put("Content-Type", "application/json");
        Response response = CommonFunctions.performRequest("PUT", "/pet", jsonBody, headers, null, 200);
        String categoryNameValue = response.xmlPath().getString("Pet.category.name");
        assertEquals("The category name value response its not the expected", "Dogs", categoryNameValue);

        // Negative Scenario
        Response responseNegative = CommonFunctions.performRequest("PUT", "/pet", "null", headers, null, 400);
        int apiErrorCode = responseNegative.xmlPath().getInt("ApiError.code");
        assertEquals("The category name value response its not the expected", 400, apiErrorCode);
    }

    @Test
    public void getPetsByStatus() {
        String status = "available";
        Map<String, String> headers = new HashMap<>();
        Map<String, String> parameters = new HashMap<>();
        headers.put("accept", "application/xml");
        parameters.put("status", status);
        Response response = CommonFunctions.performRequest("GET", "/pet/findByStatus", null, headers, parameters, 200);
        String statusValue = response.xmlPath().getString("ArrayList.item[0].status");
        assertEquals("The status value response its not the expected", status, statusValue);

        // Negative Scenario
        CommonFunctions.performRequest("GET", "/pet/findByStatus", "null", headers, null, 400);
    }

    @Test
    public void getPetsByTags() {
        String tagName = "string";
        Map<String, String> headers = new HashMap<>();
        Map<String, String> parameters = new HashMap<>();
        headers.put("accept", "application/xml");
        parameters.put("tags", tagName);
        Response response = CommonFunctions.performRequest("GET", "/pet/findByTags", null, headers, parameters, 200);
        String tagNameValue = response.xmlPath().getString("ArrayList.item[0].tags.tag.name");
        assertEquals("The tags value response its not the expected", tagName, tagNameValue);

        // Negative Scenario
        CommonFunctions.performRequest("GET", "/pet/findByTags", null, headers, null, 400);     
    }

    @Test
    public void getPetById() {
        String petID = "10";
        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "application/xml");
        Response response = CommonFunctions.performRequest("GET", "/pet/" + petID, null, headers, null, 200);
        String petIDValue = response.xmlPath().getString("Pet.id");
        assertEquals("The pet ID value response its not the expected", petID, petIDValue);

        // Negative Scenario
        CommonFunctions.performRequest("GET", "/pet/101", null, headers, null, 404);         
    }

    @Test
    public void postPet() throws IOException {
        String jsonBody = new String(Files.readAllBytes(Paths.get("src/test/resources/json/postPet.json")));

        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "application/xml");
        headers.put("Content-Type", "application/json");
        Response response = CommonFunctions.performRequest("POST", "/pet", jsonBody, headers, null, 200);
        String petIDValue = response.xmlPath().getString("Pet.id");
        assertEquals("The pet ID value response its not the expected", "10", petIDValue);

        // Negative Scenario
        CommonFunctions.performRequest("POST", "/pet", "null", headers, null, 400);             
    }    

    @Test
    public void postPetWithParams() {
        String petID = "10";
        Map<String, String> headers = new HashMap<>();
        Map<String, String> parameters = new HashMap<>();
        headers.put("accept", "*/*");
        parameters.put("name", "string");
        parameters.put("status", "pending");
        Response response = CommonFunctions.performRequest("POST", "/pet/"+ petID, "", headers, parameters, 200);
        String petIDValue = response.jsonPath().getString("id");
        assertEquals("The pet ID value response its not the expected", petID, petIDValue);

        // Negative Scenario
        CommonFunctions.performRequest("POST", "/pet/"+ petID, "", headers, null, 400);
        CommonFunctions.performRequest("POST", "/pet/101", "", headers, parameters, 404);             
    }

    @Test
    @Ignore("Test is temporarily disabled - Bad 415")
    public void uploadPetImage() {
        File imageFile = new File("src/test/resources/files/QAAT_Challenge_YP.jpg");

        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "application/json");
        //headers.put("Content-Type", "application/octet-stream");
        Response response = CommonFunctions.performRequest("POST_FILE", "/pet/2/uploadImage", imageFile, headers, null, 200);                        
    }    

    @Test
    public void deletePet() {
        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "*/*");
        Response response = CommonFunctions.performRequest("DELETE", "/pet/10", null, headers, null, 200);   

        // Create again the petID=10
        try {
            postPet();
        } catch (IOException e) {
            System.err.println("Error occurred while creating the pet: " + e.getMessage());
            throw new RuntimeException("Failed to create pet after deletion", e);
        }
    }    
}
