package com.library.utilities;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LibraryUtils {

    public static String getToken(String email, String password) {

        JsonPath jsonPath = RestAssured.given().log().uri()
                .accept(ContentType.JSON)
                .contentType(ContentType.URLENC)
                .formParam("email", email)
                .formParam("password", password)
                .when().post("/login")
                .then().statusCode(200)
                .extract().jsonPath();

        String token = jsonPath.getString("token");
        System.out.println("token = " + token);

        return token;
    }

    public static String getTokenByRole(String userType) {

        String email = ConfigurationReader.getProperty(userType + "_username");
        String password = ConfigurationReader.getProperty(userType + "_password");

        return getToken(email, password);

    }


    public static Map<String, Object> getRandomBookMap() {

        Faker faker = new Faker();
        Map<String, Object> bookMap = new LinkedHashMap<>();
        String randomBookName = faker.book().title() + faker.number().numberBetween(0, 1000);
        bookMap.put("name", randomBookName);
        bookMap.put("isbn", faker.code().isbn10());
        bookMap.put("year", String.valueOf(faker.number().numberBetween(1000, 2021)));
        bookMap.put("author", faker.book().author());
        bookMap.put("book_category_id", String.valueOf(faker.number().numberBetween(1, 20)));
        bookMap.put("description", faker.chuckNorris().fact());

        return bookMap;
    }

    public static Map<String, Object> getRandomUserMap() {

        Faker faker = new Faker();
        Map<String, Object> userMap = new LinkedHashMap<>();
        Date startDate = faker.date().past(365, TimeUnit.DAYS);
        Date endDate = faker.date().future(365, TimeUnit.DAYS);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String randomUser = faker.name().fullName();
        userMap.put("full_name", randomUser);
        userMap.put("email", randomUser.toLowerCase().replace(" ", "") + faker.number().digits(4) + "@gmail.com");
        userMap.put("password", randomUser.toLowerCase().replace(" ", "") + faker.number().digits(4));
        userMap.put("user_group_id", 2);
        userMap.put("status", "ACTIVE");
        userMap.put("start_date", sdf.format(startDate));
        userMap.put("end_date", sdf.format(endDate));
        userMap.put("address", faker.address().fullAddress());

        return userMap;
    }


    public static Map<String, String> returnCredentials(String role) {
        String email = "";
        String password = "";

        switch (role) {
            case "librarian":
                email = ConfigurationReader.getProperty("librarian_username");
                password = ConfigurationReader.getProperty("librarian_password");

                break;

            case "student":
                email = ConfigurationReader.getProperty("student_username");
                password = ConfigurationReader.getProperty("student_password");

                break;

            default:

                throw new RuntimeException("Invalid Role Entry :\n>> " + role + " <<");
        }

        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", email);
        credentials.put("password", password);

        return credentials;
    }

}




