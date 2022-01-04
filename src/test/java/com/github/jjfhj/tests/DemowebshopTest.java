package com.github.jjfhj.tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.github.jjfhj.config.App;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;

public class DemowebshopTest {

    public static String authorizationCookie;
    public static String updateTopCartSection;

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = App.config.apiUrl();
        Configuration.baseUrl = App.config.webUrl();
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());

        step("Получить cookie через API и установить его в браузере", () -> {
            authorizationCookie =
                    given()
                            .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                            .formParam("Email", App.config.userLogin())
                            .formParam("Password", App.config.userPassword())
                            .when()
                            .post("login")
                            .then()
                            .statusCode(302)
                            .extract()
                            .cookie("NOPCOMMERCE.AUTH");

            step("Открыть минимальный контент для установки cookie при открытии сайта", () ->
                    open("Themes/DefaultClean/Content/images/logo.png"));

            step("Установить cookie в браузер", () ->
                    getWebDriver().manage().addCookie(
                            new Cookie("NOPCOMMERCE.AUTH", authorizationCookie)));
        });
    }

    @Test
    @DisplayName("Отображение количества товара в корзине после его добавления через API")
    void displayingNumberOfItemsInTheCartAfterAddingItViaAPITest() {

        step("Добавить товар 'Computing and Internet' в корзину", () ->
                updateTopCartSection = given()
                        .cookie("NOPCOMMERCE.AUTH", authorizationCookie)
                        .when()
                        .post("addproducttocart/details/13/1")
                        .then().log().all()
                        .statusCode(200)
                        .extract()
                        .path("updatetopcartsectionhtml"));

        step("Открыть главную страницу магазина", () ->
                open(""));

        step("Количество товара = " + updateTopCartSection, () ->
                $(".cart-qty").shouldHave(text(updateTopCartSection)));
    }
}
