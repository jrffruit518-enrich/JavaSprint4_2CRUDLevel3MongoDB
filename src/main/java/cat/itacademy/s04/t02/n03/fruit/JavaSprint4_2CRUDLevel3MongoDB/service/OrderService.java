package cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.service;

import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.DTO.OrderRequest;
import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.DTO.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);

    List<OrderResponse> findAllOrders();

    OrderResponse findOrderById(String id);

    OrderResponse updateOrderById(String id ,OrderRequest request);

    void deleteOrderById(String id);

}
