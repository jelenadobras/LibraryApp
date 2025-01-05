package com.library.step_definitions;


import com.library.pages.UIHelperPage;
import com.library.pages.UsersPage;
import com.library.utilities.ConfigurationReader;
import com.library.utilities.DB_Util;
import com.library.utilities.LibraryUtils;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.junit.Assert;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class StepDefs {

    RequestSpecification givenPart = RestAssured.given().log().all();
    Response response;
    JsonPath jp;
    ValidatableResponse thenPart;
    String pathParam;
    UIHelperPage uiHelperPage = new UIHelperPage();
    String tokenResult;


    @Given("I logged Library api as a {string}")
    public void iLoggedLibraryApiAsA(String role) {
        givenPart.header("x-library-token", LibraryUtils.getTokenByRole(role));

    }

    @And("Accept header is {string}")
    public void acceptHeaderIs(String acceptHeader) {
        givenPart.accept(acceptHeader);
    }

    @When("I send GET request to {string} endpoint")
    public void iSendGETRequestToGet_all_usersEndpoint(String endpoint) {

        response = givenPart.when().get(endpoint);
        jp = response.jsonPath();
        thenPart = response.then();

        response.prettyPrint();
    }

    @Then("status code should be {int}")
    public void statusCodeShouldBe(int statusCode) {
        thenPart.statusCode(statusCode);
    }

    @And("Response Content type is {string}")
    public void responseContentTypeIs(String expectedContentType) {
        thenPart.contentType(expectedContentType);
    }

    @And("{string} field should not be null")
    public void fieldShouldNotBeNull(String path) {

        thenPart.body(path, Matchers.notNullValue());
    }

    @And("{string} field should be same with path param")
    public void fieldShouldBeSameWithPathParam(String idField) {
        String result = jp.getString(idField);
        assertEquals(pathParam, result);


    }


    @And("following fields should not be null")
    public void followingFieldsShouldNotBeNull(List<String> path) {

        for (String eachPath : path) {
            Assert.assertNotNull(jp.getString(eachPath));
        }

    }

    @And("Path Param {string} is {string}")
    public void pathParamIs(String paramName, String paramValue) {
        givenPart.pathParam(paramName, paramValue);
        pathParam = paramValue;

    }

    //TASK - CREATE A BOOK
    @And("Request Content Type header is {string}")
    public void requestContentTypeHeaderIs(String contentType) {
        givenPart.contentType("application/x-www-form-urlencoded");
    }

    Map<String, Object> randomDataMap;

    @And("I create a random {string} as request body")
    public void iCreateARandomAsRequestBody(String dataType) {

        switch (dataType) {

            case "book":
                randomDataMap = LibraryUtils.getRandomBookMap();
                break;

            case "user":
                randomDataMap = LibraryUtils.getRandomUserMap();
                break;

            default:
                throw new RuntimeException("Wrong data type is provide");
        }


        givenPart.formParams(randomDataMap);
    }

    @When("I send POST request to {string} endpoint")
    public void iSendPOSTRequestToEndpoint(String endpoint) {
        response = givenPart.when().post(endpoint);
        jp = response.jsonPath();
        thenPart = response.then();

        response.prettyPrint();

    }

    @And("the field value for {string} path should be equal to {string}")
    public void theFieldValueForPathShouldBeEqualTo(String fieldName, String expectedValue) {

        String actualValue = jp.get(fieldName);
        assertEquals(actualValue, expectedValue);

    }

    @And("I logged in Library UI as {string}")
    public void iLoggedInLibraryUIAs(String role) {

        String email = ConfigurationReader.getProperty(role + "_username");
        String password = ConfigurationReader.getProperty(role + "_password");

        uiHelperPage.login(email, password);
    }

    @And("I navigate to {string} page")
    public void iNavigateToPage(String navigation) {
        uiHelperPage.bookCount.click();

    }

    @And("UI, Database and API created book information must match")
    public void uiDatabaseAndAPICreatedBookInformationMustMatch() {

        String bookId = jp.getString("book_id");
        System.out.println("id = " + bookId);

        //2.API - get expected Data from API
        Map<String, Object> expectedAPIData = randomDataMap;

        //3.DATABASE - RUN QUERY AND STORE
        String query = "select * from books where id = " + bookId;
        DB_Util.runQuery(query);
        Map<String, String> dataMap = DB_Util.getRowMap(1);
        System.out.println("dataMap = " + dataMap);

        Assert.assertEquals(randomDataMap.get("name"), dataMap.get("name"));
        Assert.assertEquals(randomDataMap.get("isbn"), dataMap.get("isbn"));
        Assert.assertEquals(randomDataMap.get("author"), dataMap.get("author"));
        Assert.assertEquals(randomDataMap.get("book_category_id"), dataMap.get("book_category_id"));
        Assert.assertEquals(randomDataMap.get("year"), dataMap.get("year"));
        Assert.assertEquals(randomDataMap.get("description"), dataMap.get("description"));

        // GET DATA FROM API
        System.out.println("randomDataMap = " + randomDataMap);

    }

    @And("created user information should match with Database")
    public void createdUserInformationShouldMatchWithDatabase() {

        //1.expected full_name from API response
        String userId = jp.getString("user_id");
        System.out.println("userId = " + userId);


        //2.API - get expected Data from API
        Map<String, Object> expectedAPIData = randomDataMap;

        String query = "select * from users where id = " + userId;
        DB_Util.runQuery(query);
        Map<String, String> dataMap = DB_Util.getRowMap(1);
        System.out.println("dataMap = " + dataMap);

        Assert.assertEquals(randomDataMap.get("full_name"), dataMap.get("full_name"));
        Assert.assertEquals(randomDataMap.get("email"), dataMap.get("email"));
        //   Assert.assertEquals(randomDataMap.get("password"), dataMap.get("password"));
        Assert.assertEquals(randomDataMap.get("user_group_id"), Integer.parseInt(dataMap.get("user_group_id")));
        Assert.assertEquals(randomDataMap.get("status"), dataMap.get("status"));
        Assert.assertEquals(randomDataMap.get("start_date"), dataMap.get("start_date"));
        Assert.assertEquals(randomDataMap.get("end_date"), dataMap.get("end_date"));
        Assert.assertEquals(randomDataMap.get("address"), dataMap.get("address"));

        // GET DATA FROM API
        System.out.println("randomDataMap = " + randomDataMap);
    }

    @And("created user should be able to login Library UI")
    public void createdUserShouldBeAbleToLoginLibraryUI() {

        UIHelperPage uiHelperPage1 = new UIHelperPage();

        String email = (String) randomDataMap.get("email");
        System.out.println("email = " + email);
        String password = (String) randomDataMap.get("password");
        System.out.println("password = " + password);

        uiHelperPage1.login(email, password);


    }

    @And("created user name should appear in Dashboard Page")
    public void createdUserNameShouldAppearInDashboardPage() {

        UsersPage usersPage = new UsersPage();
        String UIuserName = usersPage.dropDown.getText();
        String APIfullName = (String) randomDataMap.get("full_name");
        Assert.assertEquals(APIfullName, UIuserName);


    }

    @Given("I logged Library api with credentials {string} and {string}")
    public void iLoggedLibraryApiWithCredentialsAnd(String email, String password) {
        tokenResult = LibraryUtils.getToken(email, password);


    }

    @And("I send token information as request body")
    public void iSendTokenInformationAsRequestBody() {
        givenPart.formParam("token", tokenResult);


    }

}