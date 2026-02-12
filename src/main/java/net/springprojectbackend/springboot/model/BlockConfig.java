package net.springprojectbackend.springboot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "block_config")
public class BlockConfig {

	public BlockConfig() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne(optional = false)
	@JoinColumn(name = "child_id", unique = true)
	private FamilyMember child;
	
	@Column(columnDefinition = "TEXT")
    private String blockedAppsJson;

    @Column(columnDefinition = "TEXT")
    private String blockedBrowserAppsJson;

    @Column(columnDefinition = "TEXT")
    private String blockedWebsitesJson;
    
    @Column(columnDefinition = "TEXT")
    private String blockedPermanentWebsitesJson;
    
    private Boolean rewritePermanentWebsites=false;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public FamilyMember getChild() {
		return child;
	}

	public void setChild(FamilyMember child) {
		this.child = child;
	}

	public String getBlockedAppsJson() {
		return blockedAppsJson;
	}

	public void setBlockedAppsJson(String blockedAppsJson) {
		this.blockedAppsJson = blockedAppsJson;
	}

	public String getBlockedBrowserAppsJson() {
		return blockedBrowserAppsJson;
	}

	public void setBlockedBrowserAppsJson(String blockedBrowserAppsJson) {
		this.blockedBrowserAppsJson = blockedBrowserAppsJson;
	}

	public String getBlockedWebsitesJson() {
		return blockedWebsitesJson;
	}

	public void setBlockedWebsitesJson(String blockedWebsitesJson) {
		this.blockedWebsitesJson = blockedWebsitesJson;
	}

	public String getBlockedPermanentWebsitesJson() {
		return blockedPermanentWebsitesJson;
	}

	public void setBlockedPermanentWebsitesJson(String blockedPermanentWebsitesJson) {
		this.blockedPermanentWebsitesJson = blockedPermanentWebsitesJson;
	}

	public Boolean getRewritePermanentWebsites() {
		return rewritePermanentWebsites;
	}

	public void setRewritePermanentWebsites(Boolean rewritePermanentWebsites) {
		this.rewritePermanentWebsites = rewritePermanentWebsites;
	}
    
    
}
