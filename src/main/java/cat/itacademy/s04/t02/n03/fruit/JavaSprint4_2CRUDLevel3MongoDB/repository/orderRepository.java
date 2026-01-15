package cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.repository;

import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.entities.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface orderRepository extends MongoRepository<Order,String> {

}
