package cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.entities;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;


@Document(collection = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    private String id;

    @NotBlank(message = "Client name is required")
    private String clientName;

    @NotEmpty(message = "At least one fruit must be selected")
    @Valid // Necessary to trigger validation inside the Fruit record
    private List<Fruit> fruitList;

    @NotNull(message = "Delivery date is required")
    private LocalDate deliveryDate;

}
