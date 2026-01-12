package net.springprojectbackend.springboot.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "monthly_category_average")
public class MonthlyCategoryAverage {
	
	 

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	 
	private LocalDate date;
	 
	public MonthlyCategoryAverage() {
		super();
			
		}
	 

}
