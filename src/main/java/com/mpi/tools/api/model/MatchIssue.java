package com.mpi.tools.api.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "mpi_match_issue")
public class MatchIssue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "date_created", nullable = false, updatable = false)
    private Date dateCreated;
	
    @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST)
	private List<MatchedRecord> matchedRecords;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public List<MatchedRecord> getMatchedRecords() {
		return matchedRecords;
	}

	public void setMatchedRecords(List<MatchedRecord> matchedRecords) {
		this.matchedRecords = matchedRecords;
	}
}
