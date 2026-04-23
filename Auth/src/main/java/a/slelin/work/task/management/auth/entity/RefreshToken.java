package a.slelin.work.task.management.auth.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = RefreshToken.TABLE_NAME)
@Entity(name = RefreshToken.ENTITY_NAME)
public class RefreshToken {

    public static final String ENTITY_NAME = "RefreshToken";

    public static final String TABLE_NAME = "refresh_token";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Size(min = 3, max = 255)
    @Column(nullable = false, unique = true)
    private String token;

    @NotNull
    @Column(nullable = false,
            updatable = false,
            name = "created_at")
    private LocalDateTime createdAt;

    @NotNull
    @Column(nullable = false, name = "expiry_date")
    private LocalDateTime expiryDate;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    protected void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
