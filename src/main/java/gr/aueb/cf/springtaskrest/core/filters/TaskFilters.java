package gr.aueb.cf.springtaskrest.core.filters;

import gr.aueb.cf.springtaskrest.core.enums.TaskStatus;
import lombok.*;
import org.springframework.lang.Nullable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskFilters extends GenericFilters {
    @Nullable
    private String uuid;

    @Nullable
    private String title;

    //TODO: List<TaskStatus> for accepting multiple status in filtering
    @Nullable
    private TaskStatus status;
}
