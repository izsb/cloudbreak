package com.sequenceiq.cloudbreak.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sequenceiq.cloudbreak.domain.CustomConfigurationProperty;
import com.sequenceiq.cloudbreak.workspace.repository.EntityType;

@EntityType(entityClass = CustomConfigurationProperty.class)
@Transactional(Transactional.TxType.REQUIRED)
public interface CustomConfigurationPropertyRepository extends JpaRepository<CustomConfigurationProperty, Long> {
}
