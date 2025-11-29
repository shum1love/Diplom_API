package SupportClasses;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.hc.core5.http.HttpStatus;
import java.util.List;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class ApiSteps {
    public static final String BASE_URL = "https://stellarburgers.nomoreparties.site";

    @Step("Регистрация нового пользователя")
    public Response registerUser(User user) {
        return prepareRequest()
                .body(user)
                .when()
                .post("/api/auth/register")
                .then()
                .log().all()
                .extract().response();
    }

    @Step("Проверка успешной регистрации")
    public void validateSuccessfulRegistration(Response response) {
        response.then()
                .log().all()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true));
    }

    @Step("Авторизация пользователя")
    public Response loginUser(User user) {
        return prepareRequest()
                .body(user)
                .when()
                .post("/api/auth/login")
                .then()
                .log().all()
                .extract().response();
    }

    @Step("Генерация случайного пользователя")
    public User generateRandomUser() {
        String randomEmail = GenerateRandomString.generateRandomEmail();
        String randomPassword = GenerateRandomString.generateRandomPassword();
        return new User(randomEmail, randomPassword);
    }

    @Step("Изменение данных пользователя")
    public Response updateUser(String token, UserEmailName updateData) {
        return prepareRequest()
                .header("Authorization", token)
                .body(updateData)
                .when()
                .patch("/api/auth/user")
                .then()
                .log().all()
                .extract().response();
    }

    @Step("Получение списка ингредиентов")
    public List<String> getIngredients() {
        Response response = prepareRequest()
                .when()
                .get("/api/ingredients");
        response.then()
                .log().all()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true));
        return response.jsonPath().getList("data._id");
    }

    @Step("Создание заказа")
    public Response createOrder(String token, String ingredientsJson) {
        return prepareRequest()
                .header("Authorization", token)
                .body(ingredientsJson)
                .when()
                .post("/api/orders")
                .then()
                .log().all()
                .extract().response();
    }

    @Step("Проверка ответа при отсутствии параметров")
    public void validateMissingParameterResponse(Response response) {
        response.then()
                .log().all()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Step("Проверка регистрации с повторными данными")
    public void validateDuplicateRegistration(Response response) {
        response.then()
                .log().all()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("message", equalTo("User already exists"));
    }

    @Step("Получение заказов пользователя без токена")
    public void getUserOrdersWithoutToken() {
        Response response = prepareRequest()
                .when()
                .get("/api/orders");
        response.then()
                .log().all()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Step("Получение заказов пользователя с токеном")
    public void getUserOrdersWithToken(String token) {
        Response response = prepareRequest()
                .header("Authorization", token)
                .when()
                .get("/api/orders");
        response.then()
                .log().all()
                .statusCode(HttpStatus.SC_OK);
    }

    @Step("Сохранение токена")
    public static String saveToken(Response response) {
        return response.jsonPath().getString("accessToken");
    }

    @Step("Удаление пользователя")
    public void deleteUser(String token) {
        Response response = prepareRequest()
                .header("Authorization", token)
                .when()
                .delete("/api/auth/user");
        response.then()
                .log().all()
                .statusCode(HttpStatus.SC_ACCEPTED);
    }

    /**
     * Подготовка базового запроса с базовым URL и логами.
     */
    private io.restassured.specification.RequestSpecification prepareRequest() {
        return given()
                .log().all()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json");
    }
}
