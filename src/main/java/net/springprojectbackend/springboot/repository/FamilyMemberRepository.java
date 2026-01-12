package net.springprojectbackend.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.springprojectbackend.springboot.model.FamilyMember;
import net.springprojectbackend.springboot.model.FamilyMember.UserRole;
import java.util.List;
import net.springprojectbackend.springboot.model.AppUser;


public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long>{

	FamilyMember findByUser(AppUser user);
	FamilyMember findByFirebaseUid(String uid);
	

}
