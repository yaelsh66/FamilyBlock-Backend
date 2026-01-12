package net.springprojectbackend.springboot.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "visa_call_input_future")
public class FutureExpenses {
	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "purchase_date")
	private LocalDate purchaseDate;
	
	@Column(name = "business_name")
	private String businessName;
	
	@Column(name = "price", nullable = false, precision = 10, scale = 2) // maps to DECIMAL(10,2)
	private BigDecimal price;
	
	private String card;
	
	private String purchesKind;
	
	private Double discount;
	
	private String notes;
	
	public FutureExpenses(LocalDate purchaseDate, String businessName, BigDecimal price, String card) {
		super();
		this.purchaseDate = purchaseDate;
		this.businessName = businessName;
		this.price = price;
		this.card = card;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public LocalDate getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(LocalDate purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getCard() {
		return card;
	}

	public void setCard(String card) {
		this.card = card;
	}

	public String getPurchesKind() {
		return purchesKind;
	}

	public void setPurchesKind(String purchesKind) {
		this.purchesKind = purchesKind;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

}
