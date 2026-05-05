package a.slelin.work.task.management.auth.entity;

import a.slelin.work.task.management.core.entity.Audit;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Table(name = Role.TABLE_NAME)
@Entity(name = Role.ENTITY_NAME)
@EqualsAndHashCode(callSuper = false)
public class Role extends Audit {

    public static final String ENTITY_NAME = "Role";

    public static final String TABLE_NAME = "roles";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 6, max = 50)
    @Pattern(regexp = "^ROLE_[A-Z_]+$")
    @Column(nullable = false,
            unique = true,
            length = 50)
    private String name;

    @Size(min = 3, max = 255)
    private String description;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    @SuppressWarnings("unused")
    public void addUser(User user) {
        if (users == null) {
            users = new HashSet<>();
        }

        if (user == null || users.contains(user)) {
            return;
        }

        users.add(user);

        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }

        user.getRoles().add(this);
    }

    @SuppressWarnings("unused")
    public void removeUser(User user) {
        if (users == null) {
            users = new HashSet<>();
        }

        if (user == null || !users.contains(user)) {
            return;
        }

        users.remove(user);

        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }

        user.getRoles().remove(this);
    }
}
