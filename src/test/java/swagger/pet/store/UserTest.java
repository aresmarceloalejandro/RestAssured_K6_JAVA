package swagger.pet.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Test;

import commons.CommonFunctions;
import io.restassured.response.Response;

public class UserTest {
    @Test
    public void postUser() throws IOException {
        String jsonBody = new String(Files.readAllBytes(Paths.get("src/test/resources/json/postUser.json")));

        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "application/json");
        headers.put("Content-Type", "application/json");
        Response response = CommonFunctions.performRequest("POST", "/user", jsonBody, headers, null, 200);     
        String emailValue = response.jsonPath().getString("email");
        assertEquals("The email value response its not the expected", "john@email.com", emailValue);

        // Negative Scenario
        CommonFunctions.performRequest("POST", "/user", "null", headers, null, 500);     
    }

    @Test
    public void createUserWithList() throws IOException {
        String jsonBody = new String(Files.readAllBytes(Paths.get("src/test/resources/json/postUser.json")));
        jsonBody = "[" + jsonBody + "]";

        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "application/xml");
        headers.put("Content-Type", "application/json");
        Response response = CommonFunctions.performRequest("POST", "/user/createWithList", jsonBody, headers, null, 200);    

        String usernameValue = response.xmlPath().getString("Users.item.username");
        assertEquals("The username value response its not the expected", "theUser", usernameValue);

        // Negative Scenario
        CommonFunctions.performRequest("POST", "/user/createWithList", "null", headers, null, 500);
    }

    @Test
    public void loginUser() {
        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "application/xml");
        Response response = CommonFunctions.performRequest("GET", "/user/login?username=string&password=string", null, headers, null, 200);     

        String[] parts = response.getBody().asString().split("Logged in user session: ");
        if (parts.length > 1) {
            String loggedInUserSession = parts[1].trim();
            assertNotNull("The response should contain a non-null loggedInUserSession", loggedInUserSession);
            assertTrue("The loggedInUserSession should be 17 characters long", loggedInUserSession.length() >= 17);
        } else {
            fail("The response does not contain the expected format");
        }
    }

    @Test
    public void logoutUser() {
        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "*/*");
        Response response = CommonFunctions.performRequest("GET", "/user/logout", null, headers, null, 200);  
        String responseBody = response.getBody().asString();
        assertTrue("The response does not contain 'User logged out'", responseBody.contains("User logged out"));        
    }

    @Test
    public void getUser() {
        String username = "theUser";
        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "application/xml");
        Response response = CommonFunctions.performRequest("GET", "/user/" + username, null, headers, null, 200);
        String usernameValue = response.xmlPath().getString("User.username");
        assertEquals("The username value response its not the expected", username, usernameValue);

        // Negative Scenario
        CommonFunctions.performRequest("GET", "/user/theUser2", null, headers, null, 404);  
    }

    @Test
    public void updateUser() throws IOException {
        String jsonBody = new String(Files.readAllBytes(Paths.get("src/test/resources/json/putUser.json")));
        
        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "*/*");
        headers.put("Content-Type", "application/json");
        Response response = CommonFunctions.performRequest("PUT", "/user/theUser", jsonBody, headers, null, 200);  
        String emailValue = response.jsonPath().getString("email");
        assertEquals("The email value response its not the expected", "john@email.com", emailValue);
        
        // Negative Scenario
        CommonFunctions.performRequest("PUT", "/user/theUser22", jsonBody, headers, null, 404); 
        CommonFunctions.performRequest("PUT", "/user/theUser", "null", headers, null, 500); 
    }

    @Test
    public void deleteUser() {
        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "*/*");
        Response response = CommonFunctions.performRequest("DELETE", "/user/theUser", null, headers, null, 200);  

        // Create again the userID=11
        try {
            postUser();
        } catch (IOException e) {
            System.err.println("Error occurred while creating the user: " + e.getMessage());
            throw new RuntimeException("Failed to create user after deletion", e);
        }            
    }    
}
