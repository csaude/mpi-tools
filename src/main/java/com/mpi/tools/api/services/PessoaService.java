package com.mpi.tools.api.services;

import org.springframework.beans.BeanUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.mpi.tools.api.model.Pessoa;
import com.mpi.tools.api.repository.PersonRepository;

import java.util.Optional;

@Service
public class PessoaService {

    private PersonRepository personRepository;

    public Optional<Pessoa> updatePerson(Long codigo, Pessoa pessoaFromClient){
        Optional<Pessoa> pessoa = personRepository.findById(codigo);

        if (!pessoa.isPresent()){
            throw new EmptyResultDataAccessException(1);
        }
        BeanUtils.copyProperties(pessoaFromClient,pessoa, "codigo");
        personRepository.save(pessoa.get());

        return pessoa;
    }

    public void updatePartialInfoPerson(Long codigo, Boolean activo) {


        Optional<Pessoa> pessoa = personRepository.findById(codigo);

        if (!pessoa.isPresent()){
            throw new EmptyResultDataAccessException(1);
        }

        pessoa.get().setActivo(activo);

        personRepository.save(pessoa.get());
    }
}
