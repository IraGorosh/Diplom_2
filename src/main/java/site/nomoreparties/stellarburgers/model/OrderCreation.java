package site.nomoreparties.stellarburgers.model;

import java.util.List;

public class OrderCreation {
    private List<String> ingredients;

    public OrderCreation(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public String toString() {
        return "Order{" +
                "ingredients='" + ingredients + '\'' +
                '}';
    }
}
