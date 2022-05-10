package com.mpi.tools.api.event;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationEvent;


public class CreatedResourceEvent  extends ApplicationEvent {
    /**
	 * 
	 */
	private static final long serialVersionUID = 6878843008127225322L;

	/**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */

    private HttpServletResponse response;

    private Long codigo;

    public CreatedResourceEvent(Object source, HttpServletResponse response,Long codigo) {
        super(source);
        this.response=response;
        this.codigo=codigo;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public Long getCodigo() {
        return codigo;
    }
}
