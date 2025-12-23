package repository;

import com.example.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // --- Approach 1: Derived Query Methods (Simple) ---

    // Find products containing a keyword (e.g., "Phone") ignoring case
    // SQL: SELECT * FROM products WHERE lower(name) LIKE lower('%keyword%')
    List<Product> findByNameContainingIgnoreCase(String keyword);

    // Find products by category name (assuming you add a Category entity later)
    // List<Product> findByCategoryName(String categoryName);


    // --- Approach 2: @Query with JPQL (Complex/Custom) ---

    // JPQL targets the Java Entity (Product), not the SQL table (products).
    // This query finds generic products within a price range.
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findProductsInPriceRange(@Param("minPrice") BigDecimal min,
                                           @Param("maxPrice") BigDecimal max);

    // Searching both name AND description
    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchProducts(@Param("keyword") String keyword);
}
