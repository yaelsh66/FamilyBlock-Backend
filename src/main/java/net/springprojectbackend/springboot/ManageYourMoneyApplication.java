package net.springprojectbackend.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
public class ManageYourMoneyApplication {

	public static void main(String[] args) {
		SpringApplication.run(ManageYourMoneyApplication.class, args);
		System.out.print("ff");
	}

}
