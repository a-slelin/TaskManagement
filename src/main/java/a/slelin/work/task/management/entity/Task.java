package a.slelin.work.task.management.entity;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Task implements Entity<Long> {

    private Long id;

    private String title;

    private String description;

    private Status status;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Project project;

    @SuppressWarnings("unused")
    public static Task byId(Long id) {
        Task task = new Task();
        task.setId(id);
        return task;
    }
}
