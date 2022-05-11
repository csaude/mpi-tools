package com.mpi.tools.api.resource;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mpi.tools.api.event.CreatedResourceEvent;
import com.mpi.tools.api.model.Pessoa;
import com.mpi.tools.api.repository.PersonRepository;
import com.mpi.tools.api.services.PessoaService;

@RestController
@RequestMapping("/pessoas")
public class PersonResource {

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private PessoaService pessoaService;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	@GetMapping
	public ResponseEntity<?> findAllPeople() {
		List<Pessoa> pessoas = personRepository.findAll();

		return !pessoas.isEmpty() ? ResponseEntity.ok(pessoas) : ResponseEntity.noContent().build();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Pessoa> createCategory(@Valid @RequestBody Pessoa pessoa, HttpServletResponse response) {

		Pessoa person = personRepository.save(pessoa);
		applicationEventPublisher.publishEvent(new CreatedResourceEvent(this, response, person.getCodigo()));

		return ResponseEntity.status(HttpStatus.CREATED).body(person);
	}

	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removePerson(@PathVariable Long codigo) {
		personRepository.deleteById(codigo);
	}

	/**
	 * A actualizacao ainda nao funciona como deve ser por causa da retorno do
	 * objecto ao saltar para a base
	 * 
	 * @param codigo
	 * @param pessoaFromClient
	 * @return
	 */
	@PutMapping("/{codigo}")
	public ResponseEntity<Pessoa> updatePerson(@PathVariable Long codigo, @Valid Pessoa pessoaFromClient) {
		Optional<Pessoa> pessoa = pessoaService.updatePerson(codigo, pessoaFromClient);
		return ResponseEntity.ok(pessoa.get());

	}

	@PutMapping("/{codigo}/activo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updatePartialInfoPerson(@PathVariable Long codigo, @RequestBody Boolean activo) {
		pessoaService.updatePartialInfoPerson(codigo, activo);
	}
}
