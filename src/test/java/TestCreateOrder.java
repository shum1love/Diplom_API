import SupportClasses.User;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Test;
import java.util.List;
import static org.hamcrest.CoreMatchers.equalTo;
import org.apache.hc.core5.http.HttpStatus;
import SupportClasses.ApiSteps;
import com.github.javafaker.Faker;

public class TestCreateOrder {
    private String token;
    private ApiSteps apiSteps = new ApiSteps();
    Faker faker = new Faker();
    @Test
    @DisplayName("Создание заказа с авторизацией")
    public void testCreateOrderWithLogin() {
        String name = faker.name().fullName();
        String password = faker.internet().password(6, 10, true, true, true);
        String email = faker.internet().emailAddress();

        User user = new User(email, password, name);
        User userLogin = new User(email, password);

        apiSteps.registerUser(user).then().statusCode(HttpStatus.SC_OK);
        token = apiSteps.loginUser(userLogin).then().statusCode(HttpStatus.SC_OK)
                .extract().jsonPath().getString("accessToken");

        List<String> ingredients = apiSteps.getIngredients();
        String ingredientsJson = "{\"ingredients\": [\"" + ingredients.get(0) + "\"]}";

        apiSteps.createOrder(token, ingredientsJson).then().statusCode(HttpStatus.SC_OK);
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void testCreateOrderWithoutLogin() {
        List<String> ingredients = apiSteps.getIngredients();
        String ingredientsJson = "{\"ingredients\": [\"" + ingredients.get(0) + "\"]}";

        apiSteps.createOrder("", ingredientsJson).then().statusCode(HttpStatus.SC_UNAUTHORIZED); // Нет авторизации
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и с ингедиентами")
    public void testCreateOrderWithIngredients() {
        String name = faker.name().fullName();
        String password = faker.internet().password(6, 10, true, true, true);
        String email = faker.internet().emailAddress();
        User user = new User(email, password, name);
        User userLogin = new User(email, password);

        apiSteps.registerUser(user).then().statusCode(HttpStatus.SC_OK);
        token = apiSteps.loginUser(userLogin).then().statusCode(HttpStatus.SC_OK)
                .extract().jsonPath().getString("accessToken");

        List<String> ingredients = apiSteps.getIngredients();
        String ingredientsJson = "{\"ingredients\": [\"" + ingredients.get(1) + "\"]}";

        apiSteps.createOrder(token, ingredientsJson).then().statusCode(HttpStatus.SC_OK);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией, но без ингредиентов")
    public void testCreateOrderWithoutIngredients() {
        String name = faker.name().fullName();
        String password = faker.internet().password(6, 10, true, true, true);
        String email = faker.internet().emailAddress();
        User user = new User(email, password, name);
        User userLogin = new User(email, password);

        apiSteps.registerUser(user).then().statusCode(HttpStatus.SC_OK);
        token = apiSteps.loginUser(userLogin).then().statusCode(HttpStatus.SC_OK)
                .extract().jsonPath().getString("accessToken");

        apiSteps.createOrder(token, "{\"ingredients\": []}").then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("success", equalTo(false));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией, но с неправильным хешем ингедиентов")
    public void testCreateOrderWithWrongHash() {
        String name = faker.name().fullName();
        String password = faker.internet().password(6, 10, true, true, true);
        String email = faker.internet().emailAddress();
        User user = new User(email, password, name);
        User userLogin = new User(email, password);

        apiSteps.registerUser(user).then().statusCode(HttpStatus.SC_OK);
        token = apiSteps.loginUser(userLogin).then().statusCode(HttpStatus.SC_OK)
                .extract().jsonPath().getString("accessToken");

        apiSteps.createOrder(token, "{\"ingredients\": [\"wrong_hash\"]}").then()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @After
    public void tearDown() {
        if (token != null) {
            apiSteps.deleteUser(token);
        }
    }
}
