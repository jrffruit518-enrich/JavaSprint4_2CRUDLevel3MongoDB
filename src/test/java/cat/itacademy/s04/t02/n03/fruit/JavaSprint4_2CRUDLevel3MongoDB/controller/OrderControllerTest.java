package cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.controller;

import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.DTO.OrderRequest;
import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.DTO.OrderResponse;
import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.entities.Fruit;
import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.entities.Order;
import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.exception.ResourceNotFoundException;
import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired // Changed: Use real ObjectMapper
    private ObjectMapper objectMapper;

    @MockitoBean // Changed: Mock the service to isolate Controller
    private OrderService orderService;

    // 1. Success case: Should return 201 Created
    @Test
    void createOrder_whenValidRequest_shouldReturnOrder() throws Exception {
        OrderRequest request = new OrderRequest("Rong",
                List.of(new Fruit("apple", 12)),
                LocalDate.now().plusDays(1));
        OrderResponse response = new OrderResponse(
                "1",
                "Rong",
                List.of(new Fruit("apple", 12)),
                LocalDate.now().plusDays(1));

        when(orderService.createOrder(any(OrderRequest.class)))
                .thenReturn(response);
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.clientName").value("Rong"))
                .andExpect(jsonPath("$.fruitList[0].name").value("apple"))
                .andExpect(jsonPath("$.fruitList[0].number").value(12));

    }

    // 2. Validation case: Missing client name should return 400 Bad Request
    @Test
    void createOrder_whenClientNameIsBlank_shouldReturnBadRequest() throws Exception {
        OrderRequest invalidRequest = new OrderRequest("", // Blank name
                List.of(new Fruit("apple", 12)),
                LocalDate.now().plusDays(1));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    // 3. Validation case: Fruit quantity <= 0 should return 400 Bad Request
    @Test
    void createOrder_whenFruitQuantityIsInvalid_shouldReturnBadRequest() throws Exception {
        OrderRequest invalidRequest = new OrderRequest("Rong",
                List.of(new Fruit("apple", 0)), // Invalid quantity
                LocalDate.now().plusDays(1));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    // 4. Business Logic case: Date before tomorrow should return 400 Bad Request
    @Test
    void createOrder_whenDateIsInvalid_shouldReturnBadRequest() throws Exception {
        OrderRequest invalidDateRequest = new OrderRequest("Rong",
                List.of(new Fruit("apple", 10)),
                LocalDate.now()); // Today is invalid

        // Stubbing the service to throw exception as per service logic
        when(orderService.createOrder(any(OrderRequest.class)))
                .thenThrow(new IllegalArgumentException("Delivery date must be tomorrow or later"));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDateRequest)))
                .andExpect(status().isBadRequest());
    }

    // 1. Success case: Should return 200 OK and a list of orders in JSON format
    @Test
    void findAllOrders_shouldReturnOkAndList() throws Exception {
        // Arrange: Prepare a list with one order response
        OrderResponse response = new OrderResponse(
                "123",
                "Rong",
                List.of(new Fruit("banana", 5)),
                LocalDate.now().plusDays(2)
        );
        List<OrderResponse> allOrders = List.of(response);

        // Stubbing: When service is called, return the list
        when(orderService.findAllOrders()).thenReturn(allOrders);

        // Act & Assert
        mockMvc.perform(get("/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Acceptance Criteria: HTTP 200 OK
                .andExpect(jsonPath("$.size()").value(1)) // Verify array size
                .andExpect(jsonPath("$[0].id").value("123"))
                .andExpect(jsonPath("$[0].clientName").value("Rong"))
                .andExpect(jsonPath("$[0].fruitList[0].name").value("banana"));
    }

    // 2. Empty case: Should return 200 OK and an empty array []
    @Test
    void findAllOrders_whenEmpty_shouldReturnEmptyArray() throws Exception {
        // Arrange: Stubbing service to return an empty list
        when(orderService.findAllOrders()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Acceptance Criteria: HTTP 200 OK
                .andExpect(jsonPath("$").isArray()) // Verify it is an array
                .andExpect(jsonPath("$.size()").value(0)); // Acceptance Criteria: empty array
    }

    @Test
    void findOrderById_whenIdExists_shouldReturnOrder() throws Exception {
        LocalDate deliveryDate = LocalDate.of(2026, 1, 20);
        // 假设你的 JSON 输出格式是 "yyyy-MM-dd"
        String expectedDate = "2026-01-20";
        OrderResponse response = new OrderResponse("1",
                "Rong",
                List.of(new Fruit("apple", 10)),
                deliveryDate);
        when(orderService.findOrderById("1")).thenReturn(response);
        mockMvc.perform(get("/orders/{id}","1")
                        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientName").value("Rong"))
                .andExpect(jsonPath("$.fruitList[0].name").value("apple"))
                .andExpect(jsonPath("$.fruitList[0].number").value(10))
                .andExpect(jsonPath("$.deliveryDate").value(expectedDate));
    }

    @Test
    void findOrderById_whenIdDoesNotExist_shouldReturn404() throws Exception {
        // Arrange: Mock the service to throw the custom exception
        String nonExistentId = "999";
        when(orderService.findOrderById(nonExistentId))
                .thenThrow(new ResourceNotFoundException(nonExistentId));

        // Act & Assert
        mockMvc.perform(get("/orders/{id}", nonExistentId)
                        .accept(MediaType.APPLICATION_JSON)) // Use accept instead of contentType
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Order with ID [999] not found"));
    }

    @Test
    void updateOrder_whenIdExists_shouldReturn200() throws Exception {
        // Arrange
        String orderId = "1";
        LocalDate futureDate = LocalDate.of(2026, 1, 20);
        OrderRequest request = new OrderRequest("Rong",
                List.of(new Fruit("Peach", 12)), futureDate);
        OrderResponse response = new OrderResponse(orderId, "Rong",
                List.of(new Fruit("Peach", 12)), futureDate);

        // Correctly use eq() when combined with any()
        when(orderService.updateOrderById(eq(orderId), any(OrderRequest.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientName").value("Rong"))
                .andExpect(jsonPath("$.fruitList[0].name").value("Peach"));
    }

    @Test
    void updateOrder_whenIdDoesNotExist_shouldReturn404() throws Exception {
        // Arrange
        String nonExistentId = "999";
        OrderRequest request = new OrderRequest("Rong",
                List.of(new Fruit("Apple", 1)), LocalDate.now().plusDays(2));

        when(orderService.updateOrderById(eq(nonExistentId), any(OrderRequest.class)))
                .thenThrow(new ResourceNotFoundException(nonExistentId));

        // Act & Assert
        mockMvc.perform(put("/orders/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order with ID [999] not found"));
    }

    @Test
    void delete_WhenIdExists_ShouldReturn204() throws Exception {
        String id = "1";
        doNothing().when(orderService).deleteOrderById(id);
        mockMvc.perform(delete("/orders/{id}",id))
                .andExpect(status().isNoContent());
        verify(orderService,times(1)).deleteOrderById(id);
    }
    @Test
    void delete_WhenIdDoesNotExist_ShouldReturn404() throws Exception {
        String id = "1";

        // Mock failure: Service throws ResourceNotFoundException
        doThrow(new ResourceNotFoundException(id))
                .when(orderService).deleteOrderById(id);

        mockMvc.perform(delete("/orders/{id}", id))
                .andExpect(status().isNotFound()); // 验证返回 404
    }


}
