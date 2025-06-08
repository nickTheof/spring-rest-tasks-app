package gr.aueb.cf.springtaskrest.core.specifications;

import gr.aueb.cf.springtaskrest.core.enums.TaskStatus;
import gr.aueb.cf.springtaskrest.model.Task;
import gr.aueb.cf.springtaskrest.model.User;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class TaskSpecification {
    private TaskSpecification() {

    }

    private static final List<String> allowedFields = List.of("uuid", "title");

    public static Specification<Task> tasksFieldLike(String field, String value) {
        return ((root, query, builder) -> {
            if ((field == null || field.isBlank() || !allowedFields.contains(field))) return builder.conjunction();
            if (value == null || value.isBlank()) return builder.conjunction();
            return builder.like(builder.upper(root.get(field)), "%" + value.toUpperCase() + "%");
        });
    }

    public static Specification<Task> tasksUserIsActive(Boolean isActive) {
        return ((root, query, builder) -> {
            if (isActive == null) return builder.conjunction();
            Join<Task, User> user = root.join("user");
            return builder.equal(user.get("isActive"), isActive);
        });
    }

    public static Specification<Task> tasksUserUuid(String uuid) {
        return ((root, query, builder) -> {
            if (uuid == null) return builder.conjunction();
            Join<Task, User> user = root.join("user");
            return builder.equal(user.get("uuid"), uuid);
        });
    }

//    public static Specification<Task> tasksStatusIs(TaskStatus status) {
//        return ((root, query, builder) -> {
//            if (status == null) return builder.conjunction();
//            return builder.equal(root.get("status"), status);
//        });
//    }

    public static Specification<Task> taskStatusIn(List<TaskStatus> statusList) {
        return (root, query, builder) -> {
            if (statusList == null || statusList.isEmpty()) return builder.conjunction();
            return root.get("role").in(statusList);
        };
    }

}
