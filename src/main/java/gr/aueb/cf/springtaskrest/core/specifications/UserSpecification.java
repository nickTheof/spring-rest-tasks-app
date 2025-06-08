package gr.aueb.cf.springtaskrest.core.specifications;

import gr.aueb.cf.springtaskrest.core.enums.Role;
import gr.aueb.cf.springtaskrest.model.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class UserSpecification {
    private UserSpecification() {

    }

    private static final List<String> allowedFields = List.of("uuid", "username");

    public static Specification<User> usersFieldLike(String field, String value) {
        return ((root, query, builder) -> {
            if (field == null || field.isBlank() || !allowedFields.contains(field)) return builder.conjunction();
            if (value == null || value.isBlank()) return builder.conjunction();
            return builder.like(builder.upper(root.get(field)), "%" + value.toUpperCase() + "%");
        });
    }

    public static Specification<User> usersStatusIs(Boolean status) {
        return ((root, query, builder) -> {
            if (status == null) return builder.conjunction();
            return builder.equal(root.get("status"), status);
        });
    }

    public static Specification<User> usersRoleIs(Role role) {
        return ((root, query, builder) -> {
            if (role == null) return builder.conjunction();
            return builder.equal(root.get("role"), role);
        });
    }

}
