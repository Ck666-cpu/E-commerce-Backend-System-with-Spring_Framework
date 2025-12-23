package model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "app_users") // 'user' is often a reserved SQL keyword
@Data // Lombok: Generates getters, setters, toString, etc.
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; // Store the BCrypt encoded hash, NOT plain text

    private String fullName;

    private String address;

    @Enumerated(EnumType.STRING)
    private Role role; // ADMIN or CUSTOMER

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Optional: Link to orders so you can say user.getOrders()
    // mappedBy refers to the field name in the Order class
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}