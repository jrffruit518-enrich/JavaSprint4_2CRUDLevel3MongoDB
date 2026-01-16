package cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.service;

import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.DTO.OrderRequest;
import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.DTO.OrderResponse;
import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.entities.Order;
import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.exception.ResourceNotFoundException;
import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;



@Service
@AllArgsConstructor
public class OrderServiceImp implements OrderService{
    private final OrderRepository repository;
    @Transactional
    @Override
    public OrderResponse createOrder(OrderRequest request) {
        if (request.deliveryDate().isBefore(LocalDate.now().plusDays(1))) {
            throw new IllegalArgumentException("Delivery date must be tomorrow or later");
        }
        Order order = Order.builder()
                .clientName(request.clientName())
                .fruitList(request.fruitList())
                .deliveryDate(request.deliveryDate())
                .build();

        Order savedOrder = repository.save(order);
        return getOrderResponse(savedOrder);
    }

    @Override
    public List<OrderResponse> findAllOrders() {
       return repository.findAll().stream().map(this::getOrderResponse)
                .toList();
    }

    @Override
    public OrderResponse findOrderById(String id) {
        Order order = repository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException(id));

        return getOrderResponse(order);
    }

    @Override
    public OrderResponse updateOrderById(String id, OrderRequest request) {
        return null;
    }

    @Override
    public void deleteOrderById(String id) {

    }
    private OrderResponse getOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getClientName(),
                order.getFruitList(),
                order.getDeliveryDate()
        );
    }
}
