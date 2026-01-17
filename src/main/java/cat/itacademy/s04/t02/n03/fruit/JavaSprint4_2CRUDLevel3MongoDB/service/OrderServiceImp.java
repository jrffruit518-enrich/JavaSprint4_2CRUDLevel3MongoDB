package cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.service;

import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.DTO.OrderRequest;
import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.DTO.OrderResponse;
import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.entities.Order;
import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.exception.ResourceNotFoundException;
import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;



@Service
@AllArgsConstructor
public class OrderServiceImp implements OrderService{
    private final OrderRepository repository;
    @Override
    public OrderResponse createOrder(OrderRequest request) {
        validateDate(request.deliveryDate());
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
        // 1. Check ID exists
        Order existedOrder = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        // 2. Validate new delivery date (Consistent with createOrder logic)
       validateDate(request.deliveryDate());

        // 3. Map new data to the existed entity
        existedOrder.setClientName(request.clientName());
        existedOrder.setFruitList(request.fruitList());
        existedOrder.setDeliveryDate(request.deliveryDate());

        // 4. Save and return
        Order updatedOrder = repository.save(existedOrder);
        return getOrderResponse(updatedOrder);
    }

    @Override
    public void deleteOrderById(String id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        repository.deleteById(id);
    }

    private OrderResponse getOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getClientName(),
                order.getFruitList(),
                order.getDeliveryDate()
        );
    }

    private void validateDate(LocalDate date) {
        // Common validation logic
        if (date.isBefore(LocalDate.now().plusDays(1))) {
            throw new IllegalArgumentException("Delivery date must be tomorrow or later");
        }
    }
}
