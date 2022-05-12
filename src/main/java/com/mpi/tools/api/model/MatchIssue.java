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

	private String givenName;
	private String familyName;
	private String tarvNid;
	private String gender;
	private String birthDate;
	private Boolean transferedTo;

	@OneToMany(mappedBy = "matchIssue", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
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

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public String getTarvNid() {
		return tarvNid;
	}

	public void setTarvNid(String tarvNid) {
		this.tarvNid = tarvNid;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	public Boolean getTransferedTo() {
		return transferedTo;
	}

	public void setTransferedTo(Boolean transferedTo) {
		this.transferedTo = transferedTo;
	}

}
