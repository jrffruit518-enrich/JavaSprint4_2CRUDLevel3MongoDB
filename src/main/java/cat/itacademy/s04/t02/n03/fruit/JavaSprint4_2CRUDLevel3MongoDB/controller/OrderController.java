package cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.controller;

import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.DTO.OrderRequest;
import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.DTO.OrderResponse;
import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(
            @RequestBody @Valid OrderRequest request
            ) {
        return orderService.createOrder(request);

    }

    @GetMapping
    public List<OrderResponse> findAllOrders() {
        return orderService.findAllOrders();
    }

    @GetMapping("/{id}")
    public OrderResponse findOrderById(
            @PathVariable String id
    ) {
    return orderService.findOrderById(id);

    }

    @PutMapping("/{id}")
    public OrderResponse updateOrderById(
            @PathVariable String id,
            @RequestBody @Valid OrderRequest request) {

        return orderService.updateOrderById(id,request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrderById(
            @PathVariable String id
    ) {
        orderService.deleteOrderById(id);
    }


}
