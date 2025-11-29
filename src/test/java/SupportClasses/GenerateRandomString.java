package SupportClasses;

import java.util.Random;

public class GenerateRandomString {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    // Метод для генерации случайной строки
    public static String generateRandomString(int length) {
        StringBuilder randomString = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            randomString.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return randomString.toString();
    }

    public static String generateRandomEmail() {
        return generateRandomString(10) + "@mail.com";
    }

    public static String generateRandomPassword() {
        return generateRandomString(8);
    }
}
