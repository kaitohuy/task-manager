package com.example.taskmanager.spec;

import com.example.taskmanager.entity.User;
import com.example.taskmanager.enums.Gender;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> titleContains(String keyword) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%");
    }

    public static Specification<User> hasGender(Gender gender) {
        return (root, query, cb) ->
                cb.equal(root.get("user").get("gender"), gender);
    }
}
