package repository;

import model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Fetch all orders belonging to a specific user ID
    // SQL: SELECT * FROM orders WHERE user_id = ?
    List<Order> findByUserId(Long userId);

    // Find all orders sorted by date (newest first)
    List<Order> findAllByOrderByOrderDateDesc();
}
