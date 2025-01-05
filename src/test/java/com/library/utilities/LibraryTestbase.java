package com.library.utilities;

import io.cucumber.java.BeforeAll;
import io.restassured.RestAssured;

public class LibraryTestbase {

    @BeforeAll
    public static void init(){

        RestAssured.baseURI = "https://library2.cydeo.com/rest/v1";

    }
}
