package commons;

import static io.restassured.RestAssured.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class CommonFunctions {   

    private static final String BASE_URI = "http://localhost:8080/api/v3";
    
    public static Response performRequest(String method, String endpoint, Object bodyObject, Map<String, String> headers, Map<String, String> params, int expectedStatusCode) {
        RestAssured.baseURI = BASE_URI;
        Response response = null;

        // Start Generic Validations
        if (endpoint == null || method == null) {throw new IllegalArgumentException("Endpoint and method must not be null");}
        if (headers == null) {headers = new HashMap<>();}
        if (params == null) {params = new HashMap<>();}

        Object body = null;
        if (bodyObject instanceof String) {
            body = bodyObject;
        } else if (bodyObject instanceof File) {
            body = bodyObject;
        } else if (bodyObject != null) {
            throw new IllegalArgumentException("Invalid body type");
        }

        // End Generic Validations

        switch (method.toUpperCase()) {
            case "GET":
                if (params != null) {
                    response = given().
                                    queryParams(params).
                                    headers(headers).
                                when().
                                    get(endpoint);
                } else {
                    response = given().
                                    headers(headers).
                                when().
                                    get(endpoint);
                }
                break;
            case "POST":
            if (params != null) {
                response = given().
                                queryParams(params).
                                headers(headers).
                                body(body).
                            when().
                                post(endpoint);
            } else {
                response = given().
                                headers(headers).
                                body(body).
                            when().
                                post(endpoint);
            }
            break;
            case "DELETE":
                response = given().
                                headers(headers).
                            when().
                                delete(endpoint);
                break;
            case "PUT":
                response = given().
                                headers(headers).
                                body(body).
                            when().
                                put(endpoint);
                break;
            case "POST_FILE":
                response = given().
                                headers(headers).
                                multiPart((File) bodyObject).
                            when().
                                post(endpoint);
                break;            
            default:
                throw new IllegalArgumentException("Invalid method: " + method);
        }
        
        response.then().statusCode(expectedStatusCode);
        return response;
    }
}
