import SupportClasses.User;
import SupportClasses.UserEmailName;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import SupportClasses.ApiSteps;
import org.apache.hc.core5.http.HttpStatus;
import com.github.javafaker.Faker;

public class TestChangeDataUser {
    private String token;
    private ApiSteps apiSteps = new ApiSteps();
    Faker faker = new Faker();

    @Test
    @DisplayName("Изменение данных пользователя с авторизацией")
    public void testChangeDataUser() {
        String name = faker.name().fullName();
        String password = faker.internet().password(6, 10, true, true, true);
        String email = faker.internet().emailAddress();
        String nameNew = faker.name().fullName();
        String emailNew = faker.internet().emailAddress();

        User user = new User(email, password, name);
        User userForLogin = new User(email, password);
        UserEmailName updateData = new UserEmailName(emailNew, nameNew);

        Response registerResponse = apiSteps.registerUser(user);
        registerResponse.then().statusCode(HttpStatus.SC_OK);
        token = registerResponse.jsonPath().getString("accessToken");

        Response loginResponse = apiSteps.loginUser(userForLogin);
        loginResponse.then().statusCode(HttpStatus.SC_OK);

        Response updateResponse = apiSteps.updateUser(token, updateData);
        updateResponse.then().statusCode(HttpStatus.SC_OK);
    }

    @Test
    @DisplayName("Изменение данных пользователя без авторизации")
    public void testChangeDataWithoutLogin() {
        // Задаём тестовые данные фейкером
        String name = faker.name().fullName();
        String password = faker.internet().password(6, 10, true, true, true);
        String email = faker.internet().emailAddress();
        String nameNew = faker.name().fullName();
        String emailNew = faker.internet().emailAddress();

        // Задаём данные для регистраии пользователя и для обновления данных
        User user = new User(email, password, name);
        UserEmailName updateData = new UserEmailName(emailNew, nameNew);

        Response registerResponse = apiSteps.registerUser(user);
        registerResponse.then().statusCode(HttpStatus.SC_OK);
        token = registerResponse.jsonPath().getString("accessToken");

        Response updateResponse = apiSteps.updateUser("", updateData);
        updateResponse.then().statusCode(HttpStatus.SC_UNAUTHORIZED);
    }
    @After
    public void tearDown() {
        if (token != null) {
            apiSteps.deleteUser(token);
        }
    }
}
