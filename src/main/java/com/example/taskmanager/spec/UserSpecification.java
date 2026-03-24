package com.example.taskmanager.spec;

import com.example.taskmanager.entity.User;
import com.example.taskmanager.enums.Gender;
import com.example.taskmanager.enums.Role;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class UserSpecification {

    public static Specification<User> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(keyword)) return null;
            String lowerKeyword = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("username")), lowerKeyword),
                    cb.like(cb.lower(root.get("email")), lowerKeyword),
                    cb.like(cb.lower(root.get("fullName")), lowerKeyword)
            );
        };
    }

    public static Specification<User> hasGender(Gender gender) {
        return (root, query, cb) -> {
            if (gender == null) return null;
            return cb.equal(root.get("gender"), gender);
        };
    }

    public static Specification<User> hasPhone(String phone) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(phone)) return null;
            return cb.like(root.get("phone"), "%" + phone + "%");
        };
    }

    public static Specification<User> hasRole(Role role) {
        return (root, query, cb) -> {
            if (role == null) return null;
            return cb.isMember(role, root.get("roles"));
        };
    }
}