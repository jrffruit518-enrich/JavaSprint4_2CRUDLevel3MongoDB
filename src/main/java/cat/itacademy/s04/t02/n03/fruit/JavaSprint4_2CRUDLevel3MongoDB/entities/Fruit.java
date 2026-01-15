package cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.entities;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record Fruit(
        @NotBlank(message = "Fruit name cannot be empty")
        String name,
        @NotNull(message = "Fruit quantity is required") // Ensures the field exists in JSON
        @Positive(message = "Fruit quantity must be a positive number") // Ensures it's > 0
        Integer number // Changed from int to Integer
) {
}
