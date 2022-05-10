create table mpi_match_issue (	id bigint(20) NOT NULL AUTO_INCREMENT, 
								date_created datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
								PRIMARY KEY (id)
							) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table mpi_matched_record (id bigint(20) NOT NULL AUTO_INCREMENT, 
								date_created datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
								openmrs_uuid varchar(36) NOT NULL,
								opencr_cruid varchar(36) NOT NULL,
								match_issue_id bigint(20) NOT NULL, 
								PRIMARY KEY (id),
								UNIQUE KEY `unq_matched_openmrs_uuid` (`match_issue_id`, `openmrs_uuid`),
								UNIQUE KEY `unq_matched_opencr_cruid` (`match_issue_id`, `opencr_cruid`),
								CONSTRAINT `matched_record_fk1` FOREIGN KEY (`match_issue_id`) REFERENCES `mpi_match_issue` (`id`)
 
							) ENGINE=InnoDB DEFAULT CHARSET=utf8;
							
							