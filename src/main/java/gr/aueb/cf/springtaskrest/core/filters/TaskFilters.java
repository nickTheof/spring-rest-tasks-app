package gr.aueb.cf.springtaskrest.core.filters;

import gr.aueb.cf.springtaskrest.core.enums.TaskStatus;
import lombok.*;
import org.springframework.lang.Nullable;
import java.util.List;

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

    @Nullable
    private List<TaskStatus> status;

    @Nullable
    private Boolean userIsActive;

    @Nullable
    private String userUuid;
}
