package com.mpi.tools.api.event.listener;

import java.net.URI;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mpi.tools.api.event.CreatedResourceEvent;

@Component
public class CreatedResourceListener implements ApplicationListener<CreatedResourceEvent> {

    @Override
    public void onApplicationEvent(CreatedResourceEvent createdResourceEvent) {
        HttpServletResponse response = createdResourceEvent.getResponse();
        Long codigo = createdResourceEvent.getCodigo();

        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{codigo}")
                .buildAndExpand(codigo).toUri();
        response.setHeader("Location",uri.toASCIIString());
    }
}
