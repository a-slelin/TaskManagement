package a.slelin.work.task.management.auth.entity;

import a.slelin.work.task.management.auth.util.SystemRole;
import a.slelin.work.task.management.auth.util.validate.Phone;
import a.slelin.work.task.management.core.entity.Audit;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@Table(name = User.TABLE_NAME)
@Entity(name = User.ENTITY_NAME)
@EqualsAndHashCode(callSuper = false)
public class User extends Audit {

    public static final String ENTITY_NAME = "User";

    public static final String TABLE_NAME = "users";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Size(min = 3, max = 50)
    @Pattern(regexp = "[A-Za-z0-9._-]+")
    @Column(length = 50, nullable = false, unique = true)
    private String username;

    @NotBlank
    @ToString.Exclude
    @Column(nullable = false)
    @Size(min = 8, max = 255)
    private String password;

    @NotNull
    @Column(length = 9, nullable = false)
    @Convert(converter = GenderConverter.class)
    private Gender gender;

    @SuppressWarnings("unused")
    public void setGender(Gender gender) {
        this.gender = Objects.requireNonNullElse(gender, Gender.UNDEFINED);
    }

    @Phone
    @Size(min = 5, max = 15)
    @Column(length = 15, unique = true)
    private String phone;

    @Email
    @Size(min = 5, max = 50)
    @Column(length = 50, unique = true)
    private String email;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "role_id", nullable = false)
    )
    private Set<Role> roles;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "user",
            orphanRemoval = true,
            cascade = CascadeType.REMOVE)
    private Set<RefreshToken> refreshTokens;

    @SuppressWarnings("unused")
    public User(UUID id, String username, String password,
                Gender gender, String phone, String email,
                Set<Role> roles, Set<RefreshToken> refreshTokens) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.gender = Objects.requireNonNullElse(gender, Gender.UNDEFINED);
        this.phone = phone;
        this.email = email;
        this.roles = roles;
        this.refreshTokens = refreshTokens;
    }

    @SuppressWarnings("unused")
    public void addRole(Role role) {
        if (roles == null) {
            roles = new HashSet<>();
        }

        if (role == null || roles.contains(role)) {
            return;
        }

        roles.add(role);

        if (role.getUsers() == null) {
            role.setUsers(new HashSet<>());
        }

        role.getUsers().add(this);
    }

    @SuppressWarnings("unused")
    public void removeRole(Role role) {
        if (roles == null) {
            roles = new HashSet<>();
        }

        if (role == null || !roles.contains(role)) {
            return;
        }

        roles.remove(role);

        if (role.getUsers() == null) {
            role.setUsers(new HashSet<>());
        }

        role.getUsers().remove(this);
    }

    @SuppressWarnings("unused")
    public void addRefreshToken(RefreshToken refreshToken) {
        if (refreshTokens == null) {
            refreshTokens = new HashSet<>();
        }

        if (refreshToken == null || refreshTokens.contains(refreshToken)) {
            return;
        }

        refreshTokens.add(refreshToken);

        refreshToken.setUser(this);
    }

    @SuppressWarnings("unused")
    public void removeRefreshToken(RefreshToken refreshToken) {
        if (refreshTokens == null) {
            refreshTokens = new HashSet<>();
        }

        if (refreshToken == null || !refreshTokens.contains(refreshToken)) {
            return;
        }

        refreshTokens.remove(refreshToken);
        refreshToken.setUser(null);
    }

    public boolean isAdmin() {
        return SystemRole.hasAdminRole(roles);
    }
}
