package com.codersthathum.chat_app_service.repository.user;

import com.codersthathum.chat_app_service.dto.user.UserParam;
import com.codersthathum.chat_app_service.entity.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<User> param(UserParam param) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (param.getId() != null) {
                predicates.add(cb.equal(root.get("id"), param.getId()));
            }

            if (param.getName() != null) {
                predicates.add(cb.equal(root.get("name"), param.getName()));
            }

            if (param.getEmail() != null) {
                predicates.add(cb.equal(root.get("email"), param.getEmail()));
            }

            if (param.getIsActive() != null) {
                predicates.add(cb.equal(root.get("isActive"), param.getIsActive()));
            }

            if (param.getRefreshToken() != null) {
                predicates.add(cb.equal(root.get("refreshToken"), param.getRefreshToken()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
