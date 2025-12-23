package controller;

import dto.OrderRequest;
import model.Order;
import service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Tells Spring this class handles Web Requests and returns JSON
@RequestMapping("/api/orders") // Base URL: http://localhost:8080/api/orders
public class OrderController {

    private final OrderService orderService;

    // Constructor Injection
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Endpoint to Place a New Order
     * URL: POST /api/orders
     * Body: JSON (OrderRequest)
     */
    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestBody OrderRequest orderRequest) {
        try {
            Order newOrder = orderService.placeOrder(orderRequest);
            // Return HTTP 200 OK with the created order
            return ResponseEntity.ok(newOrder);
        } catch (RuntimeException e) {
            // Return HTTP 400 Bad Request if stock is low or user not found
            // In a real app, you would use a global @ControllerAdvice for this
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint to Get History for a User
     * URL: GET /api/orders/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable Long userId) {
        List<Order> orders = orderService.getUserOrders(userId);
        return ResponseEntity.ok(orders);
    }
}
