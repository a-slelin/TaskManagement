package a.slelin.work.task.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Entity(name = Task.ENTITY_NAME)
@EqualsAndHashCode(callSuper = false)
@Table(name = Task.TABLE_NAME,
        uniqueConstraints = @UniqueConstraint(columnNames = {"title", "project_id"}))
public class Task extends Audit {

    public static final String ENTITY_NAME = "Task";

    public static final String TABLE_NAME = "task";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 255)
    @Column(nullable = false)
    private String title;

    private String description;

    @NotNull
    @Column(nullable = false, length = 11)
    @Convert(converter = StatusConverter.class)
    private Status status;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
}
