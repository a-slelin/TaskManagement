package a.slelin.work.task.management.entity;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class User implements Entity<UUID> {

    private UUID id;

    private String username;

    private String password;

    private Gender gender;

    private String phone;

    private String email;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Project> projects;

    @SuppressWarnings("unused")
    public static User byId(String id) {
        return byId(UUID.fromString(id));
    }

    public static User byId(UUID id) {
        User user = new User();
        user.setId(id);
        return user;
    }
}
