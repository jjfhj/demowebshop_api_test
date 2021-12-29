package com.github.jjfhj.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;

public class DemowebshopTest extends TestBase {

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
