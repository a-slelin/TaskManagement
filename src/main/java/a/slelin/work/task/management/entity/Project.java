package a.slelin.work.task.management.entity;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Project implements Entity {

    private Long id;

    private String name;

    private String description;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Task> tasks;
}
