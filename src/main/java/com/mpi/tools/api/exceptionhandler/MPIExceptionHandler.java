package com.mpi.tools.api.exceptionhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Responsavel pela captura de excepcoes de entidades e tratar
 */
@ControllerAdvice
public class MPIExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Disponibilizacao de ficheiro de mensagens
     */
    @Autowired
    private MessageSource messageSource;
    /**
     * Captura de message com propriedade nao lidas
     * @param ex
     * @param headers
     * @param status
     * @param request
     * @return
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        String prodMessage = messageSource.getMessage("mensagem.invalida",null, LocaleContextHolder.getLocale());
        String devMessage = ex.getCause() != null ? ex.getCause().toString(): ex.toString();

        List<Error> erros = Arrays.asList(new Error(devMessage,prodMessage));

        return  handleExceptionInternal(ex,erros,headers,HttpStatus.BAD_REQUEST,request);
    }

    public static class Error{
        private String devMessage;
        private String prodMessage;

        public Error(String devMessage, String prodMessage) {
            this.devMessage = devMessage;
            this.prodMessage = prodMessage;
        }

        public String getDevMessage() {
            return devMessage;
        }

        public String getProdMessage() {
            return prodMessage;
        }
    }

    /**
     * Descriminar argumentos nao permitidos/Validos
     * @param ex
     * @param headers
     * @param status
     * @param request
     * @return
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        List<Error> errors = listErrors(ex.getBindingResult());
        return handleExceptionInternal(ex,errors,headers,HttpStatus.BAD_REQUEST,request);
    }

    private List<Error> listErrors(BindingResult bindingResult){
        List<Error> errors = new ArrayList<>();

        /**
         * Lista que contem todos erros que foram capturados pela excepacao handleMethodArgumentNotValid
         */
      for (FieldError fieldError : bindingResult.getFieldErrors()){
          String devMessage = fieldError.toString();
          String prodMessage = messageSource.getMessage(fieldError,LocaleContextHolder.getLocale()) ;
            errors.add(new Error(devMessage,prodMessage));
      }
        return errors;
    }

    @ExceptionHandler({EmptyResultDataAccessException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException emptyResultDataAccessException, WebRequest request){

        String prodMessage = messageSource.getMessage("recurso.nao-encontrado",null, LocaleContextHolder.getLocale());
        String devMessage = emptyResultDataAccessException.toString();

        List<Error> erros = Arrays.asList(new Error(devMessage,prodMessage));

        return handleExceptionInternal(emptyResultDataAccessException,erros,new HttpHeaders(),HttpStatus.NOT_FOUND,request);
    }
}
