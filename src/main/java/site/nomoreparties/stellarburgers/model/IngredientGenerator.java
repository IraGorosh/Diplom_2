package site.nomoreparties.stellarburgers.model;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;
import java.util.Random;

public class IngredientGenerator {

    public static Ingredient getRandomIngredient(List<Ingredient> data) {
        Random random = new Random();
        int randomIndex = random.nextInt(data.size());
        return data.get(randomIndex);
    }

    public static int getRandomAmount(List<Ingredient> data) {
        Random random = new Random();
        return random.nextInt(data.size());
    }

    public static String getRandomId() {
        return RandomStringUtils.random(24, "abcdefghijklmnopqrstuvwxyz1234567890");
    }
}
