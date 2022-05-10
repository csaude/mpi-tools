package com.mpi.tools.api.resource;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mpi.tools.api.event.CreatedResourceEvent;
import com.mpi.tools.api.model.MatchIssue;
import com.mpi.tools.api.repository.MatchIssueRepository;

@RestController
@RequestMapping("/matchissues")
public class MatchIssueResource {

    @Autowired
    private MatchIssueRepository matchIssueRepository;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MatchIssue> saveMatchIssue(@Valid @RequestBody MatchIssue recordToSave , HttpServletResponse response){
        MatchIssue savedRecord = matchIssueRepository.save(recordToSave);
        
        applicationEventPublisher.publishEvent(new CreatedResourceEvent(this, response, savedRecord.getId()));

        return  ResponseEntity.status(HttpStatus.CREATED).body(savedRecord);
    }
}
