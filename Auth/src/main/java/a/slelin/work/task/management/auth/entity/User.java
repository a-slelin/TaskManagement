package a.slelin.work.task.management.auth.entity;

import a.slelin.work.task.management.auth.util.validate.Phone;
import a.slelin.work.task.management.core.entity.Audit;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Objects;
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
    @Column(length = 50,
            nullable = false,
            unique = true)
    private String username;

    @NotBlank
    @ToString.Exclude
    @Column(nullable = false)
    @Size(min = 8, max = 255)
    private String password;

    @NotNull
    @Column(length = 9,
            nullable = false)
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

    @SuppressWarnings("unused")
    public User(UUID id, String username, String password,
                Gender gender, String phone, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.gender = Objects.requireNonNullElse(gender, Gender.UNDEFINED);
        this.phone = phone;
        this.email = email;
    }
}
