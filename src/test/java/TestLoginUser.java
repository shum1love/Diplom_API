import SupportClasses.User;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import org.apache.hc.core5.http.HttpStatus;
import SupportClasses.ApiSteps;
import com.github.javafaker.Faker;

public class TestLoginUser {
    private String token;
    private ApiSteps apiSteps = new ApiSteps();
    Faker faker = new Faker();
    @Test
    @DisplayName("Авторизация под существующим пользователем")
    public void testLoginExistingUser() {
        String name = faker.name().fullName();
        String password = faker.internet().password(6, 10, true, true, true);
        String email = faker.internet().emailAddress();

        User user = new User(email, password, name);
        User userForLogin = new User("examplr.praktikum@gmail.com", "Bogdan777");

        Response registerResponse = apiSteps.registerUser(user);
        registerResponse.then().statusCode(HttpStatus.SC_OK);

        token = registerResponse.jsonPath().getString("accessToken");

        Response loginResponse = apiSteps.loginUser(userForLogin);
        loginResponse.then().statusCode(HttpStatus.SC_OK);
    }

    @Test
    @DisplayName("Авторизация с неверным логином и паролем")
    public void testLoginRandomUser() {
        User randomUser = apiSteps.generateRandomUser();

        Response loginResponse = apiSteps.loginUser(randomUser);
        loginResponse.then().statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @After
    public void tearDown() {
        if (token != null) {
            apiSteps.deleteUser(token);
        }
    }
}
