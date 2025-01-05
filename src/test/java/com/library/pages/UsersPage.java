package com.library.pages;

import com.library.utilities.Driver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class UsersPage {

   public UsersPage() {PageFactory.initElements(Driver.get(), this);}


    @FindBy(xpath = "//span[.='Users']")
    public WebElement userBox;

   @FindBy(xpath = "//input[@type='search']")
   public WebElement searchBox;

   @FindBy(id="navbarDropdown")
   public WebElement dropDown;

    public void goToUsers() {

        userBox.click();

    }

    public void searchForUser(String full_name){
        searchBox.sendKeys(full_name);

    }
}
