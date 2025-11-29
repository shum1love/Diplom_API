package SupportClasses;

public class UserEmailName {
    private String email;
    private String name;

    // Конструктор с параметрами
    public UserEmailName(String email, String name) {
        this.email = email;
        this.name = name;
    }

    // Геттеры и сеттеры
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
