package cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.service;

import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.DTO.OrderRequest;
import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.DTO.OrderResponse;
import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.entities.Fruit;
import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.entities.Order;
import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.exception.ResourceNotFoundException;
import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

        LocalDate deliveryDate = LocalDate.of(2026, 1, 20);
        OrderRequest request = new OrderRequest("Rong", List.of(new Fruit("Apple", 1)), deliveryDate);

        Order savedOrder = new Order("ID123", "Rong", request.fruitList(), deliveryDate);
        when(repository.save(any())).thenReturn(savedOrder);

        OrderResponse response = orderService.createOrder(request);

        assertNotNull(response);
        assertEquals(deliveryDate, response.deliveryDate());
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

    @Test
    void findOrderById_whenIdExists_shouldReturnOrder() {
        LocalDate deliveryDate = LocalDate.of(2026, 1, 20);
        Order order = Order
                .builder()
                .id("1")
                .clientName("Rong")
                .fruitList(List.of(new Fruit("apple",10)))
                .deliveryDate(deliveryDate)
                .build();

        when(repository.findById("1")).thenReturn(Optional.of(order));

        OrderResponse response = orderService.findOrderById("1");

        assertNotNull(response);
        assertEquals("Rong",response.clientName());
        assertEquals("apple",response.fruitList().get(0).name());
        assertEquals(10,response.fruitList().get(0).number());
        assertEquals(deliveryDate,response.deliveryDate());

    }
    @Test
    void findOrderById_whenIdDoesNotExist_shouldThrowResourceNotFoundException() {
        // Arrange: Mock the repository to return an empty Optional
        String id = "non-existent-id";
        // Using Optional.empty() to simulate that no document was found in MongoDB
        when(repository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert: Verify that our custom ResourceNotFoundException is thrown
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            orderService.findOrderById(id);
        });

        // Verify the exception message contains the missing ID for better debugging
        assertTrue(exception.getMessage().contains(id));

        // Ensure findById was called once
        verify(repository, times(1)).findById(id);
    }

    @Test
    void updateOrderById_whenIdExists_shouldUpdateOrder() {
        // 1. Arrange: Use fixed date to ensure stability
        String orderId = "1";
        LocalDate deliveryDate = LocalDate.of(2026, 1, 20);

        Order existedOrder = Order.builder()
                .id(orderId)
                .clientName("Rong")
                .fruitList(List.of(new Fruit("apple", 10)))
                .deliveryDate(deliveryDate)
                .build();

        OrderRequest updateRequest = new OrderRequest(
                "Rong",
                List.of(new Fruit("Peach", 12)),
                deliveryDate
        );

        // Corrected: Match the actual ID "1" instead of empty string
        when(repository.findById(orderId)).thenReturn(Optional.of(existedOrder));

        // Mock save to return the object being saved
        when(repository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. Act
        OrderResponse response = orderService.updateOrderById(orderId, updateRequest);

        // 3. Assert
        assertNotNull(response);
        assertEquals(orderId, response.id()); // Ensure ID remains the same
        assertEquals("Peach", response.fruitList().get(0).name());
        assertEquals(12, response.fruitList().get(0).number());

        // Verify interactions
        verify(repository).findById(orderId);
        verify(repository).save(any(Order.class));
    }

    @Test
    void updateOrderById_whenIdDoesNotExist_shouldThrowResourceNotFoundException() {
        // 1. Arrange
        String nonExistentId = "999";
        LocalDate deliveryDate = LocalDate.of(2026, 1, 20);
        OrderRequest updateRequest = new OrderRequest(
                "Rong",
                List.of(new Fruit("Peach", 12)),
                deliveryDate
        );

        // Mock: When findById is called with "999", return an empty Optional
        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        // 2. Act & 3. Assert
        // Verify that the custom exception is thrown
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            orderService.updateOrderById(nonExistentId, updateRequest);
        });

        // Verify the exception message is correct
        assertEquals("Order with ID [999] not found", exception.getMessage());

        // Verify that save() was NEVER called because the ID was invalid
        verify(repository, never()).save(any(Order.class));
    }

    @Test
    void delete_WhenIdExists_ShouldInvokeDelete() {
        String id = "1";
        when(repository.existsById(id)).thenReturn(true);
        orderService.deleteOrderById(id);
        verify(repository,times(1)).deleteById(id);

    }
    @Test
    void deleteOrderById_whenIdDoesNotExist_shouldThrowResourceNotFoundException() {
        // Arrange
        String id = "999";
        when(repository.existsById(id)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.deleteOrderById(id);
        });

        // Verify: Ensure deleteById is NEVER called if the ID doesn't exist
        verify(repository, never()).deleteById(anyString());
    }







}
