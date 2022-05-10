package com.mpi.tools.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mpi.tools.api.model.Pessoa;

public interface PersonRepository extends JpaRepository<Pessoa,Long> {
}
