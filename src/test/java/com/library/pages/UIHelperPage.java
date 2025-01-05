package com.library.pages;


import com.library.utilities.BrowserUtils;
import com.library.utilities.Driver;
import com.library.utilities.LibraryUtils;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.Map;

public class UIHelperPage {

    public UIHelperPage() {
        PageFactory.initElements(Driver.get(), this);
    }

    @FindBy(id = "inputEmail")
    public WebElement emailAddress;

    @FindBy(id = "inputPassword")
    public WebElement password;

    @FindBy(xpath = "//button[@type='submit']")
    public WebElement signInButton;

    @FindBy(xpath = "//span[.='Books']")
    public WebElement bookCount;

    @FindBy(xpath = "//span[.='Users']")
    public WebElement userBox;

    @FindBy(xpath = "//span[.='Dashboard']")
    public WebElement dashboardBox;

    public void login(String email, String password) {

        emailAddress.sendKeys(email);
        this.password.sendKeys(password);

        signInButton.click();
        BrowserUtils.waitFor(2);

    }

    public void login(String role) {

        // Get Credentials
        Map<String, String> roleCredentials = LibraryUtils.returnCredentials(role);
        String email = roleCredentials.get("email");
        String password = roleCredentials.get("password");


        // login
        login(email, password);
    }
}