package com.mpi.tools.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mpi.tools.api.model.MatchConfig;
import com.mpi.tools.api.repository.MatchConfigRepository;

@Service
public class MatchConfigService {

	@Autowired
	private MatchConfigRepository matchConfigRepository;

	public MatchConfig createMatchConfig(MatchConfig matchConfig) {

		MatchConfig config = matchConfigRepository.saveAndFlush(matchConfig);

		return config;
	}

	public List<MatchConfig> findMatchConfig(String matchConfig) {

		List<MatchConfig> config = matchConfigRepository.findByType(matchConfig);

		return config;
	}

	public void deleteMatchConfig() {
		this.matchConfigRepository.deleteAll();
	}

}
