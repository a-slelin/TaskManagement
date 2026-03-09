package a.slelin.work.task.management.entity;

import a.slelin.work.task.management.util.validate.Phone;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
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
    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "user",
            cascade = CascadeType.ALL)
    private List<Project> projects;

    @SuppressWarnings("unused")
    public void addProject(Project project) {
        if (projects == null) {
            projects = new ArrayList<>();
        }

        if (project != null && !projects.contains(project)) {
            projects.add(project);
            project.setUser(this);
        }
    }

    @SuppressWarnings("unused")
    public void removeProject(Project project) {
        if (projects == null) {
            projects = new ArrayList<>();
        }


        if (project != null) {
            projects.remove(project);
            project.setUser(null);
        }
    }

    public User(UUID id, String username, String password,
                Gender gender, String phone, String email,
                List<Project> projects) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.gender = Objects.requireNonNullElse(gender, Gender.UNDEFINED);
        this.phone = phone;
        this.email = email;
        this.projects = projects;
    }
}
