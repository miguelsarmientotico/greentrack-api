package com.greentrack.greentrack_api.repository.specifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.greentrack.greentrack_api.dto.loan.LoanFilterDTO;
import com.greentrack.greentrack_api.entity.DeviceEntity;
import com.greentrack.greentrack_api.entity.LoanEntity;
import com.greentrack.greentrack_api.entity.UserEntity;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LoanSpecifications {

    private static final Logger LOG = LoggerFactory.getLogger(LoanSpecifications.class);

    // Constantes - Entidad Loan
    private static final String FIELD_ID = "id";
    private static final String FIELD_LOAN_STATUS = "status";
    private static final String FIELD_ISSUED_AT = "issuedAt";
    private static final String FIELD_RETURNED_AT = "returnedAt";
    
    // Constantes - Relaciones
    private static final String REL_DEVICE = "device";
    private static final String REL_EMPLOYEE = "employee";

    // Constantes - Entidad Employee
    private static final String FIELD_EMPLOYEE_ID = "id";
    private static final String FIELD_USERNAME = "username";
    private static final String FIELD_FULLNAME = "fullName";
    private static final String FIELD_EMAIL = "email";
    private static final String FIELD_ROLE = "role";

    // Constantes - Entidad Device
    private static final String FIELD_DEVICE_ID = "id";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_BRAND = "brand";
    private static final String FIELD_TYPE = "type";
    private static final String FIELD_STATUS = "status";

    public static Specification<LoanEntity> getLoans(LoanFilterDTO filter) {
        return (root, query, criteriaBuilder) -> {
            if (filter == null) {
                return criteriaBuilder.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();
            
            Join<LoanEntity, DeviceEntity> deviceJoin = root.join(REL_DEVICE, JoinType.LEFT);
            Join<LoanEntity, UserEntity> employeeJoin = root.join(REL_EMPLOYEE, JoinType.LEFT);

            if (StringUtils.hasText(filter.id())) {
                try {
                    UUID uuid = UUID.fromString(filter.id());
                    predicates.add(criteriaBuilder.equal(root.get(FIELD_ID), uuid));
                } catch (IllegalArgumentException e) {
                    LOG.warn("Filtro de Loan con ID numérico inválido ignorado: {}", filter.id());
                    return criteriaBuilder.disjunction();
                }
            }

            if (StringUtils.hasText(filter.employeeId())) {
                try {
                    UUID empUuid = UUID.fromString(filter.employeeId());
                    predicates.add(criteriaBuilder.equal(employeeJoin.get(FIELD_EMPLOYEE_ID), empUuid));
                } catch (IllegalArgumentException e) {
                    LOG.warn("ID de empleado inválido ignorado: {}", filter.employeeId());
                    return criteriaBuilder.disjunction();
                }
            }

            if (StringUtils.hasText(filter.deviceId())) {
                try {
                    UUID devUuid = UUID.fromString(filter.deviceId());
                    predicates.add(criteriaBuilder.equal(deviceJoin.get(FIELD_DEVICE_ID), devUuid));
                } catch (IllegalArgumentException e) {
                    LOG.warn("ID de equipo inválido ignorado: {}", filter.deviceId());
                    return criteriaBuilder.disjunction();
                }
            }

            if (filter.loanStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get(FIELD_LOAN_STATUS), filter.loanStatus()));
            }

            if (filter.issuedAt() != null) {
                predicates.add(criteriaBuilder.equal(root.get(FIELD_ISSUED_AT), filter.issuedAt()));
            }
            if (filter.startIssuedAt() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(FIELD_ISSUED_AT), filter.startIssuedAt()));
            }
            if (filter.endIssuedAt() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(FIELD_ISSUED_AT), filter.endIssuedAt()));
            }

            if (filter.returnedAt() != null) {
                predicates.add(criteriaBuilder.equal(root.get(FIELD_RETURNED_AT), filter.returnedAt()));
            }
            if (filter.startReturnedAt() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(FIELD_RETURNED_AT), filter.startReturnedAt()));
            }
            if (filter.endReturnedAt() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(FIELD_RETURNED_AT), filter.endReturnedAt()));
            }

            if (StringUtils.hasText(filter.deviceName())) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(deviceJoin.get(FIELD_NAME)), 
                    buildLikePattern(filter.deviceName())
                ));
            }
            if (StringUtils.hasText(filter.deviceBrand())) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(deviceJoin.get(FIELD_BRAND)), 
                    buildLikePattern(filter.deviceBrand())
                ));
            }
            if (filter.deviceType() != null) {
                predicates.add(criteriaBuilder.equal(deviceJoin.get(FIELD_TYPE), filter.deviceType()));
            }
            if (filter.deviceStatus() != null) {
                predicates.add(criteriaBuilder.equal(deviceJoin.get(FIELD_STATUS), filter.deviceStatus()));
            }

            if (StringUtils.hasText(filter.employeeUsername())) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(employeeJoin.get(FIELD_USERNAME)), 
                    buildLikePattern(filter.employeeUsername())
                ));
            }
            if (StringUtils.hasText(filter.employeeFullName())) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(employeeJoin.get(FIELD_FULLNAME)), 
                    buildLikePattern(filter.employeeFullName())
                ));
            }
            if (StringUtils.hasText(filter.employeeEmail())) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(employeeJoin.get(FIELD_EMAIL)), 
                    buildLikePattern(filter.employeeEmail())
                ));
            }
            if (filter.employeeStatus() != null) {
                predicates.add(criteriaBuilder.equal(employeeJoin.get(FIELD_STATUS), filter.employeeStatus()));
            }
            if (filter.employeeRole() != null) {
                predicates.add(criteriaBuilder.equal(employeeJoin.get(FIELD_ROLE), filter.employeeRole()));
            }

            if (StringUtils.hasText(filter.globalSearch())) {
                String searchPattern = buildLikePattern(filter.globalSearch());
                Predicate globalPredicate = criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(employeeJoin.get(FIELD_FULLNAME)), searchPattern),
                    criteriaBuilder.like(criteriaBuilder.lower(employeeJoin.get(FIELD_EMAIL)), searchPattern),
                    criteriaBuilder.like(criteriaBuilder.lower(deviceJoin.get(FIELD_NAME)), searchPattern),
                    criteriaBuilder.like(criteriaBuilder.lower(deviceJoin.get(FIELD_BRAND)), searchPattern)
                );
                predicates.add(globalPredicate);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static String buildLikePattern(String term) {
        if (term == null) return "%";
        String escapedTerm = term.replace("\\", "\\\\")
                                 .replace("%", "\\%")
                                 .replace("_", "\\_");
        return "%" + escapedTerm.toLowerCase() + "%";
    }
}
