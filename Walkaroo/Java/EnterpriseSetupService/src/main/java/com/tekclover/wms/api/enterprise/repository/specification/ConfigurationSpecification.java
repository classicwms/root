package com.tekclover.wms.api.enterprise.repository.specification;

import com.tekclover.wms.api.enterprise.model.configuration.Configuration;
import com.tekclover.wms.api.enterprise.model.configuration.SearchConfiguration;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ConfigurationSpecification implements Specification<Configuration> {

    SearchConfiguration searchConfiguration;

    public ConfigurationSpecification(SearchConfiguration inputSearchParams) {
        this.searchConfiguration = inputSearchParams;
    }

    @Override
    public Predicate toPredicate(Root<Configuration> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchConfiguration.getCompanyCodeId() != null && !searchConfiguration.getCompanyCodeId().isEmpty()) {
            predicates.add(cb.equal(root.get("companyCodeId"), searchConfiguration.getCompanyCodeId()));
        }

        if (searchConfiguration.getPlantId() != null && !searchConfiguration.getPlantId().isEmpty()) {
            predicates.add(cb.equal(root.get("plantId"), searchConfiguration.getPlantId()));
        }

        if (searchConfiguration.getWarehouseId() != null && !searchConfiguration.getWarehouseId().isEmpty()) {
            predicates.add(cb.equal(root.get("warehouseId"), searchConfiguration.getWarehouseId()));
        }
        if (searchConfiguration.getLanguageId() != null && !searchConfiguration.getLanguageId().isEmpty()) {
            predicates.add(cb.equal(root.get("languageId"), searchConfiguration.getLanguageId()));
        }

        if (searchConfiguration.getProfile() != null && !searchConfiguration.getProfile().isEmpty()) {
            predicates.add(cb.equal(root.get("profile"), searchConfiguration.getProfile()));
        }

        if (searchConfiguration.getCreatedBy() != null && !searchConfiguration.getCreatedBy().isEmpty()) {
            predicates.add(cb.equal(root.get("createdBy"), searchConfiguration.getCreatedBy()));
        }

        if (searchConfiguration.getStartCreatedOn() != null && searchConfiguration.getEndCreatedOn() != null) {
            predicates.add(cb.between(root.get("createdOn"), searchConfiguration.getStartCreatedOn(), searchConfiguration.getEndCreatedOn()));
        }
        predicates.add(cb.equal(root.get("deletionIndicator"), 0L));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}
