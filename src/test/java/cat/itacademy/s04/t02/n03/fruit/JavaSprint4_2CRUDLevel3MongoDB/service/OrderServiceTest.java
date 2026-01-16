package cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.service;

import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.DTO.OrderRequest;
import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.DTO.OrderResponse;
import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.entities.Fruit;
import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.entities.Order;
import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    OrderRepository repository;

    @InjectMocks
    OrderServiceImp orderService;

    @Test
    @DisplayName("Success Path: Mapping and Saving")
    void createOrder_ShouldMapAndSaveCorrectly() {

        LocalDate validDate = LocalDate.now().plusDays(1);
        OrderRequest request = new OrderRequest("Rong", List.of(new Fruit("Apple", 1)), validDate);

        Order savedOrder = new Order("ID123", "Rong", request.fruitList(), validDate);
        when(repository.save(any())).thenReturn(savedOrder);

        OrderResponse response = orderService.createOrder(request);

        assertNotNull(response);
        assertEquals(validDate, response.deliveryDate());
        verify(repository, times(1)).save(any(Order.class));
    }
    @Test
    @DisplayName("Should throw exception when delivery date is before tomorrow")
    void createOrder_DateBeforeTomorrow_ShouldThrowException() {
        // Arrange: Set date to TODAY
        OrderRequest request = new OrderRequest("Rong", List.of(new Fruit("apple", 15)), LocalDate.now());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(request));

        // Verify save was NEVER called
        verify(repository, never()).save(any());
    }

    // 1. Test when there are orders in the database
    @Test
    void findAllOrders_shouldReturnMappedList() {
        // Arrange: Prepare mock entities
        Order order1 = Order.builder()
                .id("1")
                .clientName("Rong")
                .fruitList(List.of(new Fruit("apple", 10)))
                .deliveryDate(LocalDate.now().plusDays(1))
                .build();

        Order order2 = Order.builder()
                .id("2")
                .clientName("John")
                .fruitList(List.of(new Fruit("orange", 5)))
                .deliveryDate(LocalDate.now().plusDays(2))
                .build();

        // Stubbing the repository to return the mock entities
        when(repository.findAll()).thenReturn(List.of(order1, order2));

        // Act: Execute the service method
        List<OrderResponse> result = orderService.findAllOrders();

        // Assert: Verify the results and mapping logic
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo("1");
        assertThat(result.get(0).clientName()).isEqualTo("Rong");
        assertThat(result.get(1).clientName()).isEqualTo("John");

        // Ensure repository.findAll() was called exactly once
        verify(repository, times(1)).findAll();
    }

    // 2. Test when the database is empty (Acceptance Criteria: return empty array)
    @Test
    void findAllOrders_whenNoOrders_shouldReturnEmptyList() {
        // Arrange: Repository returns an empty list
        when(repository.findAll()).thenReturn(List.of());

        // Act
        List<OrderResponse> result = orderService.findAllOrders();

        // Assert: Ensure it's an empty list, not null
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(repository, times(1)).findAll();
    }



}
