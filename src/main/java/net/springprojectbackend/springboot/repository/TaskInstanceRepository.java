package net.springprojectbackend.springboot.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.springprojectbackend.springboot.dto.FamilyPendingCompletionResponse;
import net.springprojectbackend.springboot.dto.TaskDto.TaskHistoryResponse;
import net.springprojectbackend.springboot.model.TaskInstance;
import net.springprojectbackend.springboot.model.TaskInstance.TaskStatus;

public interface TaskInstanceRepository extends JpaRepository<TaskInstance, Long> {

	List<TaskInstance> findAllByFamilyMember_id(Long familyMember_id);
	
	@Query("""
	        SELECT ti
	        FROM TaskInstance ti
	        JOIN ti.familyMember fm
	        WHERE fm.family.id = :familyId
	          AND fm.role = 'CHILD'
	    """)
	    List<TaskInstance> findAllChildTasksByFamilyId(@Param("familyId") Long familyId);
	
	@Query("""
		    SELECT new net.springprojectbackend.springboot.dto.FamilyPendingCompletionResponse(
		        fm.firebaseUid,
		        ti.id,
		        ti.title,
		        ti.description,
		        ti.minutesReward,
		        ti.childComment,
		        ti.parentComment
		    )
		    FROM TaskInstance ti
		    JOIN ti.familyMember fm
		    WHERE fm.family.id = :familyId
		      AND fm.role = 'CHILD'
		      AND ti.status = :status
		""")
		List<FamilyPendingCompletionResponse>
		findPendingCompletionsByFamilyId(@Param("familyId") Long familyId, @Param("status") TaskStatus status);
	
	
	List<TaskHistoryResponse> findByFamilyMember_Id(Long familyMemberId);
	
	
}
