package com.mpi.tools.api.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "mpi_matched_record")
public class MatchedRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
    
    @NotNull
    @Column(name = "date_created", nullable = false, updatable = false)
    private Date dateCreated;
    
	@Column(nullable = false, unique = false)
	private String opencr_cruid;

	@Column(nullable = false, unique = false)
	private String cruid;
	
	@JoinColumn(name="match_issue_id", nullable=false)
	@ManyToOne(optional=false)
	private MatchIssue matchIssue;

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOpencr_cruid() {
		return opencr_cruid;
	}

	public void setOpencr_cruid(String opencr_cruid) {
		this.opencr_cruid = opencr_cruid;
	}

	public String getCruid() {
		return cruid;
	}

	public void setCruid(String cruid) {
		this.cruid = cruid;
	}

	public MatchIssue getMatchIssue() {
		return matchIssue;
	}

	public void setMatchIssue(MatchIssue matchIssue) {
		this.matchIssue = matchIssue;
	}
}
