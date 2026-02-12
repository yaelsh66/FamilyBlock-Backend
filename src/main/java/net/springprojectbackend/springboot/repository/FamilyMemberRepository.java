package net.springprojectbackend.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.springprojectbackend.springboot.model.FamilyMember;
import net.springprojectbackend.springboot.model.FamilyMember.UserRole;
import java.util.List;
import net.springprojectbackend.springboot.model.AppUser;


public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long>{

	public FamilyMember findByUser(AppUser user);
	public FamilyMember findByFirebaseUid(String uid);
	public List<FamilyMember> findAllByFamily_id(Long familyId);
	public List<FamilyMember> findAllByFamilyIdAndRole(Long familyId, UserRole role);
	

}
