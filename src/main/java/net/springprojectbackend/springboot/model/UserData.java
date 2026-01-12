package net.springprojectbackend.springboot.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_data")
public class UserData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String tableName;
	
	@Column(columnDefinition = "json")
	private String rowData;

	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	
	
	
	

}
