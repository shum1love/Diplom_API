import SupportClasses.User;
import SupportClasses.UserData;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import java.util.Collection;
import SupportClasses.ApiSteps;
import com.github.javafaker.Faker;

public class TestCreateUser {
    private String token;
    private ApiSteps apiSteps = new ApiSteps();
    Faker faker = new Faker();
    @Test
    @DisplayName("Создание уникального пользователя")
    public void testRegisterUserSuccessfully() {
        String name = faker.name().fullName();
        String password = faker.internet().password(6, 10, true, true, true);
        String email = faker.internet().emailAddress();
        User user = new User(email, password, name);

        Response response = apiSteps.registerUser(user);

        apiSteps.validateSuccessfulRegistration(response);

        token = apiSteps.saveToken(response);
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    public void testRegisterUserRepeat() {
        String name = faker.name().fullName();
        String password = faker.internet().password(6, 10, true, true, true);
        String email = faker.internet().emailAddress();
        User user = new User(email, password, name);

        Response firstResponse = apiSteps.registerUser(user);
        apiSteps.validateSuccessfulRegistration(firstResponse);
        token = apiSteps.saveToken(firstResponse);
        try {
            Response secondResponse = apiSteps.registerUser(user);
            apiSteps.validateDuplicateRegistration(secondResponse);
        } finally {
            // Логика удаления будет выполнена в @After
        }
    }

    @Test
    @DisplayName("Создание пользователя: одно из обязательных полей не заполнено")
    public void testRegisterUserWithoutParameter() {
        Collection<Object[]> testData = UserData.getTestData();

        for (Object[] data : testData) {
            String email = (String) data[0];
            String password = (String) data[1];
            String name = (String) data[2];

            User user = new User(email, password, name);

            Response response = apiSteps.registerUser(user);
            token = apiSteps.saveToken(response);
            apiSteps.validateMissingParameterResponse(response);
        }
    }

    @After
    public void tearDown() {
        if (token != null) {
            apiSteps.deleteUser(token);
        }
    }
}
