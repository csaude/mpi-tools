package com.mpi.tools.api.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
	@Column(updatable = false)
	private Date dateCreated;

	@Column(nullable = false, unique = false)
	private String opencr_cruid;

	@Column(name = "openmrs_uuid", nullable = false)
	private String openmrsUuid;

	@JoinColumn(name = "match_issue_id")
	@ManyToOne(targetEntity = MatchIssue.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER, optional = false)
	private MatchIssue matchIssue;

	private String givenName;
	private String familyName;
	private String tarvNid;
	private String gender;
	private String birthDate;

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

	public String getOpencr_cruid() {
		return opencr_cruid;
	}

	public void setOpencr_cruid(String opencr_cruid) {
		this.opencr_cruid = opencr_cruid;
	}

	public MatchIssue getMatchIssue() {
		return matchIssue;
	}

	public void setMatchIssue(MatchIssue matchIssue) {
		this.matchIssue = matchIssue;
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

	public String getOpenmrsUuid() {
		return openmrsUuid;
	}

	public void setOpenmrsUuid(String openmrsUuid) {
		this.openmrsUuid = openmrsUuid;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof MatchedRecord)) return false;
		
		MatchedRecord toCompare = (MatchedRecord)obj;
		
		return this.getOpenmrsUuid().equals(toCompare.getOpenmrsUuid());
	}

}
