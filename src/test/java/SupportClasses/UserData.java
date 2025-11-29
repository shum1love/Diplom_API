package SupportClasses;

import java.util.Arrays;
import java.util.Collection;
import com.github.javafaker.Faker;

public class UserData {
    public static Collection<Object[]> getTestData() {
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String password = faker.internet().password(6, 10, true, true, true);
        String email = faker.internet().emailAddress();
        return Arrays.asList(new Object[][]{
                {"", password, name},
                {email, "", name},
                {email, password, ""}
        });
    }
}
