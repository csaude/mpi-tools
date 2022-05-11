package com.mpi.tools.api.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
	@Column(name = "openmrs_uuid", updatable = false)
	private String openmrsUuid;

	@Column(name = "opencr_cruid", updatable = false)
	private String openCrCruid;

	@NotNull
	@Column(name = "date_created", updatable = false)
	private Date dateCreated;

	@OneToMany(mappedBy = "matchIssue", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
	private List<MatchedRecord> matchedRecords = new ArrayList<>();

	public void addMatcheRecords(MatchedRecord matched) {
		this.matchedRecords.add(matched);
	}

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

	public String getOpenmrsUuid() {
		return openmrsUuid;
	}

	public void setOpenmrsUuid(String openmrsUuid) {
		this.openmrsUuid = openmrsUuid;
	}

	public String getOpenCrCruid() {
		return openCrCruid;
	}

	public void setOpenCrCruid(String openCrCruid) {
		this.openCrCruid = openCrCruid;
	}

}
