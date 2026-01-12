package net.springprojectbackend.springboot.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "visa_call_input", 
uniqueConstraints = @UniqueConstraint(columnNames = {"purchase_date", "business_name", "price"})
)
public class AllExpenses {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "expense_category")
	ExpenseCategory categoryId;
	
	@ManyToOne
	@JoinColumn(name = "gemini_expense_category")
	ExpenseCategory geminiCategoryId;
	
	@Column(name = "purchase_date")
	private LocalDate purchaseDate;
	
	@Column(name = "business_name")
	private String businessName;
	
	@Column(name = "price", nullable = false, precision = 10, scale = 2) // maps to DECIMAL(10,2)
	private BigDecimal price;
	
	private String card;
	
	private LocalDate chargeDate;
	
	private String purchesKind;
	
	private Double discount;
	
	private String notes;
	
	private LocalDateTime importDate;
	
	public AllExpenses(LocalDate purchesDate, String bussinessName, BigDecimal price, String card,
			LocalDate chargeDate, String purchesKind, double discount, String notes, LocalDateTime ImportDate) {
		super();
		this.purchaseDate = purchesDate;
		this.businessName = bussinessName;
		this.price = price;
		this.card = card;
		this.chargeDate = chargeDate;
		this.purchesKind = purchesKind;
		this.discount = discount;
		this.notes = notes;
	}

	
	public AllExpenses() {}

	
	public AllExpenses(LocalDate purchesDate, String bussinnesName, BigDecimal price, String cardName,
			LocalDate chargeDate) {
		super();
		this.purchaseDate = purchesDate;
		this.businessName = bussinnesName;
		this.price = price;
		this.card = cardName;
		this.chargeDate = chargeDate;
		
	}

	
	public ExpenseCategory getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(ExpenseCategory categoryId) {
		this.categoryId = categoryId;
	}

	public ExpenseCategory getGeminiCategoryId() {
		return geminiCategoryId;
	}

	public void setGeminiCategoryId(ExpenseCategory geminiCategoryId) {
		this.geminiCategoryId = geminiCategoryId;
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

	public void setImportDate(LocalDateTime importDate) {
		this.importDate = importDate;
	}

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getPurchesDate() {
		return purchaseDate;
	}

	public void setPurchesDate(LocalDate purchesDate) {
		this.purchaseDate = purchesDate;
	}

	public String getBussinessName() {
		return businessName;
	}

	public void setBussinessName(String bussinessName) {
		this.businessName = bussinessName;
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

	public LocalDate getChargeDate() {
		return chargeDate;
	}

	public void setChargeDate(LocalDate chargeDate) {
		this.chargeDate = chargeDate;
	}

	public String getPurchesKind() {
		return purchesKind;
	}

	public void setPurchesKind(String purchesKind) {
		this.purchesKind = purchesKind;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public LocalDateTime getImportDate() {
		return importDate;
	}

	

	
}
	
	
	