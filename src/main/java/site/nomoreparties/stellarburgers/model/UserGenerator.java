package site.nomoreparties.stellarburgers.model;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

public class UserGenerator {
    public static User getRandomUser() {
        String email = generateEmail();
        String password = RandomStringUtils.randomAlphabetic(15);
        String name = RandomStringUtils.randomAlphabetic(15);
        return new User(email, password, name);
    }

    public static String getRandomString() {
        return RandomStringUtils.randomAlphabetic(15);
    }

    public static String getEmailDomain() {
        Random random = new Random();
        final String[] domain = {"gmail.com",
                "yandex.ru",
                "yahoo.com",
                "mail.com"};
        int index = random.nextInt(domain.length);
        return domain[index];
    }

    public static String generateEmail() {
        return RandomStringUtils.random(16, "abcdefghijklmnopqrstuvwxyz1234567890") + "@" + getEmailDomain();
    }
}
