package cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.DTO;

import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.entities.Fruit;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record OrderRequest(
        @NotBlank (message = "Client name is required")
        String clientName,
        @NotEmpty(message = "Fruit list cannot be empty")
        @Valid
        List<Fruit> fruitList,
        @NotNull(message = "Delivery date is required")
        LocalDate deliveryDate
) {
}
