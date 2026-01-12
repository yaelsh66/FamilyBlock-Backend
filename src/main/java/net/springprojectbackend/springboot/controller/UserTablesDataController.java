package net.springprojectbackend.springboot.controller;

import org.springframework.web.bind.annotation.*;

import net.springprojectbackend.springboot.repository.UserDataRepository;
import net.springprojectbackend.springboot.repository.UserTablesRepository;

import java.util.List;

@RestController
@RequestMapping("/apinotused")
public class UserTablesDataController {

	private final UserTablesRepository userTRepo;
	private final UserDataRepository userDRepo;
	
	public UserTablesDataController(UserTablesRepository userTRepo, UserDataRepository userDRepo) {
		this.userTRepo = userTRepo;
		this.userDRepo = userDRepo;
		
	}
	
	
	
}
