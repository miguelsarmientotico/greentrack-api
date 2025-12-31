package com.greentrack.greentrack_api.repository.specifications;

import com.greentrack.greentrack_api.dto.user.UserFilterDTO;
import com.greentrack.greentrack_api.entity.UserEntity;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class UserSpecifications {

  private static final Logger LOG = LoggerFactory.getLogger(UserSpecifications.class);

  private static final String FIELD_ID = "id";
  private static final String FIELD_USERNAME = "username";
  private static final String FIELD_FULLNAME = "fullName";
  private static final String FIELD_EMAIL = "email";
  private static final String FIELD_STATUS = "status";
  private static final String FIELD_ROLE = "role";

  public static Specification<UserEntity> getUsers(UserFilterDTO filter) {
    return (root, query, criteriaBuilder) -> {
      if (filter == null) {
        return criteriaBuilder.conjunction();
      }

      List<Predicate> predicates = new ArrayList<>();

      if (StringUtils.hasText(filter.id())) {
        try {
          UUID uuid = UUID.fromString(filter.id());
          predicates.add(criteriaBuilder.equal(root.get(FIELD_ID), uuid));
        } catch (IllegalArgumentException e) {
          LOG.warn("Intento de filtro con UUID inválido: {}", filter.id());
          return criteriaBuilder.disjunction();
        }
      }

      if (StringUtils.hasText(filter.username())) {
        predicates.add(
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get(FIELD_USERNAME)),
                buildLikePattern(filter.username())));
      }

      if (StringUtils.hasText(filter.fullName())) {
        predicates.add(
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get(FIELD_FULLNAME)),
                buildLikePattern(filter.fullName())));
      }

      if (StringUtils.hasText(filter.email())) {
        predicates.add(
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get(FIELD_EMAIL)), buildLikePattern(filter.email())));
      }

      if (filter.status() != null) {
        predicates.add(criteriaBuilder.equal(root.get(FIELD_STATUS), filter.status()));
      }

      if (filter.role() != null) {
        predicates.add(criteriaBuilder.equal(root.get(FIELD_ROLE), filter.role()));
      }

      if (StringUtils.hasText(filter.globalSearch())) {
        String searchPattern = buildLikePattern(filter.globalSearch());
        Predicate globalPredicate =
            criteriaBuilder.or(
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(FIELD_USERNAME)), searchPattern),
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(FIELD_FULLNAME)), searchPattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get(FIELD_EMAIL)), searchPattern));
        predicates.add(globalPredicate);
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }

  private static String buildLikePattern(String term) {
    if (term == null) return "%";
    String escapedTerm = term.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    return "%" + escapedTerm.toLowerCase() + "%";
  }
}
