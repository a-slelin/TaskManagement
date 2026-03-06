package a.slelin.work.task.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Entity(name = Project.ENTITY_NAME)
@EqualsAndHashCode(callSuper = false)
@Table(name = Project.TABLE_NAME,
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "owner_id"}))
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

    @NotNull
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User user;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "project",
            orphanRemoval = true)
    private List<Task> tasks;

    public void addTask(Task task) {
        if (tasks == null) {
            tasks = new ArrayList<>();
        }

        if (task != null && !tasks.contains(task)) {
            tasks.add(task);
            task.setProject(this);
        }
    }

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
