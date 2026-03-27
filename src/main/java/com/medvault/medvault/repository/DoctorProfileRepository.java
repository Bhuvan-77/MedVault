package com.medvault.medvault.repository;

import com.medvault.medvault.entity.DoctorProfile;
import com.medvault.medvault.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorProfileRepository extends JpaRepository<DoctorProfile, Long> {

    Optional<DoctorProfile> findByUser(User user);
    List<DoctorProfile> findBySpecializationIgnoreCase(String specialization);

}
