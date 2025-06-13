package epam.entity;


import epam.enums.Role;
import epam.util.GenerateUsername;
import epam.util.UsernameGeneratorListener;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@EntityListeners(value = {UsernameGeneratorListener.class})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @GenerateUsername
    @Column(nullable = false, unique = true, updatable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean isActive;
}

