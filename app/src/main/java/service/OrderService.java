package service;

import dto.OrderRequest;
import model.*;
import repository.OrderRepository;
import repository.ProductRepository;
import repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // Constructor Injection
    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    /**
     * PLACING AN ORDER
     * This method is Transactional. If an error occurs (e.g., out of stock),
     * the database rolls back to its previous state.
     */
    @Transactional
    public Order placeOrder(OrderRequest request) {

        // 1. Validate User
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Create the Order object (initially empty)
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        // 3. Process each item in the request
        for (OrderRequest.OrderItemRequest itemRequest : request.getItems()) {

            // Fetch Product
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // CHECK INVENTORY (Crucial Step)
            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }

            // REDUCE STOCK
            // Since we are inside a Transaction, this update is pending until the method finishes successfully.
            product.setStockQuantity(product.getStockQuantity() - itemRequest.getQuantity());
            productRepository.save(product);

            // Create OrderItem
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .price(product.getPrice()) // Snapshot the CURRENT price!
                    .build();

            orderItems.add(orderItem);

            // Calculate Totals
            BigDecimal itemTotal = product.getPrice().multiply(new BigDecimal(itemRequest.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        // 4. Finalize Order Data
        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        // 5. Save (Cascading will save the OrderItems automatically)
        return orderRepository.save(order);
    }

    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}
