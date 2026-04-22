package a.slelin.work.task.management.api.entity;

import a.slelin.work.task.management.core.entity.Audit;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Entity(name = Project.ENTITY_NAME)
@EqualsAndHashCode(callSuper = false)
@Table(name = Project.TABLE_NAME,
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "user_id"}))
public class Project extends Audit {

    public static final String ENTITY_NAME = "Project";

    public static final String TABLE_NAME = "project";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 255)
    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false, name = "user_id")
    private UUID user;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "project",
            orphanRemoval = true)
    private List<Task> tasks;

    @SuppressWarnings("unused")
    public void addTask(Task task) {
        if (tasks == null) {
            tasks = new ArrayList<>();
        }

        if (task != null && !tasks.contains(task)) {
            tasks.add(task);
            task.setProject(this);
        }
    }

    @SuppressWarnings("unused")
    public void removeTask(Task task) {
        if (tasks == null) {
            tasks = new ArrayList<>();
        }

        if (task != null) {
            tasks.remove(task);
            task.setProject(null);
        }
    }
}
