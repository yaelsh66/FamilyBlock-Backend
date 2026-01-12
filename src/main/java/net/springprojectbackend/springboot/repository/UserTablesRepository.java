package net.springprojectbackend.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import net.springprojectbackend.springboot.model.UserTables;

public interface UserTablesRepository extends JpaRepository<UserTables, Long> {

}


