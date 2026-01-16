package cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.controller;

import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.DTO.OrderRequest;
import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.DTO.OrderResponse;
import cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.entities.Fruit;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ObjectMapper objectMapper;

    @Autowired
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
}
