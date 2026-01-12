package net.springprojectbackend.springboot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.GrantedAuthority;

import net.springprojectbackend.springboot.model.Family;

public interface FamilyRepository extends JpaRepository<Family, Long>{

	Family findByFamily(String familyName);

}
