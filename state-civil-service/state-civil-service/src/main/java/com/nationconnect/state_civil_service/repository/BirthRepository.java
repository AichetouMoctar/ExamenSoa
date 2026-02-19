package com.nationconnect.state_civil_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.nationconnect.state_civil_service.model.BirthCertificate;

@Repository
public interface BirthRepository extends JpaRepository<BirthCertificate, Long> {
    boolean existsByChildId(Long childId);
    
}
