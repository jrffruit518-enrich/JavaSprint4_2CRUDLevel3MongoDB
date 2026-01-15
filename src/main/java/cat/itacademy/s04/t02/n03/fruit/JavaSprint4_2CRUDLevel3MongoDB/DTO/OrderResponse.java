package cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.DTO;

import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.entities.Fruit;

import java.time.LocalDate;
import java.util.List;

public record OrderResponse(
        String id,
        String clientName,
        List<Fruit> fruitList,
        LocalDate deliveryDate
) {
}
