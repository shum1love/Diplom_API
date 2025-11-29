import SupportClasses.User;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import SupportClasses.ApiSteps;
import com.github.javafaker.Faker;

public class TestGetOrderUser {
    private String token;
    private ApiSteps apiSteps = new ApiSteps();
    Faker faker = new Faker();
    @Test
    @DisplayName("Получение данных заказа конкретного пользователя с авторизацией")
    public void testGetOrderUserWithLogin() {
        String name = faker.name().fullName();
        String password = faker.internet().password(6, 10, true, true, true);
        String email = faker.internet().emailAddress();
        User user = new User(email, password, name);
        User user1 = new User(email, password);

        Response response = apiSteps.registerUser(user);
        apiSteps.loginUser(user1);
        token = apiSteps.saveToken(response);
        apiSteps.getUserOrdersWithToken(token);
    }

    @Test
    @DisplayName("Получение данных заказа конкретного пользователя без авторизации")
    public void testGetOrderUserWithoutLogin() {
        String name = faker.name().fullName();
        String password = faker.internet().password(6, 10, true, true, true);
        String email = faker.internet().emailAddress();
        User user = new User(email, password, name);

        Response response = apiSteps.registerUser(user);
        token = apiSteps.saveToken(response);
        apiSteps.getUserOrdersWithoutToken();
    }

    @After
    public void tearDown() {
        if (token != null) {
            apiSteps.deleteUser(token);
        }
    }
}
