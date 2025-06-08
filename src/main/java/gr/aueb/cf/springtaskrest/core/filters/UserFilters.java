package gr.aueb.cf.springtaskrest.core.filters;


import gr.aueb.cf.springtaskrest.core.enums.Role;
import lombok.*;
import org.springframework.lang.Nullable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserFilters extends GenericFilters {
    @Nullable
    String uuid;

    @Nullable
    String username;

    @Nullable
    Role role;

    @Nullable
    Boolean active;
}
