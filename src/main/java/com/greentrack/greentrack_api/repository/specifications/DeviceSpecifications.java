package com.greentrack.greentrack_api.repository.specifications;

import com.greentrack.greentrack_api.dto.device.DeviceFilterDTO;
import com.greentrack.greentrack_api.entity.DeviceEntity;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class DeviceSpecifications {

  private static final Logger LOG = LoggerFactory.getLogger(DeviceSpecifications.class);

  private static final String FIELD_ID = "id";
  private static final String FIELD_NAME = "name";
  private static final String FIELD_BRAND = "brand";
  private static final String FIELD_TYPE = "type";
  private static final String FIELD_STATUS = "status";

  public static Specification<DeviceEntity> getDevices(DeviceFilterDTO filter) {
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
      if (StringUtils.hasText(filter.name())) {
        predicates.add(
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get(FIELD_NAME)), buildLikePattern(filter.name())));
      }
      if (StringUtils.hasText(filter.brand())) {
        predicates.add(
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get(FIELD_BRAND)), buildLikePattern(filter.brand())));
      }
      if (filter.type() != null) {
        predicates.add(criteriaBuilder.equal(root.get(FIELD_TYPE), filter.type()));
      }
      if (filter.status() != null) {
        predicates.add(criteriaBuilder.equal(root.get(FIELD_STATUS), filter.status()));
      }
      if (StringUtils.hasText(filter.globalSearch())) {
        String searchPattern = buildLikePattern(filter.globalSearch());
        Predicate globalPredicate =
            criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get(FIELD_NAME)), searchPattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get(FIELD_BRAND)), searchPattern));
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
