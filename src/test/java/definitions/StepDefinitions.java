package definitions;

import com.sei.findgo.FindGoApplication;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = FindGoApplication.class)
public class StepDefinitions {

    private static final String BASE_URL = "http://localhost:";

    @LocalServerPort
    String port;

    private static Response response;

    private String token;

    private static ResponseEntity<String> responseEntity;
    private static List<?> list;

    public String JWTTestKeyAdmin() throws JSONException {
        RequestSpecification request = RestAssured.given();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", "jay@example.com");
        jsonObject.put("password", "password5");
        request.header("Content-Type", "application/json");
        response = request.body(jsonObject.toString()).post(BASE_URL + port + "/api/users/login");
        return response.jsonPath().getString("message");
    }

    public String JWTTestKeyManager() throws JSONException {
        RequestSpecification request = RestAssured.given();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", "marie@example.com");
        jsonObject.put("password", "password4");
        request.header("Content-Type", "application/json");
        response = request.body(jsonObject.toString()).post(BASE_URL + port + "/api/users/login");
        return response.jsonPath().getString("message");
    }

    @Given("email is not registered")
    public void emailIsNotRegistered() throws JSONException {
        RequestSpecification request = RestAssured.given();
        JSONObject requestBody = new JSONObject();
        requestBody.put("username", "JayCee");
        requestBody.put("email", "jay@example.com");
        requestBody.put("password", "password5");
        requestBody.put("role", "User");
        request.header("Content-Type", "application/json");
        response = request.body(requestBody.toString()).post(BASE_URL + port + "/api/users/register");
        Assert.assertEquals(409, response.getStatusCode());
    }


    @When("user enters valid registration details \\(username, email, password)")
    public void userEntersValidRegistrationDetailsUsernameEmailPassword() throws JSONException {
        RequestSpecification request = RestAssured.given();
        JSONObject requestBody = new JSONObject();
        requestBody.put("username", "Carter");
        requestBody.put("email", "carter@example.com");
        requestBody.put("password", "password5");
        requestBody.put("role", "User");
        request.header("Content-Type", "application/json");
        response = request.body(requestBody.toString()).post(BASE_URL + port + "/api/users/register");
    }

    @Then("user should be successfully registered")
    public void userShouldBeSuccessfullyRegistered() {
        Assert.assertEquals(200, response.getStatusCode());
    }

    @Given("User is Admin or Manager")
    public void userIsAdminOrManager() {
    }

    @When("I enter valid login credentials \\(username, password)")
    public void iEnterValidLoginCredentialsUsernamePassword() throws JSONException {
        RequestSpecification request = RestAssured.given();
        JSONObject requestBody = new JSONObject();
        requestBody.put("email", "jay@example.com");
        requestBody.put("password", "password5");
        request.header("Content-Type", "application/json");
        response = request.body(requestBody.toString()).post(BASE_URL + port + "/api/users/login");
        Assert.assertEquals(200, response.getStatusCode());
    }

    @Then("I should be logged in successfully")
    public void iShouldBeLoggedInSuccessfully() {
        Assert.assertEquals(200, response.getStatusCode());
    }

    @Given("there are multiple users in the system")
    public void thereAreMultipleUsersInTheSystem() throws JSONException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(JWTTestKeyAdmin());
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        responseEntity = new RestTemplate().exchange(BASE_URL + port + "/api/auth/users", HttpMethod.GET, entity, String.class);
        list = JsonPath.from(String.valueOf(responseEntity.getBody())).get();
        Assert.assertEquals(200, response.getStatusCode());
    }


    @When("the client requests to get all users")
    public void theClientRequestsToGetAllUsers() throws JSONException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(JWTTestKeyAdmin());
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        responseEntity = new RestTemplate().exchange(BASE_URL + port + "/api/auth/users", HttpMethod.GET, entity, String.class);
        list = JsonPath.from(String.valueOf(responseEntity.getBody())).get();
    }

    @Then("the response should contain a list of all users")
    public void theResponseShouldContainAListOfAllUsers() {
        Assert.assertTrue(list.size() > 0);
        Assert.assertEquals(200, response.getStatusCode());
    }

    @Given("user is an Admin")
    public void userIsAnAdmin() throws JSONException {
        RequestSpecification request = RestAssured.given();
        token = JWTTestKeyAdmin();
        request.header("Authorization", "Bearer " + token);
    }

    @When("I search for a user by Id")
    public void iSearchForAUserById() {
        RestAssured.baseURI = BASE_URL + port + "/api/auth/users/1";
        RequestSpecification request = RestAssured.given().header("Authorization", "Bearer " + token);
        response = request.get();
    }

    @Then("the response should contain the user details")
    public void theResponseShouldContainTheUserDetails() {
        Assert.assertNotNull(String.valueOf(response));
        Assert.assertEquals(200, response.getStatusCode());
    }

    @When("I update a users details")
    public void iUpdateAUsersDetails() throws JSONException {
        RequestSpecification request = RestAssured.given();
        JSONObject requestBody = new JSONObject();
        request.header("Authorization", "Bearer " + token);
        requestBody.put("userName", "Tim");
        requestBody.put("email", "Rodriguez@example.com");
        requestBody.put("password", "password35");
        requestBody.put("role", "Manager");
        request.header("Content-Type", "application/json");
        response = request.body(requestBody.toString()).put(BASE_URL + port + "/api/auth/users/1");

    }

    @Then("the user should be successfully updated")
    public void theUserShouldBeSuccessfullyUpdated() {
        Assert.assertNotNull(String.valueOf(response));
        Assert.assertEquals(200, response.getStatusCode());
    }

    @When("I delete a user")
    public void iDeleteAUser() {
        RequestSpecification request = RestAssured.given();
        request.header("Authorization", "Bearer " + token);
        response = request.delete(BASE_URL + port + "/api/auth/users/1");
    }

    @Then("the user should be successfully deleted")
    public void theUserShouldBeSuccessfullyDeleted() {
        Assert.assertNotNull(String.valueOf(response));
        Assert.assertEquals(200, response.getStatusCode());
    }

    @When("user creates a store")
    public void userCreatesAStore() throws JSONException {
        RequestSpecification request = RestAssured.given();
        JSONObject requestBody = new JSONObject();
        request.header("Authorization", "Bearer " + token);
        requestBody.put("storeName", "Circuit City");
        requestBody.put("description", "Electronics Store");
        requestBody.put("city", "Cerritos");
        requestBody.put("map", "floorPlan.png");
        request.header("Content-Type", "application/json");
        response = request.body(requestBody.toString()).post(BASE_URL + port + "/api/auth/stores");
    }

    @Then("the store should successfully be added")
    public void theStoreShouldSuccessfullyBeAdded() {
        Assert.assertNotNull(String.valueOf(response));
        Assert.assertEquals(200, response.getStatusCode());
    }

    @When("the user submits the updated store details")
    public void theUserSubmitsTheUpdatedStoreDetails() throws JSONException {
        RequestSpecification request = RestAssured.given();
        JSONObject requestBody = new JSONObject();
        request.header("Authorization", "Bearer " + token);
        requestBody.put("storeName", "Circuit City");
        requestBody.put("description", "Electronics Store");
        requestBody.put("city", "Cerritos");
        requestBody.put("map", "floorPlan.png");
        request.header("Content-Type", "application/json");
        response = request.body(requestBody.toString()).put(BASE_URL + port + "/api/auth/stores/1");
        Assert.assertNotNull(String.valueOf(response));
    }

    @Then("the store should be updated successfully")
    public void theStoreShouldBeUpdatedSuccessfully() {
        Assert.assertEquals(200, response.getStatusCode());
    }

    @When("the owner sends a request to delete the store")
    public void theOwnerSendsARequestToDeleteTheStore() {
        RequestSpecification request = RestAssured.given();
        request.header("Authorization", "Bearer " + token);
        response = request.delete(BASE_URL + port + "/api/auth/stores/2");
    }

    @Then("the store should be deleted successfully")
    public void theStoreShouldBeDeletedSuccessfully() {
        Assert.assertEquals(500, response.getStatusCode());
    }

    @Given("the user is a Manager")
    public void theUserIsAManager() throws JSONException {
        RequestSpecification request = RestAssured.given();
        token = JWTTestKeyManager();
        request.header("Authorization", "Bearer " + token);
    }

    @When("the Manager sends a request to get the store by ID")
    public void theManagerSendsARequestToGetTheStoreByID() {
        RestAssured.baseURI = BASE_URL + port + "/api/stores/storeId/1";
        RequestSpecification request = RestAssured.given().header("Authorization", "Bearer " + token);
        response = request.get();
    }

    @Then("the response should contain the store details")
    public void theResponseShouldContainTheStoreDetails() {
        Assert.assertNotNull(String.valueOf(response));
        Assert.assertEquals(200, response.getStatusCode());
    }

    @When("the manager submits the product details")
    public void theManagerSubmitsTheProductDetails() throws JSONException {
        RequestSpecification request = RestAssured.given();
        JSONObject requestBody = new JSONObject();
        request.header("Authorization", "Bearer " + token);
        requestBody.put("productName", "Samsung Galaxy");
        requestBody.put("description", "Electronics");
        requestBody.put("price", "1000.00");
        requestBody.put("storeId", "1");
        requestBody.put("image", "image.png");
        request.header("Content-Type", "application/json");
        response = request.body(requestBody.toString()).post(BASE_URL + port + "/api/auth/products");
    }

    @Then("the product should be added successfully")
    public void theProductShouldBeAddedSuccessfully() {
        Assert.assertNotNull(String.valueOf(response));
        Assert.assertEquals(200, response.getStatusCode());
    }

    @When("the Manager sends a request to get the product by ID")
    public void theManagerSendsARequestToGetTheProductByID() {
        RestAssured.baseURI = BASE_URL + port + "/api/auth/products/1";
        RequestSpecification request = RestAssured.given().header("Authorization", "Bearer " + token);
        response = request.get();
        Assert.assertNotNull(String.valueOf(response));
    }

    @Then("the response should contain the product details")
    public void theResponseShouldContainTheProductDetails() {
        Assert.assertNotNull(String.valueOf(response));
        Assert.assertEquals(200, response.getStatusCode());
    }

    @Given("the user wants to find all stores")
    public void theUserWantsToFindAllStores() {
    }


    @When("the user sends a request to get all stores")
    public void theUserSendsARequestToGetAllStores() {
        responseEntity = new RestTemplate().exchange(BASE_URL + port + "/api/stores", HttpMethod.GET, null, String.class);
        list = JsonPath.from(String.valueOf(responseEntity.getBody())).get();
    }

    @Then("the response should contain a list of all stores")
    public void theResponseShouldContainAListOfAllStores() {
        Assert.assertTrue(list.size() > 0);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Given("a store is available")
    public void aStoreIsAvailable() {
        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();
        response = request.get(BASE_URL + port + "/api/stores/search/Best Buy");
    }

    @When("the user sends a request to get the store by name")
    public void theUserSendsARequestToGetTheStoreByName() {
        Assert.assertNotNull(String.valueOf(response));
    }

    @Then("the response should return the store details")
    public void theResponseShouldReturnTheStoreDetails() {
        Assert.assertEquals(200, response.getStatusCode());
    }

    @Given("the user wants to get a store by city")
    public void theUserWantsToGetAStoreByCity() {
        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();
        response = request.get(BASE_URL + port + "/api/stores/city/Phoenix");
    }

    @When("the user sends a request to get the store by city")
    public void theUserSendsARequestToGetTheStoreByCity() {
        Assert.assertNotNull(String.valueOf(response));
    }

    @Then("the response should contain the store information")
    public void theResponseShouldContainTheStoreInformation() {
        Assert.assertEquals(200, response.getStatusCode());
    }

    @Given("a list of products is available")
    public void aListOfProductsIsAvailable() {
        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();
        response = request.get(BASE_URL + port + "/api/products/");
        Assert.assertNotNull(String.valueOf(response));
    }

    @When("the user sends a request to get all products")
    public void theUserSendsARequestToGetAllProducts() {
        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();
        response = request.get(BASE_URL + port + "/api/products");
        list = response.jsonPath().getList("$");
    }

    @Then("the response should contain a list of all products")
    public void theResponseShouldContainAListOfAllProducts() {
        Assert.assertTrue(list.size() > 0);
        Assert.assertEquals(200, response.getStatusCode());
    }

    @When("the user sends a request to get the product by name")
    public void theUserSendsARequestToGetTheProductByName() {
        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();
        response = request.get(BASE_URL + port + "/api/products/search/jeans");
    }

    @Then("the response should return the product details")
    public void theResponseShouldReturnTheProductDetails() {
        Assert.assertNotNull(String.valueOf(response));
        Assert.assertEquals(200, response.getStatusCode());
    }

    @When("the user sends a request to get the products by category")
    public void theUserSendsARequestToGetTheProductsByCategory() {
        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();
        response = request.get(BASE_URL + port + "/api/products/search/category/Clothing");
    }

    @Then("the response should contain a list of products in the specified category")
    public void theResponseShouldContainAListOfProductsInTheSpecifiedCategory() {
        Assert.assertNotNull(String.valueOf(response));
        Assert.assertEquals(200, response.getStatusCode());
    }

    @When("the user submits the updated product details")
    public void theUserSubmitsTheUpdatedProductDetails() throws JSONException {
        RequestSpecification request = RestAssured.given();
        JSONObject requestBody = new JSONObject();
        request.header("Authorization", "Bearer " + JWTTestKeyManager());
        requestBody.put("productName", "Levis Jeans");
        requestBody.put("description", "34w 32h Levis Jeans");
        requestBody.put("category", "Clothing");
        requestBody.put("price", "120.00");
        requestBody.put("quantity", "10");
        requestBody.put("storeSection", "E40");
        requestBody.put("image", "image.png");
        request.header("Content-Type", "application/json");
        response = request.body(requestBody.toString()).put(BASE_URL + port + "/api/auth/products/2");
    }

    @Then("the product should be updated successfully")
    public void theProductShouldBeUpdatedSuccessfully() {
        Assert.assertNotNull(String.valueOf(response));
        Assert.assertEquals(200, response.getStatusCode());
    }

    @When("the user sends a request to delete the product")
    public void theUserSendsARequestToDeleteTheProduct() throws JSONException {
        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();
        request.header("Authorization", "Bearer " + JWTTestKeyManager());
        response = request.delete(BASE_URL + port + "/api/auth/products/6");
    }

    @Then("the product should be deleted successfully")
    public void theProductShouldBeDeletedSuccessfully() {
        Assert.assertEquals(200, response.getStatusCode());
    }
}



